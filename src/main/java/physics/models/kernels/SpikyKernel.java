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
        if (0 <= rm && rm <= h)
            return (15 / (Math.PI * Math.pow(h, 6))) * Math.pow(h - rm, 3);
        else return 0;
    }

    @Override
    public double[] applyGradient(Particle pj) {
        double[] r = vecDiff(pi.getPosition(), pj.getPosition());
        double rm = vecModule(r);
        if (0 <= rm && rm <= h)
            return vecTimesScalar(r, (- 45 / (Math.PI * Math.pow(h, 6) * rm)) * Math.pow(h - rm, 2));
        else return new double[pi.getPosition().length];
    }

    @Override
    public double applyLaplacian(Particle pj) {
        throw new NotImplementedException();
    }

}
