package physics.models.forces;

import physics.models.Grid2D;
import physics.models.kernels.ViscosityKernel;
import physics.models.particles.FluidParticle2D;

import static physics.LinearSolver.*;

public class ViscosityForce2D implements Force{

    private FluidParticle2D particle;
    private Grid2D grid2D;
    private double mu;
    private double h;

    public ViscosityForce2D(FluidParticle2D particle, Grid2D grid2D, double mu, double h) {
        this.particle = particle;
        this.grid2D = grid2D;
        this.mu = mu;
        this.h = h;
    }

    @Override
    public void apply() {
        double[] forces = new double[]{0,0};
        ViscosityKernel kernel = new ViscosityKernel(particle, h);
        for (FluidParticle2D fluidParticle : grid2D.get(particle.getIndex())) {
            if (particle != fluidParticle && vecModule(vecDiff(particle.getPosition(), fluidParticle.getPosition())) <= h && fluidParticle.getDensity() > 0) {
                forces = vecAdd(forces, vecTimesScalar(vecDiff(fluidParticle.getVelocity(), particle.getVelocity()),
                        fluidParticle.getMass() * kernel.applyLaplacian(fluidParticle) / fluidParticle.getDensity()));
            }
        } particle.setForces(vecAdd(particle.getForces(), vecTimesScalar(forces, mu)));
    }

    @Override
    public void draw() {}
}
