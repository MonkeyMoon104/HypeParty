package com.monkey.acromvote;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class donationCommand implements CommandExecutor {
    private final AcromVote plugin;
    private Map<Player, Integer> donationCounts;

    public donationCommand(AcromVote plugin) {
        this.plugin = plugin;
        donationCounts = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("donazione-eseguita")) {
            if (!(sender instanceof Player)) {
                if (args.length == 1) {
                    String playerName = args[0];


                    Player targetPlayer = Bukkit.getPlayerExact(playerName);

                    if (plugin.isHypePartyActive()) {
                        try {
                            plugin.addDonationFromConsole(targetPlayer);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        String DonationHypeOn = "&x&f&f&d&f&0&0&o&n" + playerName + "&r &#F58100&lʜ&#F6890A&lᴀ &#F89A1E&lᴅ&#F8A228&lᴏ&#F9AB32&lɴ&#FAB33C&lᴀ&#FBBB45&lᴛ&#FCC44F&lᴏ &#FDD463&lᴀ&#FEDD6D&lʟ &#FFDB73&lꜱ&#FFD16F&lᴇ&#FFC76B&lʀ&#FFBC67&lᴠ&#FFB263&lᴇ&#FFA85F&lʀ &#FF9457&lᴇ &#FF7F4E&lᴄ&#FF754A&lᴏ&#FF6B46&lɴ&#FF6142&lᴛ&#FF573E&lʀ&#FF4D3A&lɪ&#FF4236&lʙ&#FF3832&lᴜ&#FF2E2E&lɪ&#FA2945&lᴛ&#F5245C&lᴏ &#EA1A8A&lᴀ&#E514A0&lʟ&#E00FB7&lʟ&#DA0ACE&l'&#D505E5&lʜ&#D000FC&lʏ&#D612F9&lᴘ&#DB24F6&lᴇ &#E648F0&lᴘ&#EC5AEC&lᴀ&#F16CE9&lʀ&#F77EE6&lᴛ&#FC90E3&lʏ";
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#F1FF00&lᴅ&#ECEE02&lᴏ&#E6DD03&lɴ&#E1CC05&lᴀ&#DBBB06&lᴢ&#E4AE05&lɪ&#EDA103&lᴏ&#F69302&lɴ&#FF8600&lᴇ &l&7»&r " + DonationHypeOn);
                    } else {
                        try {
                            plugin.addDonationFromConsole(targetPlayer);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        String DonationHypeOff = "&x&f&f&d&f&0&0&o&n" + playerName + "&r &#F58100&lʜ&#F6870D&lᴀ &#F89428&lᴅ&#F99A35&lᴏ&#FBA042&lɴ&#FCA64F&lᴀ&#FDAD5D&lᴛ&#FEB36A&lᴏ &#FFAB70&lᴀ&#FF9D68&lʟ &#FF815A&lꜱ&#FF7453&lᴇ&#FF664B&lʀ&#FF5844&lᴠ&#FF4A3D&lᴇ&#FF3C35&lʀ&#FF2E2E&l!";
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#F1FF00&lᴅ&#ECEE02&lᴏ&#E6DD03&lɴ&#E1CC05&lᴀ&#DBBB06&lᴢ&#E4AE05&lɪ&#EDA103&lᴏ&#F69302&lɴ&#FF8600&lᴇ &l&7»&r " + DonationHypeOff);
                    }
                } else {
                    sender.sendMessage("Utilizzo: /donazione-eseguita <player>");
                }
            } else {

                sender.sendMessage("Questo comando può essere eseguito solo dalla console.");
            }
            return true;
        }
        return false;
    }
}
