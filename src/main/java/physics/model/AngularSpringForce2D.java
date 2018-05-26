package physics.model;

import java.util.Vector;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static physics.LinearSolver.*;

public class AngularSpringForce2D implements Force {

	protected double ks; // Spring constant
    protected double kd; // Dampening constant
    protected double thetaRad; // Spring rest angle, in ..
    private  Vector<Particle> pVector = new Vector<Particle>();


    public AngularSpringForce2D(Particle p0, Particle p1, Particle p2, double ks, double kd, double theta){
       
    	pVector.add(p0);
    	pVector.add(p1);
    	pVector.add(p2);
        this.ks = ks;
        this.kd = kd;
        this.thetaRad = Math.toRadians(theta);
    }


    @Override
	public
    void apply(){
        double[] components1 = getComponents(pVector.get(0), pVector.get(1), pVector.get(2), thetaRad);
        double[] components2 = getComponents(pVector.get(0), pVector.get(2), pVector.get(1), -thetaRad);
        double[] dCdx1 = new double[]{components1[0], components1[1]};
        double[] dCdx2 = new double[]{components2[0], components2[1]};
        double scalar1 = -ks*components1[3] - kd*components1[2];
        double scalar2 = -ks*components2[3] - kd*components2[2];
        double[] vec1 = vecTimesScalar(dCdx1, scalar1);
        double[] vec2 = vecTimesScalar(dCdx2, scalar2);
        double[] vec0 = vecAdd(vec1, vec2);
        pVector.get(1).setForces(vecAdd(pVector.get(1).getForces(), vec1));
        pVector.get(2).setForces(vecAdd(pVector.get(2).getForces(), vec2)); 
        pVector.get(0).setForces(vecDiff(pVector.get(0).getForces(), vec0)); 
    }

    // Returns position derivative, followed by time derivative of C and C itself, total size 4
    double[] getComponents(Particle pivot, Particle target, Particle source, double angle){
        double[] result = new double[4];
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double eps = Double.MIN_VALUE;

        double[] p1 = vecDiff(target.getPosition(), pivot.getPosition());
        double[] p2 = new double[]{cos*(source.getPosition()[0]-pivot.getPosition()[0]) - sin*(source.getPosition()[1]-pivot.getPosition()[1]),
                sin*(source.getPosition()[0]-pivot.getPosition()[0]) + cos*(source.getPosition()[1]-pivot.getPosition()[1])};
        double dp1xdt = target.getVelocity()[0] - pivot.getVelocity()[0];
        double dp1ydt = target.getVelocity()[1] - pivot.getVelocity()[1];
        double dp2xdt = cos*(source.getVelocity()[0]-pivot.getVelocity()[0]) - sin*(source.getVelocity()[1]-pivot.getVelocity()[1]);
        double dp2ydt = sin*(source.getVelocity()[0]-pivot.getVelocity()[0]) + cos*(source.getVelocity()[1]-pivot.getVelocity()[1]);
        /*double dp2xdx2 = cos; //Irrelevant, never derived for source, only target
        double dp2ydx2 = sin;
        double dp2xdy2 = -sin;
        double dp2ydy2 = cos;*/
        //double dRight = 1; // Position derivative with relevance in p1
        //double dWrong = 0; //Position derivative without relevance

        double p1Dotp2 = vecDot(p1, p2);
        double p1l = Math.sqrt(vecDot(p1,p1));
        double p2l = Math.sqrt(vecDot(p2,p2));
        double p1lProdp2l = p1l*p2l;
        double dotDivLen = p1Dotp2/(p1lProdp2l + eps);

        result[3] = Math.acos(dotDivLen); // Value of C

        double derivDenomInv = 1/-(p1lProdp2l*p1lProdp2l*Math.sqrt(1 - 0.8*dotDivLen*dotDivLen) + eps); //0.9 for softening

        double dotDenomInv = 1/(p1Dotp2 + eps);

        double p1lDt = (p1[0]*dp1xdt + p1[1]*dp1ydt)*dotDenomInv;
        double p1lDx = p1[0]*dotDenomInv;
        double p1lDy = p1[1]*dotDenomInv;

        double p2lDt = (p2[0]*dp2xdt + p2[1]*dp2ydt)*dotDenomInv;
        double p2lDx = 0;
        double p2lDy = 0;

        double lenProdDt = p2l*p1lDt + p1l*p2lDt;
        double lenProdDx = p2l*p1lDx + p1l*p2lDx;
        double lenProdDy = p2l*p1lDy + p1l*p2lDy;

        double dotDt = dp1xdt*p2[0] + p1[0]*dp2xdt + dp1ydt*p2[1] + p1[1]*dp2ydt;
        double dotDx = p2[0];
        double dotDy = p2[1];

        double numert = dotDt*p1lProdp2l + lenProdDt*p1Dotp2;
        double numerx = dotDx*p1lProdp2l + lenProdDx*p1Dotp2;
        double numery = dotDy*p1lProdp2l + lenProdDy*p1Dotp2;


        result[2] = numert*derivDenomInv; // Value of Cdt
        result[1] = numery*derivDenomInv; // Value of Cdy
        result[0] = numerx*derivDenomInv; // Value of Cdx

        return result;
    }


	@Override
	public void draw() {
		   glBegin(GL_LINES);
	        glColor3d(0.6, 0.7, 0.8);
	        glVertex2d(pVector.get(0).getPosition()[0], pVector.get(0).getPosition()[1]);
	        glColor3d(0.6, 0.7, 0.8);
	        glVertex2d(pVector.get(1).getPosition()[0], pVector.get(1).getPosition()[1]);
	        glColor3d(0.6, 0.7, 0.8);
	        glVertex2d(pVector.get(2).getPosition()[0], pVector.get(2).getPosition()[1]);
	        glEnd();		
	}


}
