package physics.model;

import org.ejml.simple.SimpleMatrix;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Constraint {

    static void apply(List<Particle> particles, List<Constraint> constraints, double ks, double kd) {
        if (particles.stream().filter(distinctByKey((Particle p) -> p.getConstructPos().length)).limit(2).count() > 1)
            throw new IllegalArgumentException("The input contained particles with a different number of dimensions.");

        int dimensions = particles.get(0).getConstructPos().length;
        SimpleMatrix W = new SimpleMatrix(particles.size() * dimensions, particles.size() * dimensions);
        SimpleMatrix J0 = new SimpleMatrix(constraints.size(), particles.size() * dimensions);
        SimpleMatrix J1 = new SimpleMatrix(constraints.size(), particles.size() * dimensions);
        SimpleMatrix q1 = new SimpleMatrix(1, particles.size());
        SimpleMatrix Q = new SimpleMatrix(1, particles.size());
        SimpleMatrix C0ks = new SimpleMatrix(1, constraints.size());
        SimpleMatrix C1kd = new SimpleMatrix(1, constraints.size());

        for (int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            for (int j = 0; j < dimensions; j++) {
                W.set(dimensions * i + j, dimensions * i + j, 1 / particle.getMass());
                Q.set(dimensions * i + j, particle.getForces()[j]);
                q1.set(dimensions * i + j, particle.getVelocity()[j]);
            }
        }

        for (int i = 0; i < constraints.size(); i++) {
            Constraint constraint = constraints.get(i);
            C0ks.set(i,constraint.calculateC0() * ks);
            C1kd.set(i, constraint.calculateC1() * kd);
            Particle[] cParticles = constraint.getParticles();
            for (int j = 0; j < cParticles.length; j++) {
                int index = particles.indexOf(cParticles[j]);
                if (index < 0) throw new IllegalArgumentException("A constraint references an unknown particle.");
                for (int k = 0; k < dimensions; k++) {
                    J0.set(i, dimensions * index + k, constraint.calculateJ0()[j][k]);
                    J1.set(i, dimensions * index + k, constraint.calculateJ1()[j][k]);
                }
            }
        }

        // (J0 * W * J0T) * lambda = - (J1 * q1) - (J * W * QT) - (ks * C0) - (kd * C1)
        SimpleMatrix A = J0.mult(W).mult(J0.transpose());
        SimpleMatrix B = J1.mult(q1).plus(J0.mult(W).mult(Q.transpose())).plus(C0ks).plus(C1kd).negative();
        SimpleMatrix lambda = A.solve(B);

        // QH = J0 * lambda
        SimpleMatrix QH = J0.mult(lambda);
        for (int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            double[] forces = particle.getForces();
            for (int j = 0; j < dimensions; j++) {
                forces[j] += QH.get(dimensions * i + j);
            } particle.setForces(forces);
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    Particle[] getParticles();
    double calculateC0();
    double calculateC1();
    double[][] calculateJ0();
    double[][] calculateJ1();
    void draw();

}
