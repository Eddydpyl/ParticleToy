import physics.model.Particle2D;
import physics.LinearSolver;

import java.util.Set;

public class Solver {

    public static void simulationStep(Set<Particle2D> particles, double dt) {
        for (Particle2D particle : particles) {
            particle.setPosition(LinearSolver.vecAdd(particle.getPosition(), LinearSolver.vecTimesScalar(particle.getVelocity(), dt)));
        }
    }

}
