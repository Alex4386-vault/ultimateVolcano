package com.playmintnetwork.ultimatevolcano;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class VolcanoLavaFlow implements Listener {
    private Random rand = new Random();
    public Volcano volcano;
    public List<FallingBlock> currentFB = new ArrayList<FallingBlock>();
    public List<Chunk> loadChunkList = new ArrayList<>();
    private Material[] explode = { Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2, Material.LONG_GRASS, Material.YELLOW_FLOWER, Material.SIGN, Material.WOOD, Material.WOOD_DOOR, Material.WOOD_STAIRS, Material.WOOD_PLATE};
    private Material[] explodeAndRemove = { Material.WATER, Material.STATIONARY_WATER, Material.SNOW, Material.SNOW_BLOCK };
    private Material[] toMagmaBlock = { Material.STONE, Material.GRAVEL, Material.COBBLESTONE, Material.GRASS, Material.DIRT, Material.CLAY, Material.CONCRETE, Material.SAND };
    private int scheduleID = -1;
    public VolcanoLavaFlowSettings settings = new VolcanoLavaFlowSettings();

    public void registerEventHandler() {
        Bukkit.getServer().getPluginManager().registerEvents(this, MainPlugin.plugin);
    }

    public VolcanoLavaFlow() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this, MainPlugin.plugin);
    }

    public boolean explodeLava(Volcano volcano, Block block) {
        int n = rand.nextInt(500) + 1;
        //Classic!
        if ((10 <= n) && (n < 20)) {
            makeExplosion(block, 2F, true, false);
            return true;
        } else if (n == 50) {
            makeExplosion(block, 2F, true, false);
            return true;
        } else if (n == 44) {
            makeExplosion(block, 6F, true, true);
            return true;
        } else if (n == 4) {
            makeExplosion(block, 4F, true, false);
            return true;
        } else {
            return true;
        }
    }


    public boolean lavaCollideWater(Volcano volcano, Block block) {
        int n = rand.nextInt(100) + 1;
        //Classic!
        if ((1 <= n) && (n < 20)) {
            makeExplosion(block, 1F, true, false);
            return true;
        } else if (n == 50) {
            makeExplosion(block, 2F, true, false);
            return true;
        } else if (n == 44) {
            makeExplosion(block, 6F, true, true);
            return true;
        } else if (n == 4) {
            makeExplosion(block, 4F, true, false);
            return true;
        } else {
            return true;
        }
    }

    public void makeExplosion(Block block, float power, boolean setFire, boolean breakBlocks) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
            block.getLocation().getWorld().createExplosion(block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ(), power, setFire, breakBlocks);
        }, 0L);
    }

    //MultiThreading LAVA DESTROYS EVERYTHING
    public class nearBlockUpdate extends Thread {
        public Volcano volcano;
        public Block block;
        public void setup(Volcano volcano, Block block) {
            this.volcano = volcano;
            this.block = block;
        }
        public void run() {
            try {
                Block nearByBlock = block;
                for (int a = 0; a < 6; a++) {
                    switch (a) {
                        case 0:
                            nearByBlock = block.getRelative(BlockFace.SOUTH);
                            break;
                        case 1:
                            nearByBlock = block.getRelative(BlockFace.WEST);
                            break;
                        case 2:
                            nearByBlock = block.getRelative(BlockFace.NORTH);
                            break;
                        case 3:
                            nearByBlock = block.getRelative(BlockFace.EAST);
                            break;
                        case 4:
                            nearByBlock = block.getRelative(BlockFace.UP);
                            break;
                        case 5:
                            nearByBlock = block.getRelative(BlockFace.DOWN);
                            break;
                        default:
                            break;
                    }

                    if (Arrays.asList(explode).contains(nearByBlock.getType())) {
                        if (volcano.affected(nearByBlock.getLocation())) {
                            if (Arrays.asList(explode).contains(nearByBlock.getType())) {
                                blockSet(nearByBlock, Material.AIR);
                                explodeLava(volcano, nearByBlock);
                                final Block theBlock = nearByBlock;
                                MainPlugin.plugin.getServer().getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
                                    lavaCooldown(theBlock);
                                }, settings.flowed * 20);
                            }
                        }
                    } else if (Arrays.asList(explodeAndRemove).contains(nearByBlock.getType())) {
                        lavaCollideWater(volcano, nearByBlock);
                        final Block theBlock = nearByBlock;
                        MainPlugin.plugin.getServer().getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
                            lavaCooldown(theBlock);
                        }, settings.flowed * 20);
                    }
                }
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.INFO, "Exception Occurred in Multi-Threading lavaBlockCollision Updates");
                e.printStackTrace();
                return;
            }
            return;
        }
    }

    public void blockSet(Block block, Material material) {
        if (material == null) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
            block.setType(material);
        }, 0L);
    }

    //MultiThreading LAVA Updates
    public class blockUpdate extends Thread {
        public Volcano volcano;
        public Block block;
        public Block toBlock;
        public void setup(Volcano volcano, Block block, Block toBlock) {
            this.volcano = volcano;
            this.block = block;
            this.toBlock = toBlock;
        }
        public void run() {
            try {
                //nearBlockUpdate thr = new nearBlockUpdate();
                //thr.setup(volcano, block);
                //thr.start();

                Block nearByBlock = block;
                for (int a = 0; a < 6; a++) {
                    switch (a) {
                        case 0:
                            nearByBlock = block.getRelative(BlockFace.SOUTH);
                            break;
                        case 1:
                            nearByBlock = block.getRelative(BlockFace.WEST);
                            break;
                        case 2:
                            nearByBlock = block.getRelative(BlockFace.NORTH);
                            break;
                        case 3:
                            nearByBlock = block.getRelative(BlockFace.EAST);
                            break;
                        case 4:
                            nearByBlock = block.getRelative(BlockFace.UP);
                            break;
                        case 5:
                            nearByBlock = block.getRelative(BlockFace.DOWN);
                            break;
                        default:
                            break;
                    }

                    if (Arrays.asList(explode).contains(nearByBlock.getType())) {
                        if (volcano.affected(nearByBlock.getLocation())) {
                            if (Arrays.asList(explode).contains(nearByBlock.getType())) {
                                blockSet(nearByBlock, Material.AIR);
                                explodeLava(volcano, nearByBlock);
                                final Block theBlock = nearByBlock;
                                MainPlugin.plugin.getServer().getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
                                    lavaCooldown(theBlock);
                                }, settings.flowed * 20);
                            }
                        }
                    } else if (Arrays.asList(explodeAndRemove).contains(nearByBlock.getType())) {
                        lavaCollideWater(volcano, nearByBlock);
                        final Block theBlock = nearByBlock;
                        MainPlugin.plugin.getServer().getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
                            lavaCooldown(theBlock);
                        }, settings.flowed * 20);
                    }
                }

                MainPlugin.plugin.getServer().getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
                    lavaCooldown(block);
                }, settings.flowed*20);
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.INFO, "Exception Occurred in Multi-Threading nearByBlock Updates");
                return;
            }
            return;
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event){
        Block block = event.getBlock();
        Block toBlock = event.getToBlock();
        if((toBlock.getType() == Material.AIR || toBlock.getType() == Material.STATIONARY_LAVA || toBlock.getType() == Material.LAVA) && (block.getType() == Material.STATIONARY_LAVA || block.getType() == Material.LAVA)){
            for (Volcano volcano : MainPlugin.listVolcanoes) {

                if (!volcano.isChunkLoaded()) volcano.location.getChunk().load();

                if (volcano.location.getWorld() != null && volcano.location.getWorld().getUID() == block.getWorld().getUID()) {
                    if (volcano.lavaFlowAffected(block.getLocation()) && !(volcano.generator.throat && volcano.inCrater(block.getLocation()))) {
                        Chunk toBlockChunk = toBlock.getLocation().getChunk();
                        if (!toBlockChunk.isLoaded()) {
                            toBlockChunk.load();
                        }

                        blockUpdate thr = new blockUpdate();
                        thr.setup(volcano, block, toBlock);
                        thr.start();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (volcano.affected(event.getChunk().getBlock(0, volcano.location.getBlockY(),0).getLocation())
            || volcano.affected(event.getChunk().getBlock(15, volcano.location.getBlockY(),0).getLocation())
            || volcano.affected(event.getChunk().getBlock(0, volcano.location.getBlockY(),15).getLocation())
            || volcano.affected(event.getChunk().getBlock(15, volcano.location.getBlockY(),15).getLocation())) {
            event.setCancelled(true);
        }
    }


    /*
    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            if (currentFB.contains(event.getEntity())) {
                if (event.getEntity().isDead()) {
                    Location loc = new Location(
                            event.getEntity().getWorld(),
                            event.getEntity().getLocation().getBlockX(),
                            event.getEntity().getLocation().getBlockY(),
                            event.getEntity().getLocation().getBlockZ()
                    );
                    currentFB.remove(event.getEntity());
                    loc.getBlock().setType(event.getBlock().getType());
                }
            }
        }
    }
    */

    private long nextFlowTime = 0;


    public void flowLavaToVolcano() {
        if (volcano.enabled) {
            long timeNow = System.currentTimeMillis();
            if (System.currentTimeMillis() >= nextFlowTime) {
                Block whereToFlow = volcano.getRandomBlockOnCraterForLavaFlow();
                whereToFlow.setType(Material.LAVA);
                nextFlowTime = timeNow + settings.delayFlowed;
            }
        }
    }

    public void lavaCooldown(Block block) {
        blockSet(block, volcano.getRandomBlock());
    }

    public void registerTask() {
        if (scheduleID == -1) {
            scheduleID = MainPlugin.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(MainPlugin.plugin, () -> {
                flowLavaToVolcano();
                volcano.updateData();
            },0L,20L);
        }
    }

    public void unregisterTask() {
        if (scheduleID != -1) {
            MainPlugin.plugin.getServer().getScheduler().cancelTask(scheduleID);
            scheduleID = -1;
        }
    }
}

class VolcanoLavaFlowDefaultSettings {
    public static int flowed = 6;
    public static int delayFlowed = 3;
}

class VolcanoLavaFlowSettings {
    public int flowed = VolcanoLavaFlowDefaultSettings.flowed;
    public int delayFlowed = VolcanoLavaFlowDefaultSettings.delayFlowed;
}

