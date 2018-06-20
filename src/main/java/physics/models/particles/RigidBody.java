package physics.models.particles;

import org.ejml.simple.SimpleMatrix;

public interface RigidBody {

    double[] getPosition();
    void setPosition(double[] position);
    double[] getVelocity();
    void setVelocity(double[] velocity);
    double[] getForce();
    void setForce(double[] force);
    double getMass();

    double[] getTorque();
    void setTorque(double[] torque);
    double[] getInertia();
    void setInertia(double[] inertia);
    double getOrientation();
    void setOrientation(double orientation);
    double getAngular();
    void setAngular(double angular);

    void updateState(double time);
    void updateTorque();
    void updateInertia();
    void updateVelocity(double time);
    void updatePosition(double time);
    void updateAngular(double time);
    void updateOrientation(double time);
    void updatePoints();

    boolean isActive();
    void setActive(boolean active);
    boolean isDraw();
    void setDraw(boolean draw);

    SimpleMatrix rotation(double radians);

    boolean containsPoint(double[] point); // Returns whether the point is inside the body;
    double[][] calculatePoints(); // Returns an array of points used to encapsulate the body, in drawing order.
    double[][] calculateClosestEdge(double[] position); // Returns the origin and destination conforming the edge.
    SimpleMatrix calculateBodyInertia(double[] params);
}
