package physics.model;

public interface Particle {

    double[] getConstructPos();
    void setConstructPos(double[] constructPos);
    double[] getPosition();
    void setPosition(double[] position);
    double[] getVelocity();
    void setVelocity(double[] velocity);
    double[] getForces();
    void setForces(double[] forces);
    double getMass();
    void setMass(double mass);
    void reset();
    void draw();

}
