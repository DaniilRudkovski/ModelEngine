/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  org.bukkit.entity.Display$Billboard
 *  org.bukkit.entity.TextDisplay$TextAlignment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone.type;

import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface NameTag {
    public Vector3f getLocation();

    public void setString(String var1);

    public void setComponent(Component var1);

    public String getJsonString();

    public void setJsonString(String var1);

    public void setComponentSupplier(@Nullable Supplier<Component> var1);

    @Nullable
    public Supplier<String> getJsonStringSupplier();

    public void setJsonStringSupplier(@Nullable Supplier<String> var1);

    public boolean isVisible();

    public void setVisible(boolean var1);

    public void setLineWidth(int var1);

    public int getLineWidth();

    public void setBackgroundColor(int var1);

    public int getBackgroundColor();

    public void setUseDefaultBackgroundColor(boolean var1);

    public boolean isUseDefaultBackgroundColor();

    public void setAlignment(TextDisplay.TextAlignment var1);

    public TextDisplay.TextAlignment getAlignment();

    public void setTextOpacity(byte var1);

    public byte getTextOpacity();

    public void setShadow(boolean var1);

    public boolean isShadow();

    public void setSeeThrough(boolean var1);

    public boolean isSeeThrough();

    public void setBillboard(Display.Billboard var1);

    public Display.Billboard getBillboard();

    public void setScale(Vector3f var1);

    public Vector3f getScale();
}

