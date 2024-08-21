package com.monkey.acromvote;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VotePartyBossbarReset implements CommandExecutor, TabCompleter {
    private final AcromVote plugin;

    public VotePartyBossbarReset(AcromVote plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("reset")) {
                if (args.length > 1) {
                    String resetType = args[1].toLowerCase();
                    switch (resetType) {
                        case "voti":
                            try {
                                Database.resetVoteCountInDatabase();
                                plugin.updateVotePartyBossBar();
                                sender.sendMessage(ChatColor.GREEN + "Voti nel database resettati");
                            } catch (SQLException e) {
                                sender.sendMessage(ChatColor.RED + "Impossibile resettare i voti nel database");
                                e.printStackTrace();
                            }

                            break;
                        case "donazioni":
                            try {
                                Database.resetDonationCountInDatabase();
                                plugin.updateVotePartyBossBar();
                                sender.sendMessage(ChatColor.GREEN + "Donazioni nel database resettate");
                            } catch (SQLException e) {
                                sender.sendMessage(ChatColor.RED + "Impossibile resettare le donazioni nel database");
                                e.printStackTrace();
                            }
                            break;
                        case "all":
                            try {
                                Database.resetVoteCountInDatabase();
                                Database.resetDonationCountInDatabase();
                                plugin.updateVotePartyBossBar();
                                sender.sendMessage(ChatColor.GREEN + "I voti e le donazioni sono state resettate all'interno del database");
                            } catch (SQLException e) {
                                sender.sendMessage(ChatColor.RED + "Impossibile resettare i voti e le donazioni all'interno del database");
                            }
                            break;
                        case "hypepartycount":
                            try {
                                Database.resetHypePartyCountInDatabase();
                                plugin.updateVotePartyBossBar();
                                sender.sendMessage(ChatColor.GREEN + "Gli HypePartyCount sono stati resettati all'interno del database");
                            } catch (SQLException e) {
                                sender.sendMessage(ChatColor.RED + "Impossibile resettare gli HypePartyCount all'interno del database");
                            }
                            break;
                        default:
                            sender.sendMessage("Opzione non valida. Usa /bossbar reset voti/donazioni/all/hypepartycount");
                            break;
                    }
                } else {
                    sender.sendMessage("Utilizzo: /bossbar reset voti/donazioni/all/hypepartycount");
                }
            } else {
                sender.sendMessage("Sotto-comando sconosciuto. Usa /bossbar reset.");
            }
        } else {
            sender.sendMessage("Utilizzo: /bossbar <subcomando>");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("reset");
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            List<String> resetTypes = Arrays.asList("voti", "donazioni", "all", "hypepartycount");
            for (String resetType : resetTypes) {
                if (resetType.startsWith(args[1].toLowerCase())) {
                    completions.add(resetType);
                }
            }
        }

        return completions;
    }
}
