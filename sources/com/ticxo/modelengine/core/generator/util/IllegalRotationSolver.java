/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package com.ticxo.modelengine.core.generator.util;

import com.ticxo.modelengine.api.utils.data.Triple;
import com.ticxo.modelengine.api.utils.math.Axis;
import com.ticxo.modelengine.api.utils.math.Direction;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.generator.processed.ProcessedBone;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class IllegalRotationSolver {
    private static final List<Vector3d> AXES = List.of(Axis.X.getVector(), Axis.Y.getVector(), Axis.Z.getVector());

    public static ProcessedBone.Cube solve(ProcessedBone.Cube cube) {
        Quaterniond undoQuat;
        if (IllegalRotationSolver.isLegal(cube)) {
            return cube;
        }
        Quaterniond quaternion = cube.getQuaternion();
        HashMap<Direction, Triple<Vector3d, Vector3d, ProcessedBone.Face>> uvs = new HashMap<Direction, Triple<Vector3d, Vector3d, ProcessedBone.Face>>();
        for (Map.Entry<Direction, ProcessedBone.Face> entry : cube.getFaces().entrySet()) {
            Direction dir = entry.getKey();
            ProcessedBone.Face uv = entry.getValue();
            uvs.put(dir, new Triple<Vector3d, Vector3d, ProcessedBone.Face>(dir.getNormal().rotate((Quaterniondc)quaternion), dir.getUvUp().rotate((Quaterniondc)quaternion), uv));
        }
        List<Vector3d> corners = cube.getCorners();
        corners.forEach(vector3d -> vector3d.sub((Vector3dc)cube.getOrigin()).rotate((Quaterniondc)quaternion).add((Vector3dc)cube.getOrigin()));
        Vector3d srcDir = new Vector3d(0.0, 0.0, 1.0).rotate((Quaterniondc)quaternion);
        Vector3d dstAxis = IllegalRotationSolver.closestAxis(srcDir);
        double dot = srcDir.dot((Vector3dc)dstAxis);
        if (TMath.isSimilar(dot, 1.0)) {
            srcDir = new Vector3d(0.0, 1.0, 0.0).rotate((Quaterniondc)quaternion);
            dot = srcDir.dot((Vector3dc)(dstAxis = IllegalRotationSolver.closestAxis(srcDir)));
            if (TMath.isSimilar(dot, 1.0)) {
                undoQuat = new Quaterniond();
            } else {
                cross = srcDir.cross((Vector3dc)dstAxis, new Vector3d()).normalize();
                undoQuat = new Quaterniond().rotateAxis(Math.acos(dot), (Vector3dc)cross);
            }
        } else {
            cross = srcDir.cross((Vector3dc)dstAxis, new Vector3d()).normalize();
            undoQuat = new Quaterniond().rotateAxis(Math.acos(dot), (Vector3dc)cross);
        }
        HashMap<Direction, ProcessedBone.Face> newUVs = new HashMap<Direction, ProcessedBone.Face>();
        for (Map.Entry entry : uvs.entrySet()) {
            Triple triple = (Triple)entry.getValue();
            Vector3d currNormal = ((Vector3d)triple.getFirst()).rotate((Quaterniondc)undoQuat);
            Direction currDir = Direction.fromNormal(currNormal);
            if (currDir == null) continue;
            Vector3d currUVUp = currDir.getUvUp();
            Vector3d targetUVUp = ((Vector3d)triple.getSecond()).rotate((Quaterniondc)undoQuat);
            int angle = 0;
            double upDot = targetUVUp.dot((Vector3dc)currUVUp);
            if (TMath.isSimilar(upDot, -1.0)) {
                angle = 180;
            } else if (TMath.isSimilar(upDot, 0.0)) {
                Vector3d cross = targetUVUp.cross((Vector3dc)currUVUp, new Vector3d());
                angle = cross.dot((Vector3dc)currNormal) > 0.0 ? 90 : 270;
            }
            ProcessedBone.Face face = (ProcessedBone.Face)triple.getThird();
            ProcessedBone.UV uv = face.uv();
            ProcessedBone.Face tFace = new ProcessedBone.Face(new ProcessedBone.UV(uv.u1(), uv.v1(), uv.u2(), uv.v2(), (uv.rotation() + angle) % 360), face.texture());
            newUVs.put(currDir, tFace);
        }
        Vector3d from = new Vector3d(2.147483647E9);
        Vector3d to = new Vector3d(-2.147483648E9);
        corners.forEach(vector3d -> {
            Vector3d vec = vector3d.sub((Vector3dc)cube.getOrigin()).rotate((Quaterniondc)undoQuat).add((Vector3dc)cube.getOrigin());
            from.set(Math.min(from.x, vec.x), Math.min(from.y, vec.y), Math.min(from.z, vec.z));
            to.set(Math.max(to.x, vec.x), Math.max(to.y, vec.y), Math.max(to.z, vec.z));
        });
        Vector3d rotation = TMath.fixEuler(TMath.toEulerZYX(undoQuat.invert()));
        return new ProcessedBone.Cube(cube.getName(), cube.getOrigin(), rotation, from, to, newUVs, cube.getInflate());
    }

    private static Vector3d closestAxis(Vector3d dir) {
        if (TMath.isSimilar(dir.lengthSquared(), 0.0)) {
            return new Vector3d((Vector3dc)AXES.get(2));
        }
        Vector3d closest = null;
        double dot = 0.0;
        for (Vector3d axis : AXES) {
            double d = Math.abs(dir.dot((Vector3dc)axis));
            if (!(d > dot)) continue;
            dot = d;
            closest = axis;
        }
        if (closest == null) {
            return new Vector3d((Vector3dc)AXES.get(2));
        }
        return closest.dot((Vector3dc)dir) > 0.0 ? new Vector3d(closest) : closest.negate(new Vector3d());
    }

    private static boolean isLegal(ProcessedBone.Cube cube) {
        Vector3d rot = cube.getRotation();
        return TMath.isAlmostBetween(rot.x, -45.0, 45.0) && TMath.isInterval(rot.x, 22.5) && TMath.isSimilar(rot.y, 0.0) && TMath.isSimilar(rot.z, 0.0) || TMath.isAlmostBetween(rot.y, -45.0, 45.0) && TMath.isInterval(rot.y, 22.5) && TMath.isSimilar(rot.x, 0.0) && TMath.isSimilar(rot.z, 0.0) || TMath.isAlmostBetween(rot.z, -45.0, 45.0) && TMath.isInterval(rot.z, 22.5) && TMath.isSimilar(rot.x, 0.0) && TMath.isSimilar(rot.y, 0.0);
    }
}

