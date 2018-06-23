package physics.models.forces;

import physics.models.Grid2D;
import physics.models.kernels.Poly6Kernel;
import physics.models.kernels.SpikyKernel;
import physics.models.kernels.ViscosityKernel;
import physics.models.particles.FluidParticle2D;

import static physics.LinearSolver.*;

/**
 * Combination of all the forces that apply to FluidParticle2D, for the sake of efficiency.
 * Namely: PressureForce2D, SurfaceForce2D and ViscosityForce2D.
 */

public class LiquidForces2D implements Force {

    private static final double K = 8.3145;
    private static final double T = 0.001;

    private FluidParticle2D particle;
    private Grid2D grid2D;
    private double sigma;
    private double mu;
    private double p0;
    private double h;
    private double[] w; // Auxiliary variable scaling forces.

    public LiquidForces2D(FluidParticle2D particle, Grid2D grid2D, double sigma, double mu, double p0, double h, double[] w) {
        this.particle = particle;
        this.grid2D = grid2D;
        this.sigma = sigma;
        this.mu = mu;
        this.p0 = p0;
        this.h = h;
        this.w = w;
    }

    @Override
    public void apply() {
        double[] pressureForces = new double[]{0,0};
        double[] viscousForces = new double[]{0,0};
        double[] surfaceForces = new double[]{0,0};
        SpikyKernel spikyKernel = new SpikyKernel(particle, h);
        Poly6Kernel poly6Kernel = new Poly6Kernel(particle, h);
        ViscosityKernel viscosityKernel = new ViscosityKernel(particle, h);
        for (FluidParticle2D fluidParticle : grid2D.get(particle.getIndex())) {
            if (particle != fluidParticle && vecModule(vecDiff(particle.getPosition(), fluidParticle.getPosition())) <= h && fluidParticle.getDensity() > 0) {
                pressureForces = vecAdd(pressureForces, vecTimesScalar(spikyKernel.applyGradient(fluidParticle),
                        fluidParticle.getMass() * (pressure(particle) + pressure(fluidParticle)) / (2 * fluidParticle.getDensity())));
                viscousForces = vecTimesScalar(vecAdd(viscousForces, vecTimesScalar(vecDiff(fluidParticle.getVelocity(), particle.getVelocity()),
                        fluidParticle.getMass() * viscosityKernel.applyLaplacian(fluidParticle) / fluidParticle.getDensity())), mu);
                double[] gradient = vecTimesScalar(poly6Kernel.applyGradient(fluidParticle), fluidParticle.getMass() / fluidParticle.getDensity());
                if (vecModule(gradient) > T) {
                    double laplacian = (fluidParticle.getMass() / fluidParticle.getDensity()) * poly6Kernel.applyLaplacian(fluidParticle);
                    surfaceForces = vecAdd(surfaceForces, vecTimesScalar(gradient, sigma * laplacian * vecModule(gradient)));
                }
            }
        }
        particle.setForce(vecAdd(particle.getForce(), vecTimesScalar(pressureForces, w[0])));
        particle.setForce(vecAdd(particle.getForce(), vecTimesScalar(surfaceForces, w[1])));
        particle.setForce(vecAdd(particle.getForce(), vecTimesScalar(viscousForces, w[2])));
    }

    @Override
    public void draw() {}

    private double pressure(FluidParticle2D particle) {
        return K * (particle.getDensity() - p0);
    }
}
