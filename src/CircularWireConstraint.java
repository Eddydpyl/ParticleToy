import com.sun.javafx.geom.Vec2d;
import static org.lwjgl.opengl.GL11.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class CircularWireConstraint {

    private Particle p1;
    private Vec2d center;
    private double radius;

    public CircularWireConstraint(Particle p1, Vec2d center, double radius) {
        this.p1 = p1;
        this.center = center;
        this.radius = radius;
    }

    public static void drawCircle(Vec2d vect, double radius) {
        glBegin(GL_LINE_LOOP);
        glColor3d(0.0,1.0,0.0);
        for (int i = 0; i < 360; i += 18) {
            double degInRad = (i*Math.PI/180);
            glVertex2d(vect.x + cos(degInRad)*radius,vect.y + sin(degInRad)*radius);
        } glEnd();
    }

    public void draw() {
        drawCircle(center, radius);
    }

    public Particle getP1() {
        return p1;
    }

    public void setP1(Particle p1) {
        this.p1 = p1;
    }

    public Vec2d getCenter() {
        return center;
    }

    public void setCenter(Vec2d center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
