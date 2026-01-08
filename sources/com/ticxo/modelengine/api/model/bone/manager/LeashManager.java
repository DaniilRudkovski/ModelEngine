/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.api.model.bone.manager;

import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.type.Leash;
import java.util.Map;
import java.util.Optional;
import org.bukkit.entity.Entity;

public interface LeashManager {
    public <T extends Leash & BoneBehavior> void registerLeash(T var1);

    public <T extends Leash & BoneBehavior> Map<String, T> getMainLeashes();

    public <T extends Leash & BoneBehavior> Map<String, T> getLeashes();

    public <T extends Leash & BoneBehavior> Optional<T> getLeash(String var1);

    public void connectMainLeashes(Entity var1);

    public void connectMainLeashes(String var1);

    public void disconnectMainLeashes();

    public void connectLeash(Entity var1, String var2);

    public void connectLeash(String var1, String var2);

    public void disconnect(String var1);

    public Entity getLeashHolder(String var1);

    public <T extends Leash & BoneBehavior> T getLeashConnection(String var1);
}

