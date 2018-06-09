package physics.models.forces;

import physics.models.Grid2D;
import physics.models.kernels.ViscosityKernel;
import physics.models.particles.FluidParticle2D;
import physics.models.particles.Particle2D;

import java.util.List;

import static physics.LinearSolver.*;

public class ViscosityForce2D implements Force{

    private List<Particle2D> particles;
    private Grid2D grid2D;
    private double mu;
    private double h;

    public ViscosityForce2D(List<Particle2D> particles, Grid2D grid2D, double mu, double h) {
        this.particles = particles;
        this.grid2D = grid2D;
        this.mu = mu;
        this.h = h;
    }

    @Override
    public void apply() {
        particles.stream().filter(particle -> particle instanceof FluidParticle2D)
                .forEach(x -> {
                    FluidParticle2D particle = (FluidParticle2D) x;
                    double[] current = particle.getForces();
                    double[] forces = new double[]{0,0};
                    ViscosityKernel kernel = new ViscosityKernel(particle, h);
                    for (FluidParticle2D fluidParticle : grid2D.get(particle.getIndex())) {
                        if (particle != fluidParticle && vecModule(vecDiff(particle.getPosition(), fluidParticle.getPosition())) <= h) {
                            forces = vecAdd(forces, vecTimesScalar(vecDiff(fluidParticle.getVelocity(), particle.getVelocity()),
                                    fluidParticle.getMass() * kernel.applyLaplacian(fluidParticle) / fluidParticle.getDensity()));
                        }
                    } particle.setForces(vecAdd(current, vecTimesScalar(forces, mu)));
                });
    }

    @Override
    public void draw() {}
}
