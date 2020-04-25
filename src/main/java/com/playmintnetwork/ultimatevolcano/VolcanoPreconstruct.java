package com.playmintnetwork.ultimatevolcano;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class VolcanoPreconstruct {
    public Volcano volcano;
    public static int blockUpdatesPerSecond = 60000;

    public VolcanoPreconstruct(Volcano volcano) {
        this.volcano = volcano;
    }

    public static Material[] raiseBan = {
            Material.BEDROCK,
            Material.WATER,
            Material.LEGACY_STATIONARY_WATER,
            Material.KELP,
            Material.KELP_PLANT,
            Material.SEAGRASS,
            Material.SEA_PICKLE,
            Material.BUBBLE_COLUMN,
            Material.ICE,
            Material.FROSTED_ICE,
            Material.BLUE_ICE,
            Material.PACKED_ICE
    };

    public static Material[] raiseAllow = {
            Material.AIR
    };

    public static double pdf(double x){
        double mean = 0;
        double variance = 1;
        double base = 1/Math.sqrt(2*Math.PI*variance);
        double pow = -(Math.pow((x-mean), 2)/2*variance);
        return Math.pow(Math.E, pow) * base;
    }

    public void runTestPreconstruct(int baseHeight) {
        int baseRadius = (int) (baseHeight * ((Math.random() * 3) + 2.0));
        runTestPreconstruct(baseHeight, baseRadius);
    }

    public void runTestPreconstruct(CommandSender sender, int baseHeight) {
        int baseRadius = (int) (baseHeight * ((Math.random() * 3) + 2.0));
        runTestPreconstruct(sender, baseHeight, baseRadius);
    }

    public void runTestPreconstruct(int baseHeight, int baseRadius) {
        runTestPreconstruct(null, baseHeight, baseRadius);
    }

    public void runTestPreconstruct(CommandSender sender, int baseHeight, int baseRadius) {
        List<VolcanoPreconstructRadiusData> preconstructRadiusDataList = new ArrayList<>();

        int pdf_beforeY = 0;
        for (int radius = baseRadius; radius > 0; radius--) {
            double pdf_x = Math.abs(radius) / (double)baseRadius;
            double pdf_y = pdf(3 * pdf_x);
            int pdf_y_floor = (int) (pdf_y * baseHeight * 3);

            if (pdf_beforeY < pdf_y_floor) {
                preconstructRadiusDataList.add(
                        new VolcanoPreconstructRadiusData(volcano, radius, pdf_y_floor - pdf_beforeY)
                );
                pdf_beforeY = pdf_y_floor;
            }
        }

        int pastStageProcessSeconds = 0;
        int totalBlockUpdates = 0;

        Iterator<VolcanoPreconstructRadiusData> radiusDataIterator = preconstructRadiusDataList.iterator();

        for (VolcanoPreconstructRadiusData preconstructRadiusData : preconstructRadiusDataList) {
            int blockUpdates = (int) (Math.PI * Math.pow(preconstructRadiusData.radius, 2)) * 127;
            int expectedBlockUpdateSeconds = blockUpdates / blockUpdatesPerSecond;

            if (expectedBlockUpdateSeconds < 1) { expectedBlockUpdateSeconds = 1; }
            totalBlockUpdates += blockUpdates;
            pastStageProcessSeconds += expectedBlockUpdateSeconds;
        }

        Bukkit.getLogger().info("Estimated time to build volcano base "+volcano.name+": "+pastStageProcessSeconds+" seconds with total stages of "+preconstructRadiusDataList.size()+" with "+totalBlockUpdates+" blockUpdates");
        sender.sendMessage("Estimated time to build volcano base "+volcano.name+": "+pastStageProcessSeconds+" seconds with total stages of "+preconstructRadiusDataList.size()+" with "+totalBlockUpdates+" blockUpdates");

        recursiveRadius(volcano, sender, radiusDataIterator, 1, preconstructRadiusDataList.size());
    }

    public void recursiveRadius(Volcano volcano, CommandSender sender, Iterator<VolcanoPreconstructRadiusData> i, int index, int total) {
        if (i.hasNext()) {
            VolcanoPreconstructRadiusData preconstructRadiusData = i.next();

            Bukkit.getScheduler().runTaskLater(MainPlugin.plugin, (Runnable) () -> {
                Bukkit.getLogger().info("[Volcano] Volcano Base " + volcano.name + " Generation Stage " + index + "/" + total + "...");
                if (sender != null) {
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano Base " + volcano.name + " Generation Stage " + index + "/" + total + "...");
                }

                int realBlockUpdates = preconstructRadiusData.calculateAffectedBlocks();
                int realEstimation = realBlockUpdates / blockUpdatesPerSecond;

                Bukkit.getLogger().info("[Volcano] Volcano Base " + volcano.name + " Generation Stage " + index + "/" + total + "... (est. "+realEstimation+" seconds, "+realBlockUpdates+" blockUpdates)");;
                if (sender != null) {
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano Base " + volcano.name + " Generation Stage " + index + "/" + total + "... (est. "+realEstimation+" seconds, "+realBlockUpdates+" blockUpdates)");;
                }

                preconstructRadiusData.raiseBlocks(sender, (Runnable) () -> {
                    Bukkit.getLogger().info("[Volcano] Volcano Base " + volcano.name + " Generation Stage " + index + "/" + total + " complete!");
                    if (sender != null) {
                        sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano Base " + volcano.name + " Generation Stage " + index + "/" + total + " complete! est. 2 seconds");
                    }

                    Bukkit.getScheduler().runTaskLater(MainPlugin.plugin, (Runnable) () -> {
                        recursiveIslandRadius(volcano, sender, i, index + 1, total);
                    }, 20 * 2);

                    i.remove();
                });
            }, 20);

        } else {
            Bukkit.getLogger().info("Volcano Base "+volcano.name+" Build complete!");
            if (sender != null) sender.sendMessage("Volcano Base "+volcano.name+" Build complete!");
        }
    }

    public boolean isUnderWater() {
        int minY = 64;

        Location loc = volcano.location;
        World world = loc.getWorld();

        Block block = world.getBlockAt(loc);

        int y = world.getHighestBlockYAt(loc);

        while (block.getType().equals(Material.WATER)) {
            y--;
            block = block.getRelative(0, -1, 0);
        }

        minY = y < minY ? y : minY;

        return minY < 64;
    }

    public void runIslandPreconstruct(int baseRadius) {
        runIslandPreconstruct(null, baseRadius);
    }

    public void runIslandPreconstruct(CommandSender sender, int baseRadius) {
        int minY = 63;
        double descendWidth = 1.4;

        Location loc = volcano.location;
        World world = loc.getWorld();

        for (int x = -baseRadius; x <= baseRadius; x++) {
            for (int z = -baseRadius; z <= baseRadius; z++) {
                if (Math.pow(x, 2) + Math.pow(z, 2) <= Math.pow(baseRadius, 2)) {
                    int realX = loc.getBlockX() + x;
                    int realZ = loc.getBlockZ() + z;

                    int y = loc.getWorld().getHighestBlockYAt(realX, realZ);

                    Block block = world.getBlockAt(realX, y, realZ);
                    while (block.getType().equals(Material.WATER)) {
                        y--;
                        block = world.getBlockAt(realX, y, realZ);
                    }

                    minY = y < minY ? y : minY;
                }
            }
        }

        int height = 64 - minY;

        List<VolcanoPreconstructRadiusData> preconstructRadiusDataList = new ArrayList<>();


        for (double r = baseRadius; r > baseRadius - (descendWidth * height); r -= descendWidth) {
            preconstructRadiusDataList.add(new VolcanoPreconstructRadiusData(volcano, (int) r, 1));
        }

        int pastStageProcessSeconds = 0;
        int totalBlockUpdates = 0;

        Iterator<VolcanoPreconstructRadiusData> radiusDataIterator = preconstructRadiusDataList.iterator();

        for (VolcanoPreconstructRadiusData preconstructRadiusData : preconstructRadiusDataList) {
            int blockUpdates = (int) (Math.PI * Math.pow(preconstructRadiusData.radius, 2)) * 127;
            int expectedBlockUpdateSeconds = blockUpdates / blockUpdatesPerSecond;

            if (expectedBlockUpdateSeconds < 1) { expectedBlockUpdateSeconds = 1; }
            totalBlockUpdates += blockUpdates;
            pastStageProcessSeconds += expectedBlockUpdateSeconds;
        }

        Bukkit.getLogger().info("Estimated time to build volcano base "+volcano.name+": "+pastStageProcessSeconds+" seconds with total stages of "+preconstructRadiusDataList.size()+" with "+totalBlockUpdates+" blockUpdates");
        sender.sendMessage("Estimated time to build volcano base "+volcano.name+": "+pastStageProcessSeconds+" seconds with total stages of "+preconstructRadiusDataList.size()+" with "+totalBlockUpdates+" blockUpdates");

        recursiveIslandRadius(volcano, sender, radiusDataIterator, 1, preconstructRadiusDataList.size());

    }

    public void recursiveIslandRadius(Volcano volcano, CommandSender sender, Iterator<VolcanoPreconstructRadiusData> i, int index, int total) {
        if (i.hasNext()) {
            VolcanoPreconstructRadiusData preconstructRadiusData = i.next();

            Bukkit.getScheduler().runTaskLater(MainPlugin.plugin, (Runnable) () -> {
                Bukkit.getLogger().info("[Volcano] Volcano Island " + volcano.name + " Generation Stage " + index + "/" + total + "...");
                if (sender != null) {
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano Island " + volcano.name + " Generation Stage " + index + "/" + total + "...");
                }

                int realBlockUpdates = preconstructRadiusData.calculateAffectedBlocks();
                int realEstimation = realBlockUpdates / blockUpdatesPerSecond;

                Bukkit.getLogger().info("[Volcano] Volcano Island " + volcano.name + " Generation Stage " + index + "/" + total + "... (est. "+realEstimation+" seconds, "+realBlockUpdates+" blockUpdates)");;
                if (sender != null) {
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano Island " + volcano.name + " Generation Stage " + index + "/" + total + "... (est. "+realEstimation+" seconds, "+realBlockUpdates+" blockUpdates)");
                }

                preconstructRadiusData.raiseBlocks(sender, (Runnable) () -> {
                    Bukkit.getLogger().info("[Volcano] Volcano Island " + volcano.name + " Generation Stage " + index + "/" + total + " complete!");
                    if (sender != null) {
                        sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano Island " + volcano.name + " Generation Stage " + index + "/" + total + " complete! est. 2 seconds");
                    }

                    Bukkit.getScheduler().runTaskLater(MainPlugin.plugin, (Runnable) () -> {
                        recursiveIslandRadius(volcano, sender, i, index + 1, total);
                    }, 20 * 2);

                    i.remove();
                });
            }, 20);

        } else {
            Bukkit.getLogger().info("Volcano Base "+volcano.name+" Build complete!");
            if (sender != null) sender.sendMessage("Volcano Base "+volcano.name+" Build complete!");
        }
    }

    public static List<Location> getTopCylinder(Location loc, int radius) {
        List<Location> cylinderLocations = new ArrayList<>();
        World world = loc.getWorld();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.pow(x, 2) + Math.pow(z, 2) <= Math.pow(radius, 2)) {
                    int maxY = world.getHighestBlockYAt(loc.getBlockX()+x,loc.getBlockZ()+z);
                    cylinderLocations.add(new Location(world, loc.getBlockX()+x, maxY, loc.getBlockZ()+z));
                }
            }
        }

        return cylinderLocations;
    }

    public static List<Location> getTopCylinder(Location loc, int radius, int hollowRadius) {
        List<Location> cylinderLocations = new ArrayList<>();
        World world = loc.getWorld();


        for (int x = -radius; x <= radius && Math.abs(x) > hollowRadius; x++) {
            for (int z = -radius; z <= radius && Math.abs(z) > hollowRadius; z++) {
                double distanceSquared = Math.pow(x, 2) + Math.pow(z, 2);
                if (distanceSquared <= Math.pow(radius, 2) && distanceSquared > Math.pow(hollowRadius, 2)) {
                    int maxY = world.getHighestBlockYAt(loc.getBlockX()+x,loc.getBlockZ()+z);
                    cylinderLocations.add(new Location(world, loc.getBlockX()+x, maxY, loc.getBlockZ()+z));
                }
            }
        }

        return cylinderLocations;
    }
}

