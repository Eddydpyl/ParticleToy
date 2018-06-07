package physics.models.solids;

import physics.LinearSolver;
import physics.models.particles.Particle;
import physics.models.particles.Particle2D;

import java.util.Collection;

import static org.lwjgl.opengl.GL11.*;

public class Wall implements Solid {

    private static final double DISTANT = 999;

    private Collection<Particle2D> particles;
    private double[] position;
    private double[] normal;
    private double elasticity;
    private double boundary;

    public Wall(Collection<Particle2D> particles, double[] position, double[] normal, double elasticity, double boundary) {
        this.particles = particles;
        this.position = position;
        this.normal = normal;
        this.elasticity = elasticity;
        this.boundary = boundary;
    }

    @Override
    public void apply() {
        for (Particle particle : particles) {
            double direction = LinearSolver.vecDot(normal, particle.getVelocity());
            double distance = LinearSolver.vecDot(LinearSolver.vecDiff(particle.getPosition(), position), normal);
            // Check if the particle is traveling towards the wall and if it's close enough to it.
            if (direction < 0 && distance < boundary) {
                // V2 = V1 - 2 * (V1 * N) * N
                double[] reflection = LinearSolver.vecTimesScalar(normal, 2 * LinearSolver.vecDot(particle.getVelocity(), normal));
                particle.setVelocity(LinearSolver.vecTimesScalar(LinearSolver.vecDiff(particle.getVelocity(), reflection), elasticity));
            }
        }
    }

    @Override
    public void draw() {
        double[] line = new double[]{normal[1], - normal[0]};
        glBegin(GL_LINES);
        glColor3d(0.6, 0.7, 0.8);
        glVertex2d(position[0] + DISTANT * line[0], position[1] + DISTANT * line[1]);
        glColor3d(0.6, 0.7, 0.8);
        glVertex2d(position[0] - DISTANT * line[0], position[1] - DISTANT * line[1]);
        glEnd();
    }

}
