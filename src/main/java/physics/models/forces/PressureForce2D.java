package physics.models.forces;

import physics.models.Grid2D;
import physics.models.kernels.SpikyKernel;
import physics.models.particles.FluidParticle2D;

import static physics.LinearSolver.*;

public class PressureForce2D implements Force {

    private FluidParticle2D particle;
    private Grid2D grid2D;
    private double p0;
    private double k;
    private double h;

    public PressureForce2D(FluidParticle2D particle, Grid2D grid2D, double p0, double k, double h) {
        this.particle = particle;
        this.grid2D = grid2D;
        this.p0 = p0;
        this.k = k;
        this.h = h;
    }

    @Override
    public void apply() {
        double[] current = particle.getForces();
        double[] forces = new double[]{0,0};
        SpikyKernel kernel = new SpikyKernel(particle, h);
        for (FluidParticle2D fluidParticle : grid2D.get(particle.getIndex())) {
            forces = vecAdd(forces, vecTimesScalar(kernel.applyGradient(fluidParticle),
                    fluidParticle.getMass() * (pressure(particle) + pressure(fluidParticle)) / (2 * fluidParticle.getDensity())));
        } particle.setForces(vecDiff(current, forces));
    }

    @Override
    public void draw() {}

    private double pressure(FluidParticle2D particle) {
        return k * (particle.getDensity() - p0);
    }
}
