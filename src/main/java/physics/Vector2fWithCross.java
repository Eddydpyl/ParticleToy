package physics;

import org.lwjgl.util.vector.Vector2f;

public class Vector2fWithCross extends Vector2f{
	public Vector2fWithCross(float i, float j) {
		super(i,j);
	}

	public Vector2fWithCross() {
		super();
	}

	static public float cross(Vector2fWithCross v1,Vector2fWithCross v2) {
		return  v1.x * v2.y - v1.y * v2.x;
	}
}