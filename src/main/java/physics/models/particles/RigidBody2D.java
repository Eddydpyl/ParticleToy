package physics.models.particles;

import org.ejml.simple.SimpleMatrix;

import static physics.LinearSolver.*;

public abstract class RigidBody2D extends Particle2D implements RigidBody{

    SimpleMatrix bInertia, bInertiaInv;
    double[] torque, inertia;
    double orientation, angular; // Expressed in radians.
    double[][] points, spins;

    private boolean rotate; // Whether the object should rotate.

    public RigidBody2D(double[] constructPos, double mass, double[] params) {
        super(constructPos, mass);
        this.orientation = 0.0;
        this.angular = 0.0;
        this.points = calculatePoints();
        this.spins = new double[points.length][2];
        this.torque = new double[2];
        this.bInertia = calculateBodyInertia(params);
        this.bInertiaInv = bInertia.invert();
        this.rotate = true;
    }

    public RigidBody2D(double[] constructPos, double mass, boolean active, boolean rotate, boolean draw, double[] params) {
        super(constructPos, mass, active, draw);
        this.orientation = 0.0;
        this.angular = 0.0;
        this.points = calculatePoints();
        this.spins = new double[points.length][2];
        this.torque = new double[2];
        this.bInertia = calculateBodyInertia(params);
        this.bInertiaInv = bInertia.invert();
        this.rotate = rotate;
    }

    public RigidBody2D(double[] constructPos, double mass) {
        super(constructPos, mass);
        this.orientation = 0.0;
        this.angular = 0.0;
        this.points = calculatePoints();
        this.spins = new double[points.length][2];
        this.torque = new double[2];
        this.bInertia = calculateBodyInertia(null);
        this.bInertiaInv = bInertia.invert();
        this.rotate = true;
    }

    public RigidBody2D(double[] constructPos, double mass,  boolean active, boolean rotate, boolean draw) {
        super(constructPos, mass, active, draw);
        this.orientation = 0.0;
        this.angular = 0.0;
        this.points = calculatePoints();
        this.spins = new double[points.length][2];
        this.torque = new double[2];
        this.bInertia = calculateBodyInertia(null);
        this.bInertiaInv = bInertia.invert();
        this.rotate = rotate;
    }

    public double[] getTorque() {
        return torque;
    }

    public void setTorque(double[] torque) {
        this.torque = torque;
    }

    public double[] getInertia() {
        return inertia;
    }

    public void setInertia(double[] inertia) {
        this.inertia = inertia;
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

    public double getAngular() {
        return angular;
    }

    public void setAngular(double angular) {
        this.angular = angular;
    }

    @Override
    public void updateState(double time) {
        updateTorque();
        updateInertia();
        updateVelocity(time);
        updatePosition(time);
        updateAngular(time);
        updateOrientation(time);
        updatePoints();
    }

    public void updateTorque() {
        if (active) {
            torque = new double[2];
            for (int i = 0; i < points.length; i++) {
                double[] point = points[i];
                double[] spin = spins[i];
                double[] r = vecDiff(point, position);
                double w = vecCross(r, vecAdd(velocity, spin))[0];
                torque = vecAdd(torque, vecCross(r, w));
                spins[i] = vecCross(angular, r);
            }
        }
    }

    public void updateInertia() {
        SimpleMatrix rotation = rotation(orientation);
        SimpleMatrix matrix = rotation.mult(bInertia).mult(rotation.transpose());
        inertia = new double[]{matrix.get(0), matrix.get(3)};
    }

    @Override
    public void updateVelocity(double time) {
        if (active) velocity = vecAdd(velocity, vecTimesScalar(force, time / getMass()));
    }

    @Override
    public void updatePosition(double time) {
        if (active) position = vecAdd(position, vecTimesScalar(velocity, time));
    }

    public void updateAngular(double time) {
        if (active && rotate) angular += vecCross(torque, inertia)[0] * time;
    }

    public void updateOrientation(double time) {
        if (active && rotate) {
            double aux = orientation + angular * time;
            orientation = Math.atan2(Math.sin(aux), Math.cos(aux));
        }
    }

    public void updatePoints() {
        points = calculatePoints();
    }

    @Override
    public void clearForce() {
        this.force = new double[2];
    }

    @Override
    public void reset() {
        position = constructPos;
        velocity = new double[2];
        force = new double[2];
        orientation = 0.0;
        angular = 0.0;
        torque = new double[2];
        points = calculatePoints();
        spins = new double[points.length][2];
    }

    public SimpleMatrix rotation(double radians) {
        SimpleMatrix matrix = new SimpleMatrix(2,2);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        matrix.set(0, 0, + cos);
        matrix.set(0, 1, - sin);
        matrix.set(1, 0, + sin);
        matrix.set(1, 1, + cos);
        return matrix;
    }

    public abstract boolean containsPoint(double[] point);
    public abstract double[][] calculatePoints();
    public abstract double[][] calculateClosestEdge(double[] position);
    public abstract SimpleMatrix calculateBodyInertia(double[] params);
}
