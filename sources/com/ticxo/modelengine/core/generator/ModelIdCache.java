/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.core.generator;

import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;

public class ModelIdCache {
    protected final Map<String, Integer> cachedId = new HashMap<String, Integer>();
    protected final Queue<Integer> unused = new LinkedList<Integer>();
    private final transient Map<Integer, String> dataToModel = new HashMap<Integer, String>();
    private final transient Set<String> pendingRemove = new HashSet<String>();
    protected int nextId = 1;
    private transient int maxId;

    public void gatherExistingIds(BaseItemEnum baseItem, Map<String, BlueprintBone> requested) {
        this.maxId = 0;
        this.dataToModel.clear();
        for (Map.Entry<String, Integer> entry : this.cachedId.entrySet()) {
            String modelId = entry.getKey();
            Integer dataId = entry.getValue();
            if (requested.containsKey(modelId)) {
                BlueprintBone bone = requested.remove(modelId);
                bone.setDataId(dataId);
                bone.setBaseItem(baseItem);
                ItemModelData.SubModel submodel = bone.getModelData().getMultiModels().getSubModel(modelId);
                submodel.setData(dataId);
                submodel.setItem(baseItem);
                this.dataToModel.put(dataId, modelId);
                this.maxId = Math.max(dataId, this.maxId);
                continue;
            }
            this.pendingRemove.add(modelId);
            this.unused.add(this.cachedId.get(modelId));
        }
        this.pendingRemove.forEach(this.cachedId::remove);
        this.pendingRemove.clear();
    }

    public void generateNewIds(BaseItemEnum baseItem, String modelId, BlueprintBone bone) {
        int n;
        if (this.unused.isEmpty()) {
            int n2 = this.nextId;
            n = n2;
            this.nextId = n2 + 1;
        } else {
            n = this.unused.poll();
        }
        int id = n;
        bone.setDataId(id);
        bone.setBaseItem(baseItem);
        ItemModelData.SubModel submodel = bone.getModelData().getMultiModels().getSubModel(modelId);
        submodel.setData(id);
        submodel.setItem(baseItem);
        this.maxId = Math.max(id, this.maxId);
        this.dataToModel.put(id, modelId);
        this.cachedId.put(modelId, id);
    }

    public void endSession() {
        Iterator iterator = this.unused.iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            this.maxId = Math.max(this.maxId, id);
        }
        this.nextId = this.maxId + 1;
    }

    public void sortedIterate(BiConsumer<String, Integer> consumer) {
        for (int i = 1; i < this.nextId; ++i) {
            String id = this.dataToModel.get(i);
            if (id == null) continue;
            consumer.accept(id.replace(":", "/"), i);
        }
    }

    public void cleanUp() {
        this.dataToModel.clear();
        this.pendingRemove.clear();
    }

    public int getCacheLoad() {
        return this.cachedId.size();
    }
}

