/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Display$Billboard
 *  org.bukkit.entity.TextDisplay$TextAlignment
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.renderer.NameTagRenderer;
import com.ticxo.modelengine.api.model.bone.type.NameTag;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.model.bone.render.AbstractBehaviorRenderer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector3f;

public class NameTagRendererImpl
extends AbstractBehaviorRenderer
implements NameTagRenderer {
    private final Map<String, NameTagRenderer.NameTag> spawnQueue = new HashMap<String, NameTagRenderer.NameTag>();
    private final Map<String, NameTagRenderer.NameTag> rendered = new HashMap<String, NameTagRenderer.NameTag>();
    private final Map<String, NameTagRenderer.NameTag> destroyQueue = new HashMap<String, NameTagRenderer.NameTag>();
    private boolean initialized;

    public NameTagRendererImpl(ActiveModel activeModel) {
        super(activeModel);
    }

    @Override
    public void initialize() {
        for (Map.Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
            String boneId = boneEntry.getKey();
            ModelBone modelBone = boneEntry.getValue();
            this.create(boneId, modelBone);
        }
        this.initialized = true;
    }

    private void create(String boneId, ModelBone modelBone) {
        Optional<? extends NameTag> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG);
        if (maybeData.isEmpty()) {
            return;
        }
        BoneBehavior nameTagData = (BoneBehavior)((Object)maybeData.get());
        NameTagImpl nameTag = new NameTagImpl(this.nmsHandler.getEntityHandler().getNextEntityId(), UUID.randomUUID(), this.nmsHandler.getEntityHandler().getNextEntityId(), UUID.randomUUID());
        nameTag.position.set(((NameTag)((Object)nameTagData)).getLocation());
        nameTag.jsonString.set(((NameTag)((Object)nameTagData)).getJsonString());
        nameTag.visibility.set(((NameTag)((Object)nameTagData)).isVisible());
        nameTag.backgroundColor.set(((NameTag)((Object)nameTagData)).isUseDefaultBackgroundColor() ? 0x40000000 : ((NameTag)((Object)nameTagData)).getBackgroundColor());
        nameTag.billboard.set(((NameTag)((Object)nameTagData)).getBillboard());
        nameTag.textOpacity.set(((NameTag)((Object)nameTagData)).getTextOpacity());
        nameTag.lineWidth.set(((NameTag)((Object)nameTagData)).getLineWidth());
        nameTag.scale.set(((NameTag)((Object)nameTagData)).getScale());
        nameTag.getStyle().set(this.getStyle((NameTag)((Object)nameTagData)));
        this.spawnQueue.put(boneId, nameTag);
        this.destroyQueue.remove(boneId);
    }

    @Override
    public void readBoneData() {
        if (!this.initialized) {
            return;
        }
        this.destroyQueue.putAll(this.rendered);
        for (Map.Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
            String boneId = boneEntry.getKey();
            ModelBone modelBone = boneEntry.getValue();
            NameTagRenderer.NameTag renderer = (NameTagRenderer.NameTag)this.getQueued(boneId);
            if (renderer != null) {
                this.read(boneId, renderer, modelBone);
                continue;
            }
            this.create(boneId, modelBone);
        }
    }

    private void read(String boneId, NameTagRenderer.NameTag nameTag, ModelBone modelBone) {
        Optional<? extends NameTag> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG);
        maybeData.ifPresent(nameTagData -> {
            nameTag.getPosition().set(((NameTag)((Object)nameTagData)).getLocation());
            nameTag.getJsonString().set(((NameTag)((Object)nameTagData)).getJsonString());
            nameTag.getVisibility().set(((NameTag)((Object)nameTagData)).isVisible());
            nameTag.getBillboard().set(((NameTag)((Object)nameTagData)).getBillboard());
            nameTag.getBackgroundColor().set(((NameTag)((Object)nameTagData)).isUseDefaultBackgroundColor() ? 0x40000000 : ((NameTag)((Object)nameTagData)).getBackgroundColor());
            nameTag.getTextOpacity().set(((NameTag)((Object)nameTagData)).getTextOpacity());
            nameTag.getLineWidth().set(((NameTag)((Object)nameTagData)).getLineWidth());
            nameTag.getScale().set(((NameTag)((Object)nameTagData)).getScale());
            nameTag.getStyle().set(this.getStyle((NameTag)((Object)nameTagData)));
            this.destroyQueue.remove(boneId);
        });
    }

    private byte getStyle(NameTag nameTagData) {
        byte style = 0;
        style = TMath.setBit(style, 0, nameTagData.isShadow());
        style = TMath.setBit(style, 1, nameTagData.isSeeThrough());
        style = TMath.setBit(style, 2, nameTagData.isUseDefaultBackgroundColor());
        style = switch (nameTagData.getAlignment()) {
            default -> throw new IncompatibleClassChangeError();
            case TextDisplay.TextAlignment.CENTER -> style;
            case TextDisplay.TextAlignment.LEFT -> (byte)(style | 8);
            case TextDisplay.TextAlignment.RIGHT -> (byte)(style | 0x10);
        };
        return style;
    }

    @Override
    public void sendToClient(RenderParsers parsers) {
        if (!this.initialized) {
            return;
        }
        this.destroyQueue.keySet().forEach(this.rendered::remove);
        parsers.getBehaviorParser(this).sendToClients(this);
        this.rendered.putAll(this.spawnQueue);
        this.spawnQueue.clear();
        this.destroyQueue.clear();
    }

    @Override
    public void destroy(RenderParsers parsers) {
        if (!this.initialized) {
            return;
        }
        parsers.getBehaviorParser(this).destroy(this);
    }

    @Override
    @Generated
    public Map<String, NameTagRenderer.NameTag> getSpawnQueue() {
        return this.spawnQueue;
    }

    @Override
    @Generated
    public Map<String, NameTagRenderer.NameTag> getRendered() {
        return this.rendered;
    }

    @Override
    @Generated
    public Map<String, NameTagRenderer.NameTag> getDestroyQueue() {
        return this.destroyQueue;
    }

    public static class NameTagImpl
    implements NameTagRenderer.NameTag {
        private final int pivotId;
        private final UUID pivotUuid;
        private final int tagId;
        private final UUID tagUuid;
        private final DataTracker<Vector3f> position = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<String> jsonString = new DataTracker();
        private final DataTracker<Boolean> visibility = new DataTracker<Boolean>(true);
        private final DataTracker<Integer> backgroundColor = new DataTracker<Integer>(0x40000000);
        private final DataTracker<Display.Billboard> billboard = new DataTracker<Display.Billboard>(Display.Billboard.CENTER);
        private final DataTracker<Byte> textOpacity = new DataTracker<Byte>((byte)0);
        private final DataTracker<Integer> lineWidth = new DataTracker<Integer>(0);
        private final DataTracker<Vector3f> scale = new DataTracker<Vector3f>(new Vector3f(1.0f));
        private final DataTracker<Byte> style = new DataTracker<Byte>((byte)0);

        @Generated
        public NameTagImpl(int pivotId, UUID pivotUuid, int tagId, UUID tagUuid) {
            this.pivotId = pivotId;
            this.pivotUuid = pivotUuid;
            this.tagId = tagId;
            this.tagUuid = tagUuid;
        }

        @Override
        @Generated
        public int getPivotId() {
            return this.pivotId;
        }

        @Override
        @Generated
        public UUID getPivotUuid() {
            return this.pivotUuid;
        }

        @Override
        @Generated
        public int getTagId() {
            return this.tagId;
        }

        @Override
        @Generated
        public UUID getTagUuid() {
            return this.tagUuid;
        }

        @Override
        @Generated
        public DataTracker<Vector3f> getPosition() {
            return this.position;
        }

        @Override
        @Generated
        public DataTracker<String> getJsonString() {
            return this.jsonString;
        }

        @Override
        @Generated
        public DataTracker<Boolean> getVisibility() {
            return this.visibility;
        }

        @Override
        @Generated
        public DataTracker<Integer> getBackgroundColor() {
            return this.backgroundColor;
        }

        @Override
        @Generated
        public DataTracker<Display.Billboard> getBillboard() {
            return this.billboard;
        }

        @Override
        @Generated
        public DataTracker<Byte> getTextOpacity() {
            return this.textOpacity;
        }

        @Override
        @Generated
        public DataTracker<Integer> getLineWidth() {
            return this.lineWidth;
        }

        @Override
        @Generated
        public DataTracker<Vector3f> getScale() {
            return this.scale;
        }

        @Override
        @Generated
        public DataTracker<Byte> getStyle() {
            return this.style;
        }
    }
}