class VolcanoPreconstructRadiusData {
    Volcano volcano;
    int radius;
    int raiseAmount;
    List<VolcanoPreconstructBlockData> preconstructDataList = new ArrayList<>();

    public VolcanoPreconstructRadiusData(Volcano volcano, int radius, int raiseAmount) {
        this.volcano = volcano;
        this.radius = radius;
        this.raiseAmount = raiseAmount;
    }

    public int calculateAffectedBlocks() {
        if (preconstructDataList.size() != 0) {
            preconstructDataList.clear();
        }

        List<Location> topLocations = VolcanoPreconstruct.getTopCylinder(volcano.location, radius);

        Iterator<Location> topLocationsIterator = topLocations.iterator();
        World world = volcano.location.getWorld();

        while (topLocationsIterator.hasNext()) {
            Location topLocation = topLocationsIterator.next();

            int x = topLocation.getBlockX();
            int topY = topLocation.getBlockY();
            int z = topLocation.getBlockZ();

            for (int y = topY; y >= 0 + raiseAmount && y < 256 - raiseAmount; y--) {
                Block block = world.getBlockAt(x, y, z);

                Material material = block.getType();
                Block toBlock = block.getRelative(0, raiseAmount, 0);
                if (!Arrays.asList(VolcanoPreconstruct.raiseBan).contains(material) &&
                    (material.isSolid() || Arrays.asList(VolcanoPreconstruct.raiseAllow).contains(material))
                ) {
                    preconstructDataList.add(new VolcanoPreconstructBlockData(volcano, block, toBlock, material));
                }
            }
        }

        return preconstructDataList.size();
    }

