package physics.models.forces;

import physics.models.Grid2D;
import physics.models.kernels.Poly6Kernel;
import physics.models.particles.FluidParticle2D;

import static physics.LinearSolver.*;

public class SurfaceForce2D implements Force{

    private static final double T = 0.001;

    private FluidParticle2D particle;
    private Grid2D grid2D;
    private double sigma;
    private double h;

    public SurfaceForce2D(FluidParticle2D particle, Grid2D grid2D, double sigma, double h) {
        this.particle = particle;
        this.grid2D = grid2D;
        this.sigma = sigma;
        this.h = h;
    }

    @Override
    public void apply() {
        double[] forces = new double[]{0,0};
        Poly6Kernel kernel = new Poly6Kernel(particle, h);
        for (FluidParticle2D fluidParticle : grid2D.get(particle.getIndex())) {
            if (particle != fluidParticle && vecModule(vecDiff(particle.getPosition(), fluidParticle.getPosition())) <= h && fluidParticle.getDensity() > 0) {
                double[] gradient = vecTimesScalar(kernel.applyGradient(fluidParticle), fluidParticle.getMass() / fluidParticle.getDensity());
                if (vecModule(gradient) > T) {
                    double laplacian = (fluidParticle.getMass() / fluidParticle.getDensity()) * kernel.applyLaplacian(fluidParticle);
                    forces = vecAdd(forces, vecTimesScalar(gradient, sigma * laplacian * vecModule(gradient)));
                }
            }
        } particle.setForces(vecDiff(particle.getForces(), forces));
    }

    @Override
    public void draw() {}
}
