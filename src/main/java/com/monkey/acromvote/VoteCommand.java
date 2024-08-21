package com.monkey.acromvote;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {

    private final AcromVote plugin;
    private final VoteGUI voteGUI;

    public VoteCommand(AcromVote plugin) {
        this.plugin = plugin;
        this.voteGUI = new VoteGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            voteGUI.openGUI(player);
        } else {
            sender.sendMessage("Questo comando può essere eseguito solo da un giocatore.");
        }
        return true;
    }
}