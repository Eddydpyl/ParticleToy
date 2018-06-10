package physics.models.particles;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;

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

    @Override
    public void draw() {
        final double h = 0.01;
        glColor3f(0.1f, 0.5f, 1.f);
        glBegin(GL_QUADS);
        glVertex2d(getPosition()[0] - h / 2.0, getPosition()[1] - h / 2.0);
        glVertex2d(getPosition()[0] + h / 2.0, getPosition()[1] - h / 2.0);
        glVertex2d(getPosition()[0] + h / 2.0, getPosition()[1] + h / 2.0);
        glVertex2d(getPosition()[0] - h / 2.0, getPosition()[1] + h / 2.0);
        glEnd();
    }
}
