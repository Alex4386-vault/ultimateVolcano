package com.playmintnetwork.ultimatevolcano;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class VolcanoBomb {
    public Volcano volcano;
    public float bombPower;
    FallingBlock block;
    public int bombRadius;
    public int bombTrailScheduleId;

    public Location landingLocation;
    public Location prevLocation = null;
    public int bombDelay;
    public boolean isTrailOn = false;

    public int lifeTime = 0;

    public boolean isLanded = false;

    VolcanoBomb(Volcano volcano, float bombLaunchPowerX, float bombLaunchPowerZ, float bombPower, int bombRadius, int bombDelay) {
        this.volcano = volcano;
        this.bombPower = bombPower;
        this.bombRadius = bombRadius;
        this.bombDelay = bombDelay;

        Random random = new Random();
        float bombLaunchPowerY = (random.nextFloat() * 2) + 4f;

        int theY = volcano.summitBlock.getY() >= volcano.location.getWorld().getHighestBlockYAt(volcano.location) ? volcano.summitBlock.getY() : volcano.location.getWorld().getHighestBlockYAt(volcano.location)+2;
        this.block = volcano.location.getWorld().spawnFallingBlock(
                new Location(volcano.location.getWorld(), volcano.location.getX(), theY, volcano.location.getZ()),
                VolcanoBombs.volcanoBombFallingBlockMaterial, (byte) 0
        );

        this.block.setVelocity(new Vector(bombLaunchPowerX, bombLaunchPowerY, bombLaunchPowerZ));
        this.block.setGravity(true);
        this.block.setInvulnerable(true);
        this.block.setMetadata("DropItem", new FixedMetadataValue(MainPlugin.plugin, 0));
        this.block.setDropItem(false);

        //MainPlugin.plugin.getLogger().log(Level.INFO, "Volcano Bomb Launched from "+volcano.name+" with power: "+bombLaunchPowerX+","+bombLaunchPowerZ+" with radius:"+bombRadius);


    }

    public static List<Location> generateSphere(Location centerBlock, int radius, boolean hollow) {

        List<Location> circleBlocks = new ArrayList<Location>();

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {

                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));

                    if(distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {

                        Location l = new Location(centerBlock.getWorld(), x, y, z);

                        circleBlocks.add(l);

                    }

                }
            }
        }

        return circleBlocks;
    }

    public void createSmoke() {
        Location loc = block.getLocation();
        loc.getChunk().load();

        loc.getWorld().spawnParticle(
                Particle.EXPLOSION_HUGE,
                loc,
                1
        );
    }

    public void startTrail() {
        if (!isTrailOn) {
            bombTrailScheduleId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MainPlugin.plugin, (Runnable) () -> {
                createSmoke();
            }, 0L, 1L);
            isTrailOn = true;
        }
    }

    public void stopTrail() {
        if (isTrailOn) {
            Bukkit.getScheduler().cancelTask(bombTrailScheduleId);
            isTrailOn = false;
        }
    }

    public void land() {
        stopTrail();
        isLanded = true;

        this.landingLocation = block.getLocation();
        Location loc = block.getLocation();

        if (!VolcanoBombListener.groundChecker(loc, bombRadius)) {
            isLanded = true;
            return;
        }

        MainPlugin.plugin.getLogger().log(Level.ALL, "Volcanic Bomb erupted from "+volcano.name+" just landed @ "+volcano.location.getWorld().getName()+" "+this.landingLocation.getBlockX()+", "+this.landingLocation.getBlockY()+", "+this.landingLocation.getBlockZ()+" with scale of power "+bombPower+" and Radius:"+bombRadius+" with explosiveMode:"+(!volcano.inCrater(landingLocation))+" @ lifeTime: "+lifeTime+" (= "+(lifeTime/4.0)+" seconds)");

        Bukkit.getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, (Runnable) () -> {
            if (bombRadius <= 1) {
                List<Location> bomb = generateSphere(loc, bombRadius, false);

                for (Location bombLoc:bomb) {
                    bombLoc.getBlock().setType(volcano.getRandomBlock());
                }
            } else {
                List<Location> bomb = generateSphere(loc, bombRadius, false);

                for (Location bombLoc:bomb) {
                    Random random = new Random();
                    switch(random.nextInt(3)) {
                        case 0:
                        case 1:
                            bombLoc.getBlock().setType(volcano.getRandomBlock());
                        case 2:
                            bombLoc.getBlock().setType(Material.LAVA);
                            volcano.lavaFlow.lavaCoolData.add(new VolcanoLavaCoolData(bombLoc.getBlock(), volcano.getRandomBlock(), volcano.lavaFlow.settings.flowed));
                    }
                }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, (Runnable) () -> {
                explode();
            });

        }, 1L);

    }

    public void explode() {
        landingLocation.getWorld().createExplosion(landingLocation.add(0,bombRadius,0), bombPower*2, true, !volcano.inCrater(landingLocation));
    }
}
