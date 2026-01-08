/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.api.nms.ui;

import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Generated;
import org.bukkit.entity.Player;

public interface AnvilHandler {
    public void openAnvil(Player var1, Function<String, Boolean> var2, Supplier<Boolean> var3);

    public void reopenAnvil(Player var1);

    public void consumeConfirm(Player var1);

    public void consumeClose(Player var1);

    public void removeAnvil(Player var1);

    public void closeAnvil(Player var1);

    public boolean isListening(Player var1);

    public Menu getAnvilMenu(Player var1);

    public static class Menu {
        private final int id;
        private final Function<String, Boolean> onInput;
        private final Supplier<Boolean> onClose;
        private String value;

        public boolean confirm() {
            return this.onInput.apply(this.value);
        }

        public boolean close() {
            return this.onClose.get();
        }

        @Generated
        public Menu(int id, Function<String, Boolean> onInput, Supplier<Boolean> onClose) {
            this.id = id;
            this.onInput = onInput;
            this.onClose = onClose;
        }

        @Generated
        public int getId() {
            return this.id;
        }

        @Generated
        public Function<String, Boolean> getOnInput() {
            return this.onInput;
        }

        @Generated
        public Supplier<Boolean> getOnClose() {
            return this.onClose;
        }

        @Generated
        public String getValue() {
            return this.value;
        }

        @Generated
        public void setValue(String value) {
            this.value = value;
        }
    }
}

