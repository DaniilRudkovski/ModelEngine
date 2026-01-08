/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Color
 */
package com.ticxo.modelengine.core.command;

import com.ticxo.modelengine.api.model.ActiveModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Color;

public final class ModelOptionParser {
    private Boolean doDamageTint;
    private Boolean lockPitch;
    private Boolean lockYaw;
    private Boolean showHitbox;
    private Boolean showShadow;
    private Double stepHeight;
    private Double scale;
    private Double hitboxScale;
    private Integer viewRadius;
    private Color color;
    public Boolean hideSelf = true;
    public Boolean hideSelfDisguise = false;
    public Boolean pivotOverride = true;

    public static ModelOptionParser parse(int offset, String[] argss) {
        ModelOptionParser options = new ModelOptionParser();
        if (argss.length <= offset) {
            return options;
        }
        String[] args = Arrays.copyOfRange(argss, offset, argss.length);
        block30: for (int i = 0; i < args.length; ++i) {
            switch (args[i].toLowerCase()) {
                case "dodamagetint": {
                    options.doDamageTint = ModelOptionParser.getNextBoolean(args, ++i);
                    continue block30;
                }
                case "lockpitch": {
                    options.lockPitch = ModelOptionParser.getNextBoolean(args, ++i);
                    continue block30;
                }
                case "lockyaw": {
                    options.lockYaw = ModelOptionParser.getNextBoolean(args, ++i);
                    continue block30;
                }
                case "showhitbox": {
                    options.showHitbox = ModelOptionParser.getNextBoolean(args, ++i);
                    continue block30;
                }
                case "showshadow": {
                    options.showShadow = ModelOptionParser.getNextBoolean(args, ++i);
                    continue block30;
                }
                case "stepheight": {
                    options.stepHeight = ModelOptionParser.getNextDouble(args, ++i);
                    continue block30;
                }
                case "scale": {
                    options.scale = ModelOptionParser.getNextDouble(args, ++i);
                    continue block30;
                }
                case "hitboxscale": {
                    options.hitboxScale = ModelOptionParser.getNextDouble(args, ++i);
                    continue block30;
                }
                case "viewradius": {
                    options.viewRadius = ModelOptionParser.getNextInteger(args, ++i);
                    continue block30;
                }
                case "hideself": {
                    options.hideSelf = ModelOptionParser.getNextBoolean(args, ++i);
                    continue block30;
                }
                case "hideselfdisguise": {
                    options.hideSelfDisguise = ModelOptionParser.getNextBoolean(args, ++i);
                    continue block30;
                }
                case "color": {
                    String colorString = ModelOptionParser.getNextString(args, ++i);
                    if (colorString.startsWith("#")) {
                        colorString = colorString.substring(1);
                    }
                    options.color = Color.fromRGB((int)Integer.parseInt(colorString, 16));
                    continue block30;
                }
                case "pivotoverride": {
                    options.pivotOverride = ModelOptionParser.getNextBoolean(args, ++i);
                }
            }
        }
        return options;
    }

    public static List<String> getTabCompletion(int offset, String[] argss) {
        String[] args = Arrays.copyOfRange(argss, offset, argss.length);
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("doDamageTint", "lockPitch", "lockYaw", "showHitbox", "showShadow", "stepHeight", "scale", "hitboxScale", "viewRadius", "hideSelfDisguise", "color", "pivotOverride", "hideSelf"));
            return completions;
        }
        String lastArg = args[args.length - 2].toLowerCase();
        if (!ModelOptionParser.isValueExpected(lastArg)) {
            completions.addAll(Arrays.asList("doDamageTint", "lockPitch", "lockYaw", "showHitbox", "showShadow", "stepHeight", "scale", "hitboxScale", "viewRadius", "hideSelfDisguise", "color", "pivotOverride", "hideSelf"));
        }
        return completions;
    }

    private static boolean isValueExpected(String lastArg) {
        return lastArg.matches("stepheight|scale|hitboxscale|viewradius|color");
    }

    public void applyDisguiseOptions(ActiveModel activeModel) {
        if (this.scale != null) {
            activeModel.setScale(this.scale);
        }
        if (this.hitboxScale != null) {
            activeModel.setHitboxScale(this.hitboxScale);
        }
        if (this.doDamageTint != null) {
            activeModel.setCanHurt(this.doDamageTint);
        }
        if (this.lockPitch != null) {
            activeModel.setLockPitch(this.lockPitch);
        }
        if (this.lockYaw != null) {
            activeModel.setLockYaw(this.lockYaw);
        }
        if (this.showHitbox != null) {
            activeModel.setHitboxVisible(this.showHitbox);
        }
        if (this.showShadow != null) {
            activeModel.setShadowVisible(this.showShadow);
        }
        if (this.viewRadius != null) {
            activeModel.getModeledEntity().getBase().setRenderRadius(this.viewRadius);
        }
        if (this.stepHeight != null) {
            activeModel.getModeledEntity().getBase().setMaxStepHeight(this.stepHeight);
        }
        if (this.color != null) {
            activeModel.setDefaultTint(this.color);
        }
    }

    private static Boolean getNextBoolean(String[] args, int index) {
        if (index < args.length) {
            String nextArg = args[index].toLowerCase();
            return nextArg.equals("true") || !nextArg.equals("false");
        }
        return true;
    }

    private static Double getNextDouble(String[] args, int index) {
        try {
            return index < args.length ? Double.valueOf(Double.parseDouble(args[index])) : null;
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer getNextInteger(String[] args, int index) {
        try {
            return index < args.length ? Integer.valueOf(Integer.parseInt(args[index])) : null;
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private static String getNextString(String[] args, int index) {
        try {
            return index < args.length ? args[index] : null;
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}

