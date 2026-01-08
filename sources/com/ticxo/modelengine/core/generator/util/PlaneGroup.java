/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 */
package com.ticxo.modelengine.core.generator.util;

import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.generator.processed.ProcessedBone;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.text.NumberFormat;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public record PlaneGroup(Vector3d axis, int modAngle, Quaterniond origin, Quaterniond invOrigin, IntSet cubes) {
    @Override
    public String toString() {
        return "PlaneGroup{axis=" + this.axis + ", modAngle=" + this.modAngle + ", origin=" + TMath.toEulerZYX(this.origin).toString(NumberFormat.getInstance()) + ", invOrigin=" + TMath.toEulerZYX(this.invOrigin).toString(NumberFormat.getInstance()) + ", cubes=" + this.cubes + "}";
    }

    public String toString(Int2ObjectMap<ProcessedBone.Cube> map) {
        return "PlaneGroup{axis=" + this.axis + ", modAngle=" + this.modAngle + ", origin=" + TMath.toEulerZYX(this.origin).toString(NumberFormat.getInstance()) + ", invOrigin=" + TMath.toEulerZYX(this.invOrigin).toString(NumberFormat.getInstance()) + ", cubes=" + map.int2ObjectEntrySet().stream().filter(cubeEntry -> this.cubes.contains(cubeEntry.getIntKey())).toList() + "}";
    }
}

