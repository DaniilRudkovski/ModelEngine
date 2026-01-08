/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.md_5.bungee.api.chat.BaseComponent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.util.Consumer
 *  org.bukkit.util.io.BukkitObjectInputStream
 *  org.bukkit.util.io.BukkitObjectOutputStream
 *  org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
 */
package com.ticxo.modelengine.api.utils.data;

import com.ticxo.modelengine.api.utils.data.ComponentUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Consumer;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemUtils {
    public static byte[] encodeItemStack(ItemStack item) {
        try {
            byte[] var3;
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream((OutputStream)outputStream);){
                dataOutput.writeObject((Object)item);
                var3 = outputStream.toByteArray();
            }
            return var3;
        }
        catch (IOException var9) {
            throw new RuntimeException(var9);
        }
    }

    public static String encodeItemStackToString(ItemStack item) {
        return ItemUtils.encode(ItemUtils.encodeItemStack(item));
    }

    public static ItemStack decodeItemStack(byte[] buf) {
        try {
            ItemStack var3;
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
                 BukkitObjectInputStream dataInput = new BukkitObjectInputStream((InputStream)inputStream);){
                var3 = (ItemStack)dataInput.readObject();
            }
            return var3;
        }
        catch (IOException | ClassNotFoundException var9) {
            throw new RuntimeException(var9);
        }
    }

    public static ItemStack decodeItemStack(String data) {
        return ItemUtils.decodeItemStack(ItemUtils.decode(data));
    }

    public static String encode(byte[] buf) {
        return Base64.getEncoder().encodeToString(buf);
    }

    public static byte[] decode(String src) {
        try {
            return Base64.getDecoder().decode(src);
        }
        catch (IllegalArgumentException var4) {
            try {
                return Base64Coder.decodeLines((String)src);
            }
            catch (Exception var3) {
                throw var4;
            }
        }
    }

    public static void name(ItemStack stack, Component component) {
        ItemUtils.meta(stack, meta -> meta.setDisplayNameComponent(ComponentUtil.base(component)));
    }

    public static void lore(ItemStack stack, Component ... components) {
        ArrayList<BaseComponent[]> list = new ArrayList<BaseComponent[]>();
        for (Component component : components) {
            if (component == null) continue;
            list.add(ComponentUtil.base(component));
        }
        ItemUtils.meta(stack, meta -> meta.setLoreComponents((List)list));
    }

    public static void lore(ItemStack stack, Collection<Component> components) {
        ArrayList<BaseComponent[]> list = new ArrayList<BaseComponent[]>();
        for (Component component : components) {
            if (component == null) continue;
            list.add(ComponentUtil.base(component));
        }
        ItemUtils.meta(stack, meta -> meta.setLoreComponents((List)list));
    }

    public static <T extends ItemMeta> void meta(ItemStack stack, Consumer<T> metaConsumer) {
        try {
            ItemMeta meta = stack.getItemMeta();
            metaConsumer.accept((Object)meta);
            stack.setItemMeta(meta);
        }
        catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}

