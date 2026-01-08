/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.profile.PlayerProfile
 *  org.bukkit.entity.Player
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.model.bone.type;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface UserLimb {
    public void setTexture(@Nullable Player var1);

    public void setTexture(@Nullable PlayerProfile var1);

    public void setPlaceholder(String var1);

    public String getPlaceholder();
}

