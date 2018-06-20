package physics.models.forces;

import physics.models.particles.FluidParticle2D;
import physics.models.particles.Particle;
import physics.models.particles.Particle2D;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static physics.LinearSolver.*;

public class AngularSpringForce2D implements Force {

    private Particle p1, p2, p3;
    private double ks, kd;
    private double rad;

    public AngularSpringForce2D(Particle2D p1, Particle2D p2, Particle2D p3, double ks, double kd, double angle) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.ks = ks;
        this.kd = kd;
        this.rad = Math.toRadians(angle);
    }

    @Override
    public void apply() {
        double[] components1 = getComponents(p1, p2, p3, rad);
        double[] components2 = getComponents(p1, p3, p2, -rad);
        double[] dCdx1 = new double[]{components1[0], components1[1]};
        double[] dCdx2 = new double[]{components2[0], components2[1]};
        double scalar1 = -ks * components1[3] - kd * components1[2];
        double scalar2 = -ks * components2[3] - kd * components2[2];
        double[] vec1 = vecTimesScalar(dCdx1, scalar1);
        double[] vec2 = vecTimesScalar(dCdx2, scalar2);
        double[] vec0 = vecAdd(vec1, vec2);

        if (p2 instanceof FluidParticle2D) {
            FluidParticle2D fluidParticle = (FluidParticle2D) p2;
            fluidParticle.setForce(vecAdd(p2.getForce(), vecTimesScalar(vec1, fluidParticle.getDensity())));
        } else p2.setForce(vecAdd(p2.getForce(), vec1));
        if (p3 instanceof FluidParticle2D) {
            FluidParticle2D fluidParticle = (FluidParticle2D) p3;
            fluidParticle.setForce(vecAdd(p3.getForce(), vecTimesScalar(vec2, fluidParticle.getDensity())));
        } else p3.setForce(vecAdd(p3.getForce(), vec2));
        if (p1 instanceof FluidParticle2D) {
            FluidParticle2D fluidParticle = (FluidParticle2D) p1;
            fluidParticle.setForce(vecDiff(p1.getForce(), vecTimesScalar(vec0, fluidParticle.getDensity())));
        } else p1.setForce(vecDiff(p1.getForce(), vec0));
    }


    double[] getComponents(Particle pivot, Particle target, Particle source, double angle) {
        double[] result = new double[4];
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double eps = Double.MIN_VALUE;

        double[] p1 = vecDiff(target.getPosition(), pivot.getPosition());
        double[] p2 = new double[]{cos * (source.getPosition()[0] - pivot.getPosition()[0]) - sin * (source.getPosition()[1] - pivot.getPosition()[1]),
                sin * (source.getPosition()[0] - pivot.getPosition()[0]) + cos * (source.getPosition()[1] - pivot.getPosition()[1])};
        double dp1xdt = target.getVelocity()[0] - pivot.getVelocity()[0];
        double dp1ydt = target.getVelocity()[1] - pivot.getVelocity()[1];
        double dp2xdt = cos * (source.getVelocity()[0] - pivot.getVelocity()[0]) - sin * (source.getVelocity()[1] - pivot.getVelocity()[1]);
        double dp2ydt = sin * (source.getVelocity()[0] - pivot.getVelocity()[0]) + cos * (source.getVelocity()[1] - pivot.getVelocity()[1]);

        double p1Dotp2 = vecDot(p1, p2);
        double p1l = Math.sqrt(vecDot(p1, p1));
        double p2l = Math.sqrt(vecDot(p2, p2));
        double p1lProdp2l = p1l * p2l;
        double dotDivLen = p1Dotp2 / (p1lProdp2l + eps);

        result[3] = Math.acos(dotDivLen);

        double derivDenomInv = 1 / -(p1lProdp2l * p1lProdp2l * Math.sqrt(1 - 0.8 * dotDivLen * dotDivLen) + eps); //0.9 for softening

        double dotDenomInv = 1 / (p1Dotp2 + eps);

        double p1lDt = (p1[0] * dp1xdt + p1[1] * dp1ydt) * dotDenomInv;
        double p1lDx = p1[0] * dotDenomInv;
        double p1lDy = p1[1] * dotDenomInv;

        double p2lDt = (p2[0] * dp2xdt + p2[1] * dp2ydt) * dotDenomInv;
        double p2lDx = 0;
        double p2lDy = 0;

        double lenProdDt = p2l * p1lDt + p1l * p2lDt;
        double lenProdDx = p2l * p1lDx + p1l * p2lDx;
        double lenProdDy = p2l * p1lDy + p1l * p2lDy;

        double dotDt = dp1xdt * p2[0] + p1[0] * dp2xdt + dp1ydt * p2[1] + p1[1] * dp2ydt;
        double dotDx = p2[0];
        double dotDy = p2[1];

        double numert = dotDt * p1lProdp2l + lenProdDt * p1Dotp2;
        double numerx = dotDx * p1lProdp2l + lenProdDx * p1Dotp2;
        double numery = dotDy * p1lProdp2l + lenProdDy * p1Dotp2;

        result[2] = numert * derivDenomInv;
        result[1] = numery * derivDenomInv;
        result[0] = numerx * derivDenomInv;

        return result;
    }


    @Override
    public void draw() {
        glBegin(GL_LINES);
        glColor3d(0.6, 0.7, 0.8);
        glVertex2d(p1.getPosition()[0], p1.getPosition()[1]);
        glColor3d(0.6, 0.7, 0.8);
        glVertex2d(p2.getPosition()[0], p2.getPosition()[1]);
        glColor3d(0.6, 0.7, 0.8);
        glVertex2d(p3.getPosition()[0], p3.getPosition()[1]);
        glEnd();
    }


}
