/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.event.HandlerList
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.events;

import com.ticxo.modelengine.api.events.AbstractEvent;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import lombok.Generated;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BoneTransformReadEvent
extends AbstractEvent {
    private static final HandlerList handlers = new HandlerList();
    private final ModelBone modelBone;
    private final Vector3f position;
    private final Quaternionf leftRotation;
    private final Vector3f scale;
    private final Quaternionf rightRotation;

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @Generated
    public ModelBone getModelBone() {
        return this.modelBone;
    }

    @Generated
    public Vector3f getPosition() {
        return this.position;
    }

    @Generated
    public Quaternionf getLeftRotation() {
        return this.leftRotation;
    }

    @Generated
    public Vector3f getScale() {
        return this.scale;
    }

    @Generated
    public Quaternionf getRightRotation() {
        return this.rightRotation;
    }

    @Generated
    public BoneTransformReadEvent(ModelBone modelBone, Vector3f position, Quaternionf leftRotation, Vector3f scale, Quaternionf rightRotation) {
        this.modelBone = modelBone;
        this.position = position;
        this.leftRotation = leftRotation;
        this.scale = scale;
        this.rightRotation = rightRotation;
    }
}

