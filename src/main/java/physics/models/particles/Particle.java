package physics.models.particles;

import static physics.LinearSolver.*;

public abstract class Particle {

    double[] constructPos;
    double[] position;
    double[] velocity;
    double[] force;
    double mass;

    boolean active;
    boolean draw;

    public Particle(double[] constructPos, double[] position, double[] velocity, double[] force, double mass) {
        this.constructPos = constructPos;
        this.position = position;
        this.velocity = velocity;
        this.force = force;
        this.mass = mass;
        this.active = true;
        this.draw = true;
    }

    public Particle(double[] constructPos, double[] position, double[] velocity, double[] force, double mass, boolean active, boolean draw) {
        this.constructPos = constructPos;
        this.position = position;
        this.velocity = velocity;
        this.force = force;
        this.mass = mass;
        this.active = active;
        this.draw = draw;
    }

    public void updateState(double time) {
        updateVelocity(time);
        updatePosition(time);
    }

    public void updateVelocity(double time) {
        if (active) {
            double[] velocity = getVelocity();
            setVelocity(vecAdd(velocity, vecTimesScalar(getForce(), time / getMass())));
        }
    }

    public void updatePosition(double time) {
        if (active) {
            double[] position = getPosition();
            setPosition(vecAdd(position, vecTimesScalar(getVelocity(), time)));
        }
    }

    public double[] getConstructPos() {
        return constructPos;
    }

    public void setConstructPos(double[] constructPos) {
        this.constructPos = constructPos;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    public double[] getForce() {
        return force;
    }

    public void setForce(double[] force) {
        this.force = force;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public abstract void clearForce();
    public abstract void reset();
    public abstract void draw();

}
