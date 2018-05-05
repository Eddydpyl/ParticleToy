import static org.lwjgl.opengl.GL11.*;

public class SpringForce {

    private Particle p1;
    private Particle p2;
    private double distance;
    private double ks, kd;

    public SpringForce(Particle p1, Particle p2, double distance, double ks, double kd) {
        this.p1 = p1;
        this.p2 = p2;
        this.distance = distance;
        this.ks = ks;
        this.kd = kd;
    }

    public void draw() {
        glBegin( GL_LINES );
        glColor3d(0.6, 0.7, 0.8);
        glVertex2d(p1.getPosition().x, p1.getPosition().y);
        glColor3d(0.6, 0.7, 0.8);
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

    public double getKs() {
        return ks;
    }

    public void setKs(double ks) {
        this.ks = ks;
    }

    public double getKd() {
        return kd;
    }

    public void setKd(double kd) {
        this.kd = kd;
    }
}
