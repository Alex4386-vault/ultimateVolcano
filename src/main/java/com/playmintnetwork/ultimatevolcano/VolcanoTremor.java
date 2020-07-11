package com.playmintnetwork.ultimatevolcano;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class VolcanoTremor {
    public Volcano volcano;
    public boolean enable = true;
    public int scheduleID = -1;
    public int tremorCycleRate = 600;

    public VolcanoTremor(Volcano volcano) {
        this.volcano = volcano;
    }

    public Block getRandomTremorBlock() {
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


    public void runTremorCycle() {
        Random random = new Random();
        if (shouldIDoIt()) {
            Block block;
            block = getRandomTremorBlock();

            showTremorActivity(block, ((Math.random() / 2 + 0.5) * getTremorPower()));
        }
    }

    public void eruptTremor() {

        if (shouldIDoIt()) {
            Random random = new Random();

            showTremorActivity(volcano.location.getBlock(), (Math.random() / 2 + 0.5) * getTremorPower());
        }
    }

    public void tremorOnPlayer(Player player, int tremorLength, double power) {
        AtomicInteger loop = new AtomicInteger();
        AtomicInteger termorLength = new AtomicInteger(tremorLength * volcano.lavaFlow.settings.updateRate);

        AtomicInteger scheduleID = new AtomicInteger();
        Runnable tremorRunnable = (Runnable) () -> {
            Location location = player.getLocation();

            location.setYaw((float) (location.getYaw() + (Math.random() - 0.5) * 0.4 * power));
            location.setPitch((float) (location.getPitch() + (Math.random() - 0.5) * 0.4 * power));
            location.setX(location.getX() + (Math.random() - 0.5) * 0.04 * power);
            location.setZ(location.getZ() + (Math.random() - 0.5) * 0.04 * power);

            player.teleport(location);

            loop.getAndIncrement();

            if (loop.get() > termorLength.get()) {
                Bukkit.getLogger().log(Level.INFO, player.getDisplayName()+" termor sequence Pass.");
                Bukkit.getScheduler().cancelTask(scheduleID.get());
            }
        };

        scheduleID.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(MainPlugin.plugin, tremorRunnable, 0, 1));
    }

    public void showTremorActivity(Block block, double power) {
        MainPlugin.plugin.getLogger().log(Level.INFO, "running tremor for volcano " + volcano.name + " with power " + power + ".");

        for (Player player : Bukkit.getOnlinePlayers()) {
            int radius = volcano.zone;
            Location location = player.getLocation();

            double distance = Math.sqrt(Math.pow(location.getBlockX() - block.getX(), 2) +
                    Math.pow(location.getBlockZ() - block.getZ(), 2));

            if (player.getWorld().getUID() == block.getWorld().getUID() &&
                    distance < radius && player.isOnGround()) {

                double impactFactor = 1.0 - (distance / volcano.zone);
                impactFactor = impactFactor > 0.2 ? impactFactor : 0;

                if (impactFactor > 0) {
                    tremorOnPlayer(player, (int) (Math.random() * 4 * impactFactor), power * impactFactor);
                }
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
                return (a < 500);
            case ERUPTING:
                return (a < 800);
            default:
                return false;
        }
    }

    public double getTremorPower() {
        switch(volcano.autoStart.status) {
            case DORMANT:
                return 0.001;
            case MINOR_ACTIVITY:
                return 0.01;
            case MAJOR_ACTIVITY:
                return 0.1;
            case ERUPTING:
                return 1;
            default:
                return 0;
        }
    }

    public void registerTask() {
        if (scheduleID == -1) {
            scheduleID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
                    MainPlugin.plugin,
                    (Runnable) () -> {
                        if (enable) {
                            runTremorCycle();
                        }
                    }, 0, tremorCycleRate
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
