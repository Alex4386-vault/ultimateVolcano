package com.playmintnetwork.ultimatevolcano;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.logging.Level;

public class VolcanoBombListener implements Listener {

    public static int bombTrackingScheduleId = -1;

    public void registerTask() {
        if (bombTrackingScheduleId < 0) {
            bombTrackingScheduleId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MainPlugin.plugin, (Runnable) () -> {
                for (Volcano volcano: MainPlugin.listVolcanoes) {
                    volcano.bombs.trackAll();
                }
            }, 5L, 5L);
        }
    }

    public void unregisterTask() {
        if (bombTrackingScheduleId >= 0) {
            Bukkit.getScheduler().cancelTask(bombTrackingScheduleId);
            bombTrackingScheduleId = -1;
        }
    }

    public void registerEventListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, MainPlugin.plugin);
    }


    public static boolean groundChecker(Location location, int offset) {
        return (location.getBlockY() - location.getWorld().getHighestBlockYAt(location) <= offset);
    }

      /*
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            FallingBlock fb = (FallingBlock) event.getEntity();
            for (Volcano volcano: MainPlugin.listVolcanoes) {
                for (VolcanoBomb bomb: volcano.bombs.bombList) {
                    if (bomb.block.equals(event.getEntity())) {
                        if (!groundChecker(bomb.block.getLocation(), bomb.bombRadius)) {
                            Location loc = fb.getLocation();
                            Vector vec = fb.getVelocity();
                            Material mat = fb.getMaterial();

                            FallingBlock newFallblock = loc.getWorld().spawnFallingBlock(loc, mat, (byte) 0);
                            newFallblock.setVelocity(vec);
                            newFallblock.setInvulnerable(true);
                            newFallblock.setGravity(true);
                            newFallblock.setMetadata("DropItem", fb.getMetadata("DropItem").get(0));

                            bomb.block = newFallblock;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBombFall(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock)
        {
            Block block = event.getBlock();


            for (Volcano volcano: MainPlugin.listVolcanoes) {
                Iterator<VolcanoBomb> bombIterator = volcano.bombs.bombList.iterator();

                while (bombIterator.hasNext()) {
                    VolcanoBomb bomb = bombIterator.next();

                    if (bomb.block.getLocation().equals(block.getLocation()) && groundChecker(bomb.block.getLocation(), bomb.bombRadius)) {
                        bomb.land();
                        bombIterator.remove();
                    } else if (bomb.isLanded) {
                        bombIterator.remove();
                    }
                }
            }

        }
    }
    */

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        for (Entity entity: player.getLocation().getChunk().getEntities()) {
            if (entity instanceof FallingBlock) {
                for (Volcano volcano: MainPlugin.listVolcanoes) {
                    if (volcano.location.getWorld().equals(player.getWorld())) {
                        for (VolcanoBomb bomb: volcano.bombs.bombList) {
                            if (entity.equals(bomb.block)) {
                                bomb.startTrail();
                            }
                        }
                    }
                }
            }
        }
    }
}
