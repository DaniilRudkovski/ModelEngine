/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.utils.config;

import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import lombok.Generated;

public class ConfigCache {
    private boolean emptyZero;
    private boolean renderFire;
    private boolean eagerGenerate;
    private String classic;
    private String slim;

    public void updateConfig() {
        this.emptyZero = ConfigProperty.EMPTY_ZERO.getBoolean();
        this.renderFire = ConfigProperty.RENDER_FIRE.getBoolean();
        this.eagerGenerate = ConfigProperty.EAGER_GENERATE.getBoolean();
        this.classic = ConfigProperty.DEFAULT_CLASSIC_SKIN.getString();
        this.slim = ConfigProperty.DEFAULT_SLIM_SKIN.getString();
    }

    @Generated
    public boolean isEmptyZero() {
        return this.emptyZero;
    }

    @Generated
    public boolean isRenderFire() {
        return this.renderFire;
    }

    @Generated
    public boolean isEagerGenerate() {
        return this.eagerGenerate;
    }

    @Generated
    public String getClassic() {
        return this.classic;
    }

    @Generated
    public String getSlim() {
        return this.slim;
    }
}