    public void raiseBlocks() {
        raiseBlocks(null, null);
    }

    public void raiseBlocks(CommandSender sender) {
        raiseBlocks(sender, null);
    }

    public void raiseBlocks(Runnable callback) {
        raiseBlocks(null, callback);
    }

    public void raiseBlocks(CommandSender sender, Runnable callback) {
        if (preconstructDataList.size() == 0) {
            this.calculateAffectedBlocks();
        }

        Iterator<VolcanoPreconstructBlockData> preconstructBlockDataIterator = preconstructDataList.iterator();

        recursiveBlockUpdates(preconstructBlockDataIterator, sender, (Runnable) () -> {
            if (callback != null) callback.run();
        }, 0);
    }

    public void recursiveBlockUpdates(Iterator<VolcanoPreconstructBlockData> i, Runnable callback, int prevAccumulatedUpdates) {
        recursiveBlockUpdates(i, null, callback, prevAccumulatedUpdates);
    }

    public void recursiveBlockUpdates(Iterator<VolcanoPreconstructBlockData> i, CommandSender sender, Runnable callback, int prevAccumulatedUpdates) {
        if (i.hasNext()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    MainPlugin.plugin,
                    (Runnable) () -> {
                        int accumulatedUpdates = prevAccumulatedUpdates;
                        int currentAccumulatedUpdates = 0;
                        while (i.hasNext() && currentAccumulatedUpdates < VolcanoPreconstruct.blockUpdatesPerSecond / 4) {
                            VolcanoPreconstructBlockData preconstructBlockData = i.next();
                            preconstructBlockData.process();
                            currentAccumulatedUpdates++;
                        }

                        accumulatedUpdates += currentAccumulatedUpdates;
                        if (accumulatedUpdates % VolcanoPreconstruct.blockUpdatesPerSecond < 100) {
                            if (sender != null) sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"Currently updating "+prevAccumulatedUpdates+"th block");
                            Bukkit.getLogger().info("[Volcano] Currently updating "+prevAccumulatedUpdates+"th block");
                        }

                        int finalAccumulatedUpdates = accumulatedUpdates;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(
                                MainPlugin.plugin,
                                (Runnable) () -> {
                                    recursiveBlockUpdates(i, sender, callback, finalAccumulatedUpdates);
                                },
                                1l
                        );
                    }
            , 0l);
        } else {
            preconstructDataList.clear();
            if (sender != null) sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"Update complete.");
            if (callback != null) {
                callback.run();
            }
        }
    }

}

class VolcanoPreconstructBlockData {
    Volcano volcano;
    Material material;
    Block block;
    //BlockState blockState;
    Block toBlock;

    public VolcanoPreconstructBlockData(Volcano volcano, Block block, Block toBlock, Material material) {
        this.volcano = volcano;
        this.block = block;
        //this.blockState = block.getState();
        this.toBlock = toBlock;
        this.material = material;
    }

    public void process() {

        /*
        // old
        toBlock.setType(material, false);

        try {
            toBlock.setBlockData(block.getBlockData());
        } catch(Exception e) {

        }
        //toBlock.setBlockData(blockState.getBlockData());

        if (block.getLocation().getBlockY() < 10 || block.getRelative(0,-1,0).getType().equals(Material.LAVA)) {
            block.setType(Material.LAVA, false);
        } else {
            block.setType(Material.AIR, false);
        }
        */

        World world = toBlock.getWorld();
        World

    }
}
