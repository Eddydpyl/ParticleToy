package physics.models.kernels;

import physics.models.particles.Particle;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static physics.LinearSolver.*;

public class ViscosityKernel extends Kernel {

    public ViscosityKernel(Particle pi, double h) {
        super(pi, h);
    }

    @Override
    public double applyFunction(Particle pj) {
        double[] r = vecDiff(pi.getPosition(), pj.getPosition());
        double rm = vecModule(r);
        if (0 <= rm && rm <= h)
            return (15 / (2 * Math.PI * Math.pow(h, 3))) * (- Math.pow(rm, 3) / (2 * Math.pow(h, 3)) + Math.pow(rm, 2) / Math.pow(h, 2) + h / (2 * rm) - 1);
        else return 0;
    }

    @Override
    public double[] applyGradient(Particle pj) {
        throw new NotImplementedException();
    }

    @Override
    public double applyLaplacian(Particle pj) {
        double[] r = vecDiff(pi.getPosition(), pj.getPosition());
        double rm = vecModule(r);
        if (0 <= rm && rm <= h)
            return (45 / (Math.PI * Math.pow(h, 5))) * (1 - rm / h);
        else return 0;
    }

}
