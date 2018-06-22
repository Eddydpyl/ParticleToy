package physics.models;

import physics.models.kernels.Poly6Kernel;
import physics.models.particles.FluidParticle2D;
import physics.models.particles.Particle;

import java.util.List;
import java.util.stream.Collectors;

import static physics.LinearSolver.*;
import static org.lwjgl.opengl.GL11.*;

public class Grid2D {

    private FluidParticle2D[][][] matrix; // Of size (cells x cells)
    private double[] position; // Upper left corner of the matrix
    private int cells; // Number of cells in one side of the grid
    private double h; // Height and width of the cells

    public Grid2D(double[] position, double h) {
        this.cells = (int) (2 / h);
        this.matrix = new FluidParticle2D[cells][cells][];
        this.position = position;
        this.h = h;
    }

    public void update(List<? extends Particle> particles) {
        // Reset all cells in the matrix
        for (int i = 0; i < cells; i++) {
            for (int j = 0; j < cells; j++) {
                matrix[i][j] = new FluidParticle2D[0];
            }
        }
        // Retrieve all fluid particles inside the grid
        List<FluidParticle2D> fluidParticles= particles.stream().filter(particle -> particle instanceof FluidParticle2D)
                .filter(particle -> particle.getPosition()[0] >= position[0]
                        && particle.getPosition()[0] <= position[0] + cells * h
                        && particle.getPosition()[1] <= position[1]
                        && particle.getPosition()[1] >= position[1] - cells * h)
                .map(x -> (FluidParticle2D) x).collect(Collectors.toList());
        // Locate fluid particles inside the grid
        for (FluidParticle2D fluidParticle : fluidParticles) {
            int x = (int) (Math.abs(fluidParticle.getPosition()[0] - position[0]) / h);
            int y = (int) (Math.abs(fluidParticle.getPosition()[1] - position[1]) / h);
            FluidParticle2D[] oldCell = matrix[x][y];
            if (oldCell.length > 0) {
                FluidParticle2D[] newCell = new FluidParticle2D[oldCell.length + 1];
                System.arraycopy(oldCell, 0, newCell, 0, oldCell.length);
                newCell[oldCell.length] = fluidParticle;
                matrix[x][y] = newCell;
            } else {
                FluidParticle2D[] newCell = new FluidParticle2D[]{fluidParticle};
                matrix[x][y] = newCell;
            } fluidParticle.setIndex(new int[]{x,y});
        }
        //  Calculate the density of fluid particles inside the grid
        for (FluidParticle2D fluidParticle : fluidParticles) {
            double density = 0.0;
            Poly6Kernel kernel = new Poly6Kernel(fluidParticle, h);
            for (FluidParticle2D particle : get(fluidParticle.getIndex())) {
                if (fluidParticle != particle && vecModule(vecDiff(fluidParticle.getPosition(), particle.getPosition())) <= h)
                    density += particle.getMass() * kernel.applyFunction(particle);
            } fluidParticle.setDensity(density);
        }
    }

    public FluidParticle2D[] get(int[] index) {
        if (index != null) {
            FluidParticle2D[] upper = index[1] - 1 >= 0 && matrix[index[0]][index[1] - 1] != null ?
                    matrix[index[0]][index[1] - 1]: new FluidParticle2D[0];
            FluidParticle2D[] lower = index[1] + 1 < cells && matrix[index[0]][index[1] + 1] != null ?
                    matrix[index[0]][index[1] + 1]: new FluidParticle2D[0];
            FluidParticle2D[] left = index[0] - 1 >= 0 && matrix[index[0] - 1][index[1]] != null ?
                    matrix[index[0] - 1][index[1]]: new FluidParticle2D[0];
            FluidParticle2D[] right = index[0] + 1 < cells && matrix[index[0] + 1][index[1]] != null ?
                    matrix[index[0] + 1][index[1]]: new FluidParticle2D[0];
            FluidParticle2D[] upperLeft = index[0] - 1 >= 0 && index[1] - 1 >= 0 && matrix[index[0] - 1][index[1] - 1] != null ?
                    matrix[index[0] - 1][index[1] - 1]: new FluidParticle2D[0];
            FluidParticle2D[] upperRight = index[0] + 1 < cells  && index[1] - 1 >= 0 && matrix[index[0] + 1][index[1] - 1] != null ?
                    matrix[index[0] + 1][index[1] - 1]: new FluidParticle2D[0];
            FluidParticle2D[] lowerLeft = index[0] - 1 >= 0 && index[1] - 1 >= 0 && matrix[index[0] - 1][index[1] - 1] != null ?
                    matrix[index[0] - 1][index[1] - 1]: new FluidParticle2D[0];
            FluidParticle2D[] lowerRight = index[0] - 1 >= 0 && index[1] + 1 < cells && matrix[index[0] - 1][index[1] + 1] != null ?
                    matrix[index[0] - 1][index[1] + 1]: new FluidParticle2D[0];
            FluidParticle2D[] aux = new FluidParticle2D[upper.length + lower.length + left.length + right.length + upperLeft.length + upperRight.length +
                    lowerLeft.length + lowerRight.length + matrix[index[0]][index[1]].length];
            int length = 0;
            System.arraycopy(upper, 0, aux, length, upper.length); length += upper.length;
            System.arraycopy(lower, 0, aux, length, lower.length); length += lower.length;
            System.arraycopy(left, 0, aux, length, left.length); length += left.length;
            System.arraycopy(right, 0, aux, length, right.length); length += right.length;
            System.arraycopy(upperLeft, 0, aux, length, upperLeft.length); length += upperLeft.length;
            System.arraycopy(upperRight, 0, aux, length, upperRight.length); length += upperRight.length;
            System.arraycopy(lowerLeft, 0, aux, length, lowerLeft.length); length += lowerLeft.length;
            System.arraycopy(lowerRight, 0, aux, length, lowerRight.length); length += lowerRight.length;
            System.arraycopy(matrix[index[0]][index[1]], 0, aux, length, matrix[index[0]][index[1]].length);
            return aux;
        } else return new FluidParticle2D[0];
    }

    public void draw() {
        for (int i = 0; i < cells; i++) {
            glBegin(GL_LINES);
            glColor3d(0.6, 0.7, 0.8);
            glVertex2d(position[0], position[1] - i * h);
            glColor3d(0.6, 0.7, 0.8);
            glVertex2d(position[0] + cells * h, position[1] - i * h);
            glEnd();
        }
        for (int i = 0; i < cells; i++) {
            glBegin(GL_LINES);
            glColor3d(0.6, 0.7, 0.8);
            glVertex2d(position[0] + i * h, position[1]);
            glColor3d(0.6, 0.7, 0.8);
            glVertex2d(position[0] + i * h, position[1] - cells * h);
            glEnd();
        }
    }
}
