/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.lod;

import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.utils.math.TMath;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.UUID;
import lombok.Generated;

public class BoneSnapshotHandler {
    private final DisplayBone bone;
    private final Object2ObjectMap<UUID, Snapshot> snapshots = new Object2ObjectOpenHashMap();
    private Snapshot snapshot;

    public void recordSnapshot() {
        this.snapshot = new Snapshot(this.bone.getPosition().get().hashCode(), this.bone.getLeftRotation().get().hashCode(), this.bone.getScale().get().hashCode(), this.bone.getRightRotation().get().hashCode());
    }

    public byte getUpdate(UUID uuid) {
        Snapshot lastSnapshot = (Snapshot)this.snapshots.put((Object)uuid, (Object)this.snapshot);
        if (lastSnapshot == null) {
            return 15;
        }
        return lastSnapshot.compare(this.snapshot);
    }

    public void remove(UUID uuid) {
        this.snapshots.remove((Object)uuid);
    }

    @Generated
    public BoneSnapshotHandler(DisplayBone bone) {
        this.bone = bone;
    }

    public record Snapshot(int position, int leftRotation, int scale, int rightRotation) {
        public byte compare(Snapshot snapshot) {
            byte b2 = 0;
            b2 = TMath.setBit(b2, 0, this.position != snapshot.position);
            b2 = TMath.setBit(b2, 1, this.leftRotation != snapshot.leftRotation);
            b2 = TMath.setBit(b2, 2, this.scale != snapshot.scale);
            b2 = TMath.setBit(b2, 3, this.rightRotation != snapshot.rightRotation);
            return b2;
        }
    }
}

