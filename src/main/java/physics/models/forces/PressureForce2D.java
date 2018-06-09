package physics.models.forces;

import physics.models.Grid2D;
import physics.models.kernels.SpikyKernel;
import physics.models.particles.FluidParticle2D;

import static physics.LinearSolver.*;

public class PressureForce2D implements Force {

    private static final double K = 8.3145;

    private FluidParticle2D particle;
    private Grid2D grid2D;
    private double sigma;
    private double p0;
    private double h;

    public PressureForce2D(FluidParticle2D particle, Grid2D grid2D, double p0, double h) {
        this.particle = particle;
        this.grid2D = grid2D;
        this.p0 = p0;
        this.h = h;
    }

    @Override
    public void apply() {
        double[] forces = new double[]{0,0};
        SpikyKernel kernel = new SpikyKernel(particle, h);
        for (FluidParticle2D fluidParticle : grid2D.get(particle.getIndex())) {
            if (particle != fluidParticle && vecModule(vecDiff(particle.getPosition(), fluidParticle.getPosition())) <= h && fluidParticle.getDensity() > 0) {
                forces = vecAdd(forces, vecTimesScalar(kernel.applyGradient(fluidParticle),
                        fluidParticle.getMass() * (pressure(particle) + pressure(fluidParticle)) / (2 * fluidParticle.getDensity())));
            }
        } particle.setForces(vecDiff(particle.getForces(), forces));
    }

    @Override
    public void draw() {}

    private double pressure(FluidParticle2D particle) {
        return K * (particle.getDensity() - p0);
    }
}
