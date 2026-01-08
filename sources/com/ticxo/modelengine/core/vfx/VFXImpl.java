/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.Color
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.vfx;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import com.ticxo.modelengine.core.vfx.render.VFXDisplayRendererImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Generated;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class VFXImpl
implements VFX {
    private final BaseEntity<?> base;
    private final VFXRenderer renderer;
    private final Vector3f modelScale = new Vector3f();
    private final boolean initialized;
    private final List<Runnable> queuedTask = new ArrayList<Runnable>();
    private final DataTracker<ItemStack> modelTracker = new DataTracker<ItemStack>(new ItemStack(Material.AIR));
    private boolean isBaseEntityVisible = true;
    private boolean destroyed;
    private boolean removed;
    private float yaw;
    private float pitch;
    private Vector origin = new Vector();
    private Vector3f position = new Vector3f();
    private Vector3f rotation = new Vector3f();
    private Vector3f scale = new Vector3f(1.0f);
    private Color color;
    private boolean enchanted;
    private boolean visible = true;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public VFXImpl(@NotNull BaseEntity<?> base, @Nullable Function<VFX, VFXRenderer> rendererSupplier, @Nullable Consumer<VFX> consumer) {
        this.base = base;
        this.registerSelf();
        this.setOrigin(base.getLocation().toVector());
        VFXRenderer renderer = rendererSupplier == null ? new VFXDisplayRendererImpl(this) : rendererSupplier.apply(this);
        VFXRenderer vFXRenderer = this.renderer = renderer == null ? new VFXDisplayRendererImpl(this) : renderer;
        if (consumer != null) {
            consumer.accept(this);
        }
        this.renderer.initialize();
        List<Runnable> list = this.queuedTask;
        synchronized (list) {
            this.queuedTask.forEach(Runnable::run);
            this.initialized = true;
        }
    }

    @Override
    public boolean tick() {
        if (!this.initialized) {
            return true;
        }
        this.setOrigin(this.base.getLocation().toVector());
        this.setYaw(this.base.getYHeadRot());
        this.setPitch(this.base.getXHeadRot());
        this.renderer.readVFXData();
        return !this.removed && !this.base.isRemoved();
    }

    @Override
    public void destroy() {
        this.destroyed = true;
    }

    @Override
    public void markRemoved() {
        this.removed = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void queuePostInitTask(Runnable runnable) {
        List<Runnable> list = this.queuedTask;
        synchronized (list) {
            if (this.initialized) {
                runnable.run();
            } else {
                this.queuedTask.add(runnable);
            }
        }
    }

    @Override
    public void setBaseEntityVisible(boolean flag) {
        if (this.isBaseEntityVisible() == flag) {
            return;
        }
        this.isBaseEntityVisible = flag;
        this.base.setVisible(flag);
    }

    @Override
    public void setModelScale(int scale) {
        this.modelScale.set((float)scale);
    }

    @Override
    public ItemStack getModel() {
        return this.modelTracker.get();
    }

    @Override
    public void setModel(ItemStack stack) {
        this.modelTracker.set(stack);
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        ItemStack model = this.modelTracker.get();
        BaseItemEnum base = BaseItemEnum.fromMaterial(model.getType());
        if (base == null) {
            return;
        }
        ItemMeta meta = model.getItemMeta();
        base.color(meta, color);
        model.setItemMeta(meta);
        this.modelTracker.markDirty();
    }

    @Override
    public void setEnchanted(boolean flag) {
        if (this.isEnchanted() == flag) {
            return;
        }
        this.enchanted = flag;
        ItemStack model = this.modelTracker.get();
        if (flag) {
            model.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
        } else {
            model.removeEnchantment(Enchantment.VANISHING_CURSE);
        }
        this.modelTracker.markDirty();
    }

    @Override
    @Generated
    public BaseEntity<?> getBase() {
        return this.base;
    }

    @Override
    @Generated
    public VFXRenderer getRenderer() {
        return this.renderer;
    }

    @Generated
    public Vector3f getModelScale() {
        return this.modelScale;
    }

    @Override
    @Generated
    public boolean isInitialized() {
        return this.initialized;
    }

    @Generated
    public List<Runnable> getQueuedTask() {
        return this.queuedTask;
    }

    @Override
    @Generated
    public DataTracker<ItemStack> getModelTracker() {
        return this.modelTracker;
    }

    @Override
    @Generated
    public boolean isBaseEntityVisible() {
        return this.isBaseEntityVisible;
    }

    @Override
    @Generated
    public boolean isDestroyed() {
        return this.destroyed;
    }

    @Generated
    public boolean isRemoved() {
        return this.removed;
    }

    @Override
    @Generated
    public float getYaw() {
        return this.yaw;
    }

    @Override
    @Generated
    public float getPitch() {
        return this.pitch;
    }

    @Override
    @Generated
    public Vector getOrigin() {
        return this.origin;
    }

    @Override
    @Generated
    public Vector3f getPosition() {
        return this.position;
    }

    @Override
    @Generated
    public Vector3f getRotation() {
        return this.rotation;
    }

    @Override
    @Generated
    public Vector3f getScale() {
        return this.scale;
    }

    @Override
    @Generated
    public Color getColor() {
        return this.color;
    }

    @Override
    @Generated
    public boolean isEnchanted() {
        return this.enchanted;
    }

    @Override
    @Generated
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    @Generated
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    @Generated
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Override
    @Generated
    public void setOrigin(Vector origin) {
        this.origin = origin;
    }

    @Override
    @Generated
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    @Override
    @Generated
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    @Override
    @Generated
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    @Override
    @Generated
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

