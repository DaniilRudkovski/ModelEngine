/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.citizensnpcs.api.CitizensAPI
 *  net.citizensnpcs.api.npc.NPC
 *  net.citizensnpcs.api.npc.NPCRegistry
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.ticxo.modelengine.core.citizens;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.citizens.ModelTrait;
import com.ticxo.modelengine.core.command.MECommand;
import java.util.ArrayList;
import java.util.List;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CitizensCommand
extends AbstractCommand {
    public CitizensCommand(AbstractCommand parent) {
        super(parent);
        this.addSubCommands(new ModelCommand(this));
        this.addSubCommands(new StateCommand(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "modelengine.command.npc";
    }

    @Override
    public boolean isConsoleFriendly() {
        return true;
    }

    @Override
    public String getName() {
        return "npc";
    }

    private static void getNPCIdTabComplete(List<String> list) {
        CitizensAPI.getNPCRegistries().forEach(npcs -> npcs.sorted().forEach(npc -> {
            if (npc.isSpawned() && npc.hasTrait(ModelTrait.class)) {
                list.add(npcs.getName() + ":" + npc.getId() + ":[" + npc.getName().replace(" ", "-") + "]");
            }
        }));
    }

    private static String tryGetOrDefault(String[] args, int index, String def) {
        if (args.length <= index) {
            return def;
        }
        return args[index];
    }

    private static NPC getNPC(String id) {
        String[] val = id.split(":");
        if (val.length < 2) {
            throw new IllegalArgumentException("NPC ID must be formatted as <registry>:<NPC ID>");
        }
        NPCRegistry reg = CitizensAPI.getNamedNPCRegistry((String)val[0]);
        if (reg == null) {
            throw new IllegalArgumentException("Unknown NPC registry: " + val[0]);
        }
        NPC npc = reg.getById(Integer.parseInt(val[1]));
        if (npc == null) {
            throw new IllegalArgumentException("Unknown NPC ID: " + val[1]);
        }
        return npc;
    }

    private static class ModelCommand
    extends AbstractCommand {
        public ModelCommand(AbstractCommand parent) {
            super(parent);
        }

        @Override
        public boolean onCommand(CommandSender sender, String[] args) {
            if (args.length < 1) {
                return false;
            }
            NPC npc = CitizensCommand.getNPC(args[0]);
            ModelTrait trait = (ModelTrait)npc.getTraitNullable(ModelTrait.class);
            if (trait == null) {
                return false;
            }
            if (!npc.isSpawned()) {
                sender.sendMessage(ChatColor.RED + "Please spawn the NPC before editing.");
                return true;
            }
            ModeledEntity modeledEntity = trait.getOrCreateModeledEntity();
            if (modeledEntity == null) {
                sender.sendMessage(ChatColor.RED + "An error occurred while retrieving the model of this NPC.");
                return true;
            }
            if (args.length < 2) {
                if (modeledEntity.getModels().isEmpty()) {
                    sender.sendMessage("This NPC has no models.");
                    return true;
                }
                StringBuilder builder = new StringBuilder("Models: ");
                for (String active : modeledEntity.getModels().keySet()) {
                    builder.append(active).append(", ");
                }
                builder.delete(builder.length() - 2, builder.length());
                sender.sendMessage(builder.toString());
                return true;
            }
            if (args.length >= 3) {
                String modelId = args[2];
                switch (args[1]) {
                    case "add": {
                        boolean showBase = Boolean.parseBoolean(CitizensCommand.tryGetOrDefault(args, 3, "false"));
                        boolean overrideHitbox = Boolean.parseBoolean(CitizensCommand.tryGetOrDefault(args, 4, "true"));
                        ActiveModel model = ModelEngineAPI.createActiveModel(modelId);
                        if (model == null) {
                            return false;
                        }
                        modeledEntity.setBaseEntityVisible(showBase);
                        modeledEntity.addModel(model, overrideHitbox).ifPresent(ActiveModel::destroy);
                        sender.sendMessage("Added model " + modelId + " to " + npc.getName());
                        break;
                    }
                    case "remove": {
                        modeledEntity.removeModel(modelId).ifPresent(ActiveModel::destroy);
                        if (modeledEntity.getModels().isEmpty()) {
                            modeledEntity.setBaseEntityVisible(true);
                        }
                        sender.sendMessage("Removed model " + modelId + " from " + npc.getName());
                    }
                }
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String[] args) {
            ArrayList<String> list = new ArrayList<String>();
            String arg = args[args.length - 1];
            switch (args.length) {
                case 1: {
                    CitizensCommand.getNPCIdTabComplete(list);
                    break;
                }
                case 2: {
                    if ("add".startsWith(arg)) {
                        list.add("add");
                    }
                    if (!"remove".startsWith(arg)) break;
                    list.add("remove");
                    break;
                }
                case 3: {
                    switch (args[1]) {
                        case "add": {
                            MECommand.getModelIdTabComplete(list, arg);
                            break;
                        }
                        case "remove": {
                            ModelTrait trait = (ModelTrait)CitizensCommand.getNPC(args[0]).getTraitNullable(ModelTrait.class);
                            if (trait == null) break;
                            MECommand.getModelIdTabComplete(list, arg, trait.getModeledEntity());
                        }
                    }
                    break;
                }
                case 4: {
                    if (!"add".equals(args[1])) break;
                    list.add("[showBaseEntity]");
                    if ("true".startsWith(arg)) {
                        list.add("true");
                    }
                    if (!"false".startsWith(arg)) break;
                    list.add("false");
                    break;
                }
                case 5: {
                    if (!"add".equals(args[1])) break;
                    list.add("[overrideHitbox]");
                    if ("true".startsWith(arg)) {
                        list.add("true");
                    }
                    if (!"false".startsWith(arg)) break;
                    list.add("false");
                }
            }
            return list;
        }

        @Override
        public String getPermissionNode() {
            return "modelengine.command.npc.model";
        }

        @Override
        public boolean isConsoleFriendly() {
            return true;
        }

        @Override
        public String getName() {
            return "model";
        }
    }

    private static class StateCommand
    extends AbstractCommand {
        public StateCommand(AbstractCommand parent) {
            super(parent);
        }

        @Override
        public boolean onCommand(CommandSender sender, String[] args) {
            if (args.length < 1) {
                return false;
            }
            NPC npc = CitizensCommand.getNPC(args[0]);
            ModelTrait trait = (ModelTrait)npc.getTraitNullable(ModelTrait.class);
            if (trait == null) {
                return false;
            }
            if (!npc.isSpawned()) {
                sender.sendMessage(ChatColor.RED + "Please spawn the NPC before editing.");
                return true;
            }
            ModeledEntity modeledEntity = trait.getOrCreateModeledEntity();
            if (modeledEntity == null) {
                sender.sendMessage(ChatColor.RED + "An error occurred while retrieving the model of this NPC.");
                return true;
            }
            ActiveModel activeModel = modeledEntity.getModel(args[1]).orElse(null);
            if (activeModel == null) {
                return false;
            }
            if (args.length < 3) {
                StringBuilder builder = new StringBuilder();
                builder.append(args[1]).append(": ");
                for (IAnimationProperty animation : activeModel.getAnimationHandler().getAnimations().values()) {
                    builder.append(animation.getName()).append(", ");
                }
                builder.delete(builder.length() - 2, builder.length());
                sender.sendMessage(builder.toString());
                return true;
            }
            if (args.length < 4) {
                return false;
            }
            String stateId = args[3];
            switch (args[2]) {
                case "add": {
                    if (!activeModel.getBlueprint().getAnimations().containsKey(stateId)) {
                        return false;
                    }
                    int lerpIn = Integer.parseInt(CitizensCommand.tryGetOrDefault(args, 4, "0"));
                    int lerpOut = Integer.parseInt(CitizensCommand.tryGetOrDefault(args, 5, "0"));
                    double speed = Double.parseDouble(CitizensCommand.tryGetOrDefault(args, 6, "1"));
                    activeModel.getAnimationHandler().playAnimation(stateId, (double)lerpIn / 20.0, (double)lerpOut / 20.0, speed, true);
                    sender.sendMessage("Added state " + stateId + " to " + npc.getName());
                    break;
                }
                case "remove": {
                    boolean ignoreLerp = Boolean.parseBoolean(CitizensCommand.tryGetOrDefault(args, 4, "false"));
                    if (ignoreLerp) {
                        activeModel.getAnimationHandler().forceStopAnimation(stateId);
                    } else {
                        activeModel.getAnimationHandler().forceStopAnimation(stateId);
                    }
                    sender.sendMessage("Removed state " + stateId + " from " + npc.getName());
                }
            }
            return false;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String[] args) {
            ArrayList<String> list = new ArrayList<String>();
            String arg = args[args.length - 1];
            switch (args.length) {
                case 1: {
                    CitizensCommand.getNPCIdTabComplete(list);
                    break;
                }
                case 2: {
                    ModelTrait trait = (ModelTrait)CitizensCommand.getNPC(args[0]).getTraitNullable(ModelTrait.class);
                    if (trait == null) break;
                    MECommand.getModelIdTabComplete(list, arg, trait.getModeledEntity());
                    break;
                }
                case 3: {
                    if ("add".startsWith(arg)) {
                        list.add("add");
                    }
                    if (!"remove".startsWith(arg)) break;
                    list.add("remove");
                    break;
                }
                case 4: {
                    ModelTrait trait = (ModelTrait)CitizensCommand.getNPC(args[0]).getTraitNullable(ModelTrait.class);
                    if (trait == null) {
                        return list;
                    }
                    ModeledEntity model = trait.getModeledEntity();
                    ActiveModel activeModel = model.getModel(args[1]).orElse(null);
                    if (activeModel == null) {
                        return list;
                    }
                    switch (args[2]) {
                        case "add": {
                            MECommand.getStateTabComplete(list, arg, activeModel.getBlueprint());
                            break;
                        }
                        case "remove": {
                            MECommand.getStateTabComplete(list, arg, activeModel);
                        }
                    }
                    break;
                }
                case 5: {
                    switch (args[2]) {
                        case "add": {
                            list.add("[lerpin]");
                            break;
                        }
                        case "remove": {
                            list.add("[ignoreLerp]");
                            if ("true".startsWith(arg)) {
                                list.add("true");
                            }
                            if (!"false".startsWith(arg)) break;
                            list.add("false");
                        }
                    }
                    break;
                }
                case 6: {
                    if (!"add".equals(args[2])) break;
                    list.add("[lerpout]");
                    break;
                }
                case 7: {
                    if (!"add".equals(args[2])) break;
                    list.add("[speed]");
                }
            }
            return list;
        }

        @Override
        public String getPermissionNode() {
            return "modelengine.command.npc.state";
        }

        @Override
        public boolean isConsoleFriendly() {
            return true;
        }

        @Override
        public String getName() {
            return "state";
        }
    }
}

