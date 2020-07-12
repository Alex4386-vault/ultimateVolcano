package com.playmintnetwork.ultimatevolcano;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;

class VolcanoBombsDefault {
    public static float minBombPower = 2.0f;
    public static float maxBombPower = 4.0f;

    public static float minBombLaunchPower = 2.0f;
    public static float maxBombLaunchPower = 40.0f;

    public static float minCalderaPower = 40.0f;
    public static float maxCalderaPower = 120.0f;

    public static int minBombRadius = 1;
    public static int maxBombRadius = 2;

    public static int bombDelay = 20;
}

public class VolcanoBombs {
    public Volcano volcano;
    public ArrayList<VolcanoBomb> bombList = new ArrayList<>();

    public static Material volcanoBombFallingBlockMaterial = Material.GRAVEL;

    public float minBombPower = VolcanoBombsDefault.minBombPower;
    public float maxBombPower = VolcanoBombsDefault.maxBombPower;

    public float minBombLaunchPower = VolcanoBombsDefault.minBombLaunchPower;
    public float maxBombLaunchPower = VolcanoBombsDefault.maxBombLaunchPower;

    public float minCalderaPower = VolcanoBombsDefault.minCalderaPower;
    public float maxCalderaPower = VolcanoBombsDefault.maxCalderaPower;

    public int minBombRadius = VolcanoBombsDefault.minBombRadius;
    public int maxBombRadius = VolcanoBombsDefault.maxBombRadius;

    public int bombDelay = VolcanoBombsDefault.bombDelay;
    public int bombTrailScheduleId = -1;

    VolcanoBombs(Volcano volcano) {
        this.volcano = volcano;
    }

    public void launchBomb() {
        Random random = new Random();
        int volcanoRealHeightLimit = (volcano.generator.heightLimit >= 255) ? 255:volcano.generator.heightLimit+volcano.location.getBlockY();

        float volcanoScaleVar = (volcano.currentHeight / (float)volcanoRealHeightLimit);
        //Bukkit.getLogger().log(Level.INFO, "volcanoScaleVar:"+volcanoScaleVar);

        double totalPower = (((maxBombLaunchPower - minBombLaunchPower) * Math.random()) + minBombLaunchPower) * volcanoScaleVar;

        // get random radian angle
        double randomAngle = Math.random() * Math.PI * 2;

        // super power ratio
        double powerRatioX = Math.sin(randomAngle);
        double powerRatioZ = Math.cos(randomAngle);

        float powerX = (float) (totalPower * powerRatioX);
        float powerZ = (float) (totalPower * powerRatioZ);

        float bombPower = random.nextFloat() * (maxBombPower - minBombPower) + minBombPower;
        int bombRadius = (int) ((Math.floor(random.nextDouble() * (maxBombRadius - minBombRadius)) * volcanoScaleVar) + minBombRadius);

        bombRadius = (bombRadius < 1) ? 1 : bombRadius;

        VolcanoBomb bomb = new VolcanoBomb(volcano, powerX, powerZ, bombPower, bombRadius, bombDelay);

        bombList.add(bomb);
        //bomb.startTrail();
    }

    public void trackAll() {
        Iterator<VolcanoBomb> bombIterator = bombList.iterator();
        while (bombIterator.hasNext()) {
            VolcanoBomb bomb = bombIterator.next();

            if (!bomb.block.getLocation().getChunk().isLoaded()) {
                bomb.block.getLocation().getChunk().load();
            }
            bomb.block.setTicksLived(1);

            if (bomb.isLanded) {
                bomb.stopTrail();
                bombIterator.remove();
            }

            if (bomb.prevLocation == null) {
                bomb.prevLocation = bomb.block.getLocation();
            } else {
                if ((bomb.prevLocation.equals(bomb.block.getLocation()) && VolcanoBombListener.groundChecker(bomb.block.getLocation(), bomb.bombRadius)) || bomb.block.isOnGround()) {

                    Bukkit.getLogger().log(Level.INFO, "[Volcano "+volcano.name+" BombTracker] Volcano Bomb from Volcano "+volcano.name+" bomb landed!");
                    bomb.land();
                    bomb.stopTrail();
                    bombIterator.remove();
                } else {
                    bomb.prevLocation = bomb.block.getLocation();
                }
            }

            // Living over 1 min
            if (bomb.lifeTime >= 120) {
                //Bukkit.getLogger().log(Level.INFO, "Volcano Bomb from Volcano "+volcano.name+" died.");
                bomb.stopTrail();
                bomb.block.remove();
                bombIterator.remove();
            } else {
                bomb.lifeTime++;
            }
        }
    }

}
