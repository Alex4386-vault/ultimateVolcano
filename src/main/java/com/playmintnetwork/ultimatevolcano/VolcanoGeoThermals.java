package com.playmintnetwork.ultimatevolcano;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
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
        //Bukkit.getServer().getPluginManager().registerEvents(this, MainPlugin.plugin);
    }

    /*
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (volcano.affected(e.getPlayer().getLocation()) &&
        (volcano.enabled || volcano.autoStart.status == VolcanoCurrentStatus.MAJOR_ACTIVITY ||
         volcano.autoStart.status == VolcanoCurrentStatus.MINOR_ACTIVITY ||
         volcano.autoStart.status == VolcanoCurrentStatus.ERUPTING)
        ) {
            // Grab the player's location, rounded block value
            Location playerPos = e.getPlayer().getLocation();
            if (playerPos.getWorld().getBiome(playerPos.getBlockX(), playerPos.getBlockZ()).equals(Biome.OCEAN) ||
                playerPos.getWorld().getBiome(playerPos.getBlockX(), playerPos.getBlockZ()).equals(Biome.DEEP_OCEAN) ||
                playerPos.getWorld().getBiome(playerPos.getBlockX(), playerPos.getBlockZ()).equals(Biome.FROZEN_OCEAN) ) {
                return;
            }
            // Radius
            int r = 7;
            // Loop through all blocks within the radius (cube, not sphere)
            for(int x = (r * -1); x <= r; x++) {
                for(int y = (r * -1); y <= r; y++) {
                    for(int z = (r * -1); z <= r; z++) {

                        // Grab the current block
                        Block b = playerPos.getWorld().getBlockAt(playerPos.getBlockX() + x, playerPos.getBlockY() + y, playerPos.getBlockZ() + z);



                        if (b.getType().equals(Material.WATER) || b.getType().equals(Material.STATIONARY_WATER)) {

                                if (!b.getRelative(BlockFace.UP).getType().equals(Material.WATER) &&
                                    !b.getRelative(BlockFace.UP).getType().equals(Material.STATIONARY_WATER) &&
                                        (b.getRelative(BlockFace.DOWN).getType().equals(Material.STATIONARY_WATER) ||
                                         b.getRelative(BlockFace.DOWN).getType().equals(Material.WATER)) &&
                                        (b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType().equals(Material.STATIONARY_WATER) ||
                                         b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType().equals(Material.WATER))
                                    ) {

                                    if (!geoThermalActiveBlocks.contains(b)) {
                                        geoThermalActiveBlocks.add(b);
                                    }
                                } else {
                                    if (!geoThermalAffectedBlocks.contains(b)) {
                                        geoThermalAffectedBlocks.add(b);
                                    }
                                }
                        }

                    }
                }
            }
        }

        geoThermalActiveBlocks.removeIf(i -> !blockChunkHasPlayer(i));
        geoThermalAffectedBlocks.removeIf(i -> !blockChunkHasPlayer(i));

        if (geoThermalActiveBlocks.contains(e.getPlayer().getLocation().getWorld().getBlockAt(e.getPlayer().getLocation()))) {
            e.getPlayer().setFireTicks(20);
        }
    }
    */

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
