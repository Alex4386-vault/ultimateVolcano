package com.playmintnetwork.ultimatevolcano;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class Volcano implements Listener {
    public static File volcanoDir = new File(MainPlugin.dataDir, "Volcanoes");
    public static String defaultComposition = "STONE,100";
    public File file;
    public String name;
    public boolean enabled;
    public boolean lavaFlowEnabled = false;
    public boolean firstStart = true;
    public Location location;
    public VolcanoGenerator generator = new VolcanoGenerator();
    public VolcanoErupt erupt = new VolcanoErupt();
    public VolcanoLavaFlow lavaFlow = new VolcanoLavaFlow();
    public VolcanoGeoThermals geoThermals = new VolcanoGeoThermals();
    public VolcanoCrater crater = new VolcanoCrater();
    public VolcanoAutoStart autoStart = new VolcanoAutoStart();
    public VolcanoBombs bombs = new VolcanoBombs(this);
    public VolcanoTremor tremor = new VolcanoTremor(this);
    public int zone;
    public Block summitBlock;

    private Random random = new Random();
    public int currentHeight;

    // tmp
    public int lavaFlowCycleCount = 0;

    public boolean inCrater(Location chkloc) {
        return (
                chkloc.getWorld().equals(location.getWorld()) &&
                (
                    Math.pow(location.getBlockX() - chkloc.getBlockX(),2) +
                    Math.pow(location.getBlockZ() - chkloc.getBlockZ(),2)
                ) < Math.pow(crater.craterRadius, 2)
                //MainPlugin.isDataInRange(location.getBlockX(), chkloc.getBlockX(), crater.craterRadius) &&
                //MainPlugin.isDataInRange(location.getBlockZ(), chkloc.getBlockZ(), crater.craterRadius)
        );
    }

    public Material getRandomBlock() {
        double a = random.nextDouble() * 100;
        double sum = 0;
        for (int i = 0; i < generator.composition.size(); i++) {
            if ((a >= sum) && (a < sum+generator.composition.get(i).percentage)) {
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

    public void generateCalderaRandomStrength() {
        float exploScale = (random.nextFloat() * (bombs.maxCalderaPower - bombs.minCalderaPower) + bombs.minCalderaPower);
        generateCaldera(exploScale);
    }

    public void generateCaldera(float defaultExplosionScale) {
        boolean wasEnabled = enabled;
        if (wasEnabled) stop();
        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano Caldera] Creating Caldera on Volcano "+name);
        getRandomLavaFlowCraterBlock();
        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Caldera] Updating currentHeight for Volcano "+name);

        int theY = location.getWorld().getHighestBlockYAt(location);
        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Caldera] Found Volcano "+name+"'s currentHeight at "+theY);
        Location top = new Location(location.getWorld(), location.getX(), theY, location.getZ());

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Caldera] Calculating Volcano "+name+"'s Explosion Scale = ("+bombs.maxBombPower+" * (("+(theY - location.getBlockY())+"/"+(255 - location.getBlockY())+"))");

        float explosionScale = defaultExplosionScale * ((theY - location.getBlockY()) / (float) (255 - location.getBlockY()));
        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Caldera] Creating explosion on Volcano "+name+"'s top @ "+top.getBlockX()+","+top.getBlockY()+","+top.getBlockZ()+" with explosion scale: "+explosionScale);
        location.getWorld().createExplosion(top, explosionScale, true, true);

        List<Location> sphereLocs = VolcanoBomb.generateSphere(top, (int) explosionScale / 6, false);

        for (Location loc : sphereLocs) {
            loc.getBlock().setType(Material.AIR);
        }

        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano Caldera] Created Caldera on Volcano "+name);
        currentHeight = top.getWorld().getHighestBlockYAt(top) - location.getBlockY();
        if (wasEnabled) start();
    }

    public void generateSmoke() {
        generateSmoke(VolcanoSmokeType.DARK);
    }

    public void generateSmoke(int amount) {
        generateSmoke(VolcanoSmokeType.DARK, amount);
    }

    public void generateSmoke(VolcanoSmokeType smokeType) {
        generateSmoke(smokeType, 100);
    }

    public void generateSmoke(VolcanoSmokeType smokeType, int smoke) {
        Random rand = new Random();

        Location posParticles = new Location(location.getWorld(), location.getX(), location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()), location.getZ());

        Particle.DustOptions options;
        float smokeSize = 1.2f;

        if (smokeType == VolcanoSmokeType.DARK) {
            options = new Particle.DustOptions(Color.fromRGB(64,64,64), smokeSize);
        } else {
            options = new Particle.DustOptions(Color.fromRGB(255,255,255), smokeSize);
        }

        for (int i = 0; i < smoke; i++) {
            VolcanoUtils.createParticle(
                    Particle.REDSTONE,
                    posParticles,
                    100,
                    ((rand.nextDouble() - 0.5) * 2),
                    1.4d + ((rand.nextDouble() - 0.5) * 2),
                    ((rand.nextDouble() - 0.5) * 2),
                    0,
                    options
            );
            VolcanoUtils.createParticle(
                Particle.CAMPFIRE_SIGNAL_SMOKE,
                posParticles,
                0,
                ((rand.nextDouble() - 0.5) * 2),
                1.4d + ((rand.nextDouble() - 0.5) * 2),
                ((rand.nextDouble() - 0.5) * 2)
            );
        }

    }

    public boolean delete() {
        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano Caldera] Deleting Volcano "+name);
        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Caldera] Stopping Volcano "+name+" to continue delete procedure");
        stop();
        MainPlugin.listVolcanoes.remove(this);
        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Caldera] Unloaded Volcano "+name);
        if (file.exists()) {
            MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Caldera] Found Volcano "+name+"'s VolcanoFile to reload");
            file.delete();
            MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano Caldera] Deleted Volcano "+name);
        }
        return true;
    }

    public boolean reload() {
        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+" Reload] Reloading Volcano");
        prepareShutdown();
        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Reload] Volcano "+name+"'s EventHandler was gracefully shutted down.");
        MainPlugin.listVolcanoes.remove(this);
        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Reload] Unloaded Volcano");
        if (file.exists()) {

            MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Reload] Found Volcano "+name+"'s VolcanoFile to reload");
            MainPlugin.listVolcanoes.add(Volcano.importFromFile(file));
            MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+" Reload] Reloaded Volcano "+name);
            return true;
        } else {
            MainPlugin.plugin.getLogger().log(Level.SEVERE, "[Volcano "+name+" Reload] Volcano "+name+" reloading failed!");
            return false;
        }
    }

    public static boolean create(String name, Location location, int height, String compositions, boolean throat, boolean activateGeoThermals) {
        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+" Create] Creating Volcano "+name);
        File volcanoFile = new File(volcanoDir, name+".yml");
        if (volcanoFile.exists() ) { MainPlugin.plugin.getLogger().log(Level.SEVERE, "[Volcano "+name+" Create] Volcano "+name+" already exists. Halting."); return false; }

        try {
            MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Create] Creating VolcanoFile for Volcano "+name);
            volcanoFile.createNewFile();
        } catch (Exception e) {
            MainPlugin.plugin.getLogger().log(Level.SEVERE, "[Volcano "+name+" Create] An Exception occurred while creating VolcanoFile for "+name+", Please refer to stacktrace below.");
            e.printStackTrace();
            return false;
        }

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Create] Creating Volcano Object for Volcano "+name);
        Volcano volcano = new Volcano();

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Create] Loading Compositions for Volcano "+name);
        if (!volcano.generator.loadCompositions(compositions)) {
            MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Create] Composition error! on Volcano "+name);
            return false;
        }

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Create] Setting default variables to Volcano "+name);
        volcano.file = volcanoFile;
        volcano.name = name;
        volcano.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        volcano.generator.heightLimit = height;
        volcano.generator.throat = throat;
        volcano.geoThermals.enable = activateGeoThermals;
        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Create] Running Crater Setup for Volcano "+name);
        volcano.crater.setCraters(location);
        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Create] Running Initial Setup for Volcanao "+name);
        volcano.initialSetup();

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Create] Loading Volcano "+name+" to plugin.");
        MainPlugin.listVolcanoes.add(volcano);

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Create] Trying to save Volcano "+name);
        volcano.saveToFile();
        return true;
    }

    public void start() {
        lavaFlowCycleCount = 0;
        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+"] Starting Volcano "+name);
        if (enabled && !firstStart) { MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+"] Volcano "+name+" was already enabled!"); return; }
        if (firstStart) {
            MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+"] First start of Volcano "+name+" since the bukkit boot detected");
            Block block = null;
            MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+"] Setting Initial Crater for Volcano "+name);
            firstStart = false;
        }
        lavaFlow.registerTask();
        erupt.registerTask();
        tremor.registerTask();
        enabled = true;
        lavaFlowEnabled = true;
        autoStart.status = VolcanoCurrentStatus.ERUPTING;

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+"] Volcano "+name+" Composition Info: "+generator.exportCompositions());
        saveToFile();
    }

    public void stop() {
        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+"] Stopping Volcano "+name);
        if (!enabled) { return; }
        enabled = false;

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+"] unregistering Volcano "+name+" 's EventHandlers");

        int mostTicks = 0;

        erupt.unregisterTask();

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+"] disabling Volcano "+name+" 's Force Loaded Chunks");
        for (int i = 0; i < lavaFlow.lavaFlowChunks.size(); i++) {
            lavaFlow.lavaFlowChunks.get(i).setForceLoaded(false);
        }
        lavaFlow.lavaFlowChunks.clear();

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+"] Setting Volcano "+name+" status to MAJOR_ACTIVITY");
        autoStart.status = VolcanoCurrentStatus.MAJOR_ACTIVITY;

        saveToFile();
        forceCoolCrater();
    }

    public void forceCool() {
        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+"] Focibily cooling volcano "+name);

        Iterator<VolcanoLavaCoolData> coolDataIterator = lavaFlow.lavaCoolData.iterator();
        while (coolDataIterator.hasNext()) {
            VolcanoLavaCoolData coolData = coolDataIterator.next();
            coolData.forceCoolDown();
            coolDataIterator.remove();
        }

        lavaFlow.lavaFlowingBlocks.clear();

        forceCoolCrater();

        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+"] Unregistering volcano lavaFlow "+name);
        lavaFlow.unregisterTask();

    }

    public void forceCoolCrater() {
        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+"] Focibily cooling crater of volcano "+name);

        List<Block> craterBlocks = getCraterBlocks();

        for (Block craterBlock : craterBlocks) {
            craterBlock.setType(getRandomBlock());
        }

        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+"] Focibily cooling crater of volcano"+name+" done.");
    }

    public void prepareShutdown() {
        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+"] Shutting down Volcano "+name);
        lavaFlow.unregisterTask();
        erupt.unregisterTask();
        geoThermals.unregisterTask();
        tremor.unregisterTask();
        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+"] Volcano "+name+" was successfully shutted down");
    }

    public void updateData() {
        zone = (location.getWorld().getEnvironment() == World.Environment.NETHER) ? (((currentHeight - location.getBlockY()) * 8) + crater.craterRadius) : ((currentHeight - location.getBlockY()) * 3 + crater.craterRadius);
    }
    
    public List<Block> getCraterBlocks() {
        List<Block> craterBlocks = new ArrayList<>();

        for (int i = 0; i < crater.xF.length; i++) {
            // get highest and reduce 1 due to the actual block.
            int currentHighestY = location.getWorld().getHighestBlockYAt(crater.xF[i], crater.zF[i]) - 1;
            Block block = location.getWorld().getBlockAt(crater.xF[i], currentHighestY, crater.zF[i]);
            while (Arrays.asList(VolcanoLavaFlowExplode.explodeAndRemove).contains(block.getType()) ||
                    block.getType() == Material.AIR) {
                currentHighestY--;
                block = block.getRelative(0,-1,0);
            }
            craterBlocks.add(block);
        }

        return craterBlocks;
    }

    public Block getRandomLavaFlowCraterBlock() {
        int totalY = 0;
        int craterOffset = 2;

        int[] highestY = new int[crater.xF.length];

        for (int i = 0; i < crater.xF.length; i++) {
            // get highest and reduce 1 due to the actual block.
            int currentHighestY = location.getWorld().getHighestBlockYAt(crater.xF[i], crater.zF[i]) - 1;
            Block block = location.getWorld().getBlockAt(crater.xF[i], currentHighestY, crater.zF[i]);
            while (Arrays.asList(VolcanoLavaFlowExplode.explodeAndRemove).contains(block.getType()) ||
                    block.getType() == Material.AIR) {
                currentHighestY--;
                block = block.getRelative(0,-1,0);
            }
            currentHighestY++;
            highestY[i] = currentHighestY;
            
            totalY += currentHighestY;
        }

        int averageY = totalY / crater.xF.length;

        List<Integer> lowerSections = new ArrayList<>();
        for (int i = 0; i < crater.xF.length; i++) {
            if (highestY[i] < averageY - random.nextInt(craterOffset)) {
                lowerSections.add(i);
            }
        }

        Collections.shuffle(lowerSections);

        int i;
        int yyy;
        boolean offSetControl = random.nextDouble() < 0.8f && lowerSections.size() > 0;

        if (offSetControl) {
            i = lowerSections.get(random.nextInt(lowerSections.size()));
        } else {
            i = random.nextInt(crater.xF.length);
        }

        yyy = highestY[i];
        if (lavaFlowCycleCount % (lavaFlow.settings.updateRate * 20) == 0) {
            if (offSetControl) {
                MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+"] Volcano "+name+" is erupting lava @ Block "+location.getWorld().getName()+" "+crater.xF[i]+","+yyy+","+crater.zF[i]+" with craterOffset control: "+(averageY - craterOffset));
            } else {
                MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+"] Volcano "+name+" is erupting lava @ Block "+location.getWorld().getName()+" "+crater.xF[i]+","+yyy+","+crater.zF[i]);
            }
        }

        crater.yF[i] = highestY[i];

        return getBlockForLavaFlow(location.getWorld().getBlockAt(crater.xF[i], highestY[i], crater.zF[i]));
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
                    if (location.getBlockY()+generator.heightLimit >= 255) {
                        currentHeight = 255;
                    } else {
                        currentHeight = location.getBlockY()+generator.heightLimit;
                    }
                    break;
                }
            }
        }

        updateData();
        return block;
    }

    public static Volcano importFromFile(File volcanoFile) {
        try {
            MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano Importer] Loading VolcanoFile "+volcanoFile.getName());
            if (volcanoFile.getName().toLowerCase().endsWith(".yml")) {

                MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Importer] Checked VolcanoFile "+volcanoFile.getName()+"'s extension");
                String[] split = volcanoFile.getName().split(".yml");

                MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Importer] Loaded VolcanoFile "+volcanoFile.getName());
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
                volcano.geoThermals.geoThermalUpdateRate = conf.getInt("geoThermals.geoThermalUpdateRate", 200);
                volcano.erupt.settings.isExplosive = conf.getBoolean("erupt.isExplosive", true);
                volcano.erupt.settings.delayExplo = conf.getInt("erupt.delayExplo", conf.getInt("delayExplo"));
                volcano.erupt.settings.damageExplo = conf.getInt("erupt.damageExplo", conf.getInt("damageExplo"));
                volcano.erupt.settings.realDamageExplo = conf.getInt("erupt.realDamageExplo", conf.getInt("erupt.damageExplo", conf.getInt("damageExplo")));
                volcano.erupt.settings.timerExplo = conf.getInt("erupt.timerExplo", conf.getInt("timerExplo"));
                volcano.erupt.settings.maxBombCount = conf.getInt("erupt.maxBombCount", VolcanoEruptionDefaultSettings.maxBombCount);
                volcano.erupt.settings.minBombCount = conf.getInt("erupt.minBombCount", VolcanoEruptionDefaultSettings.minBombCount);

                volcano.lavaFlow.settings.delayFlowed = conf.getInt("lavaFlow.delayFlowed", conf.getInt("delayFlowed"));
                volcano.lavaFlow.settings.flowed = conf.getInt("lavaFlow.flowed", conf.getInt("flowed"));
                volcano.lavaFlow.settings.updateRate = conf.getInt("lavaFlow.updateRate", VolcanoLavaFlowDefaultSettings.updateRate);

                volcano.autoStart.canAutoStart = conf.getBoolean("autoStart.canAutoStart", false);
                volcano.autoStart.eruptionTimer = conf.getInt("autoStart.eruptionTimer", 12000);
                volcano.autoStart.pourLavaStart = conf.getBoolean("autoStart.pourLavaStart", false);
                volcano.generator.heightLimit = conf.getInt("generator.heightLimit", conf.getInt("maxY") - volcano.location.getBlockY());
                volcano.generator.throat = conf.getBoolean("generator.throat", conf.getBoolean("throat"));
                volcano.generator.loadCompositions(conf.getString("generator.layer"));
                volcano.crater.craterRadius = conf.getInt("crater.craterRadius", conf.getInt("radius"));

                volcano.bombs.minBombPower = (float) conf.getDouble("bombs.minPower", VolcanoBombsDefault.minBombPower);
                volcano.bombs.maxBombPower = (float) conf.getDouble("bombs.maxPower", VolcanoBombsDefault.maxBombPower);
                volcano.bombs.minBombLaunchPower = (float) conf.getDouble("bombs.minLaunchPower", VolcanoBombsDefault.minBombLaunchPower);
                volcano.bombs.maxBombLaunchPower = (float) conf.getDouble("bombs.maxLaunchPower", VolcanoBombsDefault.maxBombLaunchPower);
                volcano.bombs.minBombRadius = conf.getInt("bombs.minRadius", VolcanoBombsDefault.minBombRadius);
                volcano.bombs.maxBombRadius = conf.getInt("bombs.maxRadius", VolcanoBombsDefault.maxBombRadius);
                volcano.bombs.bombDelay = conf.getInt("bombs.delay", VolcanoBombsDefault.bombDelay);


                MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Importer] Running Crater Setup for Volcano "+volcano.name+"!");
                volcano.crater.setCraters(volcano.location);

                MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Importer] Setting Running Initial Setup for Volcano "+volcano.name+"!");
                volcano.initialSetup();

                MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano Importer] Setting Status for Volcano "+volcano.name+"!");
                volcano.autoStart.setStatus(conf.getString("autoStart.status", "DORMANT"));
                MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano Importer] Loaded VolcanoFile "+volcanoFile.getName()+" and loaded volcano "+volcano.name);
                return volcano;
            } else {


                MainPlugin.plugin.getLogger().log(Level.SEVERE, "[Volcano Importer] Error was detected while importing a volcanoFile: extension is not YML");
                MainPlugin.plugin.getLogger().log(Level.SEVERE, "[Volcano Importer] This should be handled before calling importFromFile Function.");

                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void preConstruct() {

    }

    public void initialSetup() {
        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+" Initial Setup] Running Initial Setup for Volcano "+name+"!");
        lavaFlow.volcano = this;
        erupt.volcano = this;
        geoThermals.volcano = this;
        autoStart.volcano = this;

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Initial Setup] Registering EventHandler for Volcano "+name+"!");
        geoThermals.registerEventHandler();
        lavaFlow.registerEventHandler();

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Initial Setup] Registering Task for Volcano "+name+"!");
        geoThermals.registerTask();
        erupt.registerTask();

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Initial Setup] Running Crater Setup for Volcano "+name+"!");
        crater.setCraters(location);

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Initial Setup] Updating currentHeight information for Volcano "+name+"!");
        currentHeight = (location.getWorld().getHighestBlockAt(location).getY() < location.getBlockY()) ? location.getBlockY() : location.getWorld().getHighestBlockAt(location).getY();

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Initial Setup] Updating zone information for Volcano "+name+"!");
        updateData();

        MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Initial Setup] Updating summitBlock information for Volcano "+name+"!");
        summitBlock = (location.getWorld().getHighestBlockAt(location).getY() < location.getBlockY()) ? location.getWorld().getBlockAt(location) : location.getWorld().getHighestBlockAt(location);

        MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+" Initial Setup] Initial Setup for Volcano "+name+" success!");

        if (enabled) {
            MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+" Initial Setup] Starting Volcano "+name+" based on loaded configuration");
            start();
        }
    }

    public void saveToFile() {
        try {
            MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+" Save] Saving Volcano "+name);

            MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Save] Starting to formatting Volcano "+name+"'s data");
            YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
            conf.options().header("Ultimate Volcano Configuration");
            conf.set("enable", enabled);
            conf.set("location.world", location.getWorld().getName());
            conf.set("location.x", location.getBlockX());
            conf.set("location.y", location.getBlockY());
            conf.set("location.z", location.getBlockZ());
            conf.set("geoThermals.enable", geoThermals.enable);
            conf.set("geoThermals.geoThermalUpdateRate", geoThermals.geoThermalUpdateRate);
            conf.set("erupt.isExplosive", erupt.settings.isExplosive);
            conf.set("erupt.delayExplo", erupt.settings.delayExplo);
            conf.set("erupt.damageExplo", erupt.settings.damageExplo);
            conf.set("erupt.realDamageExplo", erupt.settings.realDamageExplo);
            conf.set("erupt.timerExplo", erupt.settings.timerExplo);
            conf.set("erupt.minBombCount", erupt.settings.minBombCount);
            conf.set("erupt.maxBombCount", erupt.settings.maxBombCount);
            conf.set("lavaFlow.delayFlowed", lavaFlow.settings.delayFlowed);
            conf.set("lavaFlow.flowed", lavaFlow.settings.flowed);
            conf.set("lavaFlow.updateRate", lavaFlow.settings.updateRate);
            conf.set("autoStart.canAutoStart", autoStart.canAutoStart);
            conf.set("autoStart.status", autoStart.getStatus());
            conf.set("autoStart.eruptionTimer", autoStart.eruptionTimer);
            conf.set("autoStart.pourLavaStart", autoStart.pourLavaStart);
            conf.set("bombs.minLaunchPower", bombs.minBombLaunchPower);
            conf.set("bombs.maxLaunchPower", bombs.maxBombLaunchPower);
            conf.set("bombs.minPower", bombs.minBombPower);
            conf.set("bombs.maxPower", bombs.maxBombPower);
            conf.set("bombs.minRadius", bombs.minBombRadius);
            conf.set("bombs.maxRadius", bombs.maxBombRadius);
            conf.set("bombs.delay", bombs.bombDelay);

            conf.set("generator.heightLimit", generator.heightLimit);
            conf.set("generator.throat", generator.throat);
            conf.set("crater.craterRadius", crater.craterRadius);
            MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Save] Starting to formatting Volcano "+name+"'s layer data");
            String layerData = "";
            for (int i = 0; i < generator.composition.size(); i++) {
                layerData += generator.composition.get(i).material+","+generator.composition.get(i).percentage;
                if ((i + 1) != generator.composition.size()) {
                    layerData += "/";
                }
            }
            conf.set("generator.layer", layerData);
            MainPlugin.plugin.getLogger().log(Level.FINEST, "[Volcano "+name+" Save] Saving Volcano "+name+"'s data to file");
            conf.save(file);
            MainPlugin.plugin.getLogger().log(Level.INFO, "[Volcano "+name+" Save] Saved Volcano "+name);
        } catch (Exception e) {
            MainPlugin.plugin.getLogger().log(Level.SEVERE, "[Volcano "+name+" Save] ERROR while saving Volcano "+name);
            e.printStackTrace();
        }
    }

}

