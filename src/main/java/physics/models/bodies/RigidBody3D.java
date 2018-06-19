package physics.models.bodies;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3d;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import physics.Quaternion;
import physics.models.particles.Particle;
import physics.models.particles.Particle3D;

public class RigidBody3D implements RigidBody {
	private List<Particle> particles;
	private Vector3f constructPos;
	private double mass;
	private Matrix3f iBody,iBodyInv;
	
	private Vector3f position;
	private Quaternion q; 
	private Vector3f P;
	private Vector3f L;
	private Vector3f size;
	
	private Matrix3f R;
	private Matrix3f Iinv;
	private Vector3f v;
	private Vector3f omega;
	
	private Vector3f force;
	private Vector3f torque;
	
	public RigidBody3D(Vector3f startPos, Vector3f s, Vector3f numParticles, double particleMass) {
		constructPos = startPos;
		size = s;
		initializeVariables();
		
	    //generate particles with body coordinates
	    for (int x = 0; x < numParticles.x; x++) {
	        for (int y = 0; y < numParticles.y; y++) {
		        	for (int z = 0; z < numParticles.z; z++) {
	                double xStart = -size.x / 2 + size.x * (float) x / (numParticles.x - 1);
	                double yStart = -size.y / 2 + size.y * (float) y / (numParticles.y - 1);
	                double zStart = -size.z / 2 + size.z * (float) z / (numParticles.z - 1);
	                Particle3D p = new Particle3D(new double[] {xStart, yStart,zStart}, particleMass);
	                //A rigid body has constant density
	                particles.add(p);
	        	}
	        }
	    }

	    //Calculate total mass
	    for (Particle p : particles) {
	        mass += p.getMass();
	    }

	    //Calculate Ibody
	    Matrix3f IbodyMatrix = Matrix3f.setIdentity(iBodyInv);
	    IbodyMatrix.m00 = (float) ((float) (Math.pow(size.y,2)+ Math.pow(size.z,2)) * mass/12);
	    IbodyMatrix.m11 = (float) ((float) (Math.pow(size.x,2)+ Math.pow(size.z,2)) * mass/12);
	    IbodyMatrix.m22 = (float) ((float) (Math.pow(size.x,2)+ Math.pow(size.y,2)) * mass/12);
	    iBody = IbodyMatrix;
	    Matrix3f.invert(iBody, iBodyInv);
	}
	public void initializeVariables() {
		position = new Vector3f();
		position = constructPos;
		R = new Matrix3f();
		iBodyInv = new Matrix3f();
	    Matrix3f.setIdentity(R);
	    q = new Quaternion(1f,0f,0f,0f);
	    P = new Vector3f(0, 0, 0);
	    L = new Vector3f(0, 0, 0);
	    Iinv = iBodyInv;
	    v = new Vector3f(0, 0, 0);
	    omega = new Vector3f(0,0,0);
	    Matrix3f.transform(Iinv, L, omega);
	    force = new Vector3f(0, 0, 0);
	    torque = new Vector3f(0, 0, 0);
	    particles = new ArrayList<Particle>();
	}
	@Override
	public void draw() {
		Vector3f v1 = Vector3f.add(Matrix3f.transform(R, new Vector3f(-size.x / 2, -size.y / 2, -size.z / 2), null),position,null);
	    Vector3f v2 = Vector3f.add(Matrix3f.transform(R, new Vector3f(size.x / 2, -size.y / 2, -size.z / 2), null),position,null);
	    Vector3f v3 = Vector3f.add(Matrix3f.transform(R, new Vector3f(-size.x / 2, -size.y / 2, size.z / 2), null),position,null);
	    Vector3f v4 = Vector3f.add(Matrix3f.transform(R, new Vector3f(size.x / 2, -size.y / 2, size.z / 2), null),position,null);
	    Vector3f v5 = Vector3f.add(Matrix3f.transform(R, new Vector3f(-size.x / 2, size.y / 2, -size.z / 2), null),position,null);
	    Vector3f v6 = Vector3f.add(Matrix3f.transform(R, new Vector3f(size.x / 2, size.y / 2, -size.z / 2), null),position,null);
	    Vector3f v7 = Vector3f.add(Matrix3f.transform(R, new Vector3f(-size.x / 2, size.y / 2, size.z / 2), null),position,null);
	    Vector3f v8 = Vector3f.add(Matrix3f.transform(R, new Vector3f(size.x / 2, size.y / 2, size.z / 2), null),position,null);
	    glBegin(GL_LINES);
	    glColor3d(1, 0, 0);
	    glVertex3d(v1.x, v1.y, v1.z);
	    glVertex3d(v2.x, v2.y, v2.z);
	    glVertex3d(v1.x, v1.y, v1.z);
	    glVertex3d(v3.x, v3.y, v3.z);
	    glVertex3d(v2.x, v2.y, v2.z);
	    glVertex3d(v4.x, v4.y, v4.z);
	    glVertex3d(v3.x, v3.y, v3.z);
	    glVertex3d(v4.x, v4.y, v4.z);	
	    
	    glVertex3d(v5.x, v5.y, v5.z);
	    glVertex3d(v6.x, v6.y, v6.z);
	    glVertex3d(v5.x, v5.y, v5.z);
	    glVertex3d(v7.x, v7.y, v7.z);
	    glVertex3d(v6.x, v6.y, v6.z);
	    glVertex3d(v8.x, v8.y, v8.z);
	    glVertex3d(v7.x, v7.y, v7.z);
	    glVertex3d(v8.x, v8.y, v8.z);

	    glVertex3d(v1.x, v1.y, v1.z);
	    glVertex3d(v5.x, v5.y, v5.z);
	    glVertex3d(v2.x, v2.y, v2.z);
	    glVertex3d(v6.x, v6.y, v6.z);
	    glVertex3d(v3.x, v3.y, v3.z);
	    glVertex3d(v7.x, v7.y, v7.z);
	    glVertex3d(v4.x, v4.y, v4.z);
	    glVertex3d(v8.x, v8.y, v8.z);
	    glEnd();		
	}
	
