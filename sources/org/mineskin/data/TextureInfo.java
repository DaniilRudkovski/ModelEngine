/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.data;

import org.mineskin.data.SkinHashes;
import org.mineskin.data.SkinUrls;
import org.mineskin.data.ValueAndSignature;

public record TextureInfo(ValueAndSignature data, SkinHashes hash, SkinUrls url) {
}

