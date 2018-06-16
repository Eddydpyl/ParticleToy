package physics.models.bodies;

import static physics.LinearSolver.vecAdd;
import static physics.LinearSolver.vecTimesScalar;

public interface RigidBody {

    static void updateState(RigidBody rigidBody, double time) {
    	double[] state = rigidBody.getState();
        rigidBody.setState(vecAdd(state, vecTimesScalar(rigidBody.getDerivativeState(), time)));
    }

	public void draw();
	public double[] getState();
	public void setState(double[] state);
	public double[] getDerivativeState();
}