	 public Vector3f getBodyCoordinates(Vector3f world) {
		 return Matrix3f.transform((Matrix3f) R.transpose(), Vector3f.sub(world, position, null), null);
		   
	}
	
	public void updateForce() {
	    force = new Vector3f(0, 0, 0);
		    for (Particle p : particles) {
		    	Vector3f f = new Vector3f((float)p.getForces()[0],(float)p.getForces()[1],0);
		    	Vector3f.add(f, force, force);
		    }    
		    	
		}

	public void updateTorque() {
		torque = new Vector3f(0, 0, 0);
	    for (Particle p : particles) {
	    	Vector3f f = new Vector3f((float)p.getForces()[0],(float)p.getForces()[1],0);
	    	Vector3f po = new Vector3f((float)p.getPosition()[0],(float)p.getPosition()[1],0);
	        Vector3f.add(torque, Vector3f.cross(po, f, null), torque);
	    }
	}
	@Override
	public void setState(double[] newState) {
	    position.x = (float) newState[0];
	    position.y = (float) newState[1];
	    position.z = (float) newState[2];

	    q.w = (float) newState[3];
	    q.x = (float) newState[4];
	    q.y = (float) newState[5];
	    q.z = (float) newState[6];

	    P.x = (float) newState[7];
	    P.y = (float) newState[8];
	    P.z = (float) newState[9];

	    L.x = (float) newState[10];
	    L.y = (float) newState[11];
	    L.z = (float) newState[12];

	    for (Particle p : particles) {
	    	Vector3f conPos = new Vector3f((float)p.getConstructPos()[0],(float)p.getConstructPos()[1],0);
	    	Vector3f curPos = new Vector3f((float)p.getPosition()[0],(float)p.getPosition()[1],0);
	    	Vector3f pos =new Vector3f(Vector3f.add(Matrix3f.transform(R, conPos, null),curPos,null));
	        p.setPosition(new double[]{pos.x,pos.y,pos.z});
	    }

	    //Compute auxiliary variables
	    recomputeAuxiliaryVars();
	}
	public void recomputeAuxiliaryVars() {
	    R = q.normalize().toRotationMatrix();
	    v = (Vector3f) P.scale((float) (1/mass));
	    Matrix3f.mul(R, iBodyInv, null);
	    Matrix3f.mul(Matrix3f.mul(R, iBodyInv, null), (Matrix3f) R.transpose(), Iinv);
	    Matrix3f.transform(Iinv, L, omega);
	}
	@Override
	public double[] getState() {
	    double[] y = new double[13];
	    y[0] = position.x;
	    y[1] = position.y;
	    y[2] = position.z;

	    y[3] = q.w;
	    y[4] = q.x;
	    y[5] = q.y;
	    y[6] = q.z;

	    y[7] = P.x;
	    y[8] = P.y;
	    y[9] = P.z;

	    y[10] = L.x;
	    y[11] = L.y;
	    y[12] = L.z;
	    return y;
	}
	@Override
	public double[] getDerivativeState() {
	    updateForce();
	    updateTorque();
	    double[] y= new double[13];
	    y[0] = v.x;
	    y[1] = v.y;
	    y[2] = v.z;

	    Quaternion omegaQuaternion= new Quaternion(0, omega.x, omega.y, omega.z);
	    Quaternion qdot= omegaQuaternion.mult(q);
	    y[3] = qdot.w * 0.05f;
	    y[4] = qdot.x * 0.05f;
	    y[5] = qdot.y * 0.05f;
	    y[6] = qdot.z * 0.05f;

	    y[7] = force.x;
	    y[8] = force.y;
	    y[9] = force.z;

	    y[10] = torque.x;
	    y[11] = torque.y;
	    y[12] = torque.z;
	    return y;
	}
	public List<Particle> getParticles() {
		return particles;
	}
}
