package physics.models.kernels;

import physics.models.particles.Particle;

import static physics.LinearSolver.*;

public class Poly6Kernel extends Kernel {

    public Poly6Kernel(Particle pi, double h) {
        super(pi, h);
    }

    @Override
    public double applyFunction(Particle pj) {
        double[] r = vecDiff(pi.getPosition(), pj.getPosition());
        double rm = vecModule(r);
        if (0 <= rm && rm <= h)
            return (315 / (64 * Math.PI * Math.pow(h, 9))) * Math.pow(Math.pow(h, 2) - Math.pow(rm, 2), 3);
        else return 0;
    }

    @Override
    public double[] applyGradient(Particle pj) {
        double[] r = vecDiff(pi.getPosition(), pj.getPosition());
        double rm = vecModule(r);
        if (0 <= rm && rm <= h)
            return (vecTimesScalar(r, (- 945 * Math.pow(Math.pow(h, 2) - Math.pow(rm, 2), 2)) / (8 * Math.PI * Math.pow(h, 9))));
        else return new double[pi.getPosition().length];
    }

    @Override
    public double applyLaplacian(Particle pj) {
        double[] r = vecDiff(pi.getPosition(), pj.getPosition());
        double rm = vecModule(r);
        if (0 <= rm && rm <= h)
            return (945 / (8 * Math.PI * Math.pow(h, 9))) * (Math.pow(h, 2) - Math.pow(rm, 2)) * (Math.pow(rm, 2) - (3/4) * (Math.pow(h, 2) - Math.pow(rm, 2)));
        else return 0;
    }

}
