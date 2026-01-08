/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderVector
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  org.bukkit.entity.Display$Billboard
 *  org.bukkit.entity.TextDisplay$TextAlignment
 */
package com.ticxo.modelengine.core21.mythic.mechanics.nametag;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.type.NameTag;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.placeholders.PlaceholderVector;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

@MythicMechanic(name="setmodeltag", aliases={})
public class SetNametagMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString partId;
    private final PlaceholderString tag;
    private final boolean visible;
    private final Integer backgroundColor;
    private final boolean useDefaultBackgroundColor;
    private final TextDisplay.TextAlignment alignment;
    private final Display.Billboard billboard;
    private final PlaceholderVector scale;
    private final boolean seeThrough;
    private final byte textOpacity;
    private final boolean shadow;
    private final int lineWidth;

    public SetNametagMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.partId = mlc.getPlaceholderString(new String[]{"b", "bone", "p", "pid", "part", "partid"}, null, new String[0]);
        this.tag = mlc.getPlaceholderString(new String[]{"t", "tag"}, null, new String[0]);
        this.visible = mlc.getBoolean(new String[]{"v", "visible"}, true);
        this.backgroundColor = mlc.getInteger(new String[]{"background", "backgroundcolor"}, 0);
        this.useDefaultBackgroundColor = mlc.getBoolean(new String[]{"usedefaultbackground", "defaultbackground", "db"}, true);
        this.alignment = (TextDisplay.TextAlignment)mlc.getEnum(new String[]{"align", "alignment"}, TextDisplay.TextAlignment.class, (Enum)TextDisplay.TextAlignment.CENTER);
        this.billboard = (Display.Billboard)mlc.getEnum(new String[]{"billboard"}, Display.Billboard.class, (Enum)Display.Billboard.CENTER);
        this.scale = mlc.getPlaceholderVector(new String[]{"scale"}, "1,1,1");
        this.seeThrough = mlc.getBoolean(new String[]{"seethrough"}, true);
        this.textOpacity = (byte)mlc.getInteger(new String[]{"opacity", "textopacity"}, -1);
        this.shadow = mlc.getBoolean(new String[]{"shadow"}, false);
        this.lineWidth = mlc.getInteger(new String[]{"linewidth"}, 0);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        model.getModel(modelId).ifPresent(activeModel -> {
            String partId = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta, target);
            activeModel.getBone(partId).flatMap(modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG)).ifPresent(nameTag -> {
                ((NameTag)((Object)nameTag)).setComponentSupplier(this.tag == null ? null : () -> {
                    String legacy = this.tag.get((PlaceholderMeta)meta, target);
                    return LegacyComponentSerializer.legacySection().deserialize(legacy);
                });
                ((NameTag)((Object)nameTag)).setVisible(this.visible);
                ((NameTag)((Object)nameTag)).setBackgroundColor(this.backgroundColor);
                ((NameTag)((Object)nameTag)).setUseDefaultBackgroundColor(this.useDefaultBackgroundColor);
                ((NameTag)((Object)nameTag)).setAlignment(this.alignment);
                ((NameTag)((Object)nameTag)).setBillboard(this.billboard);
                ((NameTag)((Object)nameTag)).setScale(this.scale.get().toVector3f());
                ((NameTag)((Object)nameTag)).setSeeThrough(this.seeThrough);
                ((NameTag)((Object)nameTag)).setTextOpacity(this.textOpacity);
                ((NameTag)((Object)nameTag)).setShadow(this.shadow);
                ((NameTag)((Object)nameTag)).setLineWidth(this.lineWidth);
            });
        });
        return SkillResult.SUCCESS;
    }
}

