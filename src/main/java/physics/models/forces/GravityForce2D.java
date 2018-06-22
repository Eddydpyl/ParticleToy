package physics.models.forces;

import physics.models.particles.FluidParticle2D;
import physics.models.particles.Particle2D;

import static physics.LinearSolver.*;

public class GravityForce2D implements Force {

    private static final double G = 9.8;

    private Particle2D particle;
    private double[] direction;

    public GravityForce2D(Particle2D particle) {
        this.particle = particle;
        direction = new double[]{0, -1};
    }

    public GravityForce2D(Particle2D particle, double[] direction) {
        this.particle = particle;
        this.direction = direction;
    }

    @Override
    public void apply() {
        double[] current = particle.getForce();
        double[] gravity = vecTimesScalar(direction, particle.getMass() * G);
        if (particle instanceof FluidParticle2D) {
            FluidParticle2D fluidParticle = (FluidParticle2D) particle;
            fluidParticle.setForce(vecAdd(current, vecTimesScalar(gravity, fluidParticle.getDensity())));
        } else particle.setForce(vecAdd(current, gravity));
    }

    @Override
    public void draw() {}

}
