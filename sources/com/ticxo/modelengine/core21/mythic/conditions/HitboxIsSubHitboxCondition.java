/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.conditions.ISkillMetaCondition
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.bukkit.events.MythicConditionLoadEvent
 */
package com.ticxo.modelengine.core21.mythic.conditions;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.core21.mythic.utils.MythicCondition;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;

@MythicCondition(name="hitboxissubhitbox", aliases={"issubhitbox"})
public class HitboxIsSubHitboxCondition
implements ISkillMetaCondition {
    private PlaceholderString hitbox;

    public HitboxIsSubHitboxCondition(ModelEngineAPI plugin, MythicConditionLoadEvent loader) {
        MythicLineConfig config = loader.getConfig();
        this.hitbox = config.getPlaceholderString(new String[]{"hitbox", "hb", "h", "ob"}, loader.getArgument(), new String[0]);
    }

    public boolean check(SkillMetadata skillMetadata) {
        boolean bool = skillMetadata.getVariables().has("hitbox");
        return bool;
    }
}

