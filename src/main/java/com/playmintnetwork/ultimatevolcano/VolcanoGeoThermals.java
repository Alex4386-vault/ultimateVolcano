package com.playmintnetwork.ultimatevolcano;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class VolcanoGeoThermals implements Listener {
    public Volcano volcano;
    public boolean enable = true;
    public int scheduleID = -1;
    public int geoThermalUpdateRate = 200;

    public void registerEventHandler() {
        Bukkit.getServer().getPluginManager().registerEvents(this, MainPlugin.plugin);
    }

    public Block getRandomGeothermallyAffectedBlock() {
        Location loc;
        Random random = new Random();
        int x = volcano.location.getBlockX() + (int) (random.nextGaussian() * (volcano.crater.craterRadius * 2));
        int z = volcano.location.getBlockZ() + (int) (random.nextGaussian() * (volcano.crater.craterRadius * 2));
        loc = new Location(volcano.location.getWorld(),
                x,
                volcano.location.getWorld().getHighestBlockYAt(x,z),
                z);


        return loc.getBlock();
    }


    public void runGeothermalCycle() {
        Random random = new Random();
        int thermalCycleCount = (int) (random.nextInt(100) * (volcano.currentHeight / (double)(volcano.generator.heightLimit >= 255 ? 255:volcano.generator.heightLimit)));
        for (int i = 0; i < thermalCycleCount; i++) {
            if (shouldIDoIt()) {
                Block block;
                block = getRandomGeothermallyAffectedBlock();

                showGeoThermalActivity(block);
            }
        }
    }

    public void showGeoThermalActivity(Block block) {
        //Bukkit.getLogger().log(Level.INFO, "Showing Geothermal Activity of "+volcano.name+" @ "+
        //        block.getWorld()+" "+block.getX()+","+block.getY()+","+block.getZ());

        for (int i = 0; i < 5; i++) {
            block.getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), i);
        }

        if (Arrays.asList(VolcanoLavaFlowExplode.blockToBurned).contains(block.getType())) {
            block.setType(VolcanoLavaFlow.getBlockAfterBurned(block.getType()));
        }
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
                return (a < 50);
            case MINOR_ACTIVITY:
                return (a < 150);
            case MAJOR_ACTIVITY:
                return (a < 400);
            case ERUPTING:
                return (a < 700);
            default:
                return false;
        }
    }

    public void registerTask() {
        if (scheduleID == -1) {
            scheduleID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
                    MainPlugin.plugin,
                    (Runnable) () -> {
                        if (enable) {
                            runGeothermalCycle();
                        }
                    }, 0, geoThermalUpdateRate
            );
        }
    }

    public void unregisterTask() {
        if (scheduleID != -1) {
            Bukkit.getScheduler().cancelTask(scheduleID);
            scheduleID = -1;
        }
    }
}
