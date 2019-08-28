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
        List<String> commandList = Arrays.asList("start","stop","info","help","reload","reloadall","create","delete","list","near","summit");
        String cmd = (args.length == 0) ? "" : args[0];

        if (args.length == 0) {
            commandList = queryList;
        } else if (args.length == 1 && !commandList.contains(args[0].toLowerCase())) {
            for (String commandFromList:commandList) {
                if (cmd.toLowerCase().startsWith(args[0].toLowerCase())) {
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
        }

        Collections.sort(queryList);
        return queryList;
    }

    public static ArrayList<Volcano> volcanoSearch(String name) {
        ArrayList<Volcano> queryResult = new ArrayList<>();
        for (Volcano volcano: MainPlugin.listVolcanoes) {
            if (volcano.name.toLowerCase().startsWith(name)) {
                queryResult.add(volcano);
            }
        }
        return queryResult;
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
                    sender.sendMessage(ChatColor.GREEN + "      geoThermalTicks: " + ChatColor.GOLD + volcano.geoThermals.geoThermalTicks);
                    sender.sendMessage(ChatColor.DARK_RED + "    Explosions: ");
                    sender.sendMessage(ChatColor.GREEN + "      timerExplo: " + ChatColor.GOLD + volcano.erupt.settings.timerExplo);
                    sender.sendMessage(ChatColor.GREEN + "      damageExplo: " + ChatColor.GOLD + volcano.erupt.settings.damageExplo);
                    sender.sendMessage(ChatColor.GREEN + "      realDamageExplo: " + ChatColor.GOLD + volcano.erupt.settings.realDamageExplo);
                    sender.sendMessage(ChatColor.GREEN + "      delayExplo: " + ChatColor.GOLD + volcano.erupt.settings.delayExplo);
                    sender.sendMessage(ChatColor.GREEN + "      isExplosive: " + ChatColor.GOLD + volcano.erupt.settings.isExplosive);
                    sender.sendMessage(ChatColor.DARK_RED + "    Volcano Formation: ");
                    sender.sendMessage(ChatColor.GREEN + "      heightLimit: " + ChatColor.GOLD + volcano.generator.heightLimit);
                    sender.sendMessage(ChatColor.GREEN + "      throat: " + ChatColor.GOLD + volcano.generator.throat);
                    sender.sendMessage(ChatColor.DARK_RED + "    Eruption: ");
                    sender.sendMessage(ChatColor.GREEN + "      eruptionEnabled: " + ChatColor.GOLD + volcano.erupt.enabled);
                    sender.sendMessage(ChatColor.GREEN + "      erupting: " + ChatColor.GOLD + volcano.erupt.erupting);
                    sender.sendMessage(ChatColor.GOLD + "  Geology: " + ChatColor.GOLD + volcano.autoStart.getStatus());
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
                    	String turnNS = (Math.abs(Math.floor(volcano.summitBlock.getZ() - player.getLocation().getBlockZ())) == 0) ? "" : (Math.floor(volcano.summitBlock.getZ() - player.getLocation().getZ()) < 0) ? "North" : "South";
                    	String turnWE = (Math.abs(Math.floor(volcano.summitBlock.getX() - player.getLocation().getBlockX())) == 0) ? "" : (Math.floor(volcano.summitBlock.getX() - player.getLocation().getX()) < 0) ? "West" : "East";
                    	sender.sendMessage(ChatColor.GOLD+"  Navigation: "+(int) volcano.summitBlock.getLocation().distance(player.getLocation())+" blocks "+turnNS+" "+turnWE);
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
                        String volDistan = volDistance < volcano.zone ? ChatColor.RED+"In Volcano-Zone ":volDistance+" blocks, ";
                        String turnNS = ((volcano.location.getBlockZ() - player.getLocation().getBlockZ()) < -(volcano.zone)) ? "North" : (Math.abs(volcano.location.getBlockZ() - player.getLocation().getBlockZ()) <= volcano.zone) ? "" : "South";
                        String turnWE = ((volcano.location.getBlockX() - player.getLocation().getBlockX()) < -(volcano.zone)) ? "West" : (Math.abs(volcano.location.getBlockX() - player.getLocation().getBlockX()) <= volcano.zone) ? "" : "East";
                        sender.sendMessage("    "+ChatColor.RED+volcano.name+ChatColor.GOLD+": "+ChatColor.GRAY+volDistan+turnNS+" "+turnWE);
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
        		sender.sendMessage(ChatColor.YELLOW + "MINOR_ACTIVITY "+ ChatColor.GOLD+"MAJOR_ACTIVITY ");
        		sender.sendMessage(ChatColor.RED+"ERUPTING");
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
            		sender.sendMessage(ChatColor.RED+args[2]+" is not a valid status!");
            		sender.sendMessage("");
            		sender.sendMessage(ChatColor.GREEN + "Volcano's Status: ");
            		sender.sendMessage(ChatColor.GRAY + "EXTINCT " + ChatColor.GREEN + "DORMANT ");
            		sender.sendMessage(ChatColor.YELLOW + "MINOR_ACTIVITY "+ ChatColor.GOLD+"MAJOR_ACTIVITY ");
            		sender.sendMessage(ChatColor.RED+"ERUPTING");
            	}
                
            } else {
                sender.sendMessage(ChatColor.RED+"[Volcano] "+ChatColor.GOLD+"Wrong Command Usage Detected, Please check /vol help.");
            }
            return true;

        }
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
