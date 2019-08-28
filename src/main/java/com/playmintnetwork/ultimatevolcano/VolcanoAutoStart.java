package com.playmintnetwork.ultimatevolcano;

import org.bukkit.Bukkit;

import java.util.Random;
import java.util.logging.Level;

public class VolcanoAutoStart {
    public Volcano volcano;
    public static boolean defaultCanAutoStart = true;
    public boolean canAutoStart = defaultCanAutoStart;
    public boolean pourLavaStart = true;
    public VolcanoCurrentStatus status = VolcanoCurrentStatus.DORMANT;
    public static int statusCheckInterval = 144000;
    public static int defaultEruptionTimer = 12000;
    public int eruptionTimer = defaultEruptionTimer;
    public static int scheduleID = -1;

    public static void registerTask() {
        if (scheduleID >= 0) { return; }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(MainPlugin.plugin,
            () -> {
                Bukkit.getLogger().log(Level.INFO, "Volcano AutoStart interval Checking...");

                Random rand = new Random();
                for (Volcano volcano:MainPlugin.listVolcanoes) {
                    if (volcano.autoStart.canAutoStart && !volcano.enabled) {
                        double a = 0.0;
                        a = rand.nextDouble();

                        switch (volcano.autoStart.status) {
                            case DORMANT:
                                if (a <= VolcanoAutoStartProbability.dormant.increase) {
                                    volcano.autoStart.status = VolcanoCurrentStatus.MINOR_ACTIVITY;
                                    Bukkit.getLogger().log(Level.INFO, "Volcano "+volcano.name+"'s status was raised to"+volcano.autoStart.getStatus());
                                } else if (a <= VolcanoAutoStartProbability.dormant.increase + VolcanoAutoStartProbability.dormant.decrease) {
                                    volcano.autoStart.status = VolcanoCurrentStatus.EXTINCT;
                                    Bukkit.getLogger().log(Level.INFO, "Volcano "+volcano.name+"'s status was decreased to"+volcano.autoStart.getStatus());
                                }
                                break;
                            case MINOR_ACTIVITY:
                                if (a <= VolcanoAutoStartProbability.minor_activity.increase) {
                                    volcano.autoStart.status = VolcanoCurrentStatus.MAJOR_ACTIVITY;
                                    Bukkit.getLogger().log(Level.INFO, "Volcano "+volcano.name+"'s status was raised to"+volcano.autoStart.getStatus());
                                } else if (a <= VolcanoAutoStartProbability.minor_activity.increase + VolcanoAutoStartProbability.minor_activity.decrease) {
                                    volcano.autoStart.status = VolcanoCurrentStatus.DORMANT;
                                    Bukkit.getLogger().log(Level.INFO, "Volcano "+volcano.name+"'s status was decreased to"+volcano.autoStart.getStatus());
                                }
                                break;
                            case MAJOR_ACTIVITY:
                                if (a <= VolcanoAutoStartProbability.major_activity.increase) {
                                    volcano.autoStart.status = VolcanoCurrentStatus.ERUPTING;
                                    Bukkit.getLogger().log(Level.INFO, "Volcano "+volcano.name+"'s status was raised into ERUPTING, It is NOW ERUPTING!!!!");
                                    volcano.start();
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(MainPlugin.plugin, () -> {
                                        Bukkit.getLogger().log(Level.INFO, "Volcano "+volcano.name+" just stopped erupting and level gone down to "+volcano.autoStart.getStatus());
                                        volcano.stop();
                                        volcano.autoStart.status = VolcanoCurrentStatus.MAJOR_ACTIVITY;
                                    }, volcano.autoStart.eruptionTimer);
                                } else if (a <= VolcanoAutoStartProbability.major_activity.increase + VolcanoAutoStartProbability.major_activity.decrease) {
                                    volcano.autoStart.status = VolcanoCurrentStatus.MINOR_ACTIVITY;
                                    Bukkit.getLogger().log(Level.INFO, "Volcano "+volcano.name+"'s status was decreased to"+volcano.autoStart.getStatus());
                                }
                                break;
                            default:
                                break;
                        }
                        volcano.saveToFile();
                    }
                }
            },
        0L, statusCheckInterval);
    }

    public boolean setStatus(String status) {
        if (status.equalsIgnoreCase("extinct")) {
            this.status = VolcanoCurrentStatus.EXTINCT;
        } else if (status.equalsIgnoreCase("dormant")) {
            this.status = VolcanoCurrentStatus.DORMANT;
        } else if (status.equalsIgnoreCase("minor_activity") || status.equalsIgnoreCase("minor-activity") || status.equals("minorActivity")) {
            this.status = VolcanoCurrentStatus.MINOR_ACTIVITY;
        } else if (status.equalsIgnoreCase("major_activity") || status.equalsIgnoreCase("major-activity") || status.equals("majorActivity")) {
            this.status = VolcanoCurrentStatus.MAJOR_ACTIVITY;
        } else if (status.equalsIgnoreCase("erupting")) {
            this.status = VolcanoCurrentStatus.ERUPTING;
            volcano.start();
        } else {
        	return false;
        }
        return true;
    }
    
    public static boolean isValidStatus(String status) {
        if (status.equalsIgnoreCase("extinct")) {
        } else if (status.equalsIgnoreCase("dormant")) {
        } else if (status.equalsIgnoreCase("minor_activity") || status.equalsIgnoreCase("minor-activity") || status.equals("minorActivity")) {
        } else if (status.equalsIgnoreCase("major_activity") || status.equalsIgnoreCase("major-activity") || status.equals("majorActivity")) {
        } else if (status.equalsIgnoreCase("erupting")) {
        } else {
        	return false;
        }
        return true;
    }

    public String getStatus() {
        switch(status) {
            case EXTINCT:
                return "EXTINCT";
            case DORMANT:
                return "DORMANT";
            case MINOR_ACTIVITY:
                return "MINOR_ACTIVITY";
            case MAJOR_ACTIVITY:
                return "MAJOR_ACTIVITY";
            case ERUPTING:
                return "ERUPTING";
            default:
                return "DORMANT";
        }

    }

    public static void unregisterTask() {
        if (scheduleID  >= 0) {
            Bukkit.getScheduler().cancelTask(scheduleID);
            scheduleID = -1;
        }
    }
}

enum VolcanoCurrentStatus {
    EXTINCT,
    DORMANT,
    MINOR_ACTIVITY,
    MAJOR_ACTIVITY,
    ERUPTING
}

class VolcanoAutoStartProbability {
    public static VolcanoAutoStartStatusProbability dormant = new VolcanoAutoStartStatusProbability(0.05, 0.005);
    public static VolcanoAutoStartStatusProbability minor_activity = new VolcanoAutoStartStatusProbability(0.2, 0.05);
    public static VolcanoAutoStartStatusProbability major_activity = new VolcanoAutoStartStatusProbability(0.35, 0.25);
}

class VolcanoAutoStartStatusProbability {
    public double increase;
    public double decrease;

    public VolcanoAutoStartStatusProbability(double increase, double decrease) {
        this.increase = increase;
        this.decrease = decrease;
    }
}
