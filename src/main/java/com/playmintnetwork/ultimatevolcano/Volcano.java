package com.playmintnetwork.ultimatevolcano;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class Volcano {
    public static File volcanoDir = new File(MainPlugin.dataDir, "Volcanoes");
    public static String defaultComposition = "STONE,100";
    public File file;
    public String name;
    public boolean enabled;
    public boolean firstStart = true;
    public Location location;
    public VolcanoGenerator generator = new VolcanoGenerator();
    public VolcanoErupt erupt = new VolcanoErupt();
    public VolcanoLavaFlow lavaFlow = new VolcanoLavaFlow();
    public VolcanoGeoThermals geoThermals = new VolcanoGeoThermals();
    public VolcanoCrater crater = new VolcanoCrater();
    public VolcanoAutoStart autoStart = new VolcanoAutoStart();
    public int zone;
    public Block summitBlock;

    private Random random = new Random();
    public int currentHeight;

    public boolean inCrater(Location chkloc) {
        return (
                chkloc.getWorld().equals(location.getWorld()) &&
                MainPlugin.isDataInRange(location.getBlockX(), chkloc.getBlockX(), crater.craterRadius) &&
                MainPlugin.isDataInRange(location.getBlockZ(), chkloc.getBlockZ(), crater.craterRadius)
        );
    }

    public Material getRandomBlock() {
        double a = random.nextDouble()*100;
        double sum = 0;
        for (int i = 0; i < generator.composition.size(); i++) {
            if ((a >= sum) && (a < generator.composition.get(i).percentage)) {
                return generator.composition.get(i).material;
            }
            sum += generator.composition.get(i).percentage;
        }

        // FALLBACK
        int b = random.nextInt(generator.composition.size());
        //System.out.println(b);
        return generator.composition.get(b).material;
    }

    public boolean affected(Location chkloc) {
        return (
                chkloc.getWorld().equals(location.getWorld()) &&
                MainPlugin.isDataInRange(location.getBlockX(), chkloc.getBlockX(), zone) &&
                MainPlugin.isDataInRange(location.getBlockY(), chkloc.getBlockY(), zone) &&
                MainPlugin.isDataInRange(location.getBlockZ(), chkloc.getBlockZ(), zone)
        );
    }

    public boolean isChunkLoaded() {
        Chunk volcanoCoreChunk = location.getChunk();
        return volcanoCoreChunk.isLoaded();
    };


    public boolean lavaFlowAffected(Location chkloc) {
        return (
                chkloc.getWorld().equals(location.getWorld()) &&
                MainPlugin.isDataInRange(location.getBlockX(), chkloc.getBlockX(), zone) &&
                MainPlugin.isDataInRange(location.getBlockZ(), chkloc.getBlockZ(), zone)
        );
    }

    public boolean delete() {
        Bukkit.getLogger().log(Level.INFO, "Deleting Volcano "+name);
        Bukkit.getLogger().log(Level.INFO, "Stopping Volcano "+name+" to continue delete procedure");
        stop();
        MainPlugin.listVolcanoes.remove(this);
        Bukkit.getLogger().log(Level.INFO, "Unloaded Volcano "+name);
        if (file.exists()) {
            Bukkit.getLogger().log(Level.INFO, "Found Volcano "+name+"'s VolcanoFile to reload");
            file.delete();
            Bukkit.getLogger().log(Level.INFO, "Deleted Volcano "+name);
        }
        return true;
    }

    public boolean reload() {
        Bukkit.getLogger().log(Level.INFO, "Reloading Volcano "+name);
        prepareShutdown();
        Bukkit.getLogger().log(Level.INFO, "Volcano "+name+" EventHandler was gracefully shutted down.");
        MainPlugin.listVolcanoes.remove(this);
        Bukkit.getLogger().log(Level.INFO, "Unloaded Volcano "+name);
        if (file.exists()) {

            Bukkit.getLogger().log(Level.INFO, "Found Volcano "+name+"'s VolcanoFile to reload");
            MainPlugin.listVolcanoes.add(Volcano.importFromFile(file));
            Bukkit.getLogger().log(Level.INFO, "Reloaded Volcano "+name);
            return true;
        } else {
            return false;
        }
    }

    public static boolean create(String name, Location location, int height, String compositions, boolean throat, boolean activateGeoThermals) {
        Bukkit.getLogger().log(Level.INFO, "Creating Volcano "+name);
        File volcanoFile = new File(volcanoDir, name+".yml");
        if (volcanoFile.exists() ) { Bukkit.getLogger().log(Level.INFO, "Volcano "+name+" already exists. Halting."); return false; }

        try {
            Bukkit.getLogger().log(Level.INFO, "Creating VolcanoFile for Volcano "+name);
            volcanoFile.createNewFile();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.INFO, "An Exception occurred while creating VolcanoFile for "+name+", Please refer to stacktrace below.");
            e.printStackTrace();
            return false;
        }

        Bukkit.getLogger().log(Level.INFO, "Creating Volcano Object for Volcano "+name);
        Volcano volcano = new Volcano();

        Bukkit.getLogger().log(Level.INFO, "Loading Compositions for Volcano "+name);
        if (!volcano.generator.loadCompositions(compositions)) {
            Bukkit.getLogger().log(Level.INFO, "Composition error! on Volcano "+name);
            return false;
        }

        Bukkit.getLogger().log(Level.INFO, "Setting default variables to Volcano "+name);
        volcano.file = volcanoFile;
        volcano.name = name;
        volcano.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        volcano.generator.heightLimit = height;
        volcano.generator.throat = throat;
        volcano.geoThermals.enable = activateGeoThermals;
        Bukkit.getLogger().log(Level.INFO, "Running Crater Setup for Volcano "+name);
        volcano.crater.setCraters(location);
        Bukkit.getLogger().log(Level.INFO, "Running Initial Setup for Volcanao "+name);
        volcano.initialSetup();

        Bukkit.getLogger().log(Level.INFO, "Loading Volcano "+name+" to plugin.");
        MainPlugin.listVolcanoes.add(volcano);

        Bukkit.getLogger().log(Level.INFO, "Trying to save Volcano "+name);
        volcano.saveToFile();
        return true;
    }

    public void start() {
        Bukkit.getLogger().log(Level.INFO, "Starting Volcano "+name);
        if (enabled && !firstStart) { Bukkit.getLogger().log(Level.INFO, "Volcano "+name+" was already enabled!"); return; }
        if (firstStart) {
            Bukkit.getLogger().log(Level.INFO, "Firststart of Volcano "+name+" since the bukkit boot detected");
            Block block = null;
            Bukkit.getLogger().log(Level.INFO, "Setting Initial Crater for Volcano "+name);
            for (int i = 0 ; i < crater.xF.length ; i++){
                block = location.getWorld().getBlockAt(crater.xF[i], crater.yF[i], crater.zF[i]);
                block.setType(getRandomBlock());
            }
            firstStart = false;
        }
        lavaFlow.registerTask();
        erupt.registerTask();
        geoThermals.registerTask();
        enabled = true;
        autoStart.status = VolcanoCurrentStatus.ERUPTING;

        saveToFile();
    }

    public void stop() {
        Bukkit.getLogger().log(Level.INFO, "Stopping Volcano "+name);
        if (!enabled) { return; }
        enabled = false;

        Bukkit.getLogger().log(Level.INFO, "unregistering Volcano "+name+" 's EventHandlers");
        lavaFlow.unregisterTask();
        erupt.unregisterTask();
        geoThermals.unregisterTask();

        Bukkit.getLogger().log(Level.INFO, "disabling Volcano "+name+" 's Force Loaded Chunks");
        for (int i = 0; i < lavaFlow.lavaFlowChunks.size(); i++) {
            lavaFlow.lavaFlowChunks.get(i).setForceLoaded(false);
        }
        lavaFlow.lavaFlowChunks.clear();

        Bukkit.getLogger().log(Level.INFO, "Setting Volcano "+name+" status to MAJOR_ACTIVITY");
        autoStart.status = VolcanoCurrentStatus.MAJOR_ACTIVITY;

        saveToFile();
    }

    public void prepareShutdown() {
        Bukkit.getLogger().log(Level.INFO, "Shutting down Volcano "+name);
        lavaFlow.unregisterTask();
        erupt.unregisterTask();
        geoThermals.unregisterTask();
        Bukkit.getLogger().log(Level.INFO, "Volcano "+name+" was successfully shutted down");
    }

    public void updateData() {
        zone = (location.getWorld().getEnvironment() == World.Environment.NETHER) ? (((currentHeight - location.getBlockY()) * 8) + crater.craterRadius) : ((currentHeight - location.getBlockY()) * 3 + crater.craterRadius);
    }

    public Block getRandomBlockOnCraterForLavaFlow() {
        int i = random.nextInt(crater.xF.length);
        crater.yF[i] = (crater.yF[i] >= location.getBlockY()+generator.heightLimit) ? location.getBlockY()+generator.heightLimit : crater.yF[i];
        return getBlockForLavaFlow(location.getWorld().getBlockAt(crater.xF[i], crater.yF[i], crater.zF[i]));
    }

    private Block getBlockForLavaFlow(Block block){
        Block bTmp;
        // DESCENTE
        if(block.getType() == Material.AIR){
            bTmp = block;
            bTmp = block.getRelative(BlockFace.DOWN);
            while (bTmp.getType() == Material.AIR){
                if(bTmp.getY() > location.getBlockY()){
                    block = bTmp;
                    bTmp = bTmp.getRelative(BlockFace.DOWN);
                }else{
                    break;
                }
            }
            // MONTER
        }else{
            while (block.getType() != Material.AIR){
                if(block.getY() < location.getBlockY()+generator.heightLimit){
                    block = block.getRelative(BlockFace.UP);
                    if(block.getY() > currentHeight) {
                        currentHeight = block.getY();
                        summitBlock = block;
                    }
                }else{
                    currentHeight = location.getBlockY()+generator.heightLimit;
                    break;
                }
            }
        }

        updateData();
        return block;
    }

    public static Volcano importFromFile(File volcanoFile) {
        try {
            Bukkit.getLogger().log(Level.INFO, "Loading VolcanoFile "+volcanoFile.getName());
            if (volcanoFile.getName().toLowerCase().endsWith(".yml")) {

                Bukkit.getLogger().log(Level.INFO, "Checked VolcanoFile "+volcanoFile.getName()+"'s extension");
                String[] split = volcanoFile.getName().split(".yml");

                Bukkit.getLogger().log(Level.INFO, "Loaded VolcanoFile "+volcanoFile.getName());
                Volcano volcano = new Volcano();
                volcano.name = split[0];

                YamlConfiguration conf = YamlConfiguration.loadConfiguration(volcanoFile);

                // Compatibility with Diwaly's version
                volcano.file = volcanoFile;
                volcano.enabled = conf.getBoolean("enable", conf.getBoolean("enable"));
                volcano.location =
                        new Location(
                            Bukkit.getServer().getWorld(conf.getString("location.world", conf.getString("world"))),
                            conf.getInt("location.x", conf.getInt("x")),
                            conf.getInt("location.y", conf.getInt("y")),
                            conf.getInt("location.z", conf.getInt("z"))
                        );
                volcano.geoThermals.enable = conf.getBoolean("geoThermals.enable", false);
                volcano.geoThermals.geoThermalTicks = conf.getInt("geoThermals.geoThermalTicks", 200);
                volcano.erupt.settings.isExplosive = conf.getBoolean("erupt.isExplosive", true);
                volcano.erupt.settings.delayExplo = conf.getInt("erupt.delayExplo", conf.getInt("delayExplo"));
                volcano.erupt.settings.damageExplo = conf.getInt("erupt.damageExplo", conf.getInt("damageExplo"));
                volcano.erupt.settings.realDamageExplo = conf.getInt("erupt.realDamageExplo", conf.getInt("erupt.damageExplo", conf.getInt("damageExplo")));
                volcano.erupt.settings.timerExplo = conf.getInt("erupt.timerExplo", conf.getInt("timerExplo"));
                volcano.lavaFlow.settings.delayFlowed = conf.getInt("lavaFlow.delayFlowed", conf.getInt("delayFlowed"));
                volcano.lavaFlow.settings.flowed = conf.getInt("lavaFlow.flowed", conf.getInt("flowed"));
                volcano.autoStart.canAutoStart = conf.getBoolean("autoStart.canAutoStart", false);
                volcano.autoStart.eruptionTimer = conf.getInt("autoStart.eruptionTimer", 12000);
                volcano.autoStart.pourLavaStart = conf.getBoolean("autoStart.pourLavaStart", false);
                volcano.generator.heightLimit = conf.getInt("generator.heightLimit", conf.getInt("maxY") - volcano.location.getBlockY());
                volcano.generator.throat = conf.getBoolean("generator.throat", conf.getBoolean("throat"));
                volcano.generator.loadCompositions(conf.getString("generator.layer"));
                volcano.crater.craterRadius = conf.getInt("crater.craterRadius", conf.getInt("radius"));

                Bukkit.getLogger().log(Level.INFO, "Running Crater Setup for Volcano "+volcano.name+"!");
                volcano.crater.setCraters(volcano.location);

                Bukkit.getLogger().log(Level.INFO, "Setting Running Initial Setup for Volcano "+volcano.name+"!");
                volcano.initialSetup();

                Bukkit.getLogger().log(Level.INFO, "Setting Status for Volcano "+volcano.name+"!");
                volcano.autoStart.setStatus(conf.getString("autoStart.status", "DORMANT"));
                Bukkit.getLogger().log(Level.INFO, "Loaded VolcanoFile "+volcanoFile.getName()+" and loaded volcano "+volcano.name);
                return volcano;
            } else {


                Bukkit.getLogger().log(Level.SEVERE, "Error was detected while importing a volcanoFile: extension is not YML");
                Bukkit.getLogger().log(Level.SEVERE, "This should be handled before calling importFromFile Function.");

                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void initialSetup() {
        Bukkit.getLogger().log(Level.INFO, "Running Initial Setup for Volcano "+name+"!");
        lavaFlow.volcano = this;
        erupt.volcano = this;
        geoThermals.volcano = this;
        autoStart.volcano = this;
        Bukkit.getLogger().log(Level.INFO, "Registering EventHandler for Volcano "+name+"!");
        geoThermals.registerEventHandler();
        lavaFlow.registerEventHandler();

        Bukkit.getLogger().log(Level.INFO, "Running Crater Setup for Volcano "+name+"!");
        crater.setCraters(location);

        Bukkit.getLogger().log(Level.INFO, "updating currentHeight information for Volcano "+name+"!");
        currentHeight = (location.getWorld().getHighestBlockAt(location).getY() < location.getBlockY()) ? location.getBlockY() : location.getWorld().getHighestBlockAt(location).getY();

        Bukkit.getLogger().log(Level.INFO, "updating zone information for Volcano "+name+"!");
        updateData();

        Bukkit.getLogger().log(Level.INFO, "updating summitBlock information for Volcano "+name+"!");
        summitBlock = (location.getWorld().getHighestBlockAt(location).getY() < location.getBlockY()) ? location.getWorld().getBlockAt(location) : location.getWorld().getHighestBlockAt(location);

        Bukkit.getLogger().log(Level.INFO, "Initial Setup for Volcano "+name+" success!");

        if (enabled) {
            Bukkit.getLogger().log(Level.INFO, "Starting Volcano "+name+" based on loaded configuration");
            start();
        }
    }

    public void saveToFile() {
        try {
            Bukkit.getLogger().log(Level.INFO, "Saving Volcano "+name);

            Bukkit.getLogger().log(Level.INFO, "Starting to formatting Volcano "+name+"'s data");
            YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
            conf.options().header("Ultimate Volcano Configuration");
            conf.set("enable", enabled);
            conf.set("location.world", location.getWorld().getName());
            conf.set("location.x", location.getBlockX());
            conf.set("location.y", location.getBlockY());
            conf.set("location.z", location.getBlockZ());
            conf.set("geoThermals.enable", geoThermals.enable);
            conf.set("geoThermals.geoThermalsTicks", geoThermals.geoThermalTicks);
            conf.set("erupt.isExplosive", erupt.settings.isExplosive);
            conf.set("erupt.delayExplo", erupt.settings.delayExplo);
            conf.set("erupt.damageExplo", erupt.settings.damageExplo);
            conf.set("erupt.realDamageExplo", erupt.settings.realDamageExplo);
            conf.set("erupt.timerExplo", erupt.settings.timerExplo);
            conf.set("lavaFlow.delayFlowed", lavaFlow.settings.delayFlowed);
            conf.set("lavaFlow.flowed", lavaFlow.settings.flowed);
            conf.set("autoStart.canAutoStart", autoStart.canAutoStart);
            conf.set("autoStart.status", autoStart.getStatus());
            conf.set("autoStart.eruptionTimer", autoStart.eruptionTimer);
            conf.set("autoStart.pourLavaStart", autoStart.pourLavaStart);
            conf.set("generator.heightLimit", generator.heightLimit);
            conf.set("generator.throat", generator.throat);
            conf.set("crater.craterRadius", crater.craterRadius);
            Bukkit.getLogger().log(Level.INFO, "Starting to formatting Volcano "+name+"'s layer data");
            String layerData = "";
            for (int i = 0; i < generator.composition.size(); i++) {
                layerData += generator.composition.get(i).material+","+generator.composition.get(i).percentage;
                if ((i + 1) != generator.composition.size()) {
                    layerData += "/";
                }
            }
            conf.set("generator.layer", layerData);
            Bukkit.getLogger().log(Level.INFO, "Saving Volcano "+name+"'s data to file");
            conf.save(file);
            Bukkit.getLogger().log(Level.INFO, "Saved Volcano "+name);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "ERROR while saving Volcano "+name);
            e.printStackTrace();
        }
    }

}

class VolcanoComposition {
    public double percentage;
    public Material material;
}

class VolcanoCrater {
    public int craterRadius = 3;
    public int[] xF;
    public int[] yF;
    public int[] zF;

    public void setCraters(Location location) {
        int x = location.getBlockX();
        int y = (location.getBlockY() < 63
                &&
                (location.getWorld().getBlockAt(location).getType().equals(Material.WATER)
                    || location.getWorld().getBlockAt(location).getType().equals(Material.LEGACY_STATIONARY_WATER)))
                ? location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()) :
                location.getBlockY();
        int z = location.getBlockZ();

        y += 2; // for a beautiful volcano

        int tmpx = x - craterRadius;
        int tmpz = z - craterRadius;
        int tmpcrater = craterRadius * 2 + 1;

        // Construction du crat�re
        xF = new int[craterRadius * 8];
        yF = new int[craterRadius * 8];
        zF = new int[craterRadius * 8];
        int num = 0;

        for (int i = 0; i < tmpcrater; i++) {
            for (int j = 0; j < tmpcrater; j++) {
                if (i == 0 || j == 0 || i == tmpcrater - 1 || j == tmpcrater - 1) {
                    xF[num] = tmpx + i;
                    yF[num] = y;
                    zF[num] = tmpz + j;
                    num++;
                }
            }
        }
    }
}

class VolcanoGenerator {
    public int heightLimit = 255;
    public boolean throat = false;
    public List<VolcanoComposition> composition = new ArrayList<VolcanoComposition>();

    public boolean loadCompositions(String layer) {
        double sum = 0;
        String[] parsedLayer = {layer};
        if (layer.contains("/")) {
            parsedLayer = layer.split("/");
        }

        for (int i = 0; i < parsedLayer.length; i++) {
            String[] data = parsedLayer[i].split(",");
            VolcanoComposition volcComp = new VolcanoComposition();
            volcComp.material = Material.getMaterial(data[0]);
            volcComp.percentage = Double.parseDouble(data[1]);
            sum += Double.parseDouble(data[1]);
            composition.add(volcComp);
        }
        if (sum < 100) {
            if (MainPlugin.debug) {
                Bukkit.getLogger().log(Level.WARNING, "sum is less than 100. May have possibility of errors while running");
            }
            return false;
        } else if (sum > 100) {
            if (MainPlugin.debug) {
                Bukkit.getLogger().log(Level.WARNING, "sum is over 100. May have possibility of missing materials while running");
            }
            return false;
        }
        return true;
    }
}