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
        double aux = 0 <= rm && rm <= h ? Math.pow(Math.pow(h, 2) - Math.pow(rm, 2), 3) : 0;
        return (315 / (64 * Math.PI * Math.pow(h, 9))) * aux;
    }

    @Override
    public double[] applyGradient(Particle pj) {
        double[] r = vecDiff(pi.getPosition(), pj.getPosition());
        double rm = vecModule(r);
        double[] aux = 0 <= rm && rm <= h ? vecTimesScalar(r, - 6 * Math.pow(Math.pow(h, 2) - Math.pow(rm, 2), 2)) : new double[pi.getPosition().length];
        return (vecTimesScalar(aux, (315 / (64 * Math.PI * Math.pow(h, 9)))));
    }

    @Override
    public double applyLaplacian(Particle pj) {
        double[] r = vecDiff(pi.getPosition(), pj.getPosition());
        double rm = vecModule(r);
        double aux = 0 <= rm && rm <= h ? Math.pow(Math.pow(h, 2) - Math.pow(rm, 2), 3) : 0;
        return (315 / (64 * Math.PI * Math.pow(h, 9))) * aux;
    }

}
