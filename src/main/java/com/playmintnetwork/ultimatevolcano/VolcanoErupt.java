package com.playmintnetwork.ultimatevolcano;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.logging.Level;

public class VolcanoErupt {
    public boolean enabled = true;
    public boolean erupting = false;
    public VolcanoEruptionSettings settings = new VolcanoEruptionSettings();
    public Volcano volcano;
    public int scheduleID = -1;

    public void registerTask() {
        if (scheduleID < 0) {
            scheduleID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MainPlugin.plugin, (Runnable) () -> {
                if (enabled && settings.isExplosive && !erupting && volcano.enabled) {
                    eruptNowRandom();
                }
            }, volcano.erupt.settings.timerExplo*volcano.lavaFlow.settings.updateRate, volcano.erupt.settings.delayExplo*volcano.lavaFlow.settings.updateRate);
        }

    }

    public void unregisterTask() {
        if (scheduleID >= 0) {
            Bukkit.getScheduler().cancelTask(scheduleID);
            scheduleID = -1;
        }

    }

    public void eruptNowRandom() {
        eruptNow(new Random().nextInt(settings.maxBombCount - settings.minBombCount + 1)+ settings.minBombCount);
    }

    public void eruptNow(int bombCount) {
        volcano.lavaFlow.registerTask();
        erupting = true;

        volcano.tremor.eruptTremor();

        //Bukkit.getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
        int y = volcano.generator.throat ? volcano.currentHeight-2 : volcano.location.getWorld().getHighestBlockYAt(volcano.location.getBlockX(), volcano.location.getBlockZ());
        volcano.location.getWorld().createExplosion(
                volcano.location.getX(),
                y,
                volcano.location.getZ(),
                1F * settings.damageExplo * ((volcano.currentHeight - volcano.location.getBlockY()) / volcano.generator.heightLimit),
                volcano.generator.throat,
                false
        );
        volcano.location.getWorld().createExplosion(
                volcano.location.getX(),
                y,
                volcano.location.getZ(),
                1F * settings.realDamageExplo * ((volcano.currentHeight - volcano.location.getBlockY()) / volcano.generator.heightLimit),
                volcano.generator.throat,
                true
        );

        Location loc = new Location(volcano.location.getWorld(), volcano.location.getBlockX(), y, volcano.location.getBlockZ());
        //volcano.location.getWorld().playEffect(loc, Effect.SMOKE, new Random().nextInt(9), (settings.damageExplo > 40) ? 40 : settings.damageExplo);

        for (int i = 0; i < bombCount; i++) {
            volcano.bombs.launchBomb();
        }


        Bukkit.getLogger().log(Level.INFO, "Volcano "+volcano.name+" is erupting now with "+bombCount+" VolcanoBombs @ "
                +volcano.location.getBlockX()+","+y+","+volcano.location.getBlockZ());

        volcano.updateData();
        //}, rand.nextInt(settings.timerExplo + settings.delayExplo));

        erupting = false;
    }
}

class VolcanoEruptionSettings {
    public int timerExplo = VolcanoEruptionDefaultSettings.timerExplo;
    public int delayExplo = VolcanoEruptionDefaultSettings.delayExplo;
    public int damageExplo = VolcanoEruptionDefaultSettings.damageExplo;
    public int realDamageExplo = VolcanoEruptionDefaultSettings.realDamageExplo;
    public boolean isExplosive = VolcanoEruptionDefaultSettings.isExplosive;
    public int minBombCount = VolcanoEruptionDefaultSettings.minBombCount;
    public int maxBombCount = VolcanoEruptionDefaultSettings.maxBombCount;
}

class VolcanoEruptionDefaultSettings {
    public static int timerExplo = 10;
    public static int delayExplo = 30;
    public static int damageExplo = 20;
    public static int realDamageExplo = 2;
    public static boolean isExplosive = true;
    public static int minBombCount = 20;
    public static int maxBombCount = 100;
}
