package physics.models;

import physics.models.kernels.Poly6Kernel;
import physics.models.particles.FluidParticle2D;
import physics.models.particles.Particle2D;

import java.util.Arrays;

public class Grid2D {

    private FluidParticle2D[][] matrix; // Of size (cells x cells)
    private double[] position; // Upper left corner of the matrix
    private int cells; // Number of cells in one side of the grid
    private double h; // Height and width of the cells

    public Grid2D(double[] position, int cells, double h) {
        this.matrix = new FluidParticle2D[cells * cells][];
        this.position = position;
        this.cells = cells;
        this.h = h;
    }

    public void update(Particle2D[] particles) {
        // Locate fluid particles inside the matrix
        Arrays.stream(particles).filter(particle -> particle instanceof FluidParticle2D)
                .filter(particle -> particle.getPosition()[0] >= position[0]
                        && particle.getPosition()[0] <= position[0] + cells * h
                        && particle.getPosition()[1] <= position[1]
                        && particle.getPosition()[1] >= position[1] - cells * h)
                .forEach(particle -> {
                    FluidParticle2D fluidParticle = (FluidParticle2D) particle;
                    double x = Math.abs(fluidParticle.getPosition()[0] - position[0]);
                    double y = Math.abs(fluidParticle.getPosition()[1] - position[1]);
                    int index = (int) (x / h) + cells * (int) (y / h);
                    FluidParticle2D[] oldCell = matrix[index];
                    if (oldCell != null) {
                        FluidParticle2D[] newCell = new FluidParticle2D[oldCell.length + 1];
                        System.arraycopy(oldCell, 0, newCell, 0, oldCell.length);
                        newCell[oldCell.length] = fluidParticle;
                        matrix[index] = newCell;
                    } else {
                        FluidParticle2D[] newCell = new FluidParticle2D[]{fluidParticle};
                        matrix[index] = newCell;
                    } fluidParticle.setIndex(index);
                });
        //  Calculate the density of fluid particles inside the grid
        Arrays.stream(particles).filter(particle -> particle instanceof FluidParticle2D)
                .forEach(x -> {
                    double density = 0.0;
                    FluidParticle2D particle = (FluidParticle2D) x;
                    Poly6Kernel kernel = new Poly6Kernel(particle, h);
                    for (FluidParticle2D fluidParticle : get(particle.getIndex())) {
                        density += fluidParticle.getMass() * kernel.applyFunction(fluidParticle);
                    } particle.setDensity(density);
                });
    }

    public FluidParticle2D[] get(int index) {
        return matrix[index];
    }
}
