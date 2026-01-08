/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.core.model;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Generated;

public class BoneSequencer {
    private final Set<String> processedBones = new HashSet<String>();
    private final List<String> boneOrders = new ArrayList<String>();

    protected void reorder(Map<String, ModelBone> roots) {
        int size;
        this.processedBones.clear();
        this.boneOrders.clear();
        do {
            size = this.boneOrders.size();
            this.order(roots);
        } while (size != this.boneOrders.size());
    }

    private void order(Map<String, ModelBone> roots) {
        for (ModelBone bone : roots.values()) {
            if (this.processedBones.contains(bone.getBoneId())) {
                this.order(bone.getChildren());
                continue;
            }
            if (!bone.getTickDependencies().isEmpty() && !this.processedBones.containsAll(bone.getTickDependencies())) continue;
            this.boneOrders.add(bone.getBoneId());
            this.processedBones.add(bone.getBoneId());
            this.order(bone.getChildren());
        }
    }

    @Generated
    public BoneSequencer() {
    }

    @Generated
    public List<String> getBoneOrders() {
        return this.boneOrders;
    }
}

