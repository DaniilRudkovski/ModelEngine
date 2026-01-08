/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.ItemDisplay
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.bukkit.event.Event
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Transformation
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.model.render;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.events.BoneTransformReadEvent;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.BoneItems;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.HeldItemRenderer;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.model.render.DisplayFire;
import com.ticxo.modelengine.api.model.render.DisplayRenderer;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.utils.data.PooledCollection;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.model.render.DisplayBoneImpl;
import com.ticxo.modelengine.core.model.render.DisplayFireImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class DisplayRendererImpl
implements DisplayRenderer {
    private final ActiveModel activeModel;
    private final EntityHandler entityHandler;
    private final Map<String, DisplayBone> spawnQueue = new HashMap<String, DisplayBone>();
    private final Map<String, DisplayBone> rendered = new HashMap<String, DisplayBone>();
    private final Map<String, DisplayBone> destroyQueue = new HashMap<String, DisplayBone>();
    private final Set<UUID> fullUpdate = new HashSet<UUID>();
    private PivotImpl pivot;
    private HitboxImpl hitbox;
    private boolean initialized;
    private boolean firstSpawned;
    private final Queue<Runnable> queuedTasks = new ConcurrentLinkedQueue<Runnable>();

    public DisplayRendererImpl(ActiveModel activeModel) {
        this.activeModel = activeModel;
        this.entityHandler = ModelEngineAPI.getEntityHandler();
    }

    @Override
    public void updatePivotOverride(PivotOverride override) {
        if (this.initialized) {
            this.updatePivotOverride0(override);
        } else {
            this.queuedTasks.add(() -> this.updatePivotOverride0(override));
        }
    }

    private void updatePivotOverride0(PivotOverride override) {
        this.activeModel.getPivotOverride().ifPresent(old -> old.removePassenger(this.pivot.id));
        if (override != null) {
            override.addPassenger(this.pivot.id);
        }
    }

    @Override
    public void initialize() {
        ModeledEntity model = this.activeModel.getModeledEntity();
        BaseEntity<?> base = model.getBase();
        Location location = base.getLocation();
        ModelBlueprint blueprint = this.activeModel.getBlueprint();
        Vector3fc scale = this.activeModel.getScale();
        Vector3fc hitboxScale = this.activeModel.getHitboxScale();
        Hitbox mainHitbox = blueprint.getMainHitbox();
        float scaledEyeHeight = (float)mainHitbox.getEyeHeight() * scale.y();
        float scaledHeight = (float)mainHitbox.getHeight() * hitboxScale.y();
        float scaledWidth = (float)mainHitbox.getMaxWidth() * hitboxScale.x();
        this.pivot = new PivotImpl(this.entityHandler.getNextEntityId());
        this.pivot.pivotOverride = this.activeModel.getPivotOverride().orElse(null);
        this.pivot.updatePosition(location, scaledEyeHeight);
        this.pivot.getYaw().set(Float.valueOf(-model.getYBodyRot() * ((float)Math.PI / 180)));
        for (Map.Entry<String, ModelBone> entry : this.activeModel.getBones().entrySet()) {
            this.create(entry.getKey(), entry.getValue(), scaledEyeHeight);
        }
        this.forBehaviorRenderer(behaviorRenderer -> {
            behaviorRenderer.setModelRenderer(this);
            behaviorRenderer.initialize();
        });
        this.hitbox = new HitboxImpl(this.entityHandler.getNextEntityId(), this.entityHandler.getNextEntityId(), this.entityHandler.getNextEntityId());
        this.hitbox.updatePosition(location);
        this.hitbox.getHeight().set(Float.valueOf(scaledHeight));
        this.hitbox.getWidth().set(Float.valueOf(scaledWidth));
        this.hitbox.getShadowRadius().set(Float.valueOf(blueprint.getShadowRadius() * scale.x()));
        this.hitbox.getHitboxVisible().set(this.activeModel.isHitboxVisible());
        this.hitbox.getShadowVisible().set(this.activeModel.isShadowVisible());
        this.hitbox.getFireVisible().set(this.activeModel.canRenderFire());
        if (this.hitbox.isHitboxDirty() || this.hitbox.getFireVisible().isDirty()) {
            this.hitbox.updateFireDisplay();
        }
        this.hitbox.setOnFire(this.activeModel.isOnFire());
        ModelEngineAPI.getInteractionTracker().setModelRelay(this.hitbox.hitboxId, this.activeModel);
        this.initialized = true;
        this.firstSpawned = true;
        while (!this.queuedTasks.isEmpty()) {
            this.queuedTasks.poll().run();
        }
    }

    private void create(String boneId, ModelBone modelBone, float eyeHeight) {
        if (!modelBone.isRenderer() || modelBone.getPivot() != null) {
            return;
        }
        BoneItems models = modelBone.getModels();
        DisplayBoneImpl bone = new DisplayBoneImpl();
        bone.getGlowing().set(modelBone.isGlowing());
        bone.getGlowColor().set(modelBone.getGlowColor());
        bone.getBrightness().set(modelBone.getBrightness());
        bone.getBillboard().set(modelBone.getBillboard());
        bone.getVisibility().set(modelBone.isVisible());
        Vector3f offset = this.pivot.getMountOffset(eyeHeight);
        SafeTransform transform = modelBone.getGlobalTransform();
        BoneTransformReadEvent event = new BoneTransformReadEvent(modelBone, transform.getPosition(), transform.getLeftQuaternion(), transform.getScale(), transform.getRightQuaternion());
        Bukkit.getPluginManager().callEvent((Event)event);
        modelBone.forBehaviors(boneBehavior -> boneBehavior.modifyTransform(event));
        bone.getPosition().set(event.getPosition().rotateY(this.pivot.yaw.get().floatValue()).sub((Vector3fc)offset));
        bone.getLeftRotation().set(event.getLeftRotation().rotateLocalY(this.pivot.yaw.get().floatValue()));
        bone.getScale().set(TMath.zerofyScale(event.getScale()));
        bone.getRightRotation().set(event.getRightRotation());
        bone.getDisplay().set(ItemDisplay.ItemDisplayTransform.HEAD);
        bone.getSnapshotHandler().recordSnapshot();
        models.forEach((uuid, stack) -> {
            DisplayBoneImpl.BoneDataImpl data = new DisplayBoneImpl.BoneDataImpl(this.entityHandler.getNextEntityId(), bone);
            data.getModel().set(stack.clone());
            bone.getModel().put((Integer)uuid, data);
            this.pivot.passengers.add(data.getId());
        });
        this.initializeSpecialBehaviorRender(modelBone, bone);
        this.spawnQueue.put(boneId, bone);
        this.destroyQueue.remove(boneId);
    }

    private void initializeSpecialBehaviorRender(ModelBone modelBone, DisplayBone bone) {
        modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM).ifPresent(item -> bone.getDisplay().set(((HeldItem)((Object)item)).getDisplay()));
        modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(limb -> bone.getDisplay().set(ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND));
    }

    @Override
    public void readModelData() {
        if (!this.initialized) {
            return;
        }
        ModeledEntity model = this.activeModel.getModeledEntity();
        BaseEntity<?> base = model.getBase();
        Location location = base.getLocation();
        ModelBlueprint blueprint = this.activeModel.getBlueprint();
        Vector3fc scale = this.activeModel.getScale();
        Vector3fc hitboxScale = this.activeModel.getHitboxScale();
        Hitbox mainHitbox = blueprint.getMainHitbox();
        float scaledEyeHeight = (float)mainHitbox.getEyeHeight() * scale.y();
        float scaledHeight = (float)mainHitbox.getHeight() * hitboxScale.y();
        float scaledWidth = (float)mainHitbox.getMaxWidth() * hitboxScale.x();
        this.pivot.pivotOverride = this.activeModel.getPivotOverride().orElse(null);
        this.pivot.updatePosition(location, scaledEyeHeight);
        this.pivot.getYaw().set(Float.valueOf(-model.getYBodyRot() * ((float)Math.PI / 180)));
        this.destroyQueue.putAll(this.rendered);
        for (Map.Entry<String, ModelBone> entry : this.activeModel.getBones().entrySet()) {
            DisplayBone bone2 = (DisplayBone)this.getQueued(entry.getKey());
            if (bone2 == null) {
                this.create(entry.getKey(), entry.getValue(), scaledEyeHeight);
                continue;
            }
            this.read(entry.getKey(), bone2, entry.getValue(), scaledEyeHeight);
        }
        this.destroyQueue.values().iterator().forEachRemaining(bone -> bone.getModel().values().iterator().forEachRemaining(boneData -> this.pivot.passengers.remove(boneData.getId())));
        this.forBehaviorRenderer(BehaviorRenderer::readBoneData);
        this.hitbox.updatePosition(location);
        this.hitbox.getHeight().set(Float.valueOf(scaledHeight));
        this.hitbox.getWidth().set(Float.valueOf(scaledWidth));
        this.hitbox.getShadowRadius().set(Float.valueOf(blueprint.getShadowRadius() * scale.x()));
        this.hitbox.getHitboxVisible().set(this.activeModel.isHitboxVisible());
        this.hitbox.getShadowVisible().set(this.activeModel.isShadowVisible());
        this.hitbox.getFireVisible().set(this.activeModel.canRenderFire());
        if (this.hitbox.isHitboxDirty() || this.hitbox.getFireVisible().isDirty()) {
            this.hitbox.updateFireDisplay();
        }
        this.hitbox.setOnFire(this.activeModel.isOnFire());
    }

    private void read(String boneId, DisplayBone bone, ModelBone modelBone, float eyeHeight) {
        DataTracker<Boolean> shouldRender = bone.getRender();
        shouldRender.set(!modelBone.isEffectivelyInvisible());
        if (shouldRender.get().booleanValue() || shouldRender.isDirty()) {
            bone.getStep().set(modelBone.pollModelScaleChanged() || modelBone.shouldStep() || shouldRender.isDirty());
            SafeTransform transform = modelBone.getGlobalTransform();
            BoneTransformReadEvent event = new BoneTransformReadEvent(modelBone, transform.getPosition(), transform.getLeftQuaternion(), transform.getScale(), transform.getRightQuaternion());
            Bukkit.getPluginManager().callEvent((Event)event);
            modelBone.forBehaviors(boneBehavior -> boneBehavior.modifyTransform(event));
            Vector3f offset = this.pivot.getMountOffset(eyeHeight);
            bone.getPosition().set(event.getPosition().rotateY(this.pivot.yaw.get().floatValue()).sub((Vector3fc)offset));
            bone.getLeftRotation().set(event.getLeftRotation().rotateLocalY(this.pivot.yaw.get().floatValue(), new Quaternionf()));
            bone.getScale().set(TMath.zerofyScale(event.getScale()));
            bone.getRightRotation().set(event.getRightRotation());
            bone.getBillboard().set(modelBone.getBillboard());
            bone.getVisibility().set(modelBone.isVisible());
            bone.getGlowing().set(modelBone.isGlowing());
            bone.getGlowColor().set(modelBone.getGlowColor());
            bone.getBrightness().set(modelBone.getBrightness());
            bone.getSnapshotHandler().recordSnapshot();
            BoneItems models = modelBone.getModels();
            if (models.isDirty()) {
                models.clearDirty();
                ((DisplayBoneImpl)bone).updateBoneData(this.entityHandler, this.pivot.getPassengers(), models);
            }
            this.updateSpecialBehaviorRender(modelBone, bone);
        }
        shouldRender.clearDirty();
        this.destroyQueue.remove(boneId);
    }

    private void updateSpecialBehaviorRender(ModelBone modelBone, DisplayBone bone) {
        modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM).ifPresent(item -> bone.getDisplay().set(((HeldItem)((Object)item)).getDisplay()));
    }

    @Override
    public void sendToClient(RenderParsers parsers) {
        if (!this.initialized) {
            return;
        }
        this.forManagers(BehaviorManager::preBoneRender);
        this.forBehavior(BoneBehavior::preRender);
        this.destroyQueue.keySet().forEach(this.rendered::remove);
        parsers.getModelParser(this).sendToClients(this);
        this.rendered.putAll(this.spawnQueue);
        this.spawnQueue.clear();
        this.destroyQueue.clear();
        this.forBehaviorRenderer(behaviorRenderer -> behaviorRenderer.sendToClient(parsers));
        this.forBehavior(BoneBehavior::onRender);
        this.forBehavior(BoneBehavior::postRender);
        this.forManagers(BehaviorManager::postBoneRender);
    }

    @Override
    public void destroy(RenderParsers parsers) {
        if (!this.initialized) {
            return;
        }
        this.forBehaviorRenderer(behaviorRenderer -> behaviorRenderer.destroy(parsers));
        parsers.getModelParser(this).destroy(this);
        ModelEngineAPI.getInteractionTracker().removeModelRelay(this.hitbox.hitboxId);
        this.activeModel.getPivotOverride().ifPresent(override -> override.removePassenger(this.pivot.id));
    }

    @Override
    public void createRealEntities() {
        World world = this.activeModel.getModeledEntity().getBase().getLocation().getWorld();
        if (world == null) {
            return;
        }
        Vector3f pos = this.pivot.getPosition().get();
        Location location = new Location(world, (double)pos.x, (double)pos.y, (double)pos.z);
        for (DisplayBone bone : this.rendered.values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                world.spawn(location, ItemDisplay.class, itemDisplay -> {
                    Quaternionf rotation = bone.getLeftRotation().get();
                    if (ServerInfo.VERSION_NUMBER <= 19) {
                        rotation = rotation.rotateY((float)Math.PI, new Quaternionf());
                    }
                    itemDisplay.setTransformation(new Transformation(bone.getPosition().get(), rotation, bone.getScale().get(), new Quaternionf()));
                    itemDisplay.setItemStack(boneData.getModel().get().clone());
                    itemDisplay.setItemDisplayTransform(bone.getDisplay().get());
                });
            }
        }
    }

    @Override
    public boolean pollFirstSpawn() {
        if (!this.firstSpawned) {
            return false;
        }
        this.firstSpawned = false;
        return true;
    }

    private void forManagers(Consumer<BehaviorManager<?>> consumer) {
        for (BehaviorManager<?> manager : this.activeModel.getBehaviorManagers().values()) {
            consumer.accept(manager);
        }
    }

    private void forBehavior(Consumer<BoneBehavior> consumer) {
        for (String boneId : this.activeModel.getBlueprint().getBones().keySet()) {
            Optional<ModelBone> maybeBone = this.activeModel.getBone(boneId);
            maybeBone.ifPresent(bone -> bone.getImmutableBoneBehaviors().values().forEach(consumer));
        }
    }

    private void forBehaviorRenderer(Consumer<BehaviorRenderer> consumer) {
        for (BehaviorRenderer renderer : this.activeModel.getBehaviorRenderers().values()) {
            if (renderer instanceof HeldItemRenderer) continue;
            consumer.accept(renderer);
        }
    }

    @Override
    public int getTick() {
        return this.activeModel.getModeledEntity().getTick();
    }

    @Override
    public void pushFullUpdate(UUID uuid) {
        this.fullUpdate.add(uuid);
    }

    @Override
    public boolean pollFullUpdate(UUID uuid) {
        return this.fullUpdate.remove(uuid);
    }

    @Override
    @Generated
    public ActiveModel getActiveModel() {
        return this.activeModel;
    }

    @Override
    @Generated
    public Map<String, DisplayBone> getSpawnQueue() {
        return this.spawnQueue;
    }

    @Override
    @Generated
    public Map<String, DisplayBone> getRendered() {
        return this.rendered;
    }

    @Override
    @Generated
    public Map<String, DisplayBone> getDestroyQueue() {
        return this.destroyQueue;
    }

    @Override
    @Generated
    public PivotImpl getPivot() {
        return this.pivot;
    }

    @Override
    @Generated
    public HitboxImpl getHitbox() {
        return this.hitbox;
    }

    @Override
    @Generated
    public boolean isInitialized() {
        return this.initialized;
    }

    public static class PivotImpl
    implements DisplayRenderer.Pivot {
        private PivotOverride pivotOverride;
        private final int id;
        private final UUID uuid = UUID.randomUUID();
        private final DataTracker<Vector3f> position = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<Float> yaw = new DataTracker();
        private final CollectionDataTracker<Integer> passengers = new CollectionDataTracker(new HashSet());

        public Vector3f getMountOffset(float eyeHeight) {
            if (this.pivotOverride == null) {
                return new Vector3f(0.0f, eyeHeight, 0.0f);
            }
            Vector3f offset = this.pivotOverride.getMountOffset();
            return offset == null ? new Vector3f(0.0f, eyeHeight, 0.0f) : offset;
        }

        public void updatePosition(Location location, float eyeHeight) {
            Vector3f offset = this.getMountOffset(eyeHeight);
            this.position.set(new Vector3f().set(location.getX(), location.getY(), location.getZ()).add((Vector3fc)offset));
        }

        @Override
        public void clearDirty() {
            this.yaw.clearDirty();
            this.position.clearDirty();
            this.passengers.clearDirty();
        }

        @Override
        public boolean isOverridden() {
            return this.pivotOverride != null;
        }

        @Generated
        public PivotOverride getPivotOverride() {
            return this.pivotOverride;
        }

        @Override
        @Generated
        public int getId() {
            return this.id;
        }

        @Override
        @Generated
        public UUID getUuid() {
            return this.uuid;
        }

        @Override
        @Generated
        public DataTracker<Vector3f> getPosition() {
            return this.position;
        }

        @Generated
        public DataTracker<Float> getYaw() {
            return this.yaw;
        }

        @Override
        @Generated
        public CollectionDataTracker<Integer> getPassengers() {
            return this.passengers;
        }

        @Generated
        public PivotImpl(int id) {
            this.id = id;
        }
    }

    public class HitboxImpl
    implements DisplayRenderer.Hitbox {
        private final int pivotId;
        private final UUID pivotUuid = UUID.randomUUID();
        private final int hitboxId;
        private final UUID hitboxUuid = UUID.randomUUID();
        private final int shadowId;
        private final UUID shadowUuid = UUID.randomUUID();
        private final DataTracker<Vector3f> position = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<Float> width = new DataTracker();
        private final DataTracker<Float> height = new DataTracker();
        private final DataTracker<Float> shadowRadius = new DataTracker();
        private final DataTracker<Boolean> hitboxVisible = new DataTracker();
        private final DataTracker<Boolean> shadowVisible = new DataTracker();
        private final DataTracker<Boolean> fireVisible = new DataTracker();
        private final DataTracker<Boolean> onFire = new DataTracker();
        private final PooledCollection<DisplayFire> fireDisplay = new PooledCollection<DisplayFire>(() -> new DisplayFireImpl(DisplayRendererImpl.this.entityHandler.getNextEntityId()));

        public void updatePosition(Location location) {
            this.position.set(new Vector3f().set(location.getX(), location.getY(), location.getZ()));
        }

        public void updateFireDisplay() {
            this.fireDisplay.releaseAll();
            if (!this.isFireVisible()) {
                return;
            }
            Map<String, BlueprintBone> fireBones = ModelEngineAPI.getBlueprint("internal_fire").getBones();
            float fireScale = this.width.get().floatValue() * 1.4f;
            if (TMath.isSimilar(fireScale, 0.0f)) {
                return;
            }
            float fireWidth = 1.0f;
            float fireHeight = this.height.get().floatValue() / fireScale;
            float fireYOffset = 0.0f;
            float fireZOffset = 0.3f - (float)((int)fireHeight) * 0.02f;
            int modelType = 0;
            while (fireHeight > 0.0f) {
                ItemModelData modelData = fireBones.get("fire_" + modelType % 2).getModelData();
                float flip = fireHeight / 2.0f % 2.0f == 0.0f ? -1.0f : 1.0f;
                Optional<ItemStack> maybeStack = modelData.createItemStack().stream().findFirst();
                if (maybeStack.isPresent()) {
                    DisplayFire fire = this.fireDisplay.getOrCreate();
                    fire.getFireModel().set(maybeStack.get());
                    fire.getPosition().set(new Vector3f(0.0f, fireYOffset, fireZOffset).mul(fireScale));
                    fire.getScale().set(new Vector3f(flip * fireWidth, 1.4f, 1.0f).mul(fireScale));
                }
                fireHeight -= 0.45f;
                fireYOffset += 0.45f;
                fireWidth *= 0.9f;
                fireZOffset -= 0.03f;
                ++modelType;
            }
        }

        public void setOnFire(boolean flag) {
            this.onFire.set(flag, () -> this.fireDisplay.getInUse().forEach(displayFire -> displayFire.getVisible().set(flag)));
            this.onFire.clearDirty();
        }

        @Override
        public void clearDirty() {
            this.width.clearDirty();
            this.height.clearDirty();
            this.shadowRadius.clearDirty();
            this.hitboxVisible.clearDirty();
            this.shadowVisible.clearDirty();
            this.fireVisible.clearDirty();
            this.fireDisplay.getInUse().forEach(DisplayFire::clearDirty);
        }

        @Override
        @Generated
        public int getPivotId() {
            return this.pivotId;
        }

        @Override
        @Generated
        public UUID getPivotUuid() {
            return this.pivotUuid;
        }

        @Override
        @Generated
        public int getHitboxId() {
            return this.hitboxId;
        }

        @Override
        @Generated
        public UUID getHitboxUuid() {
            return this.hitboxUuid;
        }

        @Override
        @Generated
        public int getShadowId() {
            return this.shadowId;
        }

        @Override
        @Generated
        public UUID getShadowUuid() {
            return this.shadowUuid;
        }

        @Override
        @Generated
        public DataTracker<Vector3f> getPosition() {
            return this.position;
        }

        @Override
        @Generated
        public DataTracker<Float> getWidth() {
            return this.width;
        }

        @Override
        @Generated
        public DataTracker<Float> getHeight() {
            return this.height;
        }

        @Override
        @Generated
        public DataTracker<Float> getShadowRadius() {
            return this.shadowRadius;
        }

        @Override
        @Generated
        public DataTracker<Boolean> getHitboxVisible() {
            return this.hitboxVisible;
        }

        @Override
        @Generated
        public DataTracker<Boolean> getShadowVisible() {
            return this.shadowVisible;
        }

        @Override
        @Generated
        public DataTracker<Boolean> getFireVisible() {
            return this.fireVisible;
        }

        @Generated
        public DataTracker<Boolean> getOnFire() {
            return this.onFire;
        }

        @Override
        @Generated
        public PooledCollection<DisplayFire> getFireDisplay() {
            return this.fireDisplay;
        }

        @Generated
        public HitboxImpl(int pivotId, int hitboxId, int shadowId) {
            this.pivotId = pivotId;
            this.hitboxId = hitboxId;
            this.shadowId = shadowId;
        }
    }
}

