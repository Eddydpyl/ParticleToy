package physics.models.particles;

import org.ejml.simple.SimpleMatrix;

import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;

import static physics.LinearSolver.*;

public class Rectangle2D extends RigidBody2D {

    double width, height;
    
    public Rectangle2D(double[] constructPos, double width, double height, double mass) {
        super(constructPos, mass, new double[]{width, height});
        this.width = width;
        this.height =  height;
    }

    public Rectangle2D(double[] constructPos, double width, double height, double mass, boolean active, boolean draw) {
        super(constructPos, mass, active, draw, new double[]{width, height});
        this.width = width;
        this.height =  height;
    }

    @Override
    public boolean containsPoint(double[] point) {
        double xMax = Stream.of(points).mapToDouble(x -> x[0]).max().getAsDouble();
        double xMin = Stream.of(points).mapToDouble(x -> x[0]).min().getAsDouble();
        double yMax = Stream.of(points).mapToDouble(x -> x[1]).max().getAsDouble();
        double yMin = Stream.of(points).mapToDouble(x -> x[1]).min().getAsDouble();
        return point[0] > xMin && point[0] < xMax && point[1] > yMin && point[1] < yMax;
    }

    @Override
    public double[][] calculatePoints() {
        double sin = Math.sin(orientation);
        double cos = Math.cos(orientation);
        return new double[][] {
                new double[]{position[0] + ( width / 2 ) * cos - ( height / 2 ) * sin , position[1] + ( height / 2 ) * cos  + ( width / 2 ) * sin},
                new double[]{position[0] - ( width / 2 ) * cos - ( height / 2 ) * sin , position[1] + ( height / 2 ) * cos  - ( width / 2 ) * sin},
                new double[]{position[0] - ( width / 2 ) * cos + ( height / 2 ) * sin , position[1] - ( height / 2 ) * cos  - ( width / 2 ) * sin},
                new double[]{position[0] + ( width / 2 ) * cos + ( height / 2 ) * sin , position[1] - ( height / 2 ) * cos  + ( width / 2 ) * sin}
        };
    }

    @Override
    public double[][] calculateClosestEdge(double[] position) {
        this.checkPosition(position);
        double[][][] edges = new double[][][] {
                new double[][]{points[0], points[1]},
                new double[][]{points[1], points[2]},
                new double[][]{points[2], points[3]},
                new double[][]{points[3], points[0]}
        };
        return Stream.of(edges).sorted((e1, e2) -> {
            double d1 = vecDistance(e1[0], e1[1], position);
            double d2 = vecDistance(e2[0], e2[1], position);
            return Double.compare(d1, d2);
        }).findFirst().get();
    }

    @Override
    public SimpleMatrix calculateBodyInertia(double[] params) {
        double width = params[0];
        double height = params[1];
        return new SimpleMatrix(new double[][]{
                {width * Math.pow(height, 2) * (mass / 3), 0},
                {0, Math.pow(width, 2) * height * (mass / 3)}
        });
    }

    @Override
    public void draw() {
        if (draw) {
            glBegin(GL_QUADS);
            glColor3d(0.5, 1.0, 0.5);
            glVertex2d(points[0][0], points[0][1]);
            glVertex2d(points[1][0], points[1][1]);
            glVertex2d(points[2][0], points[2][1]);
            glVertex2d(points[3][0], points[3][1]);
            glVertex2d(points[0][0], points[0][1]);
            glEnd();
        }
    }
}
