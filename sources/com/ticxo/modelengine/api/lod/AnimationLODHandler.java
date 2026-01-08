/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  lombok.Generated
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.util.BoundingBox
 */
package com.ticxo.modelengine.api.lod;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.math.Box;
import com.ticxo.modelengine.api.utils.math.TMath;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class AnimationLODHandler {
    private static final Set<UUID> ACTIVE = new ObjectOpenHashSet();
    private static final Callback<Consumer<Boolean>> TOGGLE_CALLBACK = new Callback<Consumer>(consumers -> flag -> consumers.forEach(booleanConsumer -> booleanConsumer.accept(flag)));
    private static final Callback<BiConsumer<UUID, Boolean>> PLAYER_CALLBACK = new Callback<BiConsumer>(biConsumers -> (uuid, flag) -> biConsumers.forEach(uuidBooleanBiConsumer -> uuidBooleanBiConsumer.accept(uuid, flag)));
    private static boolean ENABLED;
    private static boolean DEFAULT;
    private static double FALLOFF_LENGTH;
    private static double FALLOFF_MULTIPLIER;
    private static double IGNORE_DISTANCE;
    private static double IGNORE_DISTANCE_SQR;
    private static double IGNORE_MULTIPLIER;
    private static double MAX_RATE_MULTIPLIER;
    private static double MIN_RATE_MULTIPLIER;
    private final ModeledEntity modeledEntity;
    private final UUID toggleCallback;
    private final UUID playerCallback;
    private final Object2ObjectMap<UUID, LODTracker> trackers = new Object2ObjectOpenHashMap();
    private final LODTracker fullUpdate;
    private Boolean enabled;

    public static void updateConfig() {
        ENABLED = ConfigProperty.ANIMATION_LOD_ENABLED.getBoolean();
        DEFAULT = ConfigProperty.ANIMATION_LOD_DEFAULT.getBoolean();
        FALLOFF_LENGTH = ConfigProperty.ANIMATION_LOD_FALLOFF_LENGTH.getDouble();
        FALLOFF_MULTIPLIER = ConfigProperty.ANIMATION_LOD_FALLOFF_MULTIPLIER.getDouble();
        IGNORE_DISTANCE = ConfigProperty.ANIMATION_LOD_IGNORE_DISTANCE.getDouble();
        IGNORE_DISTANCE_SQR = IGNORE_DISTANCE * IGNORE_DISTANCE;
        IGNORE_MULTIPLIER = ConfigProperty.ANIMATION_LOD_IGNORE_MULTIPLIER.getDouble();
        MAX_RATE_MULTIPLIER = ConfigProperty.ANIMATION_LOD_MAX_RATE_MULTIPLIER.getDouble();
        MIN_RATE_MULTIPLIER = ConfigProperty.ANIMATION_LOD_MIN_RATE_MULTIPLIER.getDouble();
    }

    public static void setGlobalEnabled(boolean flag) {
        if (ENABLED == flag) {
            return;
        }
        ENABLED = flag;
        TOGGLE_CALLBACK.invoker().accept(flag);
    }

    public static boolean isGlobalEnabled() {
        return ENABLED;
    }

    public static void registerPlayer(UUID uuid) {
        AnimationLODHandler.setPlayerActive(uuid, DEFAULT);
    }

    public static void setPlayerActive(UUID uuid, boolean flag) {
        if (flag && !ACTIVE.contains(uuid)) {
            ACTIVE.add(uuid);
            PLAYER_CALLBACK.invoker().accept(uuid, true);
        } else if (!flag) {
            ACTIVE.remove(uuid);
            PLAYER_CALLBACK.invoker().accept(uuid, false);
        }
    }

    public AnimationLODHandler(ModeledEntity modeledEntity) {
        this.modeledEntity = modeledEntity;
        this.fullUpdate = new LODTracker(UUID.randomUUID(), modeledEntity);
        this.fullUpdate.canSkip = false;
        this.toggleCallback = TOGGLE_CALLBACK.subscribe(flag -> {
            if (!flag.booleanValue()) {
                this.trackers.clear();
            }
        });
        this.playerCallback = PLAYER_CALLBACK.subscribe(this::updatePlayer);
    }

    public LODTracker tick(UUID uuid) {
        if (!this.enabled() || !ACTIVE.contains(uuid)) {
            return this.fullUpdate;
        }
        LODTracker tracker = (LODTracker)this.trackers.computeIfAbsent((Object)uuid, uuid1 -> new LODTracker(uuid, this.modeledEntity));
        if (tracker.tick(Bukkit.getCurrentTick())) {
            this.trackers.remove((Object)uuid);
            tracker.canSkip = true;
        }
        return tracker;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
        if (!this.enabled()) {
            this.trackers.clear();
        }
    }

    public boolean enabled() {
        return this.enabled == null ? ENABLED : this.enabled;
    }

    public void destroy() {
        TOGGLE_CALLBACK.unsubscribe(this.toggleCallback);
        PLAYER_CALLBACK.unsubscribe(this.playerCallback);
    }

    private void updatePlayer(UUID uuid, boolean flag) {
        if (flag) {
            this.trackers.computeIfAbsent((Object)uuid, uuid1 -> new LODTracker(uuid, this.modeledEntity));
        } else {
            this.trackers.remove((Object)uuid);
        }
    }

    @Generated
    public Boolean getEnabled() {
        return this.enabled;
    }

    public class LODTracker {
        private final UUID uuid;
        private final ModeledEntity modeledEntity;
        private int lastCheckedTick = Bukkit.getCurrentTick();
        private final DataTracker<Integer> tickDuration = new DataTracker<Integer>(1);
        private double distanceSqr = -1.0;
        private double tickRate = 1.0;
        private double cumulatedTick = 0.0;
        private int lastSuccessfulTick = 0;
        private boolean canSkip;

        public boolean tick(int tick) {
            if (this.canRemoveSelf()) {
                this.canSkip = true;
                return true;
            }
            if (this.lastCheckedTick == tick) {
                return false;
            }
            this.lastCheckedTick = tick;
            this.calculateTickRate();
            this.cumulatedTick += this.tickRate;
            int floorTick = TMath.floor(this.cumulatedTick);
            this.canSkip = this.lastSuccessfulTick == floorTick;
            this.lastSuccessfulTick = floorTick;
            if (!this.canSkip) {
                this.tickDuration.clearDirty();
                this.tickDuration.set(Math.max(TMath.ceil((Math.ceil(this.cumulatedTick) - this.cumulatedTick) / this.tickRate), 1));
            }
            return false;
        }

        public boolean canRemoveSelf() {
            return !AnimationLODHandler.this.enabled() || !ACTIVE.contains(this.uuid) || Bukkit.getPlayer((UUID)this.uuid) == null;
        }

        public void calculateTickRate() {
            if (!AnimationLODHandler.this.enabled() || !ACTIVE.contains(this.uuid)) {
                return;
            }
            Player player = Bukkit.getPlayer((UUID)this.uuid);
            if (player == null) {
                return;
            }
            BaseEntity<?> base = this.modeledEntity.getBase();
            Location loc = base.getLocation();
            if (loc.getWorld() != player.getWorld()) {
                return;
            }
            double dSqr = loc.distanceSquared(player.getLocation());
            if (TMath.isSimilar(dSqr, this.distanceSqr)) {
                return;
            }
            this.distanceSqr = dSqr;
            if (IGNORE_DISTANCE_SQR >= dSqr) {
                this.tickRate = 1.0;
            } else {
                Box cullBox = base.getData().getCullHitbox();
                BoundingBox box = cullBox == null ? base.getBoundingBox() : cullBox.createBoundingBox(loc.toVector());
                double ignoreMul = (box.getWidthX() + box.getHeight() + box.getWidthZ()) / 3.0 * IGNORE_MULTIPLIER;
                float distance = (float)Math.max(0.0, Math.sqrt(dSqr) - IGNORE_DISTANCE * ignoreMul);
                this.tickRate = TMath.isSimilar(distance, 0.0f) ? 1.0 : Math.pow(FALLOFF_MULTIPLIER, (double)distance / FALLOFF_LENGTH);
            }
            this.tickRate = TMath.clamp(this.tickRate, MIN_RATE_MULTIPLIER, MAX_RATE_MULTIPLIER);
        }

        @Generated
        public LODTracker(UUID uuid, ModeledEntity modeledEntity) {
            this.uuid = uuid;
            this.modeledEntity = modeledEntity;
        }

        @Generated
        public DataTracker<Integer> getTickDuration() {
            return this.tickDuration;
        }

        @Generated
        public boolean isCanSkip() {
            return this.canSkip;
        }
    }
}

