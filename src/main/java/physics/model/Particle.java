package physics.model;

import static physics.LinearSolver.*;

public interface Particle {

    static void updateVelocity(Particle particle, double time) {
        double[] velocity = particle.getVelocity();
        particle.setVelocity(vecAdd(velocity, vecTimesScalar(particle.getForces(), time / particle.getMass())));
    }

    static void updatePosition(Particle particle, double time) {
        double[] position = particle.getPosition();
        particle.setPosition(vecAdd(position, vecTimesScalar(particle.getVelocity(), time)));
    }

    double[] getConstructPos();
    double[] getPosition();
    void setPosition(double[] position);
    double[] getVelocity();
    void setVelocity(double[] velocity);
    double[] getForces();
    void setForces(double[] forces);
    double getMass();
    void setMass(double mass);

    void clearForces();
    void reset();
    void draw();

}
