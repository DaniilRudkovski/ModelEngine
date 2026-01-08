/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.utils.math;

import com.ticxo.modelengine.api.utils.math.TMath;
import lombok.Generated;

public class ColorHelper {
    public static int getAlpha(int argb) {
        return argb >>> 24;
    }

    public static int getRed(int argb) {
        return argb >> 16 & 0xFF;
    }

    public static int getGreen(int argb) {
        return argb >> 8 & 0xFF;
    }

    public static int getBlue(int argb) {
        return argb & 0xFF;
    }

    public static int getArgb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int getArgbFloat(float alpha, float red, float green, float blue) {
        return ColorHelper.getArgb(TMath.floor(TMath.clamp(alpha * 255.0f, 0.0f, 1.0f)), TMath.floor(TMath.clamp(red * 255.0f, 0.0f, 1.0f)), TMath.floor(TMath.clamp(green * 255.0f, 0.0f, 1.0f)), TMath.floor(TMath.clamp(blue * 255.0f, 0.0f, 1.0f)));
    }

    public static HSL toHSL(int color) {
        return ColorHelper.toHSL(ColorHelper.getAlpha(color), ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color));
    }

    public static HSL toHSL(int alpha, int red, int green, int blue) {
        HSL color = new HSL();
        color.alpha = alpha;
        float r = (float)red / 255.0f;
        float g = (float)green / 255.0f;
        float b2 = (float)blue / 255.0f;
        float cMax = Math.max(r, Math.max(g, b2));
        float cMin = Math.min(r, Math.min(g, b2));
        float delta = cMax - cMin;
        color.lightness = (cMax + cMin) * 0.5f;
        if (Math.abs(delta) <= 1.0E-5f) {
            color.hue = 0.0f;
            color.saturation = 0.0f;
        } else {
            if (cMax == r) {
                float f;
                for (f = (g - b2) / delta; f < 0.0f; f += 6.0f) {
                }
                color.hue = 60.0f * (f % 6.0f);
            } else if (cMax == g) {
                color.hue = 60.0f * ((b2 - r) / delta + 2.0f);
            } else if (cMax == b2) {
                color.hue = 60.0f * ((r - g) / delta + 4.0f);
            }
            color.saturation = delta / (1.0f - Math.abs(2.0f * color.lightness - 1.0f));
        }
        color.sanitize();
        return color;
    }

    public static int fromHSL(HSL hsl) {
        hsl.sanitize();
        float c2 = (1.0f - Math.abs(2.0f * hsl.lightness - 1.0f)) * hsl.saturation;
        float x = c2 * (1.0f - Math.abs(hsl.hue / 60.0f % 2.0f - 1.0f));
        float m = hsl.lightness - c2 * 0.5f;
        float r = 0.0f;
        float g = 0.0f;
        float b2 = 0.0f;
        switch ((int)hsl.hue / 60 % 6) {
            case 0: {
                r = c2;
                g = x;
                break;
            }
            case 1: {
                r = x;
                g = c2;
                break;
            }
            case 2: {
                g = c2;
                b2 = x;
                break;
            }
            case 3: {
                g = x;
                b2 = c2;
                break;
            }
            case 4: {
                r = x;
                b2 = c2;
                break;
            }
            case 5: {
                r = c2;
                b2 = x;
            }
        }
        return ColorHelper.getArgb(hsl.alpha, TMath.floor((r + m) * 255.0f), TMath.floor((g + m) * 255.0f), TMath.floor((b2 + m) * 255.0f));
    }

    public static int mixColor(int first, int second) {
        return ColorHelper.getArgb(ColorHelper.getAlpha(first) * ColorHelper.getAlpha(second) / 255, ColorHelper.getRed(first) * ColorHelper.getRed(second) / 255, ColorHelper.getGreen(first) * ColorHelper.getGreen(second) / 255, ColorHelper.getBlue(first) * ColorHelper.getBlue(second) / 255);
    }

    public static int lerpColor(int colorA, int colorB, float ratio) {
        int aA = ColorHelper.getAlpha(colorA);
        int aR = ColorHelper.getRed(colorA);
        int aG = ColorHelper.getGreen(colorA);
        int aB = ColorHelper.getBlue(colorA);
        int bA = ColorHelper.getAlpha(colorB);
        int bR = ColorHelper.getRed(colorB);
        int bG = ColorHelper.getGreen(colorB);
        int bB = ColorHelper.getBlue(colorB);
        int fA = TMath.floor(Math.sqrt(TMath.lerp(aA * aA, bA * bA, ratio)));
        int fR = TMath.floor(Math.sqrt(TMath.lerp(aR * aR, bR * bR, ratio)));
        int fG = TMath.floor(Math.sqrt(TMath.lerp(aG * aG, bG * bG, ratio)));
        int fB = TMath.floor(Math.sqrt(TMath.lerp(aB * aB, bB * bB, ratio)));
        return ColorHelper.getArgb(fA, fR, fG, fB);
    }

    public static class HSL {
        public int alpha;
        public float hue;
        public float saturation;
        public float lightness;

        public HSL() {
        }

        public void sanitize() {
            while (this.hue < 0.0f) {
                this.hue += 360.0f;
            }
            this.saturation = Math.max(Math.min(this.saturation, 1.0f), 0.0f);
            this.lightness = Math.max(Math.min(this.lightness, 1.0f), 0.0f);
        }

        @Generated
        public HSL(int alpha, float hue, float saturation, float lightness) {
            this.alpha = alpha;
            this.hue = hue;
            this.saturation = saturation;
            this.lightness = lightness;
        }
    }
}

