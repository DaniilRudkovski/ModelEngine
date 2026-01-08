/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.model.bone.render.renderer;

import java.util.Map;

public interface RenderQueues<T> {
    public Map<String, T> getSpawnQueue();

    public Map<String, T> getRendered();

    public Map<String, T> getDestroyQueue();

    default public T getQueued(String id) {
        T queued = this.getSpawnQueue().get(id);
        return queued != null ? queued : this.getRendered().get(id);
    }
}

