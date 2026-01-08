/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.generator.parser.blockbench;

import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockbenchModel {
    protected Resolution resolution;
    protected Map<UUID, Element> elements;
    protected Map<UUID, Group> outliner;
    protected Map<Integer, Texture> textures;
    protected Map<String, Animation> animations;
    protected final transient Map<String, Group> flatOutlinerByName = new HashMap<String, Group>();
    protected final transient Map<UUID, Group> flatOutlinerByUUID = new HashMap<UUID, Group>();
    protected String animation_variable_placeholders;

    public BlockbenchModel preprocess() {
        LinkedList<Group> queue = new LinkedList<Group>(this.outliner.values());
        while (!queue.isEmpty()) {
            Group group = queue.poll();
            this.flatOutlinerByName.put(group.name, group);
            this.flatOutlinerByUUID.put(group.uuid, group);
            queue.addAll(group.getChildGroup().values());
        }
        return this;
    }

    public Group getGroup(@NotNull String group) {
        return this.flatOutlinerByName.get(group);
    }

    public Group getGroup(@NotNull UUID group) {
        return this.flatOutlinerByUUID.get(group);
    }

    public Group removeGroup(@NotNull String group) {
        return this.getGroup(group);
    }

    public Group removeGroup(@NotNull UUID group) {
        return this.getGroup(group);
    }

    public Group removeGroup(@Nullable Group group) {
        if (group == null) {
            return null;
        }
        this.flatOutlinerByName.remove(group.name);
        this.flatOutlinerByUUID.remove(group.uuid);
        if (group.parentGroup != null) {
            this.flatOutlinerByUUID.get((Object)group.parentGroup).childGroup.remove(group.uuid);
        } else {
            this.outliner.remove(group.uuid);
        }
        return group;
    }

    @Generated
    public BlockbenchModel(Resolution resolution, Map<UUID, Element> elements, Map<UUID, Group> outliner, Map<Integer, Texture> textures, Map<String, Animation> animations, String animation_variable_placeholders) {
        this.resolution = resolution;
        this.elements = elements;
        this.outliner = outliner;
        this.textures = textures;
        this.animations = animations;
        this.animation_variable_placeholders = animation_variable_placeholders;
    }

    @Generated
    public Resolution getResolution() {
        return this.resolution;
    }

    @Generated
    public Map<UUID, Element> getElements() {
        return this.elements;
    }

    @Generated
    public Map<UUID, Group> getOutliner() {
        return this.outliner;
    }

    @Generated
    public Map<Integer, Texture> getTextures() {
        return this.textures;
    }

    @Generated
    public Map<String, Animation> getAnimations() {
        return this.animations;
    }

    @Generated
    public Map<String, Group> getFlatOutlinerByName() {
        return this.flatOutlinerByName;
    }

    @Generated
    public Map<UUID, Group> getFlatOutlinerByUUID() {
        return this.flatOutlinerByUUID;
    }

    @Generated
    public String getAnimation_variable_placeholders() {
        return this.animation_variable_placeholders;
    }

    public static class Group {
        protected String name;
        protected Float[] origin;
        protected Float[] rotation;
        protected UUID uuid;
        protected UUID parentGroup;
        protected boolean export;
        protected final Set<UUID> element = new HashSet<UUID>();
        protected final Map<UUID, Group> childGroup = new HashMap<UUID, Group>();

        @Generated
        public String getName() {
            return this.name;
        }

        @Generated
        public Float[] getOrigin() {
            return this.origin;
        }

        @Generated
        public Float[] getRotation() {
            return this.rotation;
        }

        @Generated
        public UUID getUuid() {
            return this.uuid;
        }

        @Generated
        public UUID getParentGroup() {
            return this.parentGroup;
        }

        @Generated
        public boolean isExport() {
            return this.export;
        }

        @Generated
        public Set<UUID> getElement() {
            return this.element;
        }

        @Generated
        public Map<UUID, Group> getChildGroup() {
            return this.childGroup;
        }
    }

    public static class Resolution {
        protected Integer width;
        protected Integer height;

        @Generated
        public Integer getWidth() {
            return this.width;
        }

        @Generated
        public Integer getHeight() {
            return this.height;
        }
    }

    public static class Keyframe {
        protected String channel;
        protected List<Map<String, String>> data_points = new ArrayList<Map<String, String>>();
        protected Float time;
        protected String interpolation;
        protected Float[] bezier_left_time;
        protected Float[] bezier_left_value;
        protected Float[] bezier_right_time;
        protected Float[] bezier_right_value;

        @Generated
        public String getChannel() {
            return this.channel;
        }

        @Generated
        public List<Map<String, String>> getData_points() {
            return this.data_points;
        }

        @Generated
        public Float getTime() {
            return this.time;
        }

        @Generated
        public String getInterpolation() {
            return this.interpolation;
        }

        @Generated
        public Float[] getBezier_left_time() {
            return this.bezier_left_time;
        }

        @Generated
        public Float[] getBezier_left_value() {
            return this.bezier_left_value;
        }

        @Generated
        public Float[] getBezier_right_time() {
            return this.bezier_right_time;
        }

        @Generated
        public Float[] getBezier_right_value() {
            return this.bezier_right_value;
        }
    }

    public static class Animator {
        protected String name;
        @Nullable
        protected UUID uuid;
        protected Boolean rotation_global;
        protected Map<String, Map<Float, Keyframe>> channels = new HashMap<String, Map<Float, Keyframe>>();

        @Generated
        public String getName() {
            return this.name;
        }

        @Nullable
        @Generated
        public UUID getUuid() {
            return this.uuid;
        }

        @Generated
        public Boolean getRotation_global() {
            return this.rotation_global;
        }

        @Generated
        public Map<String, Map<Float, Keyframe>> getChannels() {
            return this.channels;
        }
    }

    public static class Animation {
        protected UUID uuid;
        protected String name;
        protected String loop;
        protected Boolean override;
        protected Float length;
        protected Animator effects;
        protected Map<UUID, Animator> animators = new HashMap<UUID, Animator>();
        protected Map<String, Animator> unknown_animators;

        @Generated
        public UUID getUuid() {
            return this.uuid;
        }

        @Generated
        public String getName() {
            return this.name;
        }

        @Generated
        public String getLoop() {
            return this.loop;
        }

        @Generated
        public Boolean getOverride() {
            return this.override;
        }

        @Generated
        public Float getLength() {
            return this.length;
        }

        @Generated
        public Animator getEffects() {
            return this.effects;
        }

        @Generated
        public Map<UUID, Animator> getAnimators() {
            return this.animators;
        }

        @Generated
        public Map<String, Animator> getUnknown_animators() {
            return this.unknown_animators;
        }
    }

    public static class Texture {
        protected String name;
        protected String folder;
        protected String namespace;
        protected String id;
        protected Integer width;
        protected Integer height;
        protected Integer uv_width;
        protected Integer uv_height;
        protected Integer frame_time;
        protected String frame_order;
        protected Boolean frame_interpolate;
        protected UUID uuid;
        protected String raw_mcmeta;
        protected String source;

        @Generated
        public String getName() {
            return this.name;
        }

        @Generated
        public String getFolder() {
            return this.folder;
        }

        @Generated
        public String getNamespace() {
            return this.namespace;
        }

        @Generated
        public String getId() {
            return this.id;
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
        public Integer getUv_width() {
            return this.uv_width;
        }

        @Generated
        public Integer getUv_height() {
            return this.uv_height;
        }

        @Generated
        public Integer getFrame_time() {
            return this.frame_time;
        }

        @Generated
        public String getFrame_order() {
            return this.frame_order;
        }

        @Generated
        public Boolean getFrame_interpolate() {
            return this.frame_interpolate;
        }

        @Generated
        public UUID getUuid() {
            return this.uuid;
        }

        @Generated
        public String getRaw_mcmeta() {
            return this.raw_mcmeta;
        }

        @Generated
        public String getSource() {
            return this.source;
        }
    }

    public static class Face {
        protected Float[] uv;
        protected Integer rotation;
        protected Integer texture;

        public boolean isEmpty() {
            for (int i = 0; i < this.uv.length; ++i) {
                if (this.uv[i] != null) continue;
                this.uv[i] = Float.valueOf(0.0f);
            }
            return this.texture == null || TMath.isSimilar(this.uv[0].floatValue(), this.uv[2].floatValue()) || TMath.isSimilar(this.uv[1].floatValue(), this.uv[3].floatValue());
        }

        @Generated
        public Float[] getUv() {
            return this.uv;
        }

        @Generated
        public Integer getRotation() {
            return this.rotation;
        }

        @Generated
        public Integer getTexture() {
            return this.texture;
        }
    }

    public static class Unknown
    extends Element {
    }

    public static class Camera
    extends Element
    implements AnimatableElement {
        protected Float[] position;
        protected Float[] rotation;

        @Override
        public Float[] getOrigin() {
            return this.position;
        }

        @Override
        @Generated
        public Float[] getPosition() {
            return this.position;
        }

        @Override
        @Generated
        public Float[] getRotation() {
            return this.rotation;
        }
    }

    public static class Locator
    extends Element {
        protected Float[] position;
        protected Float[] rotation;
        protected Boolean ignore_inherited_scale;

        @Generated
        public Float[] getPosition() {
            return this.position;
        }

        @Generated
        public Float[] getRotation() {
            return this.rotation;
        }

        @Generated
        public Boolean getIgnore_inherited_scale() {
            return this.ignore_inherited_scale;
        }
    }

    public static class NullObject
    extends Element
    implements AnimatableElement {
        protected Float[] position;
        protected String ik_target;
        protected String ik_source;
        protected Boolean lock_ik_target_rotation;

        @Override
        public Float[] getOrigin() {
            return this.position;
        }

        @Override
        @Nullable
        public Float[] getRotation() {
            return null;
        }

        @Override
        @Generated
        public Float[] getPosition() {
            return this.position;
        }

        @Generated
        public String getIk_target() {
            return this.ik_target;
        }

        @Generated
        public String getIk_source() {
            return this.ik_source;
        }

        @Generated
        public Boolean getLock_ik_target_rotation() {
            return this.lock_ik_target_rotation;
        }
    }

    public static class Cube
    extends Element {
        protected String render_order;
        protected Float[] from;
        protected Float[] to;
        protected Float inflate = Float.valueOf(0.0f);
        protected Float[] rotation;
        protected Float[] origin;
        protected Map<String, Face> faces;

        public float width() {
            return Math.abs(this.to[0].floatValue() - this.from[0].floatValue() + this.inflate.floatValue() * 2.0f);
        }

        public float height() {
            return Math.abs(this.to[1].floatValue() - this.from[1].floatValue() + this.inflate.floatValue() * 2.0f);
        }

        public float depth() {
            return Math.abs(this.to[2].floatValue() - this.from[2].floatValue() + this.inflate.floatValue() * 2.0f);
        }

        public float centerX() {
            return TMath.lerp(this.from[0].floatValue(), this.to[0].floatValue(), 0.5f);
        }

        public float centerY() {
            return TMath.lerp(this.from[1].floatValue(), this.to[1].floatValue(), 0.5f);
        }

        public float centerZ() {
            return TMath.lerp(this.from[2].floatValue(), this.to[2].floatValue(), 0.5f);
        }

        public float minX() {
            return Math.min(this.from[0].floatValue(), this.to[0].floatValue());
        }

        public float minY() {
            return Math.min(this.from[1].floatValue(), this.to[1].floatValue());
        }

        public float minZ() {
            return Math.min(this.from[2].floatValue(), this.to[2].floatValue());
        }

        public float maxX() {
            return Math.max(this.from[0].floatValue(), this.to[0].floatValue());
        }

        public float maxY() {
            return Math.max(this.from[1].floatValue(), this.to[1].floatValue());
        }

        public float maxZ() {
            return Math.max(this.from[2].floatValue(), this.to[2].floatValue());
        }

        public boolean isTranslucent() {
            return "in_front".equals(this.render_order);
        }

        @Generated
        public String getRender_order() {
            return this.render_order;
        }

        @Generated
        public Float[] getFrom() {
            return this.from;
        }

        @Generated
        public Float[] getTo() {
            return this.to;
        }

        @Generated
        public Float getInflate() {
            return this.inflate;
        }

        @Generated
        public Float[] getRotation() {
            return this.rotation;
        }

        @Generated
        public Float[] getOrigin() {
            return this.origin;
        }

        @Generated
        public Map<String, Face> getFaces() {
            return this.faces;
        }
    }

    public static interface AnimatableElement {
        public String getName();

        public UUID getUuid();

        public Float[] getOrigin();

        public Float[] getPosition();

        @Nullable
        public Float[] getRotation();
    }

    public static class Element {
        protected String name;
        protected UUID uuid;
        protected String type;

        public Element() {
        }

        @Generated
        public Element(String name, UUID uuid, String type) {
            this.name = name;
            this.uuid = uuid;
            this.type = type;
        }

        @Generated
        public String getName() {
            return this.name;
        }

        @Generated
        public UUID getUuid() {
            return this.uuid;
        }

        @Generated
        public String getType() {
            return this.type;
        }
    }
}

