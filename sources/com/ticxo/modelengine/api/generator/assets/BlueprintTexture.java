/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.generator.assets;

import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class BlueprintTexture {
    private int id;
    private int frameWidth;
    private int frameHeight;
    private ResourceLocation path;
    private MCMeta mcMeta;
    private String source;

    @Generated
    public int getId() {
        return this.id;
    }

    @Generated
    public int getFrameWidth() {
        return this.frameWidth;
    }

    @Generated
    public int getFrameHeight() {
        return this.frameHeight;
    }

    @Generated
    public ResourceLocation getPath() {
        return this.path;
    }

    @Generated
    public MCMeta getMcMeta() {
        return this.mcMeta;
    }

    @Generated
    public String getSource() {
        return this.source;
    }

    @Generated
    public void setId(int id) {
        this.id = id;
    }

    @Generated
    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    @Generated
    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    @Generated
    public void setPath(ResourceLocation path) {
        this.path = path;
    }

    @Generated
    public void setMcMeta(MCMeta mcMeta) {
        this.mcMeta = mcMeta;
    }

    @Generated
    public void setSource(String source) {
        this.source = source;
    }

    public static class MCMeta {
        private transient boolean mustGenerate;
        private Boolean interpolate;
        private Integer width;
        private Integer height;
        private Integer frametime;
        private List<Object> frames;

        public void addFrame(int frame) {
            if (this.frames == null) {
                this.frames = new ArrayList<Object>();
            }
            this.frames.add(frame);
        }

        public void addFrame(int index, int time) {
            if (this.frames == null) {
                this.frames = new ArrayList<Object>();
            }
            this.frames.add(new Frame(index, time));
        }

        @Generated
        public boolean isMustGenerate() {
            return this.mustGenerate;
        }

        @Generated
        public Boolean getInterpolate() {
            return this.interpolate;
        }

        @Generated
        public Integer getWidth() {
            return this.width;
        }

        @Generated
        public Integer getHeight() {
            return this.height;
        }

        @Generated
        public Integer getFrametime() {
            return this.frametime;
        }

        @Generated
        public List<Object> getFrames() {
            return this.frames;
        }

        @Generated
        public void setMustGenerate(boolean mustGenerate) {
            this.mustGenerate = mustGenerate;
        }

        @Generated
        public void setInterpolate(Boolean interpolate) {
            this.interpolate = interpolate;
        }

        @Generated
        public void setWidth(Integer width) {
            this.width = width;
        }

        @Generated
        public void setHeight(Integer height) {
            this.height = height;
        }

        @Generated
        public void setFrametime(Integer frametime) {
            this.frametime = frametime;
        }

        public record Frame(int index, int time) {
        }
    }
}

