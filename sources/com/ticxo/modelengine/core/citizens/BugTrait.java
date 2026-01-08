/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.citizensnpcs.api.trait.Trait
 *  net.citizensnpcs.api.trait.TraitName
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.Particle
 *  org.bukkit.Particle$DustOptions
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 */
package com.ticxo.modelengine.core.citizens;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@TraitName(value="bug")
public class BugTrait
extends Trait {
    private int tick;

    public BugTrait() {
        super("bug");
    }

    public void run() {
        Entity entity = this.npc.getEntity();
        if (entity == null) {
            return;
        }
        EntityHandler handler = ModelEngineAPI.getEntityHandler();
        Location location = entity.getLocation();
        World world = location.getWorld();
        Vector yRot = new Vector(0.0, 0.0, 0.5).rotateAroundY((double)(-handler.getYRot(entity) * ((float)Math.PI / 180)));
        Vector yBodyRot = new Vector(0, 0, 1).rotateAroundY((double)(-handler.getYBodyRot(entity) * ((float)Math.PI / 180)));
        Vector yHeadRot = new Vector(0.0, 0.0, 1.5).rotateAroundY((double)(-handler.getYHeadRot(entity) * ((float)Math.PI / 180)));
        world.spawnParticle(Particle.REDSTONE, location.clone().add(yRot), 1, (Object)new Particle.DustOptions(Color.RED, 0.25f));
        world.spawnParticle(Particle.REDSTONE, location.clone().add(yBodyRot), 1, (Object)new Particle.DustOptions(Color.GREEN, 0.25f));
        world.spawnParticle(Particle.REDSTONE, location.clone().add(yHeadRot), 1, (Object)new Particle.DustOptions(Color.BLUE, 0.25f));
    }
}