class VolcanoComposition {
    public double percentage;
    public Material material;
}

class VolcanoCrater {
    public static int craterDefault = 10;
    public int craterRadius = craterDefault;
    public int[] xF;
    public int[] yF;
    public int[] zF;

    public void setCraters(Location location) {
        int x = location.getBlockX();
        /*
        int y = (location.getBlockY() < 63
                &&
                (location.getWorld().getBlockAt(location).getType().equals(Material.WATER)
                    || location.getWorld().getBlockAt(location).getType().equals(Material.LEGACY_STATIONARY_WATER)))
                ? location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()) :
                location.getBlockY();
        */
        // override. lava can now flow under water.
        int y = location.getBlockY();
        int z = location.getBlockZ();

        y += 2; // for a beautiful volcano

        /*
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
        */

        // crater, now in the freak'in circles.
        List<Location> craterLocs = new ArrayList<>();

        for (int tmpX = x - craterRadius; tmpX <= x + craterRadius; tmpX++) {
            for (int tmpZ = z - craterRadius; tmpZ <= z + craterRadius; tmpZ++) {
                double distanceSquared = (Math.pow((tmpX - x),2)) + (Math.pow((tmpZ - z),2));
                double distance = Math.sqrt(distanceSquared);

                if (craterRadius-1 < distance && distance < craterRadius+1) {
                    craterLocs.add(new Location(location.getWorld(), tmpX, y, tmpZ));
                }
            }
        }

        Collections.shuffle(craterLocs);

        int locCount = craterLocs.size();

        xF = new int[locCount];
        yF = new int[locCount];
        zF = new int[locCount];

        for (int i = 0; i < locCount; i++) {
            xF[i] = craterLocs.get(i).getBlockX();
            yF[i] = craterLocs.get(i).getBlockY();
            zF[i] = craterLocs.get(i).getBlockZ();
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
                MainPlugin.plugin.getLogger().log(Level.WARNING, "[Volcano Crater] sum is less than 100. May have possibility of errors while running");
            }
            return false;
        } else if (sum > 100) {
            if (MainPlugin.debug) {
                MainPlugin.plugin.getLogger().log(Level.WARNING, "[Volcano Crater] sum is over 100. May have possibility of missing materials while running");
            }
            return false;
        }
        return true;
    }

    public String exportCompositions() {
        String layerData = "";
        for (int i = 0; i < composition.size(); i++) {
            layerData += composition.get(i).material+","+composition.get(i).percentage;
            if ((i + 1) != composition.size()) {
                layerData += "/";
            }
        }
        return layerData;
    }
}

enum VolcanoSmokeType {
    DARK,
    WHITE
}
