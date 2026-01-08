/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin;

import java.util.logging.Logger;
import org.mineskin.ClientBuilder;
import org.mineskin.GenerateClient;
import org.mineskin.MiscClient;
import org.mineskin.QueueClient;
import org.mineskin.SkinsClient;

public interface MineSkinClient {
    public static ClientBuilder builder() {
        return ClientBuilder.create();
    }

    public QueueClient queue();

    public GenerateClient generate();

    public SkinsClient skins();

    public MiscClient misc();

    public Logger getLogger();
}

