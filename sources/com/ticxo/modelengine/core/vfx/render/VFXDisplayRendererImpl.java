/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.vfx.render;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.api.vfx.render.VFXDisplayRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRendererParser;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class VFXDisplayRendererImpl
implements VFXDisplayRenderer {
    private final VFX vfx;
    private final EntityHandler entityHandler;
    private final VFXRendererParser<VFXDisplayRenderer> parser;
    private VFXModelImpl vfxModel;
    private boolean respawnRequired;
    private boolean initialized;

    public VFXDisplayRendererImpl(VFX vfx) {
        this.vfx = vfx;
        this.entityHandler = ModelEngineAPI.getEntityHandler();
        this.parser = ModelEngineAPI.getNMSHandler().getVFXRendererParser(this);
    }

    @Override
    public VFX getVFX() {
        return this.vfx;
    }

    @Override
    public void initialize() {
        this.vfxModel = new VFXModelImpl(this.entityHandler.getNextEntityId(), UUID.randomUUID(), this.entityHandler.getNextEntityId(), UUID.randomUUID());
        this.vfxModel.getOrigin().set(this.vfx.getOrigin().toVector3f());
        this.vfxModel.getPosition().set(this.calculatePosition());
        this.vfxModel.getLeftRotation().set(this.calculateRotation());
        this.vfxModel.getScale().set(this.vfx.getScale());
        this.vfxModel.getModel().set(this.vfx.isVisible() ? this.vfx.getModel() : null);
        this.initialized = true;
        this.respawnRequired = true;
    }

    @Override
    public void readVFXData() {
        if (!this.initialized) {
            return;
        }
        this.vfxModel.getOrigin().set(this.vfx.getOrigin().toVector3f());
        this.vfxModel.getPosition().set(this.calculatePosition());
        this.vfxModel.getLeftRotation().set(this.calculateRotation());
        this.vfxModel.getScale().set(this.vfx.getScale());
        if (!this.vfx.isVisible()) {
            this.vfxModel.getModel().set(null);
        } else {
            this.vfxModel.getModel().set(this.vfx.getModel());
            if (this.vfx.getModelTracker().isDirty()) {
                this.vfx.getModelTracker().clearDirty();
                this.vfxModel.getModel().markDirty();
            }
        }
    }

    @Override
    public void sendToClient() {
        if (!this.initialized) {
            return;
        }
        this.parser.sendToClients(this);
    }

    @Override
    public void destroy() {
        if (!this.initialized) {
            return;
        }
        this.parser.destroy(this);
    }

    @Override
    public VFXDisplayRenderer.VFXModel getVFXModel() {
        return this.vfxModel;
    }

    private Vector3f calculatePosition() {
        float yaw = this.vfx.getYaw();
        float pitch = this.vfx.getPitch();
        Vector3f pos = this.vfx.getPosition();
        return new Vector3f((Vector3fc)pos).rotateX(pitch * ((float)Math.PI / 180)).rotateY(-yaw * ((float)Math.PI / 180));
    }

    private Quaternionf calculateRotation() {
        float yaw = this.vfx.getYaw();
        float pitch = this.vfx.getPitch();
        Vector3f rot = this.vfx.getRotation();
        return new Quaternionf().rotateY((180.0f - yaw) * ((float)Math.PI / 180)).rotateX(-pitch * ((float)Math.PI / 180)).rotateZYX(rot.z, rot.y, rot.x);
    }

    @Override
    @Generated
    public boolean isRespawnRequired() {
        return this.respawnRequired;
    }

    @Override
    @Generated
    public void setRespawnRequired(boolean respawnRequired) {
        this.respawnRequired = respawnRequired;
    }

    @Override
    @Generated
    public boolean isInitialized() {
        return this.initialized;
    }

    public static class VFXModelImpl
    implements VFXDisplayRenderer.VFXModel {
        private final int pivotId;
        private final UUID pivotUuid;
        private final int modelId;
        private final UUID modelUuid;
        private final DataTracker<Vector3f> origin = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<Vector3f> position = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<Quaternionf> leftRotation = new UpdateDataTracker<Quaternionf>(new Quaternionf(), Quaternionf::set);
        private final DataTracker<Vector3f> scale = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<ItemStack> model = new DataTracker();

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
        public int getModelId() {
            return this.modelId;
        }

        @Override
        @Generated
        public UUID getModelUuid() {
            return this.modelUuid;
        }

        @Override
        @Generated
        public DataTracker<Vector3f> getOrigin() {
            return this.origin;
        }

        @Override
        @Generated
        public DataTracker<Vector3f> getPosition() {
            return this.position;
        }

        @Override
        @Generated
        public DataTracker<Quaternionf> getLeftRotation() {
            return this.leftRotation;
        }

        @Override
        @Generated
        public DataTracker<Vector3f> getScale() {
            return this.scale;
        }

        @Override
        @Generated
        public DataTracker<ItemStack> getModel() {
            return this.model;
        }

        @Generated
        public VFXModelImpl(int pivotId, UUID pivotUuid, int modelId, UUID modelUuid) {
            this.pivotId = pivotId;
            this.pivotUuid = pivotUuid;
            this.modelId = modelId;
            this.modelUuid = modelUuid;
        }
    }
}

