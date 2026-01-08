/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.generator.processed;

import com.ticxo.modelengine.api.generator.assets.JavaItemModel;
import com.ticxo.modelengine.api.generator.assets.ModelAssets;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.utils.math.Direction;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.generator.util.ItemGroup;
import com.ticxo.modelengine.core.generator.util.RotationSolver;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

public class ProcessedBone {
    private final String name;
    private final Vector3f boneOrigin;
    private final Vector3f rotation;
    private final Set<Cube> cubes = new LinkedHashSet<Cube>();
    private final Set<ItemGroup> groups = new LinkedHashSet<ItemGroup>();
    private final Set<JavaItemModel> models = new LinkedHashSet<JavaItemModel>();
    private int scale;

    public void splitModels(BlockbenchModel model, ModelAssets assets) {
        RotationSolver.solve(this.groups, this.cubes);
        float maxDistToOrigin = 0.0f;
        for (ItemGroup group : this.groups) {
            JavaItemModel jiModel = group.toJavaItemModel(this.name, model, assets);
            maxDistToOrigin = Math.max(jiModel.getMaxDistToOrigin(), maxDistToOrigin);
            this.models.add(jiModel);
        }
        for (JavaItemModel m : this.models) {
            m.setMaxDistToOrigin(maxDistToOrigin);
            this.scale = m.scaleToFit();
        }
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public Vector3f getBoneOrigin() {
        return this.boneOrigin;
    }

    @Generated
    public Vector3f getRotation() {
        return this.rotation;
    }

    @Generated
    public Set<Cube> getCubes() {
        return this.cubes;
    }

    @Generated
    public Set<ItemGroup> getGroups() {
        return this.groups;
    }

    @Generated
    public Set<JavaItemModel> getModels() {
        return this.models;
    }

    @Generated
    public int getScale() {
        return this.scale;
    }

    @Generated
    public ProcessedBone(String name, Vector3f boneOrigin, Vector3f rotation) {
        this.name = name;
        this.boneOrigin = boneOrigin;
        this.rotation = rotation;
    }

    public record UV(float u1, float v1, float u2, float v2, int rotation) {
    }

    public record Face(UV uv, int texture) {
        public boolean isEmpty() {
            return TMath.isSimilar(this.uv.u1(), this.uv.u2()) || TMath.isSimilar(this.uv.v1, this.uv.v2);
        }
    }

    public static class Cube {
        private final String name;
        private final Vector3d origin;
        private final Vector3d rotation;
        private final Quaterniond quaternion;
        private final Vector3d from;
        private final Vector3d to;
        private final Map<Direction, Face> faces;
        private final float inflate;

        public Cube(String name, Vector3d origin, Vector3d rotation, Vector3d from, Vector3d to, Map<Direction, Face> faces, float inflate) {
            this.name = name;
            this.origin = new Vector3d((Vector3dc)origin);
            this.rotation = new Vector3d((Vector3dc)rotation);
            this.quaternion = TMath.fromEulerZYX(rotation);
            this.from = new Vector3d((Vector3dc)from);
            this.to = new Vector3d((Vector3dc)to);
            this.faces = faces;
            this.inflate = inflate;
        }

        public String toString() {
            return this.name + ": " + this.rotation.toString(NumberFormat.getInstance());
        }

        public List<Vector3d> getCorners() {
            return new ArrayList<Vector3d>(){
                {
                    this.add(new Vector3d(from.x, from.y, from.z));
                    this.add(new Vector3d(from.x, from.y, to.z));
                    this.add(new Vector3d(from.x, to.y, from.z));
                    this.add(new Vector3d(from.x, to.y, to.z));
                    this.add(new Vector3d(to.x, from.y, from.z));
                    this.add(new Vector3d(to.x, from.y, to.z));
                    this.add(new Vector3d(to.x, to.y, from.z));
                    this.add(new Vector3d(to.x, to.y, to.z));
                }
            };
        }

        public void rotate(Quaterniond rot) {
            this.from.sub((Vector3dc)this.origin);
            this.to.sub((Vector3dc)this.origin);
            this.origin.rotate((Quaterniondc)rot);
            this.quaternion.premul((Quaterniondc)rot);
            this.rotation.set((Vector3dc)TMath.fixEuler(TMath.toEulerZYX(this.quaternion)));
            this.from.add((Vector3dc)this.origin);
            this.to.add((Vector3dc)this.origin);
        }

        @Nullable
        public JavaItemModel.JavaElement.Rotation rotation() {
            float[] rotation;
            int zeros = 0;
            float[] origin = TMath.unwrap(this.origin);
            for (float angle : rotation = TMath.unwrap(this.rotation)) {
                zeros += angle == 0.0f ? 1 : 0;
            }
            if (zeros == 3) {
                return null;
            }
            JavaItemModel.JavaElement.Rotation javaRotation = new JavaItemModel.JavaElement.Rotation();
            int i = TMath.absMax(rotation[0], rotation[1], rotation[2]);
            javaRotation.setAxis(switch (i) {
                case 1 -> "y";
                case 2 -> "z";
                default -> "x";
            });
            float angle = (float)Math.round(rotation[i] / 22.5f) * 22.5f;
            javaRotation.setAngle(angle);
            javaRotation.origin(origin);
            return javaRotation;
        }

        @Generated
        public String getName() {
            return this.name;
        }

        @Generated
        public Vector3d getOrigin() {
            return this.origin;
        }

        @Generated
        public Vector3d getRotation() {
            return this.rotation;
        }

        @Generated
        public Quaterniond getQuaternion() {
            return this.quaternion;
        }

        @Generated
        public Vector3d getFrom() {
            return this.from;
        }

        @Generated
        public Vector3d getTo() {
            return this.to;
        }

        @Generated
        public Map<Direction, Face> getFaces() {
            return this.faces;
        }

        @Generated
        public float getInflate() {
            return this.inflate;
        }
    }
}

