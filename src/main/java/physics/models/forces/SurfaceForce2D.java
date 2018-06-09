package physics.models.forces;

import physics.models.Grid2D;
import physics.models.kernels.Poly6Kernel;
import physics.models.particles.FluidParticle2D;
import physics.models.particles.Particle2D;

import java.util.List;

import static physics.LinearSolver.*;

public class SurfaceForce2D implements Force{

    private List<Particle2D> particles;
    private Grid2D grid2D;
    private double sigma;
    private double h;

    public SurfaceForce2D(List<Particle2D> particles, Grid2D grid2D, double sigma, double h) {
        this.particles = particles;
        this.grid2D = grid2D;
        this.sigma = sigma;
        this.h = h;
    }

    @Override
    public void apply() {
        particles.stream().filter(particle -> particle instanceof FluidParticle2D)
                .forEach(x -> {
                    FluidParticle2D particle = (FluidParticle2D) x;
                    double[] current = particle.getForces();
                    Poly6Kernel kernel = new Poly6Kernel(particle, h);
                    double[] gradient = new double[]{0,0};
                    double laplacian = 0.0;
                    for (FluidParticle2D fluidParticle : grid2D.get(particle.getIndex())) {
                        if (particle != fluidParticle && vecModule(vecDiff(particle.getPosition(), fluidParticle.getPosition())) <= h) {
                            gradient = vecAdd(gradient, vecTimesScalar(kernel.applyGradient(fluidParticle), fluidParticle.getMass() / fluidParticle.getDensity()));
                            laplacian = laplacian + (fluidParticle.getMass() / fluidParticle.getDensity()) * kernel.applyLaplacian(fluidParticle);
                        }
                    } particle.setForces(vecDiff(current, vecTimesScalar(gradient, sigma * laplacian * vecModule(gradient))));
                });
    }

    @Override
    public void draw() {}
}
