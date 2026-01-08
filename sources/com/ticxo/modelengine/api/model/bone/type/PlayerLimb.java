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

public interface PlayerLimb {
    public void setTexture(@Nullable Player var1);

    public void setTexture(@Nullable PlayerProfile var1);

    public Limb getLimbType();

    public static enum Limb {
        HEAD(0, 1, 0, 1),
        RIGHT_ARM(1, 2, 3, 7),
        LEFT_ARM(2, 3, 4, 8),
        BODY(5, 4, 5, 4),
        RIGHT_LEG(6, 5, 6, 5),
        LEFT_LEG(7, 6, 7, 6);

        public final float defaultYOffset;
        public final int defaultId;
        public final float slimYOffset;
        public final int slimId;

        private Limb(int defaultYOffset, int defaultId, int slimYOffset, int slimId) {
            this.defaultYOffset = defaultYOffset * -1024;
            this.defaultId = defaultId;
            this.slimYOffset = slimYOffset * -1024;
            this.slimId = slimId;
        }
    }
}

