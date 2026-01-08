/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPosition
 *  net.minecraft.world.level.IBlockAccess
 *  net.minecraft.world.level.RayTrace
 *  net.minecraft.world.level.RayTrace$BlockCollisionOption
 *  net.minecraft.world.level.RayTrace$FluidCollisionOption
 *  net.minecraft.world.level.block.state.IBlockData
 *  net.minecraft.world.phys.Vec3D
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraft.world.phys.shapes.VoxelShapes
 */
package com.ticxo.modelengine.v1_19_R3.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class OcclusionClipContext
extends RayTrace {
    public OcclusionClipContext(Vec3D start, Vec3D end) {
        super(start, end, RayTrace.BlockCollisionOption.c, RayTrace.FluidCollisionOption.a, null);
    }

    public VoxelShape a(IBlockData state, IBlockAccess world, BlockPosition pos) {
        return state.isOpaque() && state.c(world, pos) == VoxelShapes.b() ? VoxelShapes.b() : VoxelShapes.a();
    }
}

