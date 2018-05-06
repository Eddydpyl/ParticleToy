import java.util.Set;

public class Solver {

    public static void simulationStep(Set<Particle> particles, double dt) {
        for (Particle particle : particles) {
            particle.setPosition(LinearSolver2D.vecAdd(particle.getPosition(), LinearSolver2D.vecTimesScalar(particle.getVelocity(), dt)));
        }
    }

}
