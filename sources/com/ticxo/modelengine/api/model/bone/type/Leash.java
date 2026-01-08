/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone.type;

import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public interface Leash {
    public boolean isMainLeash();

    public int getId();

    public Vector3f getLocation();

    public void connect(Entity var1);

    public <T extends Leash & BoneBehavior> void connect(T var1);

    public void disconnect();

    public Entity getConnectedEntity();

    public <T extends Leash & BoneBehavior> T getConnectedLeash();
}

