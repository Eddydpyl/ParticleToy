package physics.models.kernels;

import physics.models.particles.Particle;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static physics.LinearSolver.*;

public class SpikyKernel extends Kernel {

    public SpikyKernel(Particle pi, double h) {
        super(pi, h);
    }

    @Override
    public double applyFunction(Particle pj) {
        double[] r = vecDiff(pi.getPosition(), pj.getPosition());
        double rm = vecModule(r);
        double aux = 0 <= rm && rm <= h ? Math.pow(h - rm, 3) : 0;
        return (15 / (Math.PI * Math.pow(h, 6))) * aux;
    }

    @Override
    public double[] applyGradient(Particle pj) {
        double[] r = vecDiff(pi.getPosition(), pj.getPosition());
        double rm = vecModule(r);
        double[] aux = 0 <= rm && rm <= h ? vecTimesScalar(r, rm * Math.pow(h - rm, 2)) : new double[pi.getPosition().length];
        return vecTimesScalar(aux, (- 45 / (Math.PI * Math.pow(h, 6))));
    }

    @Override
    public double applyLaplacian(Particle pj) {
        throw new NotImplementedException();
    }

}
