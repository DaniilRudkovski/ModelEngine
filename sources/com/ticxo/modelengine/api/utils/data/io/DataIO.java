/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.utils.data.io;

import com.ticxo.modelengine.api.utils.data.io.SavedData;
import java.util.Optional;

public interface DataIO {
    default public Optional<SavedData> save() {
        SavedData data = new SavedData();
        this.save(data);
        return data.keySet().isEmpty() ? Optional.empty() : Optional.of(data);
    }

    public void save(SavedData var1);

    public void load(SavedData var1);
}

