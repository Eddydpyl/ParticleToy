package physics.models.forces;

import physics.models.particles.FluidParticle2D;
import physics.models.particles.Particle2D;

import static physics.LinearSolver.*;

public class GravityForce2D implements Force {

    private static final double G = 9.8;

    private Particle2D particle;

    public GravityForce2D(Particle2D particle) {
        this.particle = particle;
    }

    @Override
    public void apply() {
        double[] current = particle.getForces();
        double[] gravity = new double[]{0, - particle.getMass() * G};
        if (particle instanceof FluidParticle2D) {
            FluidParticle2D fluidParticle = (FluidParticle2D) particle;
            fluidParticle.setForces(vecAdd(current, vecTimesScalar(gravity, fluidParticle.getDensity())));
        } else particle.setForces(vecAdd(current, gravity));
    }

    @Override
    public void draw() {}

}
