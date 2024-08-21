package com.monkey.acromvote;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VoteTodayCommand implements CommandExecutor {
    private final String apiUrl = "https://minecraft-italia.net/lista/api/vote/server?serverId=916";
    private final AcromVote plugin;

    public VoteTodayCommand(AcromVote plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("today_votes")) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json");

                    if (connection.getResponseCode() == 200) {
                        JSONParser parser = new JSONParser();
                        JSONArray votesArray = (JSONArray) parser.parse(new InputStreamReader(connection.getInputStream()));
                        Calendar today = Calendar.getInstance();
                        today.set(Calendar.HOUR_OF_DAY, 0);
                        today.set(Calendar.MINUTE, 0);
                        today.set(Calendar.SECOND, 0);

                        int todayVotesCount = 0;

                        StringBuilder message = new StringBuilder(ChatColor.GREEN + "Voti ricevuti oggi:\n");

                        for (Object obj : votesArray) {
                            JSONObject vote = (JSONObject) obj;
                            String timestampStr = (String) vote.get("timestamp");

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            Date timestampDate = sdf.parse(timestampStr);

                            Calendar voteDate = Calendar.getInstance();
                            voteDate.setTime(timestampDate);

                            if (voteDate.after(today)) {
                                String username = (String) vote.get("username");
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                String timeOnly = timeFormat.format(timestampDate);
                                message.append(ChatColor.GOLD).append(username).append(ChatColor.BLUE).append(" - ").append(timeOnly).append("\n");
                                todayVotesCount++;
                            }
                        }


                        message.insert(message.indexOf("\n") + 1, "[" + ChatColor.YELLOW + todayVotesCount + ChatColor.GREEN + "]\n");

                        player.sendMessage(message.toString());
                    } else {
                        player.sendMessage(ChatColor.RED + "Errore nella richiesta al server Minecraft Italia.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "Si è verificato un errore durante l'esecuzione del comando.");
                }
            }
        }

        return true;
    }
}