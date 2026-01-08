/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.conditions.IEntityCondition
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 */
package com.ticxo.modelengine.core21.mythic.conditions;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicCondition;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Locale;
import java.util.Optional;

@MythicCondition(name="modelhaspassengers", aliases={"modelhaspassenger", "modelpassengers", "modelpassenger"})
public class ModelHasPassengerCondition
implements IEntityCondition {
    private final PlaceholderString modelId;
    private final PlaceholderString pbone;
    private final String mode;

    public ModelHasPassengerCondition(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.pbone = mlc.getPlaceholderString(new String[]{"p", "pbone", "seat"}, null, new String[0]);
        this.mode = mlc.getString(new String[]{"mode"}, "AND", new String[0]).toUpperCase(Locale.ENGLISH);
    }

    public boolean check(AbstractEntity abstractEntity) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(abstractEntity.getUniqueId());
        if (modeledEntity == null) {
            return false;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, abstractEntity);
        if (modelId == null) {
            return this.noModelIdCheck(modeledEntity, abstractEntity);
        }
        Optional<ActiveModel> maybeModel = modeledEntity.getModel(modelId);
        if (maybeModel.isEmpty()) {
            return false;
        }
        ActiveModel activeModel = maybeModel.get();
        Optional maybeMountManager = activeModel.getMountManager();
        if (maybeMountManager.isEmpty()) {
            return false;
        }
        BehaviorManager mountManager = (BehaviorManager)maybeMountManager.get();
        String pBone = MythicUtils.getOrNullLowercase(this.pbone, abstractEntity);
        if (pBone == null || pBone.isBlank()) {
            return ((MountManager)((Object)mountManager)).hasPassengers();
        }
        String[] seats = pBone.split(",");
        switch (this.mode) {
            case "OR": {
                for (String seatId : seats) {
                    Optional maybeSeat = ((MountManager)((Object)mountManager)).getSeat(seatId);
                    if (maybeSeat.isEmpty() || ((MountManager)((Object)mountManager)).getDriverBone() == maybeSeat.get() || ((Mount)((Object)((BoneBehavior)maybeSeat.get()))).getPassengers().isEmpty()) continue;
                    return true;
                }
                return false;
            }
            case "AND": {
                for (String seatId : seats) {
                    Optional maybeSeat = ((MountManager)((Object)mountManager)).getSeat(seatId);
                    if (maybeSeat.isEmpty()) {
                        return false;
                    }
                    if (((MountManager)((Object)mountManager)).getDriverBone() == maybeSeat.get() || !((Mount)((Object)((BoneBehavior)maybeSeat.get()))).getPassengers().isEmpty()) continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private boolean noModelIdCheck(ModeledEntity modeledEntity, AbstractEntity abstractEntity) {
        String pBone = MythicUtils.getOrNullLowercase(this.pbone, abstractEntity);
        if (pBone == null || pBone.isBlank()) {
            for (ActiveModel activeModel : modeledEntity.getModels().values()) {
                Optional maybeMountManager = activeModel.getMountManager();
                if (maybeMountManager.isEmpty() || !((MountManager)((Object)((BehaviorManager)maybeMountManager.get()))).hasPassengers()) continue;
                return true;
            }
            return false;
        }
        String[] seats = pBone.split(",");
        switch (this.mode) {
            case "OR": {
                for (ActiveModel activeModel : modeledEntity.getModels().values()) {
                    Optional maybeMountManager = activeModel.getMountManager();
                    if (maybeMountManager.isEmpty()) continue;
                    BehaviorManager mountManager = (BehaviorManager)maybeMountManager.get();
                    for (String seatId : seats) {
                        Optional maybeSeat = ((MountManager)((Object)mountManager)).getSeat(seatId);
                        if (maybeSeat.isEmpty() || ((MountManager)((Object)mountManager)).getDriverBone() == maybeSeat.get() || ((Mount)((Object)((BoneBehavior)maybeSeat.get()))).getPassengers().isEmpty()) continue;
                        return true;
                    }
                }
                return false;
            }
            case "AND": {
                for (ActiveModel activeModel : modeledEntity.getModels().values()) {
                    Optional maybeMountManager = activeModel.getMountManager();
                    if (maybeMountManager.isEmpty()) {
                        return false;
                    }
                    BehaviorManager mountManager = (BehaviorManager)maybeMountManager.get();
                    for (String seatId : seats) {
                        Optional maybeSeat = ((MountManager)((Object)mountManager)).getSeat(seatId);
                        if (maybeSeat.isEmpty()) {
                            return false;
                        }
                        if (((MountManager)((Object)mountManager)).getDriverBone() == maybeSeat.get() || !((Mount)((Object)((BoneBehavior)maybeSeat.get()))).getPassengers().isEmpty()) continue;
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}

