package physics.model;

import static physics.LinearSolver.*;

public class GravityForce2D implements Force {

    private static final double G = 9.8;

    private Particle2D[] particles;

    public GravityForce2D(Particle2D... particles) {
        this.particles = particles;
    }

    @Override
    public void apply() {
        for (Particle2D particle : particles) {
            double[] current = particle.getForces();
            double[] gravity = new double[]{0, - particle.getMass() * G};
            particle.setForces(vecAdd(current, gravity));
        }
    }

    @Override
    public void draw() {}

}
