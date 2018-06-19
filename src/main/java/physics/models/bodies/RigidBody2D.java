package physics.models.bodies;

import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix2f;
import org.lwjgl.util.vector.Vector2f;

import physics.Vector2fWithCross;
import physics.models.particles.Particle;
import physics.models.particles.Particle2D;

public class RigidBody2D implements RigidBody {
	private List<Particle2D> particles;
	private Vector2fWithCross constructPos;
	private double mass;
	private float iBody,iBodyInv;
	
	private Vector2fWithCross position;
	private Vector2fWithCross P;
	private float L;
	private Vector2fWithCross size;
	
	private Matrix2f R;
	private float orient;
	private Vector2fWithCross v;
	
	private float omega;
	private Vector2fWithCross force;
	private float torque;
	
	public RigidBody2D(Vector2fWithCross startPos, Vector2fWithCross s, Vector2fWithCross numParticles, double particleMass) {
		constructPos = (Vector2fWithCross) startPos;
		size = (Vector2fWithCross) s;
		initializeVariables();
		double density = particleMass / size.x + 1500.f;
	    //generate particles with body coordinates
	    for (int x = 0; x < numParticles.x; x++) {
	        for (int y = 0; y < numParticles.y; y++) {
	                double xStart = -size.x / 2 + size.x * (float) x / (numParticles.x - 1);
	                double yStart = -size.y / 2 + size.y * (float) y / (numParticles.y - 1);
	                Particle2D p = new Particle2D(new double[] {xStart,yStart}, particleMass);
//	                p.setDensity(density);
	                particles.add(p);
	        }
	    }

	    //Calculate total mass
	    for (Particle p : particles) {
	        mass += p.getMass();
	    }

	    //Calculate Ibody
	    iBody= (float) ((Math.pow(size.x,2)+ Math.pow(size.y,2)) * mass/12);
	    if (iBody != 0) {
	    	iBodyInv = 1/iBody;
	    }else {
	    	iBodyInv = iBody;
	    }
	    
	}
	public void initializeVariables() {
		position = new Vector2fWithCross();
		position = constructPos;
		orient = 0;
		iBodyInv = 0;
		R = new Matrix2f();
		P = new Vector2fWithCross();
		L = 0;
	    Matrix2f.setIdentity(R);
	    v = new Vector2fWithCross(0, 0);
	    omega = 0;
	    force = new Vector2fWithCross(0, 0);
	    torque = 0;
	    particles = new ArrayList<Particle2D>();
	}
	@Override
	public void draw() {
		Vector2f v1 = Vector2f.add(Matrix2f.transform(R, new Vector2f(-size.x / 2, -size.y / 2), null),position,null);
	    Vector2f v2 = Vector2f.add(Matrix2f.transform(R, new Vector2f(-size.x / 2, size.y / 2), null),position,null);
	    Vector2f v3 = Vector2f.add(Matrix2f.transform(R, new Vector2f(size.x / 2, size.y / 2), null),position,null);
	    Vector2f v4 = Vector2f.add(Matrix2f.transform(R, new Vector2f(size.x / 2, -size.y / 2), null),position,null);
	    glBegin(GL_POLYGON);
	    glColor3d(1, 0, 0);
	    glVertex2d(v1.x, v1.y);
	    glVertex2d(v2.x, v2.y);
	    glVertex2d(v3.x, v3.y);
	    glVertex2d(v4.x, v4.y);
	    glEnd();		
	}
	
	 public Vector2f getBodyCoordinates(Vector2f world) {
		 return Matrix2f.transform((Matrix2f) R.transpose(), Vector2f.sub(world, position, null), null);
		   
	}
	
	public void updateForce() {
	    force = new Vector2fWithCross(0, 0);
		    for (Particle p : particles) {
		    	Vector2fWithCross f = new Vector2fWithCross((float)p.getForces()[0],(float)p.getForces()[1]);
		    	Vector2f.add(f, force, force);
		    }    
		    	
		}

	public void updateTorque() {
		torque = 0;
	    for (Particle p : particles) {
	    	Vector2fWithCross f = new Vector2fWithCross((float)p.getForces()[0],(float)p.getForces()[1]);
	    	Vector2fWithCross po = new Vector2fWithCross((float)p.getPosition()[0]-position.x,(float)p.getPosition()[1]-position.y);
	    	torque += Vector2fWithCross.cross(po, f);
	    }
	}
	@Override
	public void setState(double[] newState) {
	    position.x = (float) newState[0];
	    position.y = (float) newState[1];

	    orient = (float) newState[2];

	    P.x = (float) newState[3];
	    P.y = (float) newState[4];

	    L = (float) newState[5];

	    for (Particle p : particles) {
	    	Vector2f conPos = new Vector2f((float)p.getConstructPos()[0],(float)p.getConstructPos()[1]);
	    	Vector2f pos = Matrix2f.transform(R, conPos, null);
	        p.setPosition(new double[]{pos.x+position.x,pos.y+position.y});
	    }

	    //Compute auxiliary variables
	    recomputeAuxiliaryVars();
	}
	public void recomputeAuxiliaryVars() {
		
	    R.m00=(float) Math.cos(orient);
	    R.m01=(float) -Math.sin(orient);
	    R.m10=(float) Math.sin(orient);
	    R.m11=(float) Math.cos(orient);
	    v = (Vector2fWithCross) P.scale((float) (1/mass));
	    
		omega = L * iBodyInv;
	}
	@Override
	public double[] getState() {
	    double[] y = new double[13];
	    y[0] = position.x;
	    y[1] = position.y;
	    y[2] = orient;
	    y[3] = P.x;
	    y[4] = P.y;
	    y[5] = L;
	    return y;
	}
	@Override
	public double[] getDerivativeState() {
	    updateForce();
	    updateTorque();
	    double[] y= new double[13];
	    y[0] = v.x;
	    y[1] = v.y;
	    y[2] = omega;
	    y[3] = force.x;
	    y[4] = force.y;
	    y[5] = torque;
	    return y;
	}
	
	public List<Particle2D> getParticles() {
		return particles;
	}
}
