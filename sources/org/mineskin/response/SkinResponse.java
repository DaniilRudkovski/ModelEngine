/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.response;

import org.mineskin.data.SkinInfo;
import org.mineskin.response.MineSkinResponse;

public interface SkinResponse
extends MineSkinResponse<SkinInfo> {
    public SkinInfo getSkin();
}

