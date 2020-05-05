package com.playmintnetwork.ultimatevolcano;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
//import com.sk89q.worldedit.WorldEdit;
//import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MainPlugin extends JavaPlugin {
    public Logger logger = getLogger();
    public static File dataDir;
    public static List<Volcano> listVolcanoes = new ArrayList<Volcano>();
    public static List<World> allowedWorlds = new ArrayList<World>();
    public static boolean debug = true;
    public static Plugin plugin;
    public static VolcanoPlayerEvent vpe;
    public static VolcanoBombListener vbl;
    //public static WorldEditPlugin worldEditPlugin;
    //public static WorldEdit worldEdit = null;

    // For ECMAScript Style stuff
    public Plugin self = this;

    public void logMe(String str) {
        logger.log(Level.INFO, str);
    }

    public void debugLog(String str) {
        if (debug) {
            logger.log(Level.INFO, str);
        }
    }

    /*
    public void hookWorldEdit() {
        if (Bukkit.getServer().getPluginManager().getPlugin("WorldEdit").equals(null)) {
            Bukkit.getLogger().log(Level.SEVERE, "WorldEdit Dependency Missing!");
        }

        this.worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().
                getPluginManager().getPlugin("WorldEdit");

        this.worldEdit = worldEditPlugin.getWorldEdit();


    }

    public static boolean isWorldEditHooked() {
        return worldEdit != null;
    }
    */

    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        logMe("Initializing!");

        //hookWorldEdit();

        logMe("Setting up Data Directory.");
        dataDir = this.getDataFolder();
        if (!dataDir.exists()) { dataDir.mkdir(); }

        logMe("Setting up Volcano Data Directory.");
        if (!Volcano.volcanoDir.exists()) { Volcano.volcanoDir.mkdir(); }

        logMe("Writing Default plugin configuration if not exists....");
        this.saveDefaultConfig();

        logMe("Writing complete!");

        logMe("Loading plugin configuration...");
        loadPluginConfiguration();

        logMe("Data Directory setup complete.");

        logMe("Loading Volcanoes");
        loadVolcanoes();
        logMe("Volcanoes Loaded.");

        vpe = new VolcanoPlayerEvent();
        vpe.registerEventHandler();

        vbl = new VolcanoBombListener();
        vbl.registerEventListener();
        vbl.registerTask();

        VolcanoAutoStart.registerTask();
    }

    public static void loadPluginConfiguration() {
        Bukkit.getLogger().log(Level.INFO, "Loading Plugin Configuration:");

        FileConfiguration config = MainPlugin.plugin.getConfig();
        VolcanoAutoStart.statusCheckInterval = config.getInt("autoStart.statusCheckInterval", 144000);
        Bukkit.getLogger().log(Level.INFO, "loaded Volcano AutoStart Status Check Interval: "+VolcanoAutoStart.statusCheckInterval);

        Volcano.defaultComposition = config.getString("defaultvolcano.generator.layer", "STONE,100");
        Bukkit.getLogger().log(Level.INFO, "loaded Volcano Default Composition: "+Volcano.defaultComposition);

        VolcanoEruptionDefaultSettings.isExplosive = config.getBoolean("defaultvolcano.erupt.isExplosive", VolcanoEruptionDefaultSettings.isExplosive);
        VolcanoEruptionDefaultSettings.delayExplo = config.getInt("defaultvolcano.erupt.deplayExplo", VolcanoEruptionDefaultSettings.delayExplo);
        VolcanoEruptionDefaultSettings.damageExplo = config.getInt("defaultvolcano.erupt.damageExplo", VolcanoEruptionDefaultSettings.damageExplo);
        VolcanoEruptionDefaultSettings.realDamageExplo = config.getInt("defaultvolcano.erupt.realDamageExplo", VolcanoEruptionDefaultSettings.realDamageExplo);
        VolcanoEruptionDefaultSettings.timerExplo = config.getInt("defaultvolcano.erupt.timerExplo", VolcanoEruptionDefaultSettings.timerExplo);
        VolcanoEruptionDefaultSettings.minBombCount = config.getInt("defaultvolcano.erupt.minBombCount", VolcanoEruptionDefaultSettings.minBombCount);
        VolcanoEruptionDefaultSettings.maxBombCount = config.getInt("defaultvolcano.erupt.maxBombCount", VolcanoEruptionDefaultSettings.maxBombCount);


        VolcanoAutoStart.defaultEruptionTimer = config.getInt("defaultvolcano.autoStart.eruptionTimer", 12000);
        Bukkit.getLogger().log(Level.INFO, "loaded Volcano Default Eruption Timer: "+VolcanoAutoStart.defaultEruptionTimer);

        VolcanoBombsDefault.bombDelay = config.getInt("defaultvolcano.bombs.delay", VolcanoBombsDefault.bombDelay);
        Bukkit.getLogger().log(Level.INFO, "loaded Volcano Default Bombs Delay: "+VolcanoBombsDefault.bombDelay);

        VolcanoBombsDefault.maxBombLaunchPower = (float) config.getDouble("defaultvolcano.bombs.maxLaunchPower", VolcanoBombsDefault.maxBombLaunchPower);
        VolcanoBombsDefault.minBombLaunchPower = (float) config.getDouble("defaultvolcano.bombs.minLaunchPower", VolcanoBombsDefault.minBombLaunchPower);

        VolcanoBombsDefault.maxBombPower = (float) config.getDouble("defaultvolcano.bombs.maxPower", VolcanoBombsDefault.maxBombLaunchPower);
        VolcanoBombsDefault.minBombPower = (float) config.getDouble("defaultvolcano.bombs.minPower", VolcanoBombsDefault.minBombLaunchPower);

        VolcanoBombsDefault.maxBombRadius = config.getInt("defaultvolcano.bombs.maxRadius", VolcanoBombsDefault.maxBombRadius);
        VolcanoBombsDefault.minBombRadius = config.getInt("defaultvolcano.bombs.minRadius", VolcanoBombsDefault.minBombRadius);

        VolcanoBombsDefault.maxCalderaPower = config.getInt("defaultvolcano.bombs.maxCalderaPower", VolcanoBombsDefault.maxBombRadius);
        VolcanoBombsDefault.minCalderaPower = config.getInt("defaultvolcano.bombs.minCalderaPower", VolcanoBombsDefault.minBombRadius);

        VolcanoAutoStart.defaultEruptionTimer = config.getInt("defaultvolcano.autoStart.eruptionTimer", 12000);
        Bukkit.getLogger().log(Level.INFO, "loaded Volcano Default Eruption Timer: "+VolcanoAutoStart.defaultEruptionTimer);

        VolcanoLavaFlowDefaultSettings.flowed = config.getInt("defaultvolcano.lavaflow.flowed", 6);
        VolcanoLavaFlowDefaultSettings.delayFlowed = config.getInt("defaultvolcano.lavaflow.delayFlowed", 3);
        VolcanoLavaFlowDefaultSettings.updateRate = config.getInt("defaultvolcano.lavaflow.updateRate", 20);
        Bukkit.getLogger().log(Level.INFO, "loaded Volcano Default Lava Flow Timer: flow: "+VolcanoLavaFlowDefaultSettings.flowed+", delayflow: "+VolcanoLavaFlowDefaultSettings.delayFlowed);

        VolcanoAutoStart.defaultCanAutoStart = config.getBoolean("autoStart.canAutoStart", true);
        VolcanoCrater.craterDefault = config.getInt("defaultvolcano.generator.craterRadius", VolcanoCrater.craterDefault);

        VolcanoAutoStartProbability.dormant.increase = config.getDouble("autostart.probability.dormant.increase", 0.05);
        VolcanoAutoStartProbability.dormant.decrease = config.getDouble("autostart.probability.dormant.decrease", 0.005);
        VolcanoAutoStartProbability.minor_activity.increase = config.getDouble("autostart.probability.minor_activity.increase", 0.2);
        VolcanoAutoStartProbability.minor_activity.decrease = config.getDouble("autostart.probability.minor_activity.decrease", 0.05);
        VolcanoAutoStartProbability.major_activity.increase = config.getDouble("autostart.probability.major_activity.increase", 0.35);
        VolcanoAutoStartProbability.major_activity.decrease = config.getDouble("autostart.probability.major_activity.decrease", 0.25);


        Bukkit.getLogger().log(Level.INFO, "Successfully Loaded Plugin Configuration!");
    }

    public static void reloadAll() {
        VolcanoAutoStart.unregisterTask();
        Bukkit.getLogger().log(Level.INFO, "Plugin Reload Triggered!!");

        Bukkit.getLogger().log(Level.INFO, "Shutting down all loaded Volcanoes");
        for (Volcano volcano:listVolcanoes) {
            volcano.prepareShutdown();
        }

        Bukkit.getLogger().log(Level.INFO, "Unloading all volcanoes");
        listVolcanoes.clear();

        plugin.reloadConfig();

        Bukkit.getLogger().log(Level.INFO, "Loading plugin default configuration");
        loadPluginConfiguration();

        Bukkit.getLogger().log(Level.INFO, "Loading all volcanoes!");
        loadVolcanoes();

        Bukkit.getLogger().log(Level.INFO, "Plugin Reload Complete!!");
    }

    public static boolean isDataInRange(double base, double comparedValue, double range) {
        return base == comparedValue || Math.abs(base - comparedValue) <= range;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Volcano volcano : listVolcanoes) {
            volcano.saveToFile();
            volcano.prepareShutdown();
        }

        VolcanoAutoStart.unregisterTask();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("volcano") || label.equalsIgnoreCase("ultimatevolcano") || label.equalsIgnoreCase("vol")) {
            return VolcanoCommand.tabCompleteHandler(sender, command, label, args);
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("volcano") || label.equalsIgnoreCase("ultimatevolcano") || label.equalsIgnoreCase("vol")) {
            return VolcanoCommand.commandHandler(sender, command, label, args);
        }
        return false;
    }

    public static void loadVolcanoes() {
        File volcanoDir = Volcano.volcanoDir;
        if (!volcanoDir.exists()) { volcanoDir.mkdir(); }
        for (File file : volcanoDir.listFiles()) {
            if (file.isFile()) {
                if (file.getName().contains(".invalid")) {

                } else {
                    Volcano vol = Volcano.importFromFile(file);
                    if (vol != null) {
                        listVolcanoes.add(vol);
                    } else {
                        file.renameTo(new File(file.getPath() + ".invalid"));
                    }
                }
            }
        }
    }

    public static Volcano findVolcano(String name) {
        for (Volcano volcano: listVolcanoes) {
            if (volcano.name.equals(name)) {
                return volcano;
            }
        }
        return null;
    }
}
