/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.entity.Player
 *  org.bukkit.util.RayTraceResult
 *  org.bukkit.util.Vector
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.interaction;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.interaction.DynamicHitbox;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class InteractionTracker {
    private final Map<UUID, Integer> hitboxesUUID = Maps.newConcurrentMap();
    private final Map<Integer, HitboxEntity> hitboxes = Maps.newConcurrentMap();
    private final Map<UUID, DynamicHitbox> playerRelay = Maps.newConcurrentMap();
    private final Map<Integer, ActiveModel> modelRelay = Maps.newConcurrentMap();
    private final Map<Integer, Integer> entityRelay = Maps.newConcurrentMap();
    private final Callback<BiPredicate<Player, Boolean>> raytraceCallback = new Callback<BiPredicate>(predicates -> (player, cancel) -> {
        for (BiPredicate predicate : predicates) {
            cancel = predicate.test(player, cancel);
        }
        return cancel;
    });

    public void raytraceHitboxes() {
        if (this.hitboxes.isEmpty()) {
            this.playerRelay.forEach((uuid, dynamicHitbox) -> dynamicHitbox.destroy());
            this.playerRelay.clear();
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.raytraceCallback.invoker().test(player, false)) continue;
            if (player.getGameMode() == GameMode.SPECTATOR) {
                this.playerRelay.computeIfPresent(player.getUniqueId(), (uuid, dynamicHitbox) -> {
                    dynamicHitbox.destroy();
                    return null;
                });
                continue;
            }
            Vector startVec = player.getEyeLocation().toVector();
            Vector3f start = startVec.toVector3f();
            Vector dirVec = player.getEyeLocation().getDirection();
            Vector3f dir = dirVec.toVector3f();
            double traceDist = player.getGameMode() == GameMode.CREATIVE ? 5.0 : 3.0;
            double dist = Double.MAX_VALUE;
            HitboxEntity closestHitbox = null;
            RayTraceResult closestResult = null;
            for (HitboxEntity hitbox : this.hitboxes.values()) {
                double hitDistSqr;
                OrientedBoundingBox obb;
                if (hitbox.getLocation().getWorld() != player.getWorld() || this.entityRelay.getOrDefault(hitbox.getEntityId(), player.getEntityId() + 1).intValue() == player.getEntityId() || hitbox.getBone().getActiveModel().getModeledEntity().getBase().getEntityId() == player.getEntityId() || (obb = hitbox.getOrientedBoundingBox()) == null) continue;
                if (obb.contains(start)) {
                    closestHitbox = hitbox;
                    closestResult = null;
                    break;
                }
                RayTraceResult result = obb.rayTrace(start, dir, traceDist, null);
                if (result == null || dist < (hitDistSqr = result.getHitPosition().distanceSquared(startVec))) continue;
                closestHitbox = hitbox;
                closestResult = result;
            }
            if (closestHitbox == null) {
                this.playerRelay.computeIfPresent(player.getUniqueId(), (uuid, dynamicHitbox) -> {
                    dynamicHitbox.destroy();
                    return null;
                });
                continue;
            }
            Vector hitPos = closestResult == null ? startVec.add(dirVec.multiply(traceDist)) : closestResult.getHitPosition();
            DynamicHitbox dynamicHitbox2 = this.playerRelay.computeIfAbsent(player.getUniqueId(), uuid -> new DynamicHitbox(player, hitPos));
            dynamicHitbox2.setTarget(closestHitbox.getEntityId());
            dynamicHitbox2.update(hitPos);
        }
    }

    public void addHitbox(HitboxEntity hitbox) {
        this.hitboxesUUID.put(hitbox.getUniqueId(), hitbox.getEntityId());
        this.hitboxes.put(hitbox.getEntityId(), hitbox);
    }

    public void removeHitbox(UUID uuid) {
        Integer id = this.hitboxesUUID.remove(uuid);
        if (id != null) {
            this.hitboxes.remove(id);
        }
    }

    public void removeHitbox(int id) {
        HitboxEntity hitbox = this.hitboxes.remove(id);
        if (hitbox != null) {
            this.hitboxesUUID.remove(hitbox.getUniqueId());
        }
    }

    public HitboxEntity getHitbox(UUID uuid) {
        Integer id = this.hitboxesUUID.get(uuid);
        return id == null ? null : this.getHitbox(id);
    }

    public HitboxEntity getHitbox(int id) {
        return this.hitboxes.get(id);
    }

    public void removeDynamicHitbox(UUID uuid) {
        this.playerRelay.remove(uuid);
    }

    public DynamicHitbox getDynamicHitbox(UUID uuid) {
        return this.playerRelay.get(uuid);
    }

    public void setModelRelay(int id, ActiveModel activeModel) {
        this.modelRelay.put(id, activeModel);
    }

    public void removeModelRelay(int id) {
        ActiveModel activeModel = this.modelRelay.remove(id);
    }

    public ActiveModel getModelRelay(int id) {
        return this.modelRelay.get(id);
    }

    public void setEntityRelay(int id, Integer hitboxId) {
        this.entityRelay.put(id, hitboxId);
    }

    public void removeEntityRelay(int id) {
        this.entityRelay.remove(id);
    }

    public Integer getEntityRelay(int id) {
        return this.entityRelay.get(id);
    }

    @Generated
    public Callback<BiPredicate<Player, Boolean>> getRaytraceCallback() {
        return this.raytraceCallback;
    }
}

