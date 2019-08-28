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
        if (scheduleID >= 0) { return; }
        scheduleID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MainPlugin.plugin, (Runnable) () -> {
            if (enabled && settings.isExplosive && !erupting && volcano.enabled) {
                erupting = true;

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

                    for (int i = 0; i < new Random().nextInt(4); i++) {
                        FallingBlock launchBlock = volcano.location.getWorld().spawnFallingBlock(loc, new MaterialData(Material.LAVA, (byte)0));

                        float lbx =  (settings.damageExplo / 4) * ((volcano.currentHeight - volcano.location.getBlockY()) / volcano.generator.heightLimit) * (new Random().nextInt(2) - 1);
                        float lby = 3;
                        float lbz =  (settings.damageExplo / 4) * ((volcano.currentHeight - volcano.location.getBlockY()) / volcano.generator.heightLimit) * (new Random().nextInt(2) - 1);

                        launchBlock.setVelocity(new Vector(lbx, lby, lbz));
                    }


                    Bukkit.getLogger().log(Level.INFO, "Volcano "+volcano.name+" is erupting now. @ "
                    +volcano.location.getBlockX()+","+y+","+volcano.location.getBlockZ());
                    VolcanoEarthQuakes.triggerEarthQuakeForVolcano(volcano, settings.damageExplo);

                    volcano.updateData();
                //}, rand.nextInt(settings.timerExplo + settings.delayExplo));

                erupting = false;
            }
        },settings.timerExplo*20, settings.delayExplo*20);
    }

    public void unregisterTask() {
        if (scheduleID >= 0) {
            Bukkit.getScheduler().cancelTask(scheduleID);
            scheduleID = -1;
        }
    }
}

class VolcanoEruptionSettings {
    public int timerExplo = 10;
    public int delayExplo = 30;
    public int damageExplo = 20;
    public int realDamageExplo = 2;
    public boolean isExplosive = true;
}
