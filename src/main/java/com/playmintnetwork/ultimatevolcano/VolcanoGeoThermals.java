package com.playmintnetwork.ultimatevolcano;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class VolcanoGeoThermals implements Listener {
    public Volcano volcano;
    public boolean enable = false;
    public int scheduleID = -1;
    public int geoThermalTicks = 200;
    public List<Block> geoThermalActiveBlocks = new ArrayList<Block>();
    public List<Block> geoThermalAffectedBlocks = new ArrayList<Block>();

    public void registerEventHandler() {
        Bukkit.getServer().getPluginManager().registerEvents(this, MainPlugin.plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (volcano.affected(e.getPlayer().getLocation()) &&
        (volcano.enabled || volcano.autoStart.status == VolcanoCurrentStatus.MAJOR_ACTIVITY ||
         volcano.autoStart.status == VolcanoCurrentStatus.MINOR_ACTIVITY ||
         volcano.autoStart.status == VolcanoCurrentStatus.ERUPTING)
        ) {
            Random random = new Random();
            int x = e.getPlayer().getLocation().getBlockX();
            int z = e.getPlayer().getLocation().getBlockZ();

            int percent = random.nextInt(1000);
            if (percent < 100) {
                int deltaX = random.nextInt(volcano.zone);
                int deltaZ = random.nextInt(volcano.zone);

                deltaX = percent < 50 ? deltaX : -deltaX;
                deltaZ = percent < 50 ? deltaZ : -deltaZ;

                int theY = volcano.location.getWorld().getHighestBlockYAt(x+deltaX, z+deltaZ);


                volcano.location.getWorld().spawnParticle(
                        Particle.CAMPFIRE_SIGNAL_SMOKE,
                        new Location(volcano.location.getWorld(),
                                x+deltaX, theY, z+deltaZ),
                        1
                );

            }




        }
    }

    public boolean blockChunkHasPlayer(Block block) {
        int howManyPlayers = 0;
        for (int i = 0; i < block.getChunk().getEntities().length; i++) {
            if (block.getChunk().getEntities()[i] instanceof Player) {
                howManyPlayers++;
            }
        }
        if (howManyPlayers <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public List<Block> getGeoThermalAffectedBlocks() {
        List<Block> stuff = geoThermalActiveBlocks;
        return stuff;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (volcano.geoThermals.enable && volcano.affected(e.getItemDrop().getLocation()) && shouldIDoIt()) {
            switch (e.getItemDrop().getItemStack().getType()) {
                case PORKCHOP:
                    e.getItemDrop().getItemStack().setType(Material.COOKED_PORKCHOP);
                    break;
                case BEEF:
                    e.getItemDrop().getItemStack().setType(Material.COOKED_BEEF);
                    break;
                case RABBIT:
                    e.getItemDrop().getItemStack().setType(Material.COOKED_RABBIT);
                    break;
                case CHICKEN:
                    e.getItemDrop().getItemStack().setType(Material.COOKED_CHICKEN);
                    break;
                case SALMON:
                    e.getItemDrop().getItemStack().setType(Material.COOKED_SALMON);
                    break;
                case LEGACY_RAW_FISH:
                    e.getItemDrop().getItemStack().setType(Material.LEGACY_COOKED_FISH);
                    break;
                case MUTTON:
                    e.getItemDrop().getItemStack().setType(Material.COOKED_MUTTON);
                    break;
                default:
            }
        }
    }

    public boolean shouldIDoIt() {
        int a = new Random().nextInt(1000);
        switch(volcano.autoStart.status) {
            case DORMANT:
                return (a < 10);
            case MINOR_ACTIVITY:
                return (a < 50);
            case MAJOR_ACTIVITY:
                return (a < 100);
            case ERUPTING:
                return (a < 200);
            default:
                return false;
        }
    }

    /*
    public boolean geyserProbability() {
        int a = new Random().nextInt(1000);
        switch(volcano.autoStart.status) {
            case DORMANT:
                return (a < 1);
            case MINOR_ACTIVITY:
                return (a < 2);
            case MAJOR_ACTIVITY:
                return (a < 10);
            case ERUPTING:
                return (a < 15);
            default:
                return false;
        }
    }
    */

    public void registerTask() {
        if (scheduleID == -1) {
            scheduleID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
                    MainPlugin.plugin,
                    () -> {
                        //showGeoThermalActivity();
                    }, 0, geoThermalTicks
            );
        }
    }

    public void unregisterTask() {
        if (scheduleID != -1) {
            Bukkit.getScheduler().cancelTask(scheduleID);
            scheduleID = -1;
            //removeGeoThermals();
        }
    }
/*
    public void removeGeoThermals() {
        geoThermalActiveBlocks.clear();
        geoThermalAffectedBlocks.clear();
    }

    public void triggerGeyser(Block block) {
        if (shouldIDoIt()) {
            if (geyserProbability()) {
                int geyserHeight = new Random().nextInt(10)+1;
                int geyserTicks = new Random().nextInt(100)+1;
                block.getWorld().createExplosion(block.getX(), block.getY(), block.getZ(), 2F, true, false);
                Block tempBlock = block;
                for (int i = 1; i <= geyserHeight; i++) {
                    tempBlock = tempBlock.getRelative(BlockFace.UP);
                    if (tempBlock.getType().equals(Material.AIR)) {
                        tempBlock.setType(Material.WATER);
                    } else {
                        geyserHeight = i;
                    }
                }
                final int t = geyserHeight;
                final Block dudet = tempBlock;
                Bukkit.getScheduler().scheduleSyncDelayedTask(
                        MainPlugin.plugin,
                        () -> {
                            Block tempyBlock = dudet;
                            for (int i = t; i >= 1; i++) {
                                tempyBlock.setType(Material.WATER);
                                tempyBlock = tempyBlock.getRelative(BlockFace.DOWN);
                            }
                        },
                        geyserTicks
                );
            }
        }
    }

    public void showGeoThermalActivity() {
        for (Block block:getGeoThermalAffectedBlocks()) {
            if (shouldIDoIt()) {
                block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 0, 30);
                triggerGeyser(block);
            }
        }
    }
    */
}
