package physics.models.particles;

import static org.lwjgl.opengl.GL11.*;

public class Particle2D extends Particle {

    public Particle2D(double[] constructPos, double mass) {
        super(constructPos, constructPos, new double[]{0,0}, new double[]{0,0}, mass);
        checkPosition(constructPos);
    }

    public Particle2D(double[] constructPos, double mass, boolean active, boolean draw) {
        super(constructPos, constructPos, new double[]{0,0}, new double[]{0,0}, mass, active, draw);
        checkPosition(constructPos);
    }

    void checkPosition(double[] position) {
        if (position.length != 2) throw new IllegalArgumentException("Invalid number of dimensions.");
    }

    @Override
    public void reset() {
        this.position = constructPos;
        this.velocity = new double[]{0,0};
        clearForce();
    }

    @Override
    public void clearForce() {
        this.force = new double[2];
    }

    @Override
    public void draw() {
        if (draw) {
            final double h = 0.03;
            glColor3f(1.f, 1.f, 1.f);
            glBegin(GL_QUADS);
            glVertex2d(position[0] - h / 2.0, position[1] - h / 2.0);
            glVertex2d(position[0] + h / 2.0, position[1] - h / 2.0);
            glVertex2d(position[0] + h / 2.0, position[1] + h / 2.0);
            glVertex2d(position[0] - h / 2.0, position[1] + h / 2.0);
            glEnd();
        }
    }
}
