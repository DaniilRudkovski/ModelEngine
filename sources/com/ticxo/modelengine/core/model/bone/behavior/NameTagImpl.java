/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
 *  org.bukkit.Location
 *  org.bukkit.entity.Display$Billboard
 *  org.bukkit.entity.TextDisplay$TextAlignment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.NameTag;
import java.util.function.Supplier;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class NameTagImpl
extends AbstractBoneBehavior<NameTagImpl>
implements NameTag {
    private static final String EMPTY_STRING = "{\"text\":\"\"}";
    private final Vector3f location = new Vector3f();
    private String jsonString = "{\"text\":\"\"}";
    private Supplier<String> jsonStringSupplier;
    private boolean visible;
    private int backgroundColor = 0x40000000;
    private boolean useDefaultBackgroundColor = true;
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private int lineWidth;
    private byte textOpacity = (byte)-1;
    private boolean shadow = false;
    private boolean seeThrough = true;
    private Display.Billboard billboard = Display.Billboard.CENTER;
    private Vector3f scale = new Vector3f(1.0f);

    public NameTagImpl(ModelBone bone, BoneBehaviorType<NameTagImpl> type, BoneBehaviorData data) {
        super(bone, type, data);
    }

    @Override
    public void onApply() {
        Location baseLocation = this.bone.calculatePivotLocation();
        this.bone.getBlueprintBone().getLocalPosition().rotateY(-this.bone.getYaw() * ((float)Math.PI / 180), this.location).add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ());
    }

    @Override
    public void onFinalize() {
        if (this.jsonStringSupplier != null) {
            this.setJsonString(this.jsonStringSupplier.get());
        }
        Location baseLocation = this.bone.calculatePivotLocation();
        this.bone.getGlobalTransform().mutatePosition(vector3f -> vector3f.rotateY(-this.bone.getYaw() * ((float)Math.PI / 180), this.location).add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ()));
    }

    @Override
    public void setString(String name) {
        this.setComponent((Component)Component.text((String)name));
    }

    @Override
    public void setComponent(Component component) {
        this.setJsonString((String)GsonComponentSerializer.gson().serialize(component));
    }

    @Override
    public void setJsonString(String json) {
        this.jsonString = json != null ? json : EMPTY_STRING;
    }

    @Override
    public void setComponentSupplier(@Nullable Supplier<Component> component) {
        this.jsonStringSupplier = component == null ? null : () -> (String)GsonComponentSerializer.gson().serialize((Component)component.get());
    }

    @Override
    public void setUseDefaultBackgroundColor(boolean bool) {
        this.useDefaultBackgroundColor = bool;
        if (bool) {
            this.backgroundColor = 0x40000000;
        }
    }

    @Override
    @Generated
    public Vector3f getLocation() {
        return this.location;
    }

    @Override
    @Generated
    public String getJsonString() {
        return this.jsonString;
    }

    @Override
    @Generated
    public Supplier<String> getJsonStringSupplier() {
        return this.jsonStringSupplier;
    }

    @Override
    @Generated
    public void setJsonStringSupplier(Supplier<String> jsonStringSupplier) {
        this.jsonStringSupplier = jsonStringSupplier;
    }

    @Override
    @Generated
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    @Generated
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    @Generated
    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    @Override
    @Generated
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    @Generated
    public boolean isUseDefaultBackgroundColor() {
        return this.useDefaultBackgroundColor;
    }

    @Override
    @Generated
    public TextDisplay.TextAlignment getAlignment() {
        return this.alignment;
    }

    @Override
    @Generated
    public void setAlignment(TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
    }

    @Override
    @Generated
    public int getLineWidth() {
        return this.lineWidth;
    }

    @Override
    @Generated
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    @Generated
    public byte getTextOpacity() {
        return this.textOpacity;
    }

    @Override
    @Generated
    public void setTextOpacity(byte textOpacity) {
        this.textOpacity = textOpacity;
    }

    @Override
    @Generated
    public boolean isShadow() {
        return this.shadow;
    }

    @Override
    @Generated
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    @Override
    @Generated
    public boolean isSeeThrough() {
        return this.seeThrough;
    }

    @Override
    @Generated
    public void setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
    }

    @Override
    @Generated
    public Display.Billboard getBillboard() {
        return this.billboard;
    }

    @Override
    @Generated
    public void setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
    }

    @Override
    @Generated
    public Vector3f getScale() {
        return this.scale;
    }

    @Override
    @Generated
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
}

