package physics;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

public class Quaternion {

    public float x, y, z, w;

    public final static Quaternion ZERO = new Quaternion(0, 0, 0, 0);
    public final static Quaternion IDENTITY = new Quaternion(0, 0, 0, 1);
    
    public Quaternion() {
        x = y = z = 0;
        w = 1;
    }

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
    }

    public Quaternion(Vector3f axis, float angle) {
        axis.normalise();
        
        float cosHalfAngle = (float) Math.cos(angle * 0.5);
        float sinHalfAngle = (float) Math.sin(angle * 0.5);
        
        w = cosHalfAngle;
        x = axis.x * sinHalfAngle;
        y = axis.y * sinHalfAngle;
        z = axis.z * sinHalfAngle;
    }

    public Quaternion fromAxisAngle(Vector3f axis, float angle) {
        axis.normalise();
        
        float sinHalfAngle = (float) Math.sin(angle * 0.5);
        float cosHalfAngle = (float) Math.cos(angle * 0.5);

        w = cosHalfAngle;
        
        x = axis.x * sinHalfAngle;
        y = axis.y * sinHalfAngle;
        z = axis.z * sinHalfAngle;
        
        return this;
    }
    
    public Quaternion rotateX(float angle) {
        float sinHalfAngle = (float) Math.sin(angle * 0.5);
        float cosHalfAngle = (float) Math.cos(angle * 0.5);

        w = cosHalfAngle;
        
        x = sinHalfAngle;
        y = 0;
        z = 0;
        
        return this;
    }

    public Quaternion rotateY(float angle) {
        float sinHalfAngle = (float) Math.sin(angle * 0.5);
        float cosHalfAngle = (float) Math.cos(angle * 0.5);

        w = cosHalfAngle;
        
        x = 0;
        y = sinHalfAngle;
        z = 0;
        
        return this;
    }

    public Quaternion rotateZ(float angle) {
        float sinHalfAngle = (float) Math.sin(angle * 0.5);
        float cosHalfAngle = (float) Math.cos(angle * 0.5);

        w = cosHalfAngle;
        
        x = 0;
        y = 0;
        z = sinHalfAngle;
        
        return this;
    }

    public Quaternion fromAngles(float xAngle, float yAngle, float zAngle) {
        float angle;
        float sinY, sinZ, sinX, cosY, cosZ, cosX;
        angle = zAngle * 0.5f;
        sinZ = (float)Math.sin(angle);
        cosZ = (float)Math.cos(angle);
        angle = yAngle * 0.5f;
        sinY = (float)Math.sin(angle);
        cosY = (float)Math.cos(angle);
        angle = xAngle * 0.5f;
        sinX = (float)Math.sin(angle);
        cosX = (float)Math.cos(angle);

        // variables used to reduce multiplication calls.
        float cosYXcosZ = cosY * cosZ;
        float sinYXsinZ = sinY * sinZ;
        float cosYXsinZ = cosY * sinZ;
        float sinYXcosZ = sinY * cosZ;

        w = (cosYXcosZ * cosX - sinYXsinZ * sinX);
        x = (cosYXcosZ * sinX + sinYXsinZ * cosX);
        y = (sinYXcosZ * cosX + cosYXsinZ * sinX);
        z = (cosYXsinZ * cosX - sinYXcosZ * sinX);

        normalizeLocal();
        return this;
    }
    public Quaternion negate() {
        return new Quaternion(-x, -y, -z, -w);
    }

    public Quaternion negateLocal() {
        x = -x;
        y = -y;
        z = -z;
        w = -w;
        return this;
    }
    
    public void loadIdentity() {
        x = y = z = 0;
        w = 1;
    }

    public boolean isIdentity() {
        if (x == 0 && y == 0 && z == 0 && w == 1) {
            return true;
        } else {
            return false;
        }
    }

    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public Quaternion normalize() {
        float length = x * x + y * y + z * z + w * w;
        if (length != 1f && length != 0f) {
            length = (float) (1.0 / Math.sqrt(length));
            return new Quaternion(length * x, length * y, length * z, length * w);
        }
        return new Quaternion(x, y, z, w);
    }

    public Quaternion normalizeLocal() {
        float length = x * x + y * y + z * z + w * w;
        if (length != 1f && length != 0f) {
            length = (float) (1.0 / Math.sqrt(length));
            x *= length;
            y *= length;
            z *= length;
            w *= length;
        }
        return this;
    }
    
    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    public Quaternion conjugateLocal() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }
    
    public Quaternion inverse() {
        float length = x * x + y * y + z * z + w * w;
        if (length != 1f && length != 0f) {
            length = (float) (1.0 / Math.sqrt(length));
            return new Quaternion(-x * length, -y * length, -z * length, w * length);
        }
        return new Quaternion(-x, -y, -z, w);
    }
    
    public Quaternion inverseLocal() {
        float length = x * x + y * y + z * z + w * w;
        if (length != 1f && length != 0f) {
            length = (float) (1.0 / Math.sqrt(length));
            x *= length;
            y *= length;
            z *= length;
        }
        x = -x;
        y = -y;
        z = -z;
        return this;
    }
    
    public Quaternion mult(Quaternion q) {
        float qw = q.w, qx = q.x, qy = q.y, qz = q.z;
        
        float rw = w * qw - x * qx - y * qy - z * qz;
        float rx = w * qx + x * qw + y * qz - z * qy;
        float ry = w * qy + y * qw + z * qx - x * qz;
        float rz = w * qz + z * qw + x * qy - y * qx;
        
        return new Quaternion(rx, ry, rz, rw);
    }
    
    public Quaternion mult(Quaternion q, Quaternion res) {
        if (res == null) {
            res = new Quaternion();
        }
        float qw = q.w, qx = q.x, qy = q.y, qz = q.z;
        res.x = x * qw + y * qz - z * qy + w * qx;
        res.y = -x * qz + y * qw + z * qx + w * qy;
        res.z = x * qy - y * qx + z * qw + w * qz;
        res.w = -x * qx - y * qy - z * qz + w * qw;
        return res;
    }
    
    public Quaternion multLocal(Quaternion q) {
        float qw = q.w, qx = q.x, qy = q.y, qz = q.z;
        
        float rw = w * qw - x * qx - y * qy - z * qz;
        float rx = w * qx + x * qw + y * qz - z * qy;
        float ry = w * qy + y * qw + z * qx - x * qz;
        float rz = w * qz + z * qw + x * qy - y * qx;
        
        x = rx;
        y = ry;
        z = rz;
        w = rw;
        return this;
    }
    
    public Vector3f mult(Vector3f v) {
        if (v.x == 0 && v.y == 0 && v.z == 0) {
            return new Vector3f(0, 0, 0);
        } else {
            float vx = v.x, vy = v.y, vz = v.z;
            float rx = w * w * vx + 2 * y * w * vz - 2 * z * w * vy + x * x * vx + 2 * y * x * vy + 2 * z * x * vz
                    - z * z * vx - y * y * vx;
            float ry = 2 * x * y * vx + y * y * vy + 2 * z * y * vz + 2 * w * z * vx - z * z * vy + w * w * vy
                    - 2 * x * w * vz - x * x * vy;
            float rz = 2 * x * z * vx + 2 * y * z * vy + z * z * vz - 2 * w * y * vx - y * y * vz + 2 * w * x * vy
                    - x * x * vz + w * w * vz;
            return new Vector3f(rx, ry, rz);
        }
    }
    
    public Vector3f mult(Vector3f v, Vector3f store) {
        if (store == null) {
            store = new Vector3f();
        }
        if (v.x == 0 && v.y == 0 && v.z == 0) {
            store.set(0, 0, 0);
        } else {
            float vx = v.x, vy = v.y, vz = v.z;
            store.x = w * w * vx + 2 * y * w * vz - 2 * z * w * vy + x * x
                    * vx + 2 * y * x * vy + 2 * z * x * vz - z * z * vx - y
                    * y * vx;
            store.y = 2 * x * y * vx + y * y * vy + 2 * z * y * vz + 2 * w
                    * z * vx - z * z * vy + w * w * vy - 2 * x * w * vz - x
                    * x * vy;
            store.z = 2 * x * z * vx + 2 * y * z * vy + z * z * vz - 2 * w
                    * y * vx - y * y * vz + 2 * w * x * vy - x * x * vz + w
                    * w * vz;
        }
        return store;
    }
    
    public Vector3f multLocal(Vector3f v) {
        float tempX, tempY;
        tempX = w * w * v.x + 2 * y * w * v.z - 2 * z * w * v.y + x * x * v.x
                + 2 * y * x * v.y + 2 * z * x * v.z - z * z * v.x - y * y * v.x;
        tempY = 2 * x * y * v.x + y * y * v.y + 2 * z * y * v.z + 2 * w * z
                * v.x - z * z * v.y + w * w * v.y - 2 * x * w * v.z - x * x
                * v.y;
        v.z = 2 * x * z * v.x + 2 * y * z * v.y + z * z * v.z - 2 * w * y * v.x
                - y * y * v.z + 2 * w * x * v.y - x * x * v.z + w * w * v.z;
        v.x = tempX;
        v.y = tempY;
        return v;
    }
    
    public Quaternion delta(Quaternion q) {
        return this.inverse().mult(q);
    }

    public float dot(Quaternion q) {
        return x * q.x + y * q.y + z * q.z + w * q.w;
    }

    public Quaternion add(Quaternion q) {
        return new Quaternion(x + q.x, y + q.y, z + q.z, w + q.w);
    }
    
    public Quaternion subtract(Quaternion q) {
        return new Quaternion(x - q.x, y - q.y, z - q.z, w - q.w);
    }
    


    public Quaternion slerp(Quaternion src, Quaternion dest, float t) {
        if (src.x == dest.x && src.y == dest.y && src.z == dest.z && src.w == dest.w) {
            return new Quaternion(src);
        }

        float cos = src.dot(dest);

        if (cos < 0.0f) {
            cos = -cos;
            dest = dest.negate();
        }

        float srcFactor = 1 - t;
        float destFactor = t;

        if (cos > 0.999f) {
            srcFactor = 1 - t;
            destFactor = t;
        } else {
            float angle = (float) Math.acos(cos);
            float invSin = 1f / (float) Math.sin(angle);

            srcFactor = (float) Math.sin((1 - t) * angle) * invSin;
            destFactor = (float) Math.sin((t * angle)) * invSin;
        }
        
        float rx = (srcFactor * src.x) + (destFactor * dest.x);
        float ry = (srcFactor * src.y) + (destFactor * dest.y);
        float rz = (srcFactor * src.z) + (destFactor * dest.z);
        float rw = (srcFactor * src.w) + (destFactor * dest.w);

        return new Quaternion(rx, ry, rz, rw);
    }
    
    public Quaternion slerp(Quaternion dest, float t) {
        if (x == dest.x && y == dest.y && z == dest.z && w == dest.w) {
            return new Quaternion(this);
        }

        float cos = dot(dest);

        if (cos < 0.0f) {
            cos = -cos;
            dest = dest.negate();
        }

        float srcFactor = 1 - t;
        float destFactor = t;

        if (cos > 0.999f) {
            srcFactor = 1 - t;
            destFactor = t;
        } else {
            float angle = (float) Math.acos(cos);
            float invSin = 1f / (float) Math.sin(angle);

            srcFactor = (float) Math.sin((1 - t) * angle) * invSin;
            destFactor = (float) Math.sin((t * angle)) * invSin;
        }
        
        float rx = (srcFactor * x) + (destFactor * dest.x);
        float ry = (srcFactor * y) + (destFactor * dest.y);
        float rz = (srcFactor * z) + (destFactor * dest.z);
        float rw = (srcFactor * w) + (destFactor * dest.w);

        return new Quaternion(rx, ry, rz, rw);
    }
    
    public Quaternion set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Quaternion set(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
        return this;
    }
    public Matrix3f toRotationMatrix() {
        Matrix3f matrix = new Matrix3f();
        return toRotationMatrix(matrix);
    }
    
    public Matrix3f toRotationMatrix(Matrix3f result) {
        
        if (result == null)
            result = new Matrix3f();
        
        float _2x = x * 2;
        float _2y = y * 2;
        float _2z = z * 2;
        float _2xx = x * _2x;
        float _2xy = x * _2y;
        float _2xz = x * _2z;
        float _2xw = w * _2x;
        float _2yy = y * _2y;
        float _2yz = y * _2z;
        float _2yw = w * _2y;
        float _2zz = z * _2z;
        float _2zw = w * _2z;
        
        result.m00 = 1 - (_2yy + _2zz);
        result.m01 = (_2xy - _2zw);
        result.m02 = (_2xz + _2yw);
        result.m10 = (_2xy + _2zw);
        result.m11 = 1 - (_2xx + _2zz);
        result.m12 = (_2yz - _2xw);
        result.m20 = (_2xz - _2yw);
        result.m21 = (_2yz + _2xw);
        result.m22 = 1 - (_2xx + _2yy);
        
        return result;
    }
}