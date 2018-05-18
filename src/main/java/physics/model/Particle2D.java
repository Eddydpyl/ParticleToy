package physics.model;

import static org.lwjgl.opengl.GL11.*;

public class Particle2D implements Particle {

    private double[] constructPos;
    private double[] position;
    private double[] velocity;
    private double[] forces;
    private double mass;

    public Particle2D(double[] constructPos, double mass) {
        checkPosition(constructPos);
        this.constructPos = constructPos;
        this.position = constructPos;
        this.velocity = new double[]{0,0};
        this.forces = new double[]{0,0};
        this.mass = mass;
    }

    public Particle2D(double[] constructPos, double[] position, double[] velocity, double[] forces, double mass) {
        checkPosition(constructPos);
        checkPosition(position);
        this.constructPos = constructPos;
        this.position = position;
        this.velocity = velocity;
        this.forces = forces;
        this.mass = mass;
    }

    private void checkPosition(double[] position) {
        if (position.length != 2) throw new IllegalArgumentException();
    }

    @Override
    public void reset() {
        this.position = constructPos;
        this.velocity = new double[]{0,0};
        clearForces();
    }

    @Override
    public void clearForces() {
        this.forces = new double[]{0,0};
    }

    @Override
    public void draw() {
        final double h = 0.03;
        glColor3f(1.f, 1.f, 1.f);
        glBegin(GL_QUADS);
        glVertex2d(position[0] - h / 2.0, position[1] - h / 2.0);
        glVertex2d(position[0] + h / 2.0, position[1] - h / 2.0);
        glVertex2d(position[0] + h / 2.0, position[1] + h / 2.0);
        glVertex2d(position[0] - h / 2.0, position[1] + h / 2.0);
        glEnd();
    }

    public double[] getConstructPos() {
        return constructPos;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        checkPosition(position);
        this.position = position;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    public double[] getForces() {
        return forces;
    }

    public void setForces(double[] forces) {
        this.forces = forces;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }



}
