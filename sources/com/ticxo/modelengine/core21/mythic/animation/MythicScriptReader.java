/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.bukkit.MythicBukkit
 *  io.lumine.mythic.core.mobs.ActiveMob
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.core21.mythic.animation;

import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.script.ScriptReader;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;

public class MythicScriptReader
implements ScriptReader {
    @Override
    public void read(IAnimationProperty property, String script) {
        ActiveModel model = property.getModel();
        ModelBlueprint blueprint = model.getBlueprint();
        Object original = model.getModeledEntity().getBase().getOriginal();
        if (!(original instanceof Entity)) {
            return;
        }
        Entity entity = (Entity)original;
        float power = 1.0f;
        ActiveMob activeMob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity);
        if (activeMob != null) {
            power = activeMob.getPower();
        }
        String[] scriptSplit = script.split("\\{", 2);
        String skillName = scriptSplit[0];
        boolean succeed = MythicBukkit.inst().getAPIHelper().castSkill(entity, skillName, power, meta -> {
            String[] parameters;
            if (scriptSplit.length != 2) {
                return;
            }
            for (String param : parameters = scriptSplit[1].substring(0, scriptSplit[1].length() - 1).split(";")) {
                String[] entry = param.split("=", 2);
                meta.getParameters().put(entry[0], entry.length == 2 ? this.getAnimationPlaceholder(blueprint, entry[1].strip()) : "");
            }
        });
        if (!succeed) {
            TLogger.warn("Unknown MythicMobs script: " + script);
        }
    }

    private String getAnimationPlaceholder(ModelBlueprint blueprint, String placeholder) {
        if (!placeholder.startsWith("<") || !placeholder.endsWith(">")) {
            return placeholder;
        }
        String key = placeholder.substring(1, placeholder.length() - 1);
        return blueprint.getAnimationsPlaceholders().getOrDefault(key, placeholder);
    }
}

