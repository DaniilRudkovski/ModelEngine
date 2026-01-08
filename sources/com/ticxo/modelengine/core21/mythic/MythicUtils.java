/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.packs.Pack
 *  io.lumine.mythic.api.skills.IParentSkill
 *  io.lumine.mythic.api.skills.Skill
 *  io.lumine.mythic.api.skills.SkillCaster
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.bukkit.MythicBukkit
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 *  io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker
 *  org.bukkit.Color
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.core21.mythic;

import com.google.common.collect.Lists;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.mount.controller.MountControllerSupplier;
import com.ticxo.modelengine.core21.Java21Helper;
import com.ticxo.modelengine.core21.mythic.compatibility.MythicMountController;
import com.ticxo.modelengine.core21.mythic.compatibility.ProjectileEntity;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

public class MythicUtils {
    public static String getOrNull(@Nullable PlaceholderString placeholderString) {
        return placeholderString == null ? null : placeholderString.get();
    }

    public static String getOrNull(@Nullable PlaceholderString placeholderString, PlaceholderMeta meta) {
        return placeholderString == null ? null : placeholderString.get(meta);
    }

    public static String getOrNull(@Nullable PlaceholderString placeholderString, AbstractEntity entity) {
        return placeholderString == null ? null : placeholderString.get(entity);
    }

    public static String getOrNull(@Nullable PlaceholderString placeholderString, PlaceholderMeta meta, AbstractEntity entity) {
        return placeholderString == null ? null : placeholderString.get(meta, entity);
    }

    public static String getOrNull(@Nullable PlaceholderString placeholderString, SkillCaster caster) {
        return placeholderString == null ? null : placeholderString.get(caster);
    }

    public static String getOrNullLowercase(@Nullable PlaceholderString placeholderString) {
        return placeholderString == null ? null : placeholderString.get().toLowerCase(Locale.ENGLISH);
    }

    public static String getOrNullLowercase(@Nullable PlaceholderString placeholderString, PlaceholderMeta meta) {
        return placeholderString == null ? null : placeholderString.get(meta).toLowerCase(Locale.ENGLISH);
    }

    public static String getOrNullLowercase(@Nullable PlaceholderString placeholderString, AbstractEntity entity) {
        return placeholderString == null ? null : placeholderString.get(entity).toLowerCase(Locale.ENGLISH);
    }

    public static String getOrNullLowercase(@Nullable PlaceholderString placeholderString, PlaceholderMeta meta, AbstractEntity entity) {
        return placeholderString == null ? null : placeholderString.get(meta, entity).toLowerCase(Locale.ENGLISH);
    }

    public static String getOrNullLowercase(@Nullable PlaceholderString placeholderString, SkillCaster caster) {
        return placeholderString == null ? null : placeholderString.get(caster).toLowerCase(Locale.ENGLISH);
    }

    public static Color getColor(@Nullable String colorString) {
        if (colorString == null) {
            return Color.WHITE;
        }
        if (colorString.startsWith("#")) {
            colorString = colorString.substring(1);
        }
        return Color.fromRGB((int)Integer.parseInt(colorString, 16));
    }

    public static ActiveModel getActiveModelOrNull(ModeledEntity entity, @Nullable String modelId) {
        return modelId == null ? null : (ActiveModel)entity.getModel(modelId.toLowerCase(Locale.ENGLISH)).orElse(null);
    }

    public static ModelBlueprint getBlueprintOrNull(@Nullable String modelid) {
        return modelid == null ? null : ModelEngineAPI.getBlueprint(modelid);
    }

    public static ProjectileEntity<?> getProjectileEntity(SkillMetadata meta) {
        return MythicUtils.castProjectileEntity(meta).map(tracker -> Java21Helper.getMythicCompatibility().getMythicSupport().getTrackers().get(tracker)).orElse(null);
    }

    public static <T extends ProjectileBulletableTracker & IParentSkill> Optional<T> castProjectileEntity(SkillMetadata metadata) {
        Optional optional;
        IParentSkill iParentSkill = metadata.getCallingEvent();
        if (iParentSkill instanceof ProjectileBulletableTracker) {
            ProjectileBulletableTracker tracker = (ProjectileBulletableTracker)iParentSkill;
            optional = MythicUtils.castProjectileEntity(tracker);
        } else {
            optional = Optional.empty();
        }
        return optional;
    }

    public static <T extends ProjectileBulletableTracker & IParentSkill> Optional<T> castProjectileEntity(ProjectileBulletableTracker tracker) {
        return tracker instanceof IParentSkill ? Optional.of(tracker) : Optional.empty();
    }

    public static UUID getVFXUniqueId(SkillMetadata meta) {
        ProjectileEntity<?> base = MythicUtils.getProjectileEntity(meta);
        if (base != null) {
            return base.getUUID();
        }
        return meta.getCaster().getEntity().getUniqueId();
    }

    public static void executeOptModelId(ModeledEntity entity, String modelId, Consumer<ActiveModel> consumer) {
        if (modelId == null) {
            for (ActiveModel activeModel : entity.getModels().values()) {
                consumer.accept(activeModel);
            }
        } else {
            ActiveModel activeModel = MythicUtils.getActiveModelOrNull(entity, modelId);
            if (activeModel != null) {
                consumer.accept(activeModel);
            }
        }
    }

    public static void executeBoneChild(boolean child, ModelBone bone, Consumer<ModelBone> boneConsumer) {
        boneConsumer.accept(bone);
        if (!child) {
            return;
        }
        LinkedList<ModelBone> queue = new LinkedList<ModelBone>(bone.getChildren().values());
        while (!queue.isEmpty()) {
            ModelBone childBone = queue.pop();
            boneConsumer.accept(childBone);
            queue.addAll(childBone.getChildren().values());
        }
    }

    public static MountControllerSupplier createControllerSupplier(Skill skill, SkillMetadata metadata) {
        return (entity, mount) -> new MythicMountController(entity, mount, skill, metadata);
    }

    public static List<File> getPackModelFiles() {
        ArrayList files = Lists.newArrayList();
        for (Pack pack : MythicBukkit.inst().getPackManager().getPacks()) {
            File folder2;
            File folder = pack.getPackFolder("Models");
            if (folder.exists()) {
                MythicUtils.addFilesRecursively(folder, files);
            }
            if (!(folder2 = new File(pack.getPackFolder("assets"), "modelengine")).exists()) continue;
            MythicUtils.addFilesRecursively(folder2, files);
        }
        return files;
    }

    private static void addFilesRecursively(File folder, List<File> files) {
        File[] fileList = folder.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isFile() && file.getName().endsWith(".bbmodel")) {
                    files.add(file);
                    continue;
                }
                if (!file.isDirectory()) continue;
                MythicUtils.addFilesRecursively(file, files);
            }
        }
    }
}

