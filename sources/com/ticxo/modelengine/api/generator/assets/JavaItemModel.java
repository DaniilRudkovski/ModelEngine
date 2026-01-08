/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.generator.assets;

import com.ticxo.modelengine.api.generator.assets.JavaDisplay;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;

public class JavaItemModel {
    private static final float DIST_DIVIDER = 0.041666668f;
    private final Map<String, String> textures = new HashMap<String, String>();
    private final List<JavaElement> elements = new ArrayList<JavaElement>();
    private transient String name;
    private transient float maxDistToOrigin = 0.0f;
    private Map<String, Map<String, float[]>> display = new HashMap<String, Map<String, float[]>>();

    public JavaItemModel() {
        this.setDisplay(JavaDisplay.GUI, JavaDisplay.Transform.ROTATION, 30.0f, 225.0f, 0.0f);
    }

    public void setDisplay(JavaDisplay display, JavaDisplay.Transform transform, float x, float y, float z) {
        float[] array = new float[]{x, y, z};
        if (transform.isDefault(array)) {
            return;
        }
        transform.sanitize(array);
        this.display.computeIfAbsent(display.toString(), s -> new HashMap()).put(transform.toString(), array);
    }

    public void addElement(JavaElement element) {
        this.elements.add(element);
        for (int i = 0; i < 3; ++i) {
            this.maxDistToOrigin = Math.max(Math.max(Math.abs(element.from[i] - 8.0f), Math.abs(element.to[i] - 8.0f)), this.maxDistToOrigin);
        }
    }

    public int scaleToFit() {
        if (this.maxDistToOrigin <= 24.0f) {
            return 1;
        }
        int size = (int)Math.ceil(this.maxDistToOrigin * 0.041666668f);
        float scale = 1.0f / (float)size;
        for (JavaElement element : this.elements) {
            float[] origin = element.getRotation() == null ? null : element.getRotation().origin;
            for (int i = 0; i < 3; ++i) {
                element.from[i] = TMath.clamp((element.from[i] - 8.0f) * scale + 8.0f, -16.0f, 32.0f);
                element.to[i] = TMath.clamp((element.to[i] - 8.0f) * scale + 8.0f, -16.0f, 32.0f);
                if (origin == null) continue;
                origin[i] = (origin[i] - 8.0f) * scale + 8.0f;
            }
        }
        return size;
    }

    public void finalizeModel() {
    }

    @Generated
    public Map<String, String> getTextures() {
        return this.textures;
    }

    @Generated
    public List<JavaElement> getElements() {
        return this.elements;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public float getMaxDistToOrigin() {
        return this.maxDistToOrigin;
    }

    @Generated
    public Map<String, Map<String, float[]>> getDisplay() {
        return this.display;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setMaxDistToOrigin(float maxDistToOrigin) {
        this.maxDistToOrigin = maxDistToOrigin;
    }

    @Generated
    public void setDisplay(Map<String, Map<String, float[]>> display) {
        this.display = display;
    }

    public static class JavaElement {
        private final float[] from = new float[3];
        private final float[] to = new float[3];
        private final Map<String, Face> faces = new HashMap<String, Face>();
        private Rotation rotation;

        public void from(float[] origin, float[] globalFrom, float inflate) {
            this.from[0] = globalFrom[0] - origin[0] + 8.0f - inflate;
            this.from[1] = globalFrom[1] - origin[1] + 8.0f - inflate;
            this.from[2] = globalFrom[2] - origin[2] + 8.0f - inflate;
        }

        public void from(float[] origin, float inflate) {
            this.from[0] = origin[0] + 8.0f - inflate;
            this.from[1] = origin[1] + 8.0f - inflate;
            this.from[2] = origin[2] + 8.0f - inflate;
        }

        public void to(float[] origin, float[] globalTo, float inflate) {
            this.to[0] = globalTo[0] - origin[0] + 8.0f + inflate;
            this.to[1] = globalTo[1] - origin[1] + 8.0f + inflate;
            this.to[2] = globalTo[2] - origin[2] + 8.0f + inflate;
        }

        public void to(float[] origin, float inflate) {
            this.to[0] = origin[0] + 8.0f + inflate;
            this.to[1] = origin[1] + 8.0f + inflate;
            this.to[2] = origin[2] + 8.0f + inflate;
        }

        @Generated
        public float[] getFrom() {
            return this.from;
        }

        @Generated
        public float[] getTo() {
            return this.to;
        }

        @Generated
        public Map<String, Face> getFaces() {
            return this.faces;
        }

        @Generated
        public Rotation getRotation() {
            return this.rotation;
        }

        @Generated
        public void setRotation(Rotation rotation) {
            this.rotation = rotation;
        }

        public static class Rotation {
            private final float[] origin = new float[]{8.0f, 8.0f, 8.0f};
            private float angle;
            private String axis = "x";

            public void origin(float[] origin, float[] globalOrigin) {
                this.origin[0] = globalOrigin[0] - origin[0] + 8.0f;
                this.origin[1] = globalOrigin[1] - origin[1] + 8.0f;
                this.origin[2] = globalOrigin[2] - origin[2] + 8.0f;
            }

            public void origin(float[] origin) {
                this.origin[0] = origin[0] + 8.0f;
                this.origin[1] = origin[1] + 8.0f;
                this.origin[2] = origin[2] + 8.0f;
            }

            @Generated
            public float[] getOrigin() {
                return this.origin;
            }

            @Generated
            public float getAngle() {
                return this.angle;
            }

            @Generated
            public String getAxis() {
                return this.axis;
            }

            @Generated
            public void setAngle(float angle) {
                this.angle = angle;
            }

            @Generated
            public void setAxis(String axis) {
                this.axis = axis;
            }
        }

        public static class Face {
            private final float[] uv = new float[4];
            private final int tintindex = 0;
            private int rotation;
            private String texture = "";

            public void uv(int width, int height, float[] uv) {
                float factorU = 16.0f / (float)width;
                float factorV = 16.0f / (float)height;
                this.uv[0] = uv[0] * factorU;
                this.uv[1] = uv[1] * factorV;
                this.uv[2] = uv[2] * factorU;
                this.uv[3] = uv[3] * factorV;
            }

            @Generated
            public float[] getUv() {
                return this.uv;
            }

            @Generated
            public int getTintindex() {
                return this.tintindex;
            }

            @Generated
            public int getRotation() {
                return this.rotation;
            }

            @Generated
            public String getTexture() {
                return this.texture;
            }

            @Generated
            public void setRotation(int rotation) {
                this.rotation = rotation;
            }

            @Generated
            public void setTexture(String texture) {
                this.texture = texture;
            }
        }
    }
}

