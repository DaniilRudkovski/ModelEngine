/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.skills.SkillCaster
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 *  io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder
 *  io.lumine.mythic.core.utils.annotations.MythicPlaceholder
 */
package com.ticxo.modelengine.core21.mythic.placeholders;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(placeholder="caster.model.yaw")
public class CasterModelRotationPlaceholder
implements MetaPlaceholder {
    public String apply(PlaceholderMeta placeholderMeta, String s) {
        SkillCaster caster = placeholderMeta.getCaster();
        ModeledEntity model = ModelEngineAPI.getModeledEntity(caster.getEntity().getUniqueId());
        if (model == null) {
            return "[No Model]";
        }
        BodyRotationController controller = model.getBase().getBodyRotationController();
        return String.valueOf(controller.getYBodyRot());
    }
}

