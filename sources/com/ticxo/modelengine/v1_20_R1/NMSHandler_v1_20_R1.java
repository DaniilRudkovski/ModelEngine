/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.MatrixUtil
 *  lombok.Generated
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.PacketListenerPlayOut
 *  net.minecraft.network.protocol.game.PacketPlayOutMount
 *  org.apache.commons.lang3.tuple.Triple
 *  org.bukkit.util.Transformation
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.v1_20_R1;

import com.mojang.math.MatrixUtil;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.model.PivotOverrideRegistry;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.LeashRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.NameTagRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.SegmentRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.SubHitboxRenderer;
import com.ticxo.modelengine.api.model.render.DisplayRenderer;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.model.render.ModelRendererParser;
import com.ticxo.modelengine.api.nms.NMSHandler;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.network.NetworkHandler;
import com.ticxo.modelengine.api.vfx.render.VFXDisplayRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRendererParser;
import com.ticxo.modelengine.v1_20_R1.entity.EntityHandlerImpl;
import com.ticxo.modelengine.v1_20_R1.network.NetworkHandlerImpl;
import com.ticxo.modelengine.v1_20_R1.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R1.network.utils.Packets;
import com.ticxo.modelengine.v1_20_R1.parser.behavior.LeashParser;
import com.ticxo.modelengine.v1_20_R1.parser.behavior.MountParser;
import com.ticxo.modelengine.v1_20_R1.parser.behavior.NameTagParser;
import com.ticxo.modelengine.v1_20_R1.parser.behavior.SegmentParser;
import com.ticxo.modelengine.v1_20_R1.parser.behavior.SubHitboxParser;
import com.ticxo.modelengine.v1_20_R1.parser.model.DisplayParser;
import com.ticxo.modelengine.v1_20_R1.parser.vfx.VFXDisplayParser;
import java.util.Collection;
import java.util.Set;
import lombok.Generated;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.util.Transformation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class NMSHandler_v1_20_R1
implements NMSHandler {
    private final EntityHandler entityHandler = new EntityHandlerImpl();
    private final NetworkHandler networkHandler = new NetworkHandlerImpl();
    private final RenderParsers globalParsers = this.createParsers();
    private final VFXDisplayParser vfxDisplayParser = new VFXDisplayParser();

    @Override
    public RenderParsers createParsers() {
        RenderParsers parsers = new RenderParsers();
        parsers.registerModelParser(renderer -> renderer instanceof DisplayRenderer, DisplayParser::new);
        parsers.registerBehaviorParser(renderer -> renderer instanceof MountRenderer, MountParser::new);
        parsers.registerBehaviorParser(renderer -> renderer instanceof LeashRenderer, LeashParser::new);
        parsers.registerBehaviorParser(renderer -> renderer instanceof NameTagRenderer, NameTagParser::new);
        parsers.registerBehaviorParser(renderer -> renderer instanceof SubHitboxRenderer, SubHitboxParser::new);
        parsers.registerBehaviorParser(renderer -> renderer instanceof SegmentRenderer, SegmentParser::new);
        return parsers;
    }

    @Override
    public void sendPivotOverrides() {
        PivotOverrideRegistry registry = ModelEngineAPI.getPivotOverrideRegistry();
        Set<PivotOverride> dirty = registry.collectDirty();
        Packets packets = new Packets();
        for (PivotOverride override : dirty) {
            PacketDataSerializer byteBuf = NetworkUtils.createByteBuf(buf -> {
                buf.d(override.getId());
                Collection list = (Collection)override.getPassengers().get();
                buf.d(list.size());
                list.forEach(arg_0 -> ((PacketDataSerializer)buf).d(arg_0));
            });
            packets.add((Packet<PacketListenerPlayOut>)new PacketPlayOutMount(byteBuf));
            override.getPassengers().clearDirty();
        }
        NetworkUtils.sendBundled(ServerInfo.getOnlinePlayers(), packets);
    }

    @Override
    public <T extends ModelRenderer> ModelRendererParser<T> getModelRendererParser(T renderer) {
        return this.globalParsers.getModelParser(renderer);
    }

    @Override
    public <T extends BehaviorRenderer> BehaviorRendererParser<T> getBehaviorRendererParser(T renderer) {
        return this.globalParsers.getBehaviorParser(renderer);
    }

    @Override
    public <T extends VFXRenderer> VFXRendererParser<T> getVFXRendererParser(T renderer) {
        if (renderer instanceof VFXDisplayRenderer) {
            return this.vfxDisplayParser;
        }
        return null;
    }

    @Override
    public Transformation decompose(Matrix4f transform) {
        float f = 1.0f / transform.m33();
        Triple triple = MatrixUtil.a((Matrix3f)new Matrix3f((Matrix4fc)transform).scale(f));
        return new Transformation(transform.getTranslation(new Vector3f()).mul(f), new Quaternionf((Quaternionfc)triple.getLeft()), new Vector3f((Vector3fc)triple.getMiddle()), new Quaternionf((Quaternionfc)triple.getRight()));
    }

    @Override
    @Generated
    public EntityHandler getEntityHandler() {
        return this.entityHandler;
    }

    @Override
    @Generated
    public NetworkHandler getNetworkHandler() {
        return this.networkHandler;
    }

    @Override
    @Generated
    public RenderParsers getGlobalParsers() {
        return this.globalParsers;
    }
}

