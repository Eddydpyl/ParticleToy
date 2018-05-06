import static org.lwjgl.opengl.GL11.*;

public class RodConstraint {

    private Particle p1;
    private Particle p2;
    private double distance;

    public RodConstraint(Particle p1, Particle p2, double distance) {
        this.p1 = p1;
        this.p2 = p2;
        this.distance = distance;
    }

    public void draw() {
        glBegin(GL_LINES);
        glColor3d(0.8, 0.7, 0.6);
        glVertex2d(p1.getPosition().x, p1.getPosition().y);
        glColor3d(0.8, 0.7, 0.6);
        glVertex2d(p2.getPosition().x, p2.getPosition().y);
        glEnd();
    }

    public Particle getP1() {
        return p1;
    }

    public void setP1(Particle p1) {
        this.p1 = p1;
    }

    public Particle getP2() {
        return p2;
    }

    public void setP2(Particle p2) {
        this.p2 = p2;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
