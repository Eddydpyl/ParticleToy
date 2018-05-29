package physics.model;

import physics.LinearSolver;

import java.util.Collection;

/**
 * This is a very dirty workaround for the issue of particles going through the Solid.
 * It has several issues, like for example all vertical velocities being nullified once the particle is at rest on the Floor.
 */

public class Floor extends Wall {

    private static final double[] NORMAL = new double[]{0,1};

    private Collection<Particle2D> particles;
    private double[] position;
    private double elasticity;
    private double boundary;

    public Floor(Collection<Particle2D> particles, double[] position, double elasticity, double boundary) {
        super(particles, position, NORMAL, elasticity, boundary);
        this.particles = particles;
        this.position = position;
        this.elasticity = elasticity;
        this.boundary = boundary;
    }

    @Override
    public void apply() {
        for (Particle particle : particles) {
            double direction = LinearSolver.vecDot(NORMAL, particle.getVelocity());
            double distance = LinearSolver.vecDot(LinearSolver.vecDiff(particle.getPosition(), position), NORMAL);
            // Check if the particle is traveling towards the floor and if it's close enough to it.
            if (distance < boundary) {
                if (direction < 0 && Math.pow(particle.getVelocity()[1], 2) > 1) {
                    // V2 = V1 - 2 * (V1 * N) * N
                    double[] reflection = LinearSolver.vecTimesScalar(NORMAL, 2 * LinearSolver.vecDot(particle.getVelocity(), NORMAL));
                    particle.setVelocity(LinearSolver.vecTimesScalar(LinearSolver.vecDiff(particle.getVelocity(), reflection), elasticity));
                } else {
                    particle.setForces(new double[]{particle.getForces()[0], 0});
                    particle.setVelocity(new double[]{particle.getVelocity()[0], 0});
                }
            }
        }
    }
}
