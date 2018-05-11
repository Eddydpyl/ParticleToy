package physics.model;

import com.sun.javafx.geom.Vec2d;

import static org.lwjgl.opengl.GL11.*;

public class Particle {

    private Vec2d constructPos;
    private Vec2d position;
    private Vec2d velocity;
    private Vec2d forces;
    private double mass;

    public Particle(Vec2d constructPos, double mass) {
        this.constructPos = constructPos;
        this.position = new Vec2d(0.0, 0.0);
        this.velocity = new Vec2d(0.0, 0.0);
        this.forces = new Vec2d(0.0, 0.0);
        this.mass = mass;
    }

    public Particle(Vec2d constructPos, Vec2d position, Vec2d velocity, Vec2d forces, double mass) {
        this.constructPos = constructPos;
        this.position = position;
        this.velocity = velocity;
        this.forces = forces;
        this.mass = mass;
    }

    public void reset() {
        this.position = constructPos;
        this.velocity = new Vec2d(0.0, 0.0);
        this.forces = new Vec2d(0.0, 0.0);
    }

    public void draw() {
        final double h = 0.03;
        glColor3f(1.f, 1.f, 1.f);
        glBegin(GL_QUADS);
        glVertex2d(position.x - h / 2.0, position.y - h / 2.0);
        glVertex2d(position.x + h / 2.0, position.y - h / 2.0);
        glVertex2d(position.x + h / 2.0, position.y + h / 2.0);
        glVertex2d(position.x - h / 2.0, position.y + h / 2.0);
        glEnd();
    }

    public Vec2d getConstructPos() {
        return constructPos;
    }

    public void setConstructPos(Vec2d constructPos) {
        this.constructPos = constructPos;
    }

    public Vec2d getPosition() {
        return position;
    }

    public void setPosition(Vec2d position) {
        this.position = position;
    }

    public Vec2d getVelocity() {
        return velocity;
    }

    public void setVelocity(Vec2d velocity) {
        this.velocity = velocity;
    }

    public Vec2d getForces() {
        return forces;
    }

    public void setForces(Vec2d forces) {
        this.forces = forces;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

}
