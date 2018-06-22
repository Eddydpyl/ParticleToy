package physics;

import org.ejml.simple.SimpleMatrix;
import physics.models.particles.Particle;
import physics.models.particles.RigidBody;

import java.util.List;

public class Integration {

    public static final int EULER = 0;
    public static final int MID_POINT = 1;
    public static final int RUNGE_KUTA = 2;
    public static final int IMPLICIT_EURLER = 3;

    private static final int VARIABLES = 4;

    public static void apply(List<? extends Particle> particles, double time, int mode) {
        switch (mode) {
            case EULER: {
                for (Particle particle : particles)
                    particle.updateState(time);
            } break;
            case MID_POINT: {
                double[][] original = saveState(particles);
                for (Particle particle : particles)
                    particle.updateState(time);
                double[][] euler = saveState(particles);
                double[][] diff = stateDiff(euler, original);
                double[][] middle = stateAdd(original, stateTimesScalar(diff, 1.0 / 2.0));
                loadState(particles, middle);
                for (Particle particle : particles)
                    particle.updateState(time);
            } break;
            case RUNGE_KUTA: {
                double[][] o1 = saveState(particles);
                for (Particle particle : particles)
                    particle.updateState(time);
                double[][] e1 = saveState(particles);
                double[][] k1 = stateDiff(e1, o1);

                double[][] o2 = stateAdd(o1, stateTimesScalar(k1, 1.0 / 2.0));
                loadState(particles, o2);
                for (Particle particle : particles)
                    particle.updateState(time);
                double[][] e2 = saveState(particles);
                double[][] k2 = stateDiff(e2, o2);

                double[][] o3 = stateAdd(o1, stateTimesScalar(k2, 1.0 / 2.0));
                loadState(particles, o3);
                for (Particle particle : particles)
                    particle.updateState(time);
                double[][] e3 = saveState(particles);
                double[][] k3 = stateDiff(e3, o3);

                double[][] o4 = stateAdd(o1, k3);
                loadState(particles, o4);
                for (Particle particle : particles)
                    particle.updateState(time);
                double[][] e4 = saveState(particles);
                double[][] k4 = stateDiff(e4, o4);

                double[][] ks = stateAdd(stateAdd(stateTimesScalar(k1, 1.0 / 6.0), stateTimesScalar(k2, 1.0 / 3.0)),
                        stateAdd(stateTimesScalar(k3, 1.0 / 3.0), stateTimesScalar(k4, 1.0 / 6.0)));

                loadState(particles, stateAdd(o1, ks));
                } break;
            case IMPLICIT_EURLER: {
                // TODO: Implement for RigidBody
                int dimensions = particles.get(0).getConstructPos().length;
                SimpleMatrix TS = new SimpleMatrix(particles.size() * dimensions, particles.size() * dimensions);
                SimpleMatrix F1 = new SimpleMatrix(1, particles.size() * dimensions);
                SimpleMatrix F2 = new SimpleMatrix(particles.size() * dimensions, particles.size() * dimensions);
                for (int i = 0; i < particles.size(); i++) {
                    Particle particle = particles.get(i);
                    particle.updateVelocity(time);
                    for (int j = 0; j < dimensions; j++) {
                        TS.set(dimensions * i + j, dimensions * i + j, 1 / time);
                        F1.set(dimensions * i + j, particle.getVelocity()[j]);
                        F2.set(dimensions * i + j, dimensions * i + j, particle.getForce()[j]);
                    }
                }
                SimpleMatrix DX = TS.minus(F2).solve(F1.transpose());
                for (int i = 0; i < particles.size(); i++) {
                    Particle particle = particles.get(i);
                    double[] position = particle.getPosition();
                    for (int j = 0; j < dimensions; j++) {
                        position[j] += DX.get(dimensions * i + j);
                    }
                }
            } break;
            default: throw new IllegalArgumentException();
        }
    }

    private static double[][] saveState(List<? extends Particle> particles){
        int dimensions = particles.get(0).getConstructPos().length;
        double[][] state = new double[particles.size()][dimensions * VARIABLES];
        for (int i = 0; i < particles.size(); i++){
            Particle particle = particles.get(i);
            if (particle instanceof RigidBody) {
                RigidBody body = (RigidBody) particle;
                state[i][0] = body.getPosition()[0];
                state[i][1] = body.getPosition()[1];
                state[i][2] = body.getVelocity()[0];
                state[i][3] = body.getVelocity()[1];
                state[i][4] = body.getOrientation();
                state[i][5] = body.getAngular();
            } else {
                state[i][0] = particle.getPosition()[0];
                state[i][1] = particle.getPosition()[1];
                state[i][2] = particle.getVelocity()[0];
                state[i][3] = particle.getVelocity()[1];
            }
        } return state;
    }

    private static void loadState(List<? extends Particle> particles, double[][] state){
        int dimensions = particles.get(0).getConstructPos().length;
        for (int i = 0; i < particles.size(); i++){
            Particle particle = particles.get(i);
            double[] position = new double[dimensions];
            double[] velocity = new double[dimensions];
            System.arraycopy(state[i], 0, position, 0, dimensions);
            System.arraycopy(state[i], dimensions, velocity, 0, dimensions);
            particle.setPosition(position);
            particle.setVelocity(velocity);
            if (particle instanceof RigidBody) {
                RigidBody body = (RigidBody) particle;
                body.setOrientation(state[i][4]);
                body.setAngular(state[i][5]);
            }
        }
    }

    private static double[][] stateAdd(double[][] s1, double[][] s2) {
        double[][] res = new double[s1.length][s1[0].length];
        for (int i = 0; i < s1.length; i++) {
            for (int j = 0; j < s1[0].length; j++) {
                res[i][j] = s1[i][j] + s2[i][j];
            }
        } return res;
    }

    private static double[][] stateDiff(double[][] s1, double[][] s2) {
        double[][] res = new double[s1.length][s1[0].length];
        for (int i = 0; i < s1.length; i++) {
            for (int j = 0; j < s1[0].length; j++) {
                res[i][j] = s1[i][j] - s2[i][j];
            }
        } return res;
    }

    private static double[][] stateTimesScalar(double[][] state, double n) {
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                state[i][j] *= n;
            }
        } return state;
    }

}
