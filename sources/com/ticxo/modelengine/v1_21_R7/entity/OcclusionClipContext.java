/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.ticxo.modelengine.v1_21_R7.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OcclusionClipContext
extends ClipContext {
    public OcclusionClipContext(Vec3 start, Vec3 end) {
        super(start, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, (Entity)null);
    }

    public VoxelShape getBlockShape(BlockState state, BlockGetter world, BlockPos pos) {
        return state.canOcclude() && state.getOcclusionShape() == Shapes.block() ? Shapes.block() : Shapes.empty();
    }
}

