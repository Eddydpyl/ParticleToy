package physics.models.particles;

public class FluidParticle2D extends Particle2D {

    private double density;
    private int[] index; // Position inside a Grid2D

    public FluidParticle2D(double[] constructPos, double mass) {
        super(constructPos, mass);
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public int[] getIndex() {
        return index;
    }

    public void setIndex(int[] index) {
        this.index = index;
    }
}
