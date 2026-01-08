/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.profile.PlayerProfile
 *  lombok.Generated
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.SkullMeta
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.model.bone.behavior;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.skin.LimbData;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.UserLimb;
import com.ticxo.modelengine.api.utils.MojangAPI;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.concurrent.CompletableFuture;
import lombok.Generated;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

public class UserLimbImpl
extends AbstractBoneBehavior<UserLimbImpl>
implements UserLimb {
    private final String limbType;
    private final DataTracker<ItemStack> generatedStack = new DataTracker<ItemStack>(this.createHead(), ItemStack::isSimilar);
    private LimbData limbData;
    private boolean slim;
    private final Matrix4f originalTransform = new Matrix4f();
    private boolean generating;
    private String placeholder;

    public UserLimbImpl(ModelBone bone, BoneBehaviorType<UserLimbImpl> type, BoneBehaviorData data) {
        super(bone, type, data);
        this.limbType = (String)data.get("type");
    }

    @Override
    public void onApply() {
        this.bone.setRenderer(true);
        this.bone.setModel(this.generatedStack.get());
    }

    @Override
    public void preTransformDecompose() {
        if (this.limbData == null) {
            return;
        }
        this.originalTransform.set((Matrix4fc)this.bone.getTransformMatrix());
        this.bone.getTransformMatrix().translate((Vector3fc)this.limbData.translation()).rotate((Quaternionfc)new Quaternionf().rotateY((float)Math.PI)).scale((Vector3fc)this.limbData.scale());
    }

    @Override
    public void postTransformRecompose() {
        this.bone.getTransformMatrix().set((Matrix4fc)this.originalTransform);
    }

    @Override
    public void onFinalize() {
        this.generatedStack.ifDirty(this.bone::setModel);
        this.generatedStack.clearDirty();
    }

    @Override
    public void setTexture(@Nullable Player player) {
        if (player == null) {
            this.generatedStack.set(this.createHead());
            return;
        }
        this.setTexture(player.getPlayerProfile());
    }

    @Override
    public void setTexture(@Nullable PlayerProfile profile) {
        this.generatedStack.set(this.createHead());
        if (profile == null) {
            return;
        }
        switch (profile.getTextures().getSkinModel()) {
            case CLASSIC: {
                this.slim = false;
                break;
            }
            case SLIM: {
                this.slim = true;
            }
        }
        this.limbData = ModelEngineAPI.getAPI().getSkinSplitter().getLimb(this.limbType, this.slim);
        if (this.limbData.isHead()) {
            ItemStack head = this.createHead();
            head.editMeta(itemMeta -> ((SkullMeta)itemMeta).setPlayerProfile(profile));
            this.generatedStack.set(head);
            return;
        }
        this.generating = true;
        ((CompletableFuture)ModelEngineAPI.getUserLimbRegistry().getSkinUrlFromKey(this.getPlaceholder(), this.limbType, this.slim).thenApply(url -> this.limbData.createItem((String)url))).handle((stack, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return null;
            }
            if (this.generating) {
                this.generatedStack.set((ItemStack)stack);
            }
            return null;
        });
        ((CompletableFuture)((CompletableFuture)MojangAPI.getRawSkinData(profile).thenCompose(base64 -> ModelEngineAPI.getUserLimbRegistry().getSkinUrlFromSource((String)base64, this.limbType, this.slim))).thenApply(url -> this.limbData.createItem((String)url))).handle((stack, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return null;
            }
            this.generatedStack.set((ItemStack)stack);
            this.generating = false;
            return null;
        });
    }

    @Override
    public String getPlaceholder() {
        return this.placeholder != null ? this.placeholder : (this.slim ? ModelEngineAPI.getConfigCache().getSlim() : ModelEngineAPI.getConfigCache().getClassic());
    }

    private ItemStack createHead() {
        return new ItemStack(Material.PLAYER_HEAD);
    }

    @Override
    @Generated
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
}

