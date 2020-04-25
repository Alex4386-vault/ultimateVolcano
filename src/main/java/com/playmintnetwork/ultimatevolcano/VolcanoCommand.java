package com.playmintnetwork.ultimatevolcano;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VolcanoCommand {
    public static List<String> tabCompleteHandler(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> queryList = new ArrayList<>();
        List<String> commandList = Arrays.asList("start","stop","info","crater","help","reload","reloadall","create","delete","list","near","summit","forcecool","erupt","caldera","buildisland","preconstruct","updaterate","flowed","delayflowed","bombpower","bomblaunchpower","bombcount","bombradius","delayexplo","timerexplo");
        String cmd = (args.length == 0) ? "" : args[0];

        if (args.length == 0) {
            commandList = queryList;
        } else if (args.length == 1 && !commandList.contains(args[0].toLowerCase())) {
            for (String commandFromList:commandList) {
                if (commandFromList.toLowerCase().startsWith(args[0].toLowerCase())) {
                    queryList.add(commandFromList);
                }
            }
        } else if (permissionAndCmdChecker(cmd, "start", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            }
        } else if (permissionAndCmdChecker(cmd, "stop", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            }
        }  else if (permissionAndCmdChecker(cmd, "info", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            }
        } else if (permissionAndCmdChecker(cmd, "forcecool", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            }
        } else if (permissionAndCmdChecker(cmd, "caldera", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            }
        } else if (permissionAndCmdChecker(cmd, "erupt", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            }
        } else if (permissionAndCmdChecker(cmd, "reload", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            }
        } else if (permissionAndCmdChecker(cmd, "delete", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            }
        } else if (permissionAndCmdChecker(cmd, "near", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            }
        } else if (permissionAndCmdChecker(cmd, "summit", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            }
        } else if (permissionAndCmdChecker(cmd, "crater", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            }
        } else if (permissionAndCmdChecker(cmd, "buildisland", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.buildisland")) {
                queryList.add("<? basewidth>");
            }
        } else if (permissionAndCmdChecker(cmd, "preconstruct", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.preconstruct")) {
                queryList.add("<? height>");
            } else if (args.length == 4 && sender.hasPermission("ultimatevolcano.preconstruct")) {
                queryList.add("<? basewidth>");
            }
        } else if (permissionAndCmdChecker(cmd, "status", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.status")) {
                String[] states = {
                        "EXTINCT",
                        "DORMANT",
                        "MINOR_ACTIVITY",
                        "MAJOR_ACTIVITY",
                        "ERUPTING"
                };

                ArrayList<String> listStr = new ArrayList<>();
                for (String state : states) {
                    listStr.add(state);
                }

                queryList = listStr;
            }
        } else if (permissionAndCmdChecker(cmd, "updaterate", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.updaterate")) {
                ArrayList<String> opt = new ArrayList<>();
                opt.add("<? new updaterate>");
            }
        } else if (permissionAndCmdChecker(cmd, "flowed", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.flowed")) {
                queryList.add("<? new flowed value>");
            }
        } else if (permissionAndCmdChecker(cmd, "delayflowed", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.delayflowed")) {
                queryList.add("<? new delayFlowed value>");
            }
        } else if (permissionAndCmdChecker(cmd, "delayexplo", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.delayexplo")) {
                queryList.add("<? new delayExplo value>");
            }
        } else if (permissionAndCmdChecker(cmd, "timerexplo", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.timerexplo")) {
                queryList.add("<? new timerExplo value>");
            }
        } else if (permissionAndCmdChecker(cmd, "bombradius", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.bombradius")) {
                queryList.add("<? new minBombRadius value>");
            } else if (args.length == 4 && sender.hasPermission("ultimatevolcano.bombradius")) {
                queryList.add("<? new maxBombRadius value>");
            }
        } else if (permissionAndCmdChecker(cmd, "bomblaunchpower", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.bomblaunchpower")) {
                queryList.add("<? new minBombLaunchPower value>");
            } else if (args.length == 4 && sender.hasPermission("ultimatevolcano.bomblaunchpower")) {
                queryList.add("<? new maxBombLaunchPower value>");
            }
        } else if (permissionAndCmdChecker(cmd, "bombpower", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.bombpower")) {
                queryList.add("<? new minBombPower value>");
            } else if (args.length == 4 && sender.hasPermission("ultimatevolcano.bombpower")) {
                queryList.add("<? new maxBombPower value>");
            }
        } else if (permissionAndCmdChecker(cmd, "bombcount", sender)) {
            if (args.length == 2 && sender.hasPermission("ultimatevolcano.list")) {
                queryList = volcanoNameSearch(args[1]);
            } else if (args.length == 3 && sender.hasPermission("ultimatevolcano.bombcount")) {
                queryList.add("<? new minBombCount value>");
            } else if (args.length == 4 && sender.hasPermission("ultimatevolcano.bombcount")) {
                queryList.add("<? new maxBombCount value>");
            }
        }

        Collections.sort(queryList);
        return queryList;
    }

    public static ArrayList<String> volcanoNameSearch(String name) {
        ArrayList<String> queryResult = new ArrayList<>();
        for (Volcano volcano: MainPlugin.listVolcanoes) {
            if (volcano.name.toLowerCase().startsWith(name)) {
                queryResult.add(volcano.name);
            }
        }
        return queryResult;
    }

    public static String getDirections(Location from, Location to) {
        if (!from.getWorld().equals(to.getWorld())) {
            return "Different World";
        }

        double distanceN = from.getBlockZ() - to.getBlockZ();
        double distanceE = to.getBlockX() - from.getBlockX();
        double distanceDirect = Math.sqrt(Math.pow(distanceN, 2) + Math.pow(distanceE, 2));

        double theta;
        theta = Math.toDegrees(Math.acos(distanceN / distanceDirect));
        theta = (distanceE > 0) ? theta : -theta;

        float playerYaw = from.getYaw();
        float playerYawN = playerYaw-180;

        double destinationYaw = theta - playerYawN;
        destinationYaw = (Math.abs(destinationYaw) > 180) ? -(360 - destinationYaw) : destinationYaw;
        String destinationString = Math.abs(Math.floor(destinationYaw))+" degrees "
                + ((Math.abs(destinationYaw) < 1) ? "Forward" : (destinationYaw < 0) ? "Left" : "Right");

        if (Double.isNaN(destinationYaw)) {
            return "Arrived!";
        }

        return destinationString;
    }

    public static boolean commandHandler(CommandSender sender, Command command, String label, String[] args) {
        String cmd = (args.length == 0) ? "" : args[0];
        if (permissionAndCmdChecker(cmd, "help", sender)) {
            help(sender, label);
            return true;
        } else if (permissionAndCmdChecker("start", cmd, sender)) {
            if (args.length == 2) {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    MainPlugin.findVolcano(args[1]).start();
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano " + args[1] + " started!");
                } else {
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano does NOT exists. how about creating one?");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Wrong Command Usage Detected, Please check /vol help.");
            }
            return true;

        } else if (permissionAndCmdChecker("stop", cmd, sender)) {
            if (args.length == 2) {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    MainPlugin.findVolcano(args[1]).stop();
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano " + args[1] + " stopped!");
                } else {
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Unable to find volcano " + args[1]);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Wrong Command Usage Detected, Please check /vol help.");
            }
            return true;

        }  else if (permissionAndCmdChecker("forcecool", cmd, sender)) {
            if (args.length == 2) {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano " + args[1] + " cooling started!");
                    MainPlugin.findVolcano(args[1]).forceCool();
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano " + args[1] + " cooling ended!");
                } else {
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Unable to find volcano " + args[1]);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Wrong Command Usage Detected, Please check /vol help.");
            }
            return true;

        } else if (permissionAndCmdChecker("erupt", cmd, sender)) {
            if (args.length >= 2) {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    if (args.length == 3) {
                        int bombCount = Integer.parseInt(args[2]);
                        sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano " + args[1] + " erupting with "+bombCount+" volcanic bombs");
                        MainPlugin.findVolcano(args[1]).erupt.eruptNow(bombCount);
                    } else {
                        sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano " + args[1] + " erupting!");
                        MainPlugin.findVolcano(args[1]).erupt.eruptNowRandom();
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Unable to find volcano " + args[1]);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Wrong Command Usage Detected, Please check /vol help.");
            }
            return true;

        } else if (permissionAndCmdChecker("caldera", cmd, sender)) {
            if (args.length >= 2) {
                if (MainPlugin.findVolcano(args[1]) != null) {

                    if (args.length == 3) {
                        sender.sendMessage(ChatColor.RED + "[Volcano] Forming Caldera on  " + ChatColor.GOLD + "Volcano " + args[1] + "!");
                        MainPlugin.findVolcano(args[1]).generateCaldera(Float.parseFloat(args[2]));
                    } else {
                        sender.sendMessage(ChatColor.RED + "[Volcano] Forming Caldera on  " + ChatColor.GOLD + "Volcano " + args[1] + "!");
                        MainPlugin.findVolcano(args[1]).generateCalderaRandomStrength();
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Unable to find volcano " + args[1]);
                }

            } else {
                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Wrong Command Usage Detected, Please check /vol help.");
            }
            return true;

        } else if (permissionAndCmdChecker("info", cmd, sender)) {
            if (args.length == 2) {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Information of " + volcano.name);
                    sender.sendMessage(ChatColor.YELLOW + "Status: " + ChatColor.GOLD + volcano.autoStart.getStatus());
                    sender.sendMessage(ChatColor.GREEN + "  canAutoStart: " + ChatColor.GOLD + volcano.autoStart.canAutoStart);
                    sender.sendMessage(ChatColor.GREEN + "  pourLavaStart: " + ChatColor.GOLD + volcano.autoStart.pourLavaStart);
                    sender.sendMessage(ChatColor.GREEN + "  eruptingTimes: " + ChatColor.GOLD + volcano.autoStart.eruptionTimer);
                    sender.sendMessage(ChatColor.YELLOW + "Specifications: ");
                    sender.sendMessage(ChatColor.RED + "  Volcanism: ");
                    sender.sendMessage(ChatColor.DARK_RED + "    Lava Flows: ");
                    sender.sendMessage(ChatColor.GREEN + "      flowed: " + ChatColor.GOLD + volcano.lavaFlow.settings.flowed);
                    sender.sendMessage(ChatColor.GREEN + "      delayFlowed: " + ChatColor.GOLD + volcano.lavaFlow.settings.delayFlowed);
                    sender.sendMessage(ChatColor.DARK_RED + "    Geo Thermals: ");
                    sender.sendMessage(ChatColor.GREEN + "      geoThermalStat: " + ChatColor.GOLD + volcano.geoThermals.enable);
                    sender.sendMessage(ChatColor.GREEN + "      geoThermalTicks: " + ChatColor.GOLD + volcano.geoThermals.geoThermalUpdateRate);
                    sender.sendMessage(ChatColor.DARK_RED + "    Explosions: ");
                    sender.sendMessage(ChatColor.GREEN + "      timerExplo: " + ChatColor.GOLD + volcano.erupt.settings.timerExplo);
                    sender.sendMessage(ChatColor.GREEN + "      damageExplo: " + ChatColor.GOLD + volcano.erupt.settings.damageExplo);
                    sender.sendMessage(ChatColor.GREEN + "      realDamageExplo: " + ChatColor.GOLD + volcano.erupt.settings.realDamageExplo);
                    sender.sendMessage(ChatColor.GREEN + "      delayExplo: " + ChatColor.GOLD + volcano.erupt.settings.delayExplo);
                    sender.sendMessage(ChatColor.GREEN + "      isExplosive: " + ChatColor.GOLD + volcano.erupt.settings.isExplosive);
                    sender.sendMessage(ChatColor.DARK_RED + "    Volcano Formation: ");
                    sender.sendMessage(ChatColor.GREEN + "      heightLimit: " + ChatColor.GOLD + volcano.generator.heightLimit);
                    sender.sendMessage(ChatColor.GREEN + "      throat: " + ChatColor.GOLD + volcano.generator.throat);
                    sender.sendMessage(ChatColor.DARK_RED + "    Volcanic Bombs: ");
                    sender.sendMessage(ChatColor.GREEN + "      bombRadius: " + ChatColor.GOLD + volcano.bombs.minBombRadius + " ~ "+volcano.bombs.maxBombRadius);
                    sender.sendMessage(ChatColor.GREEN + "      bombPower: " + ChatColor.GOLD + volcano.bombs.minBombPower + " ~ "+volcano.bombs.maxBombPower);
                    sender.sendMessage(ChatColor.GREEN + "      bombLaunchPower: " + ChatColor.GOLD + volcano.bombs.minBombLaunchPower + " ~ "+volcano.bombs.maxBombLaunchPower);
                    sender.sendMessage(ChatColor.DARK_RED + "    Eruption: ");
                    sender.sendMessage(ChatColor.GREEN + "      eruptionEnabled: " + ChatColor.GOLD + volcano.erupt.enabled);
                    sender.sendMessage(ChatColor.GREEN + "      erupting: " + ChatColor.GOLD + volcano.erupt.erupting);
                    sender.sendMessage(ChatColor.GREEN + "      bombCount: " + ChatColor.GOLD + volcano.erupt.settings.minBombCount + " ~ "+volcano.erupt.settings.maxBombCount);
                    sender.sendMessage(ChatColor.GOLD + "  Seismic Status: " + ChatColor.GOLD + volcano.autoStart.getStatus());
                    sender.sendMessage(ChatColor.GREEN + "    currentHeight: " + ChatColor.GOLD + volcano.currentHeight+" ("+(volcano.currentHeight - volcano.location.getBlockY())+")");
                    sender.sendMessage(ChatColor.GREEN + "    Location: " + ChatColor.GOLD + volcano.location.getBlockX() + "," + volcano.location.getBlockY() + "," + volcano.location.getBlockZ() + " @ " + volcano.location.getWorld().getName());
                    sender.sendMessage(ChatColor.GREEN + "    craterRadius: " + ChatColor.GOLD + volcano.crater.craterRadius);
                } else {
                    sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Unable to find volcano " + args[1]);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Wrong Command Usage Detected, Please check /vol help.");
            }
            return true;
        } else if (permissionAndCmdChecker("list", cmd, sender)) {
            if (MainPlugin.listVolcanoes.size() > 0) {
                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano lists");
                for (Volcano volcano : MainPlugin.listVolcanoes) {
                    sender.sendMessage(ChatColor.GREEN + "- " + ChatColor.GOLD + volcano.name + " " + ChatColor.YELLOW + volcano.autoStart.getStatus());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano does NOT exists. how about creating one?");
            }
            return true;
        } else if (permissionAndCmdChecker("summit", cmd, sender)) {
        	if (sender instanceof Player) {
        		
        		Player player = (Player) sender;
        		List<Volcano> nearByVolcanoes = new ArrayList<Volcano>();
        		
        		if (args.length == 1) {
                
	                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Summit Info of Nearby Volcanoes");
	
	                
	                for (Volcano volcano: MainPlugin.listVolcanoes) {
	                    if (volcano.affected(player.getLocation())) {
	                        nearByVolcanoes.add(volcano);
	                    }
	                }
        		} else if (args.length == 2) {
        			if (MainPlugin.findVolcano(args[1]) != null) {
        				Volcano vol = MainPlugin.findVolcano(args[1]);
        				if (!vol.location.getWorld().equals(player.getLocation().getWorld())) {
        					sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "You are not in the same world!");
        					return true;
        				}
        				nearByVolcanoes.add(MainPlugin.findVolcano(args[1]));
        			} else {
                        sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Unable to find volcano " + args[1]);
                        return true;
                    }
        		} else {
        			sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"Wrong Command Usage Detected, Please check /vol help.");
        			return true;
        		}
                if (nearByVolcanoes.size() > 0) {
                    for (Volcano volcano:nearByVolcanoes) {
                    	sender.sendMessage("");
                    	sender.sendMessage(ChatColor.RED+volcano.name+ChatColor.GOLD+": ");
                    	sender.sendMessage(ChatColor.YELLOW+"Height: "+ChatColor.GRAY+"y:"+(volcano.currentHeight));
                    	sender.sendMessage(ChatColor.GOLD+"  realHeight: "+ChatColor.GRAY+(volcano.currentHeight - volcano.location.getBlockY()));
                    	sender.sendMessage(ChatColor.GOLD+"  y to Climb: "+ChatColor.GRAY+(volcano.currentHeight - player.getLocation().getBlockY()));
                    	sender.sendMessage(ChatColor.YELLOW+"Summit:");
                    	sender.sendMessage(ChatColor.GOLD+"  Location of Summit: "+ChatColor.GRAY+volcano.summitBlock.getX()+","+volcano.summitBlock.getY()+","+volcano.summitBlock.getZ());

                    	double distanceFromSummitN = player.getLocation().getBlockZ() - volcano.summitBlock.getZ();
                        double distanceFromSummitE = volcano.summitBlock.getX() - player.getLocation().getBlockX();

                        String turnNS = (Math.abs(Math.floor(distanceFromSummitN)) == 0) ? "" : (Math.floor(distanceFromSummitN) > 0) ? "North" : "South";
                    	String turnWE = (Math.abs(Math.floor(distanceFromSummitE)) == 0) ? "" : (Math.floor(distanceFromSummitE) < 0) ? "West" : "East";


                    	String destinationString = getDirections(player.getLocation(), volcano.summitBlock.getLocation());

                    	sender.sendMessage(ChatColor.GOLD+"  Distance  : "+(int) volcano.summitBlock.getLocation().distance(player.getLocation())+" blocks");
                        sender.sendMessage(ChatColor.GOLD+"  Navigation: "+turnNS+" "+turnWE+ChatColor.RESET+", "+destinationString);
                        sender.sendMessage(ChatColor.YELLOW+"Status:"+ChatColor.GRAY+volcano.autoStart.getStatus());
                    }
                } else {
                    sender.sendMessage(ChatColor.GREEN+"There is no Volcano that are in zone");
                }
        	} else {
                sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"This command should be used in ingame!");
            }
            return true;
        } else if (permissionAndCmdChecker("near", cmd, sender)) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Nearby Volcanoes");

                List<Volcano> nearByVolcanoes = new ArrayList<Volcano>();
                for (Volcano volcano: MainPlugin.listVolcanoes) {
                    if (volcano.affected(player.getLocation())) {
                        nearByVolcanoes.add(volcano);
                    }
                }

                sender.sendMessage(ChatColor.RED + "  In-Zone Volcanoes: ");
                if (nearByVolcanoes.size() > 0) {
                    for (Volcano volcano:nearByVolcanoes) {
                    	sender.sendMessage(ChatColor.RED+"    "+volcano.name+ChatColor.GOLD+": "+volcano.currentHeight+" ("+(volcano.currentHeight-volcano.location.getBlockZ())+")");
                    }
                } else {
                    sender.sendMessage(ChatColor.GREEN+"    There is no In-Zone Volcano");
                }
                sender.sendMessage(" ");
                nearByVolcanoes.clear();

                sender.sendMessage(ChatColor.GREEN+"  Volcanoes in World "+player.getLocation().getWorld().getName());
                for (Volcano volcano: MainPlugin.listVolcanoes) {
                    if (volcano.location.getWorld().equals(player.getLocation().getWorld())) {
                        nearByVolcanoes.add(volcano);
                    }
                }
                if (nearByVolcanoes.size() > 0) {
                    for (Volcano volcano: nearByVolcanoes) {
                        int volDistance = (int) volcano.location.distance(player.getLocation());

                        double distanceFromVolcanoN = player.getLocation().getBlockZ() - volcano.location.getZ();
                        double distanceFromVolcanoE = volcano.location.getX() - player.getLocation().getBlockX();

                        String turnNS = (Math.abs(Math.floor(distanceFromVolcanoN)) == 0) ? "" : (Math.floor(distanceFromVolcanoN) > 0) ? "North" : "South";
                        String turnWE = (Math.abs(Math.floor(distanceFromVolcanoE)) == 0) ? "" : (Math.floor(distanceFromVolcanoE) < 0) ? "West" : "East";

                        String destinationString = getDirections(player.getLocation(), volcano.location);

                        String volDistan = volDistance < volcano.zone ? ChatColor.RED+"In Volcano-Zone ":volDistance+" blocks, "+destinationString;

                        sender.sendMessage("    "+ChatColor.RED+volcano.name+ChatColor.GOLD+" ("+volcano.summitBlock.getY()+" ("+(volcano.currentHeight-volcano.location.getBlockZ())+")): "+ChatColor.YELLOW+turnNS+" "+turnWE);
                        sender.sendMessage("    "+ChatColor.GRAY+volDistan);
                    }
                } else {
                    sender.sendMessage(ChatColor.GREEN+"There is no volcanoes in world "+player.getLocation().getWorld().getName());
                }
            } else {
                sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"This command should be used in ingame!");
            }
            return true;
        } else if (permissionAndCmdChecker("reloadall", cmd, sender)) {
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Reloading All Volcanoes!");
                MainPlugin.reloadAll();
                sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Reload complete!");
                return true;
            }
        } else if (permissionAndCmdChecker("reload", cmd, sender)) {
            if (args.length == 2) {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    if (MainPlugin.findVolcano(args[1]).reload()) {
                        sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano " + args[1] + " was reloaded!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "[Volcano] " + ChatColor.GOLD + "Volcano " + args[1] + " cannot be reloaded!");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"Wrong Command Usage Detected, Please check /vol help.");
            }
            return true;

        } else if (permissionAndCmdChecker("create", cmd, sender)) {
            if (args.length == 3) {
                create(sender, args[1], Integer.parseInt(args[2]));
            } else {
                sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"Wrong Command Usage Detected, Please check /vol help.");
            }
            return true;
        } else if (permissionAndCmdChecker("delete", cmd, sender)) {
            if (args.length == 2) {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    MainPlugin.findVolcano(args[1]).delete();
                    sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"Volcano "+args[1]+" was deleted!");
                } else {
                	
                }
            } else {
                sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"Wrong Command Usage Detected, Please check /vol help.");
            }
            return true;

        } else if (permissionAndCmdChecker("status", cmd, sender)) {
            if (args.length == 1) {
                sender.sendMessage(ChatColor.GREEN + "Volcano's Status: ");
                sender.sendMessage(ChatColor.GRAY + "EXTINCT " + ChatColor.GREEN + "DORMANT ");
                sender.sendMessage(ChatColor.YELLOW + "MINOR_ACTIVITY " + ChatColor.GOLD + "MAJOR_ACTIVITY ");
                sender.sendMessage(ChatColor.RED + "ERUPTING");
            } else if (args.length == 2) {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Status: " + ChatColor.GOLD + volcano.autoStart.getStatus());
                }
            } else if (args.length == 3) {
                if (VolcanoAutoStart.isValidStatus(args[2])) {
                    if (MainPlugin.findVolcano(args[1]) != null) {
                        Volcano volcano = MainPlugin.findVolcano(args[1]);
                        volcano.autoStart.setStatus(args[2]);
                        volcano.saveToFile();
                        sender.sendMessage(ChatColor.GREEN + "Status: " + ChatColor.GOLD + volcano.autoStart.getStatus());
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + args[2] + " is not a valid status!");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.GREEN + "Volcano's Status: ");
                    sender.sendMessage(ChatColor.GRAY + "EXTINCT " + ChatColor.GREEN + "DORMANT ");
                    sender.sendMessage(ChatColor.YELLOW + "MINOR_ACTIVITY " + ChatColor.GOLD + "MAJOR_ACTIVITY ");
                    sender.sendMessage(ChatColor.RED + "ERUPTING");
                }


            } else {
                sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"Wrong Command Usage Detected, Please check /vol help.");
            }
            return true;
        } else if (permissionAndCmdChecker("buildisland", cmd, sender)) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED+"key argument name and base radius are missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);
                    VolcanoPreconstruct preconstruct = new VolcanoPreconstruct(volcano);

                    if (preconstruct.isUnderWater()) {
                        sender.sendMessage("Detected Volcano is underwater, building base...");
                        sender.sendMessage("Volcano island base building...");
                        preconstruct.runIslandPreconstruct(sender, Integer.parseInt(args[2]));
                    } else {
                        sender.sendMessage("Volcano is not in underwater");
                    }

                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        } else if (permissionAndCmdChecker("preconstruct", cmd, sender)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"key argument name is missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);
                    VolcanoPreconstruct preconstruct = new VolcanoPreconstruct(volcano);

                    if (args.length == 3) {
                        sender.sendMessage("Starting preconstruction...");
                        preconstruct.runTestPreconstruct(Integer.parseInt(args[2]));
                    } else if (args.length == 4) {
                        sender.sendMessage("Starting preconstruction...");
                        preconstruct.runTestPreconstruct(sender, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                    } else {
                        sender.sendMessage("Starting auto preconstruction...");
                        int realHeightLimit = volcano.generator.heightLimit > 255 ? 255 : volcano.generator.heightLimit;
                        preconstruct.runTestPreconstruct(sender, (int) ((realHeightLimit - volcano.location.getBlockY()) / (4.0 + (Math.random() * 4.0))));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        } else if (permissionAndCmdChecker("crater", cmd, sender)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"key argument name is missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);

                    if (args.length == 3) {
                        volcano.crater.craterRadius = Integer.parseInt(args[2]);
                        volcano.crater.setCraters(volcano.location);
                        volcano.saveToFile();
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s crater radius is now: " + ChatColor.GOLD + volcano.crater.craterRadius);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s crater radius: " + ChatColor.GOLD + volcano.crater.craterRadius);
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            sender.sendMessage(volcano.inCrater(player.getLocation()) ? ChatColor.RED + "You are currently in Crater of this volcano" : "");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        } else if (permissionAndCmdChecker("updaterate", cmd, sender)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"key argument name is missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);

                    if (args.length == 3) {
                        volcano.lavaFlow.settings.updateRate = Integer.parseInt(args[2]);
                        volcano.saveToFile();
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s update rate is now: " + ChatColor.GOLD + volcano.lavaFlow.settings.updateRate);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s update rate: " + ChatColor.GOLD + volcano.lavaFlow.settings.updateRate);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        } else if (permissionAndCmdChecker("flowed", cmd, sender)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"key argument name is missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);

                    if (args.length == 3) {
                        volcano.lavaFlow.settings.flowed = Integer.parseInt(args[2]);
                        volcano.saveToFile();
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s flowed is now: " + ChatColor.GOLD + volcano.lavaFlow.settings.flowed);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s flowed: " + ChatColor.GOLD + volcano.lavaFlow.settings.flowed);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        } else if (permissionAndCmdChecker("delayflowed", cmd, sender)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"key argument name is missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);

                    if (args.length == 3) {
                        volcano.lavaFlow.settings.delayFlowed = Integer.parseInt(args[2]);
                        volcano.saveToFile();
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s delayFlowed is now: " + ChatColor.GOLD + volcano.lavaFlow.settings.delayFlowed);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s delayFlowed: " + ChatColor.GOLD + volcano.lavaFlow.settings.delayFlowed);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        } else if (permissionAndCmdChecker("delayexplo", cmd, sender)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"key argument name is missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);

                    if (args.length == 3) {
                        volcano.erupt.settings.delayExplo = Integer.parseInt(args[2]);
                        volcano.saveToFile();
                        volcano.erupt.unregisterTask();
                        volcano.erupt.registerTask();
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s delayExplo is now: " + ChatColor.GOLD + volcano.erupt.settings.delayExplo);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s delayExplo: " + ChatColor.GOLD + volcano.erupt.settings.delayExplo);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        } else if (permissionAndCmdChecker("timerexplo", cmd, sender)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"key argument name is missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);

                    if (args.length == 3) {
                        volcano.erupt.settings.timerExplo = Integer.parseInt(args[2]);
                        volcano.saveToFile();
                        volcano.erupt.unregisterTask();
                        volcano.erupt.registerTask();
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s timerExplo is now: " + ChatColor.GOLD + volcano.erupt.settings.timerExplo);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s timerExplo: " + ChatColor.GOLD + volcano.erupt.settings.timerExplo);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        } else if (permissionAndCmdChecker("bombpower", cmd, sender)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"key argument name is missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);

                    if (args.length == 4) {
                        volcano.bombs.minBombPower = Float.parseFloat(args[2]);
                        volcano.bombs.maxBombPower = Float.parseFloat(args[3]);
                        volcano.saveToFile();
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s bombPower is now between: " + ChatColor.GOLD + volcano.bombs.minBombPower + ChatColor.AQUA + " between " + ChatColor.GOLD + volcano.bombs.maxBombPower);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s bombPower: " + ChatColor.GOLD + volcano.bombs.minBombPower + ChatColor.AQUA + " between " + ChatColor.GOLD + volcano.bombs.maxBombPower);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        } else if (permissionAndCmdChecker("bomblaunchpower", cmd, sender)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"key argument name is missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);

                    if (args.length == 4) {
                        volcano.bombs.minBombLaunchPower = Float.parseFloat(args[2]);
                        volcano.bombs.maxBombLaunchPower = Float.parseFloat(args[3]);
                        volcano.saveToFile();
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s bombLaunchPower is now between: " + ChatColor.GOLD + volcano.bombs.minBombLaunchPower + ChatColor.AQUA + " between " + ChatColor.GOLD + volcano.bombs.maxBombLaunchPower);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s bombLaunchPower: " + ChatColor.GOLD + volcano.bombs.minBombLaunchPower + ChatColor.AQUA + " between " + ChatColor.GOLD + volcano.bombs.maxBombLaunchPower);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        } else if (permissionAndCmdChecker("bombradius", cmd, sender)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"key argument name is missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);

                    if (args.length == 4) {
                        volcano.bombs.minBombRadius = Integer.parseInt(args[2]);
                        volcano.bombs.maxBombRadius = Integer.parseInt(args[3]);
                        volcano.saveToFile();
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s bombRadius is now between: " + ChatColor.GOLD + volcano.bombs.minBombRadius + ChatColor.AQUA + " between " + ChatColor.GOLD + volcano.bombs.maxBombRadius);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s bombRadius: " + ChatColor.GOLD + volcano.bombs.minBombRadius + ChatColor.AQUA + " between " + ChatColor.GOLD + volcano.bombs.maxBombRadius);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        } else if (permissionAndCmdChecker("bombcount", cmd, sender)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"key argument name is missing!");
            } else {
                if (MainPlugin.findVolcano(args[1]) != null) {
                    Volcano volcano = MainPlugin.findVolcano(args[1]);

                    if (args.length == 4) {
                        volcano.erupt.settings.minBombCount = Integer.parseInt(args[2]);
                        volcano.erupt.settings.maxBombCount = Integer.parseInt(args[3]);
                        volcano.saveToFile();
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s bombCount is now between: " + ChatColor.GOLD + volcano.erupt.settings.minBombCount + ChatColor.AQUA + " between " + ChatColor.GOLD + volcano.erupt.settings.maxBombCount);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Volcano "+volcano.name+"'s bombCount: " + ChatColor.GOLD + volcano.erupt.settings.minBombCount + ChatColor.AQUA + " between " + ChatColor.GOLD + volcano.erupt.settings.maxBombCount);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Volcano "+args[1]+" can not be found!");
                }
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"Invalid Command Detected, or you don't have a permission to use this command at all!");
        return false;
    }

    private static boolean permissionAndCmdChecker(String str, String cmd, CommandSender sender) {
        return str.equalsIgnoreCase(cmd) && sender.hasPermission("ultimatevolcano."+str.toLowerCase());
    }

    private static void help(CommandSender sender, String cmd) {
        sender.sendMessage(ChatColor.RED+"==[ ULTIMATE VOLCANO ]==");
        sender.sendMessage(explainCommand(cmd,"start","<name>","Starts a volcano"));
        sender.sendMessage(explainCommand(cmd,"stop","<name>","Stops a volcano"));
        sender.sendMessage(explainCommand(cmd,"info","<name>","get a detailed info about volcano"));
        sender.sendMessage(explainCommand(cmd,"help","","show this help message"));
        sender.sendMessage(explainCommand(cmd,"create","<name> <height>","Create a volcano with default settings"));
        sender.sendMessage(explainCommand(cmd,"delete","<name>","Delete a volcano"));
        sender.sendMessage(explainCommand(cmd,"reload","<name>","Reloads a volcano"));
        sender.sendMessage(explainCommand(cmd,"reloadall","","Reloads entire plugin"));
        sender.sendMessage(explainCommand(cmd,"status","<name> <status?>","Sets/Shows status of volcano"));
        sender.sendMessage(explainCommand(cmd,"list","","Shows all volcanoes"));
        sender.sendMessage(explainCommand(cmd,"near","","Shows nearest volcanoes and current world's volcanoes"));
        sender.sendMessage(explainCommand(cmd,"summit","","Shows nearest volcano's summit information"));
        sender.sendMessage(explainCommand(cmd,"forcecool","<name>","Force finish the lava flow"));
        sender.sendMessage(explainCommand(cmd,"buildisland","<name> <base radius>","Builds Baseline island for specific volcano"));
        sender.sendMessage(explainCommand(cmd,"preconstruct","<name> <? base height> <? base radius>","Builds Baseline island for specific volcano"));
        sender.sendMessage(explainCommand(cmd,"erupt","<name>","Force erupt the volcano now without considering the scheduled eruption"));
        sender.sendMessage(explainCommand(cmd,"caldera","<name>","Form a caldera by creating a maximum possible explosion on volcano's top"));
        sender.sendMessage(explainCommand(cmd,"flowed","<name> <? value>","Set timer to lava to flow and stop (seconds)"));
        sender.sendMessage(explainCommand(cmd,"delayflowed","<name> <? value>","Set timer to start next lavaflow (seconds)"));
        sender.sendMessage(explainCommand(cmd,"bombradius","<name> <? minValue> <? maxValue>","Set the range of Volcanic Bomb's radius while erupting"));
        sender.sendMessage(explainCommand(cmd,"bombpower","<name> <? minValue> <? maxValue>","Set the range of volcanic Bomb's explosion power when landed"));
        sender.sendMessage(explainCommand(cmd,"bomblaunchpower","<name> <? minValue> <? maxValue>","Set the range of velocity of launching bombs from crater"));
        sender.sendMessage(explainCommand(cmd,"bombcount","<name> <? minValue> <? maxValue>","Set the range of how many bombs are erupted while single eruption cycle"));
        sender.sendMessage(explainCommand(cmd,"delayexplo","<name> <? value>","Set seconds to wait before each eruption cycle of volcano to start erupt cycle"));
        sender.sendMessage(explainCommand(cmd,"timerexplo","<name> <? value>","Set seconds to wait after each eruption cycle of volcano to start erupt cycle"));
        sender.sendMessage(explainCommand(cmd,"updaterate","<name> <? value>","How many ticks will trigger update cycle of volcano "));

    }

    private static void create(CommandSender sender, String name, int height) {
        String Composition = Volcano.defaultComposition;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Volcano.create(name, player.getLocation(), height, Composition, false, true);
            sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"Volcano "+name+" was created at your position!");
        } else {
            sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"This command is only for In-game Players");
        }
    }

    private static String explainCommand(String baseCommand, String subCommand, String args, String explain) {
        return ChatColor.RED+"/"+baseCommand+" "+ChatColor.GOLD+subCommand+" "+ChatColor.GRAY+args+" : "+ChatColor.WHITE+explain;
    }
}
