/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.ItemDisplay
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.SkullMeta
 *  org.bukkit.profile.PlayerTextures
 *  org.bukkit.profile.PlayerTextures$SkinModel
 *  org.bukkit.util.Transformation
 *  org.joml.Quaternionf
 */
package com.ticxo.modelengine.core.command.sub;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.generator.skin.LimbData;
import com.ticxo.modelengine.api.generator.skin.SkinGeneratorService;
import com.ticxo.modelengine.api.generator.skin.SkinSplitter;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

public class TestCommand
extends AbstractCommand {
    public TestCommand(AbstractCommand parent) {
        super(parent);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        SkinGeneratorService skinGen = ModelEngineAPI.getAPI().getSkinGenerator();
        if (!skinGen.isEnabled()) {
            return false;
        }
        if (sender instanceof Player) {
            Player player = (Player)sender;
            PlayerTextures texture = player.getPlayerProfile().getTextures();
            URL skin = texture.getSkin();
            if (skin == null) {
                return false;
            }
            Location location = player.getLocation();
            boolean slim = texture.getSkinModel() == PlayerTextures.SkinModel.SLIM;
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(skin.toURI()).timeout(Duration.of(30L, ChronoUnit.SECONDS)).GET().build();
                HttpClient client = HttpClient.newHttpClient();
                ((CompletableFuture)client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).thenApply(HttpResponse::body)).thenAccept(data -> {
                    try {
                        SkinSplitter splitter = ModelEngineAPI.getAPI().getSkinSplitter();
                        Map<String, byte[]> skins = splitter.splitSkin((byte[])data, slim);
                        for (Map.Entry<String, byte[]> entry : skins.entrySet()) {
                            skinGen.generate(entry.getValue()).handle((url, throwable) -> {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                    return null;
                                }
                                LimbData limb = splitter.getLimb((String)entry.getKey(), slim);
                                ItemStack itemStack = limb.createItem((String)url);
                                DualTicker.queueSyncTask(() -> location.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
                                    itemDisplay.setRotation(location.getYaw() + 180.0f, 0.0f);
                                    itemDisplay.setTransformation(new Transformation(limb.translation().mul(0.9375f), new Quaternionf(), limb.scale().mul(0.9375f), new Quaternionf()));
                                    itemDisplay.setItemStack(itemStack);
                                }));
                                return null;
                            });
                        }
                        DualTicker.queueSyncTask(() -> {
                            LimbData headLimb = splitter.getLimb("head", slim);
                            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                            itemStack.editMeta(itemMeta -> ((SkullMeta)itemMeta).setPlayerProfile(player.getPlayerProfile()));
                            location.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
                                itemDisplay.setRotation(player.getYaw() + 180.0f, player.getPitch());
                                itemDisplay.setTransformation(new Transformation(headLimb.translation().mul(0.9375f), new Quaternionf(), headLimb.scale().mul(0.9375f), new Quaternionf()));
                                itemDisplay.setItemStack(itemStack);
                            });
                        });
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "modelengine.command.test";
    }

    @Override
    public boolean isConsoleFriendly() {
        return false;
    }

    @Override
    public String getName() {
        return "test";
    }
}

