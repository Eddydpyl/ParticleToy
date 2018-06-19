package physics.models.particles;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glVertex3d;

public class Particle3D implements Particle {

    private double[] constructPos;
    private double[] position;
    private double[] velocity;
    private double[] forces;
    private double mass;

    public Particle3D(double[] constructPos, double mass) {
        checkPosition(constructPos);
        this.constructPos = constructPos;
        this.position = constructPos;
        this.velocity = new double[]{0,0,0};
        this.forces = new double[]{0,0,0};
        this.mass = mass;
    }

    private void checkPosition(double[] position) {
        if (position.length != 3) throw new IllegalArgumentException();
    }

    @Override
    public void reset() {
        this.position = constructPos;
        this.velocity = new double[]{0,0,0};
        clearForces();
    }

    @Override
    public void clearForces() {
        this.forces = new double[]{0,0,0};
    }

    @Override
    public void draw() {
        final double h = 0.03;
        glColor3f(1.f, 1.f, 1.f);
        glBegin(GL_QUADS);
        glVertex3d(position[0] - h / 2.0, position[1] - h / 2.0,position[2]-h/2.0);
        glVertex3d(position[0] + h / 2.0, position[1] - h / 2.0,position[2]-h/2.0);
        glVertex3d(position[0] + h / 2.0, position[1] + h / 2.0,position[2]-h/2.0);
        glVertex3d(position[0] - h / 2.0, position[1] + h / 2.0,position[2]-h/2.0);
        
        glVertex3d(position[0] - h / 2.0, position[1] - h / 2.0,position[2]+h/2.0);
        glVertex3d(position[0] + h / 2.0, position[1] - h / 2.0,position[2]+h/2.0);
        glVertex3d(position[0] + h / 2.0, position[1] + h / 2.0,position[2]+h/2.0);
        glVertex3d(position[0] - h / 2.0, position[1] + h / 2.0,position[2]+h/2.0);
        
        glVertex3d(position[0] - h / 2.0, position[1] + h / 2.0,position[2]-h/2.0);
        glVertex3d(position[0] + h / 2.0, position[1] + h / 2.0,position[2]-h/2.0);
        glVertex3d(position[0] + h / 2.0, position[1] + h / 2.0,position[2]+h/2.0);
        glVertex3d(position[0] - h / 2.0, position[1] + h / 2.0,position[2]+h/2.0);
        
        glVertex3d(position[0] - h / 2.0, position[1] - h / 2.0,position[2]-h/2.0);
        glVertex3d(position[0] + h / 2.0, position[1] - h / 2.0,position[2]-h/2.0);
        glVertex3d(position[0] + h / 2.0, position[1] - h / 2.0,position[2]+h/2.0);
        glVertex3d(position[0] - h / 2.0, position[1] - h / 2.0,position[2]+h/2.0);
        
        glVertex3d(position[0] + h / 2.0, position[1] - h / 2.0,position[2]-h/2.0);
        glVertex3d(position[0] + h / 2.0, position[1] - h / 2.0,position[2]+h/2.0);
        glVertex3d(position[0] + h / 2.0, position[1] + h / 2.0,position[2]+h/2.0);
        glVertex3d(position[0] + h / 2.0, position[1] + h / 2.0,position[2]-h/2.0);
        
        glVertex3d(position[0] - h / 2.0, position[1] - h / 2.0,position[2]-h/2.0);
        glVertex3d(position[0] - h / 2.0, position[1] - h / 2.0,position[2]+h/2.0);
        glVertex3d(position[0] - h / 2.0, position[1] + h / 2.0,position[2]+h/2.0);
        glVertex3d(position[0] - h / 2.0, position[1] + h / 2.0,position[2]-h/2.0);
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

    @Override
    public String toString() {
        return "Particle2D{" +
                "constructPos=" + Arrays.toString(constructPos) +
                ", position=" + Arrays.toString(position) +
                ", velocity=" + Arrays.toString(velocity) +
                ", forces=" + Arrays.toString(forces) +
                ", mass=" + mass +
                '}';
    }
}
