/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Color
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.vfx;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import java.util.Collection;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public interface VFX {
    public BaseEntity<?> getBase();

    public VFXRenderer getRenderer();

    public boolean tick();

    public void destroy();

    public boolean isInitialized();

    public boolean isDestroyed();

    public void markRemoved();

    public void queuePostInitTask(Runnable var1);

    public boolean isBaseEntityVisible();

    public void setBaseEntityVisible(boolean var1);

    public void setModelScale(int var1);

    public float getYaw();

    public void setYaw(float var1);

    public float getPitch();

    public void setPitch(float var1);

    public Vector getOrigin();

    public void setOrigin(Vector var1);

    public Vector3f getPosition();

    public void setPosition(Vector3f var1);

    public Vector3f getRotation();

    public void setRotation(Vector3f var1);

    public Vector3f getScale();

    public void setScale(Vector3f var1);

    public ItemStack getModel();

    public void setModel(ItemStack var1);

    public DataTracker<ItemStack> getModelTracker();

    public Color getColor();

    public void setColor(Color var1);

    public boolean isEnchanted();

    public void setEnchanted(boolean var1);

    public boolean isVisible();

    public void setVisible(boolean var1);

    default public void useModel(String modelId, String boneId) {
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(modelId);
        if (blueprint == null) {
            return;
        }
        BlueprintBone bone = blueprint.getFlatMap().get(boneId);
        if (bone == null || !bone.isRenderer()) {
            return;
        }
        Collection<ItemStack> stacks = bone.getModelData().createItemStack();
        if (!stacks.isEmpty()) {
            this.setModel(stacks.iterator().next());
        }
    }

    default public void registerSelf() {
        ModelEngineAPI.getAPI().getVFXUpdater().registerVFX(this.getBase(), this);
        this.getBase().registerData();
    }
}

