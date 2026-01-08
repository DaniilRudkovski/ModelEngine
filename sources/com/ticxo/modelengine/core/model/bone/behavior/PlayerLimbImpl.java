/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.profile.PlayerProfile
 *  lombok.Generated
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.SkullMeta
 *  org.bukkit.profile.PlayerTextures$SkinModel
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.core.model.bone.behavior;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.ticxo.modelengine.api.events.BoneTransformReadEvent;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.PlayerLimb;
import lombok.Generated;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.Nullable;

public class PlayerLimbImpl
extends AbstractBoneBehavior<PlayerLimbImpl>
implements PlayerLimb {
    private final PlayerLimb.Limb limbType;
    private boolean isSlim;

    public PlayerLimbImpl(ModelBone bone, BoneBehaviorType<PlayerLimbImpl> type, BoneBehaviorData data) {
        super(bone, type, data);
        this.limbType = (PlayerLimb.Limb)((Object)data.get("limb"));
    }

    @Override
    public void onApply() {
        this.bone.setRenderer(true);
        this.bone.setModel(new ItemStack(Material.AIR));
    }

    @Override
    public void modifyTransform(BoneTransformReadEvent event) {
        event.getPosition().add(0.0f, this.isSlim ? this.limbType.slimYOffset : this.limbType.defaultYOffset, 0.0f);
    }

    @Override
    public void setTexture(@Nullable Player player) {
        if (player == null) {
            this.bone.setModel(new ItemStack(Material.AIR));
            return;
        }
        this.setTexture(player.getPlayerProfile());
    }

    @Override
    public void setTexture(@Nullable PlayerProfile profile) {
        if (profile == null) {
            this.bone.setModel(new ItemStack(Material.AIR));
            return;
        }
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = playerHead.getItemMeta();
        ((SkullMeta)meta).setPlayerProfile(profile);
        meta.setCustomModelData(switch (profile.getTextures().getSkinModel()) {
            default -> throw new IncompatibleClassChangeError();
            case PlayerTextures.SkinModel.CLASSIC -> {
                this.isSlim = false;
                yield this.limbType.defaultId;
            }
            case PlayerTextures.SkinModel.SLIM -> {
                this.isSlim = true;
                yield this.limbType.slimId;
            }
        });
        playerHead.setItemMeta(meta);
        this.bone.setModel(playerHead);
    }

    @Override
    @Generated
    public PlayerLimb.Limb getLimbType() {
        return this.limbType;
    }
}

