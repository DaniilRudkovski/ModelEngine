/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.model.bone.manager;

import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;

public interface MountData {
    public <T extends BehaviorManager<? extends Mount> & MountManager> T getMainMountManager();

    public <T extends BehaviorManager<? extends Mount> & MountManager> void setMainMountManager(T var1);
}

