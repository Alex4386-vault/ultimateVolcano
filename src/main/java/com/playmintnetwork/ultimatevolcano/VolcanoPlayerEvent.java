package com.playmintnetwork.ultimatevolcano;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class VolcanoPlayerEvent implements Listener {
    public static boolean disllowLavaFlowOnNonCrater = true;

    public void registerEventHandler() {
        Bukkit.getServer().getPluginManager().registerEvents(this, MainPlugin.plugin);
    }

    @EventHandler
    public void onPlayerBucketEmptyEvent (PlayerBucketEmptyEvent event){
        Block block = event.getBlockClicked();
        if(event.getBucket() == Material.LAVA_BUCKET){
            for (Volcano volcano : MainPlugin.listVolcanoes) {
                if(volcano.location.getWorld() != null && volcano.location.getWorld().equals(block.getWorld())){
                    if(volcano.affected(block.getLocation())) {
                        if (volcano.inCrater(block.getLocation())) {
                            if (volcano.autoStart.pourLavaStart) {
                                block.getWorld().createExplosion(volcano.location.getX(), volcano.location.getY(), volcano.location.getZ(), 4f, false, false);
                                volcano.start();
                                Bukkit.getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
                                    volcano.stop();
                                    volcano.autoStart.status = VolcanoCurrentStatus.MAJOR_ACTIVITY;
                                }, volcano.autoStart.eruptionTimer);
                            } else {
                                event.getPlayer().sendMessage(ChatColor.GRAY+"Emptying Lava Bucket in Crater is NOT allowed on volcano, and configuration of it is diabled.");
                            }
                        } else {
                            if (disllowLavaFlowOnNonCrater) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage(ChatColor.GRAY+"Emptying Lava Bucket is NOT allowed on volcano.");
                            }
                        }
                    }
                }
            }
        } else if (event.getBucket() == Material.WATER_BUCKET && !event.getPlayer().isOp()) {
            for (Volcano volcano : MainPlugin.listVolcanoes) {
                if (volcano.inCrater(block.getLocation())) {
                    block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, .4f, 4);
                    event.setCancelled(true);
                }
            }
        }
    }
}
