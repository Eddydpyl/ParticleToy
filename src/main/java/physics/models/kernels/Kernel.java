package physics.models.kernels;

import physics.models.particles.Particle;

public abstract class Kernel {

    Particle pi;
    double h;

    public Kernel(Particle pi, double h) {
        this.pi = pi;
        this.h = h;
    }

    public abstract double applyFunction(Particle pj);

    public abstract double[] applyGradient(Particle pj);

    public abstract double applyLaplacian(Particle pj);

}
