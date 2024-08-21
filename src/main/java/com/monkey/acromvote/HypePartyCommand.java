package com.monkey.acromvote;

import com.monkey.acromvote.AcromVote;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class HypePartyCommand implements CommandExecutor, TabCompleter {

    private final AcromVote plugin;

    public HypePartyCommand(AcromVote plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("Hype-Party")) {
            if (!sender.hasPermission("hype.party.permission")) {
                sender.sendMessage("Non hai il permesso necessario per utilizzare questo comando.");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage("Utilizzo: /Hype-Party [start|stop|level-set <1-5>|cooldown-start]");
                return true;
            } else if (args.length > 0) {
                if (args[0].equalsIgnoreCase("start")) {
                    if (plugin.isHypePartyActive()) {
                        sender.sendMessage("Non puoi eseguire questo comando perché c'è già un hype party in corso");
                    } else {
                        plugin.startHypeParty();
                        sender.sendMessage("Hype Party avviato con successo!");
                    }

                    return true;
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (!plugin.isHypePartyActive()) {
                        sender.sendMessage("Hype Party non è attivo al momento.");
                        return true;
                    }
                    plugin.stopHypeParty();

                    sender.sendMessage("Hype Party fermato con successo!");
                    return true;

                }else if (args[0].equalsIgnoreCase("cooldown-start")) {
                    if(!plugin.isHypePartyActive()) {
                        sender.sendMessage("Cooldown Hype Party iniziato");
                        try {
                            plugin.StartPartyTimer();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        sender.sendMessage("Non puoi eseguire questo comando perché c'è già un hype party in corso");
                    }
                    return true;
                }
            }

            sender.sendMessage("Utilizzo: /Hype-Party [start|stop|cooldown-start]");
            return false;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("Hype-Party")) {
            if (args.length == 1) {
                return Arrays.asList("start", "stop", "cooldown-start");
            }
        }
        return null;
    }
}
