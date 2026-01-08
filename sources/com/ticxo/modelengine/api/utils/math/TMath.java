/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.BoundingBox
 *  org.bukkit.util.EulerAngle
 *  org.bukkit.util.Transformation
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Math
 *  org.joml.Matrix3d
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Quaterniond
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package com.ticxo.modelengine.api.utils.math;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import java.util.UUID;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Matrix3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class TMath {
    public static final float PI = (float)java.lang.Math.PI;
    public static final double TAU = java.lang.Math.PI * 2;
    public static final double PI_2 = 1.5707963267948966;
    public static final double PI_4 = 0.7853981633974483;
    public static final float EPSILON = 1.0E-5f;
    public static final double D_EPSILON = 1.0E-5;
    public static final float DEG2RAD = (float)java.lang.Math.PI / 180;
    public static final float RAD2DEG = 57.29578f;
    public static final double D_DEG2RAD = java.lang.Math.PI / 180;
    public static final double D_RAD2DEG = 57.29577951308232;
    protected static float LOWER_SCALE_LIMIT = 0.0f;
    protected static double MOVEMENT_RESOLUTION = 0.001;

    public static boolean isSimilar(float a, float b2) {
        return Math.abs((float)(b2 - a)) < 1.0E-5f;
    }

    public static boolean isSimilar(double a, double b2) {
        return Math.abs((double)(b2 - a)) < (double)1.0E-5f;
    }

    public static double clamp(double value, double min, double max) {
        return Math.min((double)Math.max((double)value, (double)min), (double)max);
    }

    public static float clamp(float value, float min, float max) {
        return Math.min((float)Math.max((float)value, (float)min), (float)max);
    }

    public static int clamp(int value, int min, int max) {
        return Math.min((int)Math.max((int)value, (int)min), (int)max);
    }

    public static int absMax(float a, float b2, float c2) {
        float absA = Math.abs((float)a);
        float absB = Math.abs((float)b2);
        float absC = Math.abs((float)c2);
        if (absA > absB) {
            if (absA > absC) {
                return 0;
            }
            return 2;
        }
        if (absB > absC) {
            return 1;
        }
        return 2;
    }

    public static byte setBit(byte dataItem, int bitOffset, boolean flag) {
        byte f = (byte)(1 << bitOffset);
        return (byte)(flag ? dataItem | f : dataItem & ~f);
    }

    public static boolean getBit(byte dataItem, int bitOffset) {
        return (dataItem & 1 << bitOffset) != 0;
    }

    public static int floor(double val) {
        return (int)Math.floor((double)val);
    }

    public static int ceil(double val) {
        return (int)Math.ceil((double)val);
    }

    public static int tryParse(String val, int def) {
        if (val == null) {
            return def;
        }
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException ignored) {
            return def;
        }
    }

    public static double tryParse(String val, double def) {
        if (val == null) {
            return def;
        }
        try {
            return Double.parseDouble(val);
        }
        catch (NumberFormatException ignored) {
            return def;
        }
    }

    public static boolean isBoundingBoxWithinDistance(@NotNull Vector start, @NotNull Vector direction, BoundingBox boundingBox, double maxDistance) {
        double tzMax;
        double tzMin;
        double tyMax;
        double tyMin;
        double tMax;
        double tMin;
        double startX = start.getX();
        double startY = start.getY();
        double startZ = start.getZ();
        Vector dir = direction.clone();
        if (dir.getX() == 0.0) {
            dir.setX(0);
        }
        if (dir.getY() == 0.0) {
            dir.setY(0);
        }
        if (dir.getZ() == 0.0) {
            dir.setZ(0);
        }
        dir.normalize();
        double dirX = dir.getX();
        double dirY = dir.getY();
        double dirZ = dir.getZ();
        double divX = 1.0 / dirX;
        double divY = 1.0 / dirY;
        double divZ = 1.0 / dirZ;
        if (dirX >= 0.0) {
            tMin = (boundingBox.getMinX() - startX) * divX;
            tMax = (boundingBox.getMaxX() - startX) * divX;
        } else {
            tMin = (boundingBox.getMaxX() - startX) * divX;
            tMax = (boundingBox.getMinX() - startX) * divX;
        }
        if (dirY >= 0.0) {
            tyMin = (boundingBox.getMinY() - startY) * divY;
            tyMax = (boundingBox.getMaxY() - startY) * divY;
        } else {
            tyMin = (boundingBox.getMaxY() - startY) * divY;
            tyMax = (boundingBox.getMinY() - startY) * divY;
        }
        if (tMin > tyMax || tMax < tyMin) {
            return false;
        }
        if (tyMin > tMin) {
            tMin = tyMin;
        }
        if (tyMax < tMax) {
            tMax = tyMax;
        }
        if (dirZ >= 0.0) {
            tzMin = (boundingBox.getMinZ() - startZ) * divZ;
            tzMax = (boundingBox.getMaxZ() - startZ) * divZ;
        } else {
            tzMin = (boundingBox.getMaxZ() - startZ) * divZ;
            tzMax = (boundingBox.getMinZ() - startZ) * divZ;
        }
        if (tMin > tzMax || tMax < tzMin) {
            return false;
        }
        if (tzMin > tMin) {
            tMin = tzMin;
        }
        if (tzMax < tMax) {
            tMax = tzMax;
        }
        if (tMax < 0.0) {
            return false;
        }
        return !(tMin > maxDistance);
    }

    public static double distanceSquaredToBoundingBox(Vector origin, BoundingBox box) {
        double x = Math.max((double)(Math.abs((double)(origin.getX() - box.getCenterX())) - box.getWidthX() * 0.5), (double)0.0);
        double y = Math.max((double)(Math.abs((double)(origin.getY() - box.getCenterY())) - box.getHeight() * 0.5), (double)0.0);
        double z = Math.max((double)(Math.abs((double)(origin.getZ() - box.getCenterZ())) - box.getWidthZ() * 0.5), (double)0.0);
        return x * x + y * y + z * z;
    }

    public static boolean isSimilar(Vector a, Vector b2) {
        return Math.abs((double)(b2.getX() - a.getX())) < MOVEMENT_RESOLUTION && Math.abs((double)(b2.getY() - a.getY())) < MOVEMENT_RESOLUTION && Math.abs((double)(b2.getZ() - a.getZ())) < MOVEMENT_RESOLUTION;
    }

    public static EulerAngle makeAngle(double x, double y, double z) {
        return new EulerAngle(Math.toRadians((double)x), Math.toRadians((double)y), Math.toRadians((double)z));
    }

    public static EulerAngle add(EulerAngle a, EulerAngle b2) {
        return a.add(b2.getX(), b2.getY(), b2.getZ());
    }

    public static float wrapRadian(float r) {
        if ((double)(r = (float)((double)r % (java.lang.Math.PI * 2))) < -java.lang.Math.PI) {
            r = (float)((double)r + java.lang.Math.PI * 2);
        }
        if ((double)r > java.lang.Math.PI) {
            r = (float)((double)r - java.lang.Math.PI * 2);
        }
        return r;
    }

    public static float wrapDegree(float r) {
        if ((r %= 360.0f) < -180.0f) {
            r += 360.0f;
        }
        if (r > 180.0f) {
            r -= 360.0f;
        }
        return r;
    }

    public static float radianDifference(float a, float b2) {
        return TMath.wrapRadian(b2 - a);
    }

    public static float degreeDifference(float a, float b2) {
        return TMath.wrapDegree(b2 - a);
    }

    public static float rotateIfNecessary(float current, float target, float negativeClamp, float positiveClamp) {
        float delta = TMath.degreeDifference(current, target);
        float clampedDelta = TMath.clamp(delta, negativeClamp, positiveClamp);
        return target - clampedDelta;
    }

    public static byte rotToByte(float rot) {
        return (byte)(rot * 256.0f / 360.0f);
    }

    public static float byteToRot(byte rot) {
        return (float)rot / 256.0f * 360.0f;
    }

    public static double lerp(double a, double b2, double t) {
        return (1.0 - t) * a + t * b2;
    }

    public static double invLerp(double a, double b2, double l) {
        return (l - a) / (b2 - a);
    }

    public static float lerp(float a, float b2, float t) {
        return (1.0f - t) * a + t * b2;
    }

    public static float invLerp(float a, float b2, float l) {
        return (l - a) / (b2 - a);
    }

    public static double lerp(double a, double b2, double aT, double bT) {
        return aT * a + bT * b2;
    }

    public static float lerp(float a, float b2, float aT, float bT) {
        return aT * a + bT * b2;
    }

    public static double rotLerp(double a, double b2, double t) {
        return a + (double)TMath.degreeDifference((float)a, (float)b2) * t;
    }

    public static float rotLerp(float a, float b2, double t) {
        return (float)((double)a + (double)TMath.degreeDifference(a, b2) * t);
    }

    public static double smoothLerp(double a, double b2, double c2, double d, double t) {
        double t0 = 0.0;
        double t1 = 1.0;
        double t2 = 2.0;
        double t3 = 3.0;
        t = (t2 - t1) * t + t1;
        double a1 = TMath.lerp(a, b2, (t1 - t) / (t1 - t0), (t - t0) / (t1 - t0));
        double a2 = TMath.lerp(b2, c2, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
        double a3 = TMath.lerp(c2, d, (t3 - t) / (t3 - t2), (t - t2) / (t3 - t2));
        double b1 = TMath.lerp(a1, a2, (t2 - t) / (t2 - t0), (t - t0) / (t2 - t0));
        double b22 = TMath.lerp(a2, a3, (t3 - t) / (t3 - t1), (t - t1) / (t3 - t1));
        return TMath.lerp(b1, b22, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
    }

    public static Vector lerp(Vector a, Vector b2, double t) {
        return new Vector(TMath.lerp(a.getX(), b2.getX(), t), TMath.lerp(a.getY(), b2.getY(), t), TMath.lerp(a.getZ(), b2.getZ(), t));
    }

    public static Vector lerp(Vector a, Vector b2, double aT, double bT) {
        return new Vector(TMath.lerp(a.getX(), b2.getX(), aT, bT), TMath.lerp(a.getY(), b2.getY(), aT, bT), TMath.lerp(a.getZ(), b2.getZ(), aT, bT));
    }

    public static Vector3f lerp(Vector3f a, Vector3f b2, double t) {
        return new Vector3f((float)TMath.lerp((double)a.x, (double)b2.x, t), (float)TMath.lerp((double)a.y, (double)b2.y, t), (float)TMath.lerp((double)a.z, (double)b2.z, t));
    }

    public static Vector3f lerp(Vector3f a, Vector3f b2, float aT, float bT) {
        return new Vector3f(TMath.lerp(a.x, b2.x, aT, bT), TMath.lerp(a.y, b2.y, aT, bT), TMath.lerp(a.z, b2.z, aT, bT));
    }

    public static Vector smoothLerp(Vector a, Vector b2, Vector c2, Vector d, double t) {
        double t0 = 0.0;
        double t1 = 1.0;
        double t2 = 2.0;
        double t3 = 3.0;
        t = (t2 - t1) * t + t1;
        Vector a1 = TMath.lerp(a, b2, (t1 - t) / (t1 - t0), (t - t0) / (t1 - t0));
        Vector a2 = TMath.lerp(b2, c2, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
        Vector a3 = TMath.lerp(c2, d, (t3 - t) / (t3 - t2), (t - t2) / (t3 - t2));
        Vector b1 = TMath.lerp(a1, a2, (t2 - t) / (t2 - t0), (t - t0) / (t2 - t0));
        Vector b22 = TMath.lerp(a2, a3, (t3 - t) / (t3 - t1), (t - t1) / (t3 - t1));
        return TMath.lerp(b1, b22, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
    }

    public static Vector3f smoothLerp(Vector3f a, Vector3f b2, Vector3f c2, Vector3f d, float t) {
        float t0 = 0.0f;
        float t1 = 1.0f;
        float t2 = 2.0f;
        float t3 = 3.0f;
        t = (t2 - t1) * t + t1;
        Vector3f a1 = TMath.lerp(a, b2, (t1 - t) / (t1 - t0), (t - t0) / (t1 - t0));
        Vector3f a2 = TMath.lerp(b2, c2, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
        Vector3f a3 = TMath.lerp(c2, d, (t3 - t) / (t3 - t2), (t - t2) / (t3 - t2));
        Vector3f b1 = TMath.lerp(a1, a2, (t2 - t) / (t2 - t0), (t - t0) / (t2 - t0));
        Vector3f b22 = TMath.lerp(a2, a3, (t3 - t) / (t3 - t1), (t - t1) / (t3 - t1));
        return TMath.lerp(b1, b22, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
    }

    public static EulerAngle lerp(EulerAngle a, EulerAngle b2, double t) {
        return new EulerAngle(TMath.lerp(a.getX(), b2.getX(), t), TMath.lerp(a.getY(), b2.getY(), t), TMath.lerp(a.getZ(), b2.getZ(), t));
    }

    public static EulerAngle lerp(EulerAngle a, EulerAngle b2, double aT, double bT) {
        return new EulerAngle(TMath.lerp(a.getX(), b2.getX(), aT, bT), TMath.lerp(a.getY(), b2.getY(), aT, bT), TMath.lerp(a.getZ(), b2.getZ(), aT, bT));
    }

    public static EulerAngle smoothLerp(EulerAngle a, EulerAngle b2, EulerAngle c2, EulerAngle d, double t) {
        double t0 = 0.0;
        double t1 = 1.0;
        double t2 = 2.0;
        double t3 = 3.0;
        t = (t2 - t1) * t + t1;
        EulerAngle a1 = TMath.lerp(a, b2, (t1 - t) / (t1 - t0), (t - t0) / (t1 - t0));
        EulerAngle a2 = TMath.lerp(b2, c2, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
        EulerAngle a3 = TMath.lerp(c2, d, (t3 - t) / (t3 - t2), (t - t2) / (t3 - t2));
        EulerAngle b1 = TMath.lerp(a1, a2, (t2 - t) / (t2 - t0), (t - t0) / (t2 - t0));
        EulerAngle b22 = TMath.lerp(a2, a3, (t3 - t) / (t3 - t1), (t - t1) / (t3 - t1));
        return TMath.lerp(b1, b22, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
    }

    public static Vector3f slerp(Vector3f a, Vector3f b2, double t) {
        if (a.equals((Object)b2)) {
            return a;
        }
        Quaternionf qA = new Quaternionf().rotationZYX(a.z, a.y, a.x);
        Quaternionf qB = new Quaternionf().rotationZYX(b2.z, b2.y, b2.x);
        qA.slerp((Quaternionfc)qB, (float)t);
        return TMath.getEulerAnglesZYX(qA, new Vector3f());
    }

    public static Vector3f getEulerAnglesZYX(Quaternionf quat, Vector3f eulerAngles) {
        return TMath.matToEulerZYX(quat.get(new Matrix3f()), eulerAngles);
    }

    public static Vector3f matToEulerZYX(Matrix3f mat, Vector3f result) {
        result.y = Math.asin((float)(-Math.clamp((float)mat.m02, (float)-1.0f, (float)1.0f)));
        if ((double)Math.abs((float)mat.m02) < 0.9999999) {
            result.x = Math.atan2((float)mat.m12, (float)mat.m22);
            result.z = Math.atan2((float)mat.m01, (float)mat.m00);
        } else {
            result.x = 0.0f;
            result.z = Math.atan2((float)(-mat.m10), (float)mat.m11);
        }
        return result;
    }

    public static Vector3d getEulerAnglesZYX(Quaterniond quat, Vector3d eulerAngles) {
        return TMath.matToEulerZYX(quat.get(new Matrix3d()), eulerAngles);
    }

    public static Vector3d matToEulerZYX(Matrix3d mat, Vector3d result) {
        result.y = Math.asin((double)(-TMath.clamp(mat.m02, -1.0, 1.0)));
        if (Math.abs((double)mat.m02) < 0.9999999) {
            result.x = Math.atan2((double)mat.m12, (double)mat.m22);
            result.z = Math.atan2((double)mat.m01, (double)mat.m00);
        } else {
            result.x = 0.0;
            result.z = Math.atan2((double)(-mat.m10), (double)mat.m11);
        }
        return result;
    }

    public static Vector3f toEulerZYX(Quaternionf quaternion, Vector3f dest) {
        return TMath.getEulerAnglesZYX(quaternion, dest).mul(57.29578f);
    }

    public static Vector3d toEulerZYX(Quaterniond quaternion) {
        return TMath.getEulerAnglesZYX(quaternion, new Vector3d()).mul(57.29577951308232);
    }

    public static Vector3d toEulerXYZ(Quaterniond quaternion) {
        return TMath.matToEulerXYZ(quaternion.get(new Matrix3d()), new Vector3d()).mul(57.29577951308232);
    }

    public static Vector3d matToEulerXYZ(Matrix3d mat, Vector3d result) {
        result.y = Math.asin((double)TMath.clamp(mat.m20, -1.0, 1.0));
        if (Math.abs((double)mat.m20) < 0.9999999) {
            result.x = Math.atan2((double)(-mat.m21), (double)mat.m22);
            result.z = Math.atan2((double)(-mat.m10), (double)mat.m00);
        } else {
            result.x = Math.atan2((double)mat.m12, (double)mat.m11);
            result.z = 0.0;
        }
        return result;
    }

    public static Quaterniond fromEulerZYX(Vector3d euler) {
        return new Quaterniond().rotateZYX(euler.z * (java.lang.Math.PI / 180), euler.y * (java.lang.Math.PI / 180), euler.x * (java.lang.Math.PI / 180));
    }

    public static Quaterniond fromEulerXYZ(Vector3d euler) {
        return new Quaterniond().rotateXYZ(euler.x * (java.lang.Math.PI / 180), euler.y * (java.lang.Math.PI / 180), euler.z * (java.lang.Math.PI / 180));
    }

    public static boolean isInterval(double value, double interval) {
        double half = interval * 0.5;
        return TMath.isSimilar(Math.abs((double)(Math.abs((double)value) % interval - half)), half);
    }

    public static boolean isAlmostBetween(double val, double min, double max) {
        if (val >= min && val <= max) {
            return true;
        }
        return TMath.isSimilar(val, min) || TMath.isSimilar(val, max);
    }

    public static Vector3f toPitchYaw(Vector3f dir) {
        double hMagnitude = Math.sqrt((float)(dir.x * dir.x + dir.z * dir.z));
        float pitch = (float)Math.atan2((double)(-dir.y), (double)hMagnitude) * 57.29578f;
        float yaw = Math.atan2((float)(-dir.x), (float)dir.z) * 57.29578f;
        return new Vector3f(pitch, yaw, 0.0f);
    }

    public static Quaternionf toQuaternion(Vector3f euler) {
        return new Quaternionf().rotateZYX(euler.z, euler.y, euler.x);
    }

    public static Quaternionf toQuaternion(Vector3f euler, Quaternionf dest) {
        return dest.identity().rotateZYX(euler.z, euler.y, euler.x);
    }

    public static Vector3f fixVector(Vector3f v) {
        if (TMath.isSimilar(v.x, 0.0f)) {
            v.x = 0.0f;
        }
        if (TMath.isSimilar(v.y, 0.0f)) {
            v.y = 0.0f;
        }
        if (TMath.isSimilar(v.z, 0.0f)) {
            v.z = 0.0f;
        }
        return v;
    }

    public static Vector3d fixVector(Vector3d v) {
        if (TMath.isSimilar(v.x, 0.0)) {
            v.x = 0.0;
        }
        if (TMath.isSimilar(v.y, 0.0)) {
            v.y = 0.0;
        }
        if (TMath.isSimilar(v.z, 0.0)) {
            v.z = 0.0;
        }
        return v;
    }

    public static Vector3d fixEuler(Vector3d v) {
        TMath.fixVector(v);
        v.x = (double)Math.round((double)(v.x * 10000.0)) * 1.0E-4;
        v.y = (double)Math.round((double)(v.y * 10000.0)) * 1.0E-4;
        v.z = (double)Math.round((double)(v.z * 10000.0)) * 1.0E-4;
        return v;
    }

    public static Quaternionf fixQuaternion(Quaternionf q) {
        if (TMath.isSimilar(q.x, 0.0f)) {
            q.x = 0.0f;
        }
        if (TMath.isSimilar(q.y, 0.0f)) {
            q.y = 0.0f;
        }
        if (TMath.isSimilar(q.z, 0.0f)) {
            q.z = 0.0f;
        }
        if (TMath.isSimilar(q.w, 0.0f)) {
            q.w = 0.0f;
        }
        return q;
    }

    public static Quaterniond fixQuaternion(Quaterniond q) {
        if (TMath.isSimilar(q.x, 0.0)) {
            q.x = 0.0;
        }
        if (TMath.isSimilar(q.y, 0.0)) {
            q.y = 0.0;
        }
        if (TMath.isSimilar(q.z, 0.0)) {
            q.z = 0.0;
        }
        if (TMath.isSimilar(q.w, 0.0)) {
            q.w = 0.0;
        }
        return q;
    }

    public static Vector3f wrap(Float[] vec) {
        return new Vector3f(vec[0].floatValue(), vec[1].floatValue(), vec[2].floatValue());
    }

    public static float[] unwrap(Vector3f vec) {
        return new float[]{vec.x, vec.y, vec.z};
    }

    public static float[] unwrap(Vector3d vec) {
        return new float[]{(float)vec.x, (float)vec.y, (float)vec.z};
    }

    public static Transformation createIdentityTransform() {
        return new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1.0f), new Quaternionf());
    }

    public static Vector3f transform(Matrix4f transform, Vector3f pos) {
        Vector4f vec = transform.transform(new Vector4f((Vector3fc)pos, 1.0f));
        return new Vector3f(vec.x, vec.y, vec.z);
    }

    public static void clampedScale(Matrix4f mat, Vector3f scale) {
        if ((double)LOWER_SCALE_LIMIT == 0.0) {
            mat.scale((Vector3fc)scale);
            return;
        }
        Vector3f clampedScale = new Vector3f(TMath.clampedScale(scale.x), TMath.clampedScale(scale.y), TMath.clampedScale(scale.z));
        Vector3f col0 = TMath.clampedScale(new Vector3f(mat.m00(), mat.m10(), mat.m20()), clampedScale);
        Vector3f col1 = TMath.clampedScale(new Vector3f(mat.m01(), mat.m11(), mat.m21()), clampedScale);
        Vector3f col2 = TMath.clampedScale(new Vector3f(mat.m02(), mat.m12(), mat.m22()), clampedScale);
        mat.set(col0.x, col1.x, col2.x, mat.m03() * scale.x, col0.y, col1.y, col2.y, mat.m13() * scale.y, col0.z, col1.z, col2.z, mat.m23() * scale.z, mat.m30(), mat.m31(), mat.m32(), mat.m33());
    }

    private static Vector3f clampedScale(Vector3f vec, Vector3f scale) {
        Vector3f tryScale = vec.mul((Vector3fc)scale, new Vector3f());
        if (tryScale.lengthSquared() > LOWER_SCALE_LIMIT * LOWER_SCALE_LIMIT) {
            return tryScale;
        }
        return new Vector3f(TMath.clampedScale(vec.x * scale.x), TMath.clampedScale(vec.y * scale.y), TMath.clampedScale(vec.z * scale.z));
    }

    private static float clampedScale(float val) {
        float sign = Math.signum((float)val);
        if (sign == 0.0f) {
            return LOWER_SCALE_LIMIT;
        }
        return sign * Math.max((float)Math.abs((float)val), (float)LOWER_SCALE_LIMIT);
    }

    public static Vector3f zerofyScale(Vector3f vec) {
        if ((double)LOWER_SCALE_LIMIT == 0.0) {
            return vec;
        }
        return new Vector3f(LOWER_SCALE_LIMIT >= Math.abs((float)vec.x) ? 0.0f : vec.x, LOWER_SCALE_LIMIT >= Math.abs((float)vec.y) ? 0.0f : vec.y, LOWER_SCALE_LIMIT >= Math.abs((float)vec.z) ? 0.0f : vec.z);
    }

    public static String toString(EulerAngle angle) {
        return String.format("[%s, %s, %s]", Math.toDegrees((double)angle.getX()), Math.toDegrees((double)angle.getY()), Math.toDegrees((double)angle.getZ()));
    }

    public static UUID parseUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        }
        catch (IllegalArgumentException e) {
            if (uuid.length() != 32) {
                throw e;
            }
            long first = Long.parseUnsignedLong(uuid.substring(0, 16), 16);
            long last = Long.parseUnsignedLong(uuid.substring(16), 16);
            return new UUID(first, last);
        }
    }

    static {
        ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> {
            LOWER_SCALE_LIMIT = (float)ConfigProperty.LOWER_SCALE_LIMIT.getDouble();
        });
    }

    public static enum SlerpMode {
        SLERP,
        ONLERP;


        public static SlerpMode get(String name) {
            try {
                return SlerpMode.valueOf(name);
            }
            catch (IllegalArgumentException ignored) {
                return SLERP;
            }
        }
    }
}

