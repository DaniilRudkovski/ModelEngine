/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.profile.PlayerProfile
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.profile.PlayerTextures
 *  org.bukkit.profile.PlayerTextures$SkinModel
 */
package com.ticxo.modelengine.core21.mythic.mechanics.bone;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.PlayerLimb;
import com.ticxo.modelengine.api.model.bone.type.UserLimb;
import com.ticxo.modelengine.api.utils.MojangAPI;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures;

@MythicMechanic(name="modelplayerskin", aliases={"modelskin"})
public class ModelSkinMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString pbone;
    private final PlaceholderString username;
    private final PlaceholderString uuid;
    private final PlaceholderString skin;
    private final Boolean isSlim;

    public ModelSkinMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.pbone = mlc.getPlaceholderString(new String[]{"p", "pbone", "limbs"}, null, new String[0]);
        this.uuid = mlc.getPlaceholderString(new String[]{"uuid"}, null, new String[0]);
        this.username = mlc.getPlaceholderString(new String[]{"user", "name", "username"}, null, new String[0]);
        this.skin = mlc.getPlaceholderString(new String[]{"s", "skin"}, null, new String[0]);
        String slim = mlc.getString(new String[]{"slim"}, null, new String[0]);
        this.isSlim = slim == null ? null : Boolean.valueOf(Boolean.parseBoolean(slim));
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        PlayerProfile profile2;
        AbstractEntity caster = meta.getCaster().getEntity();
        ModeledEntity model = ModelEngineAPI.getModeledEntity(caster.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String username = MythicUtils.getOrNull(this.username, (PlaceholderMeta)meta, target);
        if (username != null) {
            MojangAPI.getUUIDFromUsernamePromise(username).thenApplyAsync(MojangAPI::fromUUID).thenAcceptAsync(profile -> this.apply(model, (PlayerProfile)profile, meta, target));
            return SkillResult.SUCCESS;
        }
        String uuidString = MythicUtils.getOrNull(this.uuid, (PlaceholderMeta)meta, target);
        if (uuidString != null) {
            UUID uuid = TMath.parseUUID(uuidString);
            MojangAPI.fromUUIDPromise(uuid).thenAcceptAsync(profile -> this.apply(model, (PlayerProfile)profile, meta, target));
            return SkillResult.SUCCESS;
        }
        String skin = MythicUtils.getOrNull(this.skin, (PlaceholderMeta)meta, target);
        if (skin == null) {
            Entity entity = target.getBukkitEntity();
            if (!(entity instanceof Player)) {
                return SkillResult.INVALID_TARGET;
            }
            Player player = (Player)entity;
            profile2 = player.getPlayerProfile();
        } else {
            profile2 = MojangAPI.fromBase64(skin);
        }
        return this.apply(model, profile2, meta, target);
    }

    public boolean getTargetsCreatives() {
        return true;
    }

    private SkillResult apply(ModeledEntity model, PlayerProfile profile, SkillMetadata meta, AbstractEntity target) {
        List<String> limbs;
        PlayerProfile finalProfile = this.forceModel(profile);
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        String pbone = MythicUtils.getOrNullLowercase(this.pbone, (PlaceholderMeta)meta, target);
        if (modelId == null && pbone == null) {
            for (ActiveModel activeModel2 : model.getModels().values()) {
                for (ModelBone modelBone2 : activeModel2.getBones().values()) {
                    modelBone2.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)((Object)playerLimb)).setTexture(finalProfile));
                    modelBone2.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(playerLimb -> ((UserLimb)((Object)playerLimb)).setTexture(finalProfile));
                }
            }
            return SkillResult.SUCCESS;
        }
        List<String> list = limbs = pbone == null ? null : List.of(pbone.split(","));
        if (modelId == null) {
            for (ActiveModel activeModel3 : model.getModels().values()) {
                for (String limb : limbs) {
                    activeModel3.getBone(limb).ifPresent(modelBone -> {
                        modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)((Object)playerLimb)).setTexture(finalProfile));
                        modelBone.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(playerLimb -> ((UserLimb)((Object)playerLimb)).setTexture(finalProfile));
                    });
                }
            }
        } else {
            model.getModel(modelId).ifPresent(activeModel -> {
                if (limbs == null) {
                    for (ModelBone modelBone2 : activeModel.getBones().values()) {
                        modelBone2.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)((Object)playerLimb)).setTexture(finalProfile));
                        modelBone2.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(playerLimb -> ((UserLimb)((Object)playerLimb)).setTexture(finalProfile));
                    }
                } else {
                    for (String seat : limbs) {
                        activeModel.getBone(seat).ifPresent(modelBone -> {
                            modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)((Object)playerLimb)).setTexture(finalProfile));
                            modelBone.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(playerLimb -> ((UserLimb)((Object)playerLimb)).setTexture(finalProfile));
                        });
                    }
                }
            });
        }
        return SkillResult.SUCCESS;
    }

    private PlayerProfile forceModel(PlayerProfile profile) {
        PlayerTextures.SkinModel model;
        if (this.isSlim == null) {
            return profile;
        }
        PlayerTextures texture = profile.getTextures();
        PlayerTextures.SkinModel skinModel = model = this.isSlim != false ? PlayerTextures.SkinModel.SLIM : PlayerTextures.SkinModel.CLASSIC;
        if (model != texture.getSkinModel()) {
            texture.setSkin(texture.getSkin(), model);
            profile.setTextures(texture);
        }
        return profile;
    }
}

