/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.animation;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.config.Property;
import java.util.Locale;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;

public enum ModelState implements Property
{
    IDLE(BlueprintAnimation.LoopMode.LOOP, null),
    WALK(BlueprintAnimation.LoopMode.LOOP, null),
    STRAFE(BlueprintAnimation.LoopMode.LOOP, null),
    JUMP_START(BlueprintAnimation.LoopMode.ONCE, BlueprintAnimation.OverrideMode.OVERRIDE),
    JUMP(BlueprintAnimation.LoopMode.LOOP, BlueprintAnimation.OverrideMode.OVERRIDE),
    JUMP_END(BlueprintAnimation.LoopMode.ONCE, BlueprintAnimation.OverrideMode.OVERRIDE),
    HOVER(BlueprintAnimation.LoopMode.LOOP, BlueprintAnimation.OverrideMode.OVERRIDE),
    FLY(BlueprintAnimation.LoopMode.LOOP, BlueprintAnimation.OverrideMode.OVERRIDE),
    SPAWN(BlueprintAnimation.LoopMode.ONCE, BlueprintAnimation.OverrideMode.OVERRIDE),
    DEATH(BlueprintAnimation.LoopMode.HOLD, BlueprintAnimation.OverrideMode.OVERRIDE);

    private final String path = ConfigProperty.DEFAULT_NAMES.getPath() + "." + this.name();
    private final Object def = this.name().toLowerCase(Locale.ENGLISH);
    private final BlueprintAnimation.LoopMode loopMode;
    private final BlueprintAnimation.OverrideMode override;

    private ModelState(BlueprintAnimation.LoopMode loopMode, BlueprintAnimation.OverrideMode override) {
        this.loopMode = loopMode;
        this.override = override;
    }

    @Nullable
    public static ModelState get(String value) {
        try {
            return ModelState.valueOf(value.toUpperCase(Locale.ENGLISH));
        }
        catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    @Generated
    public String getPath() {
        return this.path;
    }

    @Override
    @Generated
    public Object getDef() {
        return this.def;
    }

    @Generated
    public BlueprintAnimation.LoopMode getLoopMode() {
        return this.loopMode;
    }

    @Generated
    public BlueprintAnimation.OverrideMode getOverride() {
        return this.override;
    }
}

