package com.playmintnetwork.ultimatevolcano;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class VolcanoEarthQuakes {

    public static void triggerEarthQuake(Player player, float magnitude) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
           for (int i = 0; i < 100; i++) {
               Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
                   Location loc = player.getLocation();
                   Random rand = new Random();
                   double stuff1 = Math.pow(2,(magnitude-4)/2) * ((rand.nextInt(11) - 5) / 10);
                   double stuff2 = Math.pow(2,(magnitude-4)/2) * ((rand.nextInt(11) - 5) / 10);
                   loc.add( stuff1, rand.nextDouble() / 5, stuff2);
                   player.teleport(loc);
               }, 2*i);
           }
        });
    }


    public static void triggerEarthQuakeForVolcano(Volcano volcano, float magnitude) {
        for (Player player:Bukkit.getOnlinePlayers()) {
            if (volcano.affected(player.getLocation())) {
                //triggerEarthQuake(player, magnitude);

            }
        }
    }
}
