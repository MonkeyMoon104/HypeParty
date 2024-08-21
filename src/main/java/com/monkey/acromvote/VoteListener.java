package com.monkey.acromvote;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.*;

import static com.monkey.acromvote.Database.updateVoteCountInDatabase;

public class VoteListener implements Listener {
    private final AcromVote plugin;
    private Map<Player, Integer> voteCounts;

    public VoteListener(AcromVote plugin) {
        this.plugin = plugin;
        voteCounts = new HashMap<>();

    }

    String green = "\u001B[38;2;15;252;3m";
    String purple = "\u001B[38;2;128;0;128m";;
    String yellow = "\u001B[38;2;255;255;0m";
    String red = "\u001B[38;2;255;0;0m";
    String orange = "\u001B[38;2;255;165;0m";
    String blue = "\u001B[38;2;0;0;255m";

    @EventHandler
    public void onVoteReceived(VotifierEvent event) throws SQLException {
        plugin.getLogger().info(green + "Debug: onVoteReceived called");
        Vote vote = event.getVote();
        String playerName = vote.getUsername().trim();

        Player player = yourMethodToGetPlayerByUsername(playerName);

        if (player == null) {
            plugin.getLogger().info(green + "Player is " + red + "offline");
            String broadcastMessageOnline = "&6&n&o" + playerName + "&r &#F9FC37&lʜ&#FAE536&lᴀ &#FBB835&lᴠ&#FCA134&lᴏ&#FC8B33&lᴛ&#FD7432&lᴀ&#FE5D32&lᴛ&#FE4731&lᴏ &#FE382B&lɪ&#FD4026&lʟ &#FB501D&lꜱ&#FA5918&lᴇ&#F96113&lʀ&#F8690E&lᴠ&#F7710A&lᴇ&#F67905&lʀ&#F58100&l!";
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#FBFD40&lᴠ&#EBCF23&lᴏ&#DBA006&lᴛ&#B56104&lᴇ &l&7»&r " + broadcastMessageOnline);
            String command = "crate key give " + playerName + " voto 1";
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            startFireworkEffectForAllPlayers();
            int currentVoteCount = Database.getVoteCountFromDatabase();
            int newVoteCount = currentVoteCount + 1;

            updateVoteCountInDatabase(newVoteCount);

            int votesRemaining = Math.max(0, plugin.maxVotesForVoteParty() - currentVoteCount - 1);

            plugin.getLogger().info(blue + "Debug: Votes remaining for Hype Party: " + votesRemaining);

            plugin.updateVotePartyBossBar();
            return;
        }

        if (player.hasPlayedBefore()) {
            plugin.getLogger().info(green + "Debug: Player is online");

            String command = "crate key give " + playerName + " voto 1";
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);

            TextComponent message = new TextComponent(ChatColor.GREEN + "Grazie per aver votato il server!");
            player.spigot().sendMessage(message);

            String broadcastMessageOnline = "&6&n&o" + playerName + "&r &#F9FC37&lʜ&#FAE536&lᴀ &#FBB835&lᴠ&#FCA134&lᴏ&#FC8B33&lᴛ&#FD7432&lᴀ&#FE5D32&lᴛ&#FE4731&lᴏ &#FE382B&lɪ&#FD4026&lʟ &#FB501D&lꜱ&#FA5918&lᴇ&#F96113&lʀ&#F8690E&lᴠ&#F7710A&lᴇ&#F67905&lʀ&#F58100&l!";
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#FBFD40&lᴠ&#EBCF23&lᴏ&#DBA006&lᴛ&#B56104&lᴇ &l&7»&r " + broadcastMessageOnline);

        } else {
            if (!player.hasPlayedBefore()) {
                plugin.getLogger().info(yellow + "Debug: New player voted");

                String broadcastMessageOnline = "&6&n&o" + playerName + "&r &#026B0D&lʜ&#157610&lᴀ &#3B8C17&lᴠ&#4E981A&lᴏ&#61A31D&lᴛ&#74AE20&lᴀ&#87B924&lᴛ&#9AC427&lᴏ &#C0DB2D&lɪ&#D3E631&lʟ &#F9FC37&lꜱ&#FAEB36&lᴇ&#FADA36&lʀ&#FBC935&lᴠ&#FBB835&lᴇ&#FCA734&lʀ &#FD8533&lᴘ&#FD7432&lᴇ&#FE6332&lʀ &#FF4131&lʟ&#FF3030&lᴀ &#FD3C29&lᴘ&#FD4325&lʀ&#FC4921&lɪ&#FB4F1E&lᴍ&#FA551A&lᴀ &#F96212&lᴠ&#F8680F&lᴏ&#F76E0B&lʟ&#F77507&lᴛ&#F67B04&lᴀ&#F58100&l!";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#037A07&lꜰ&#27993E&lɪ&#4CB775&lʀ&#70D6AC&lꜱ&#5EC791&lᴛ &#3AA85A&lᴠ&#27993E&lᴏ&#158923&lᴛ&#037A07&lᴇ &l&7»&r " + broadcastMessageOnline);
            }
        }
        voteCounts.put(player, voteCounts.getOrDefault(player, 0) + 1);
        plugin.updateVotePartyBossBar();
        plugin.sendVoteToMinecraftItaliaVotifier(vote);
        plugin.addVote(player);
        if (plugin.isHypePartyActive()) {
            startFireworkEffectHypePartyForAllPlayers();
        } else {
            startFireworkEffectForAllPlayers();
        }
    }

    private Player yourMethodToGetPlayerByUsername(String playerName) {
        return Bukkit.getPlayerExact(playerName);
    }


    private void startFireworkEffectHypePartyForAllPlayers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Location location = player.getLocation().add(0, 10, 0);
                    Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                    FireworkMeta fireworkMeta = (FireworkMeta) firework.getFireworkMeta();
                    fireworkMeta.addEffect(getCircularFireworkEffectHype());
                    firework.setFireworkMeta(fireworkMeta);
                    firework.setVelocity(location.getDirection().multiply(0.2));
                    firework.setShotAtAngle(true);
                    firework.detonate();
                }
            }
        }.runTaskLater(plugin, 20L);
    }

    private FireworkEffect getCircularFireworkEffectHype() {
        Random random = new Random();

        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            colors.add(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }

        FireworkEffect.Builder builder = FireworkEffect.builder()
                .flicker(true)
                .withColor(colors)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .trail(true);

        return builder.build();
    }

    private void startFireworkEffectForAllPlayers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Location location = player.getLocation().add(0, 10, 0);
                    Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                    FireworkMeta fireworkMeta = (FireworkMeta) firework.getFireworkMeta();
                    fireworkMeta.addEffect(getCircularFireworkEffect());
                    firework.setFireworkMeta(fireworkMeta);
                    firework.setVelocity(location.getDirection().multiply(0.2));
                    firework.setShotAtAngle(true);
                    firework.detonate();
                }
            }
        }.runTaskLater(plugin, 20L);
    }

    private FireworkEffect getCircularFireworkEffect() {
        List<Color> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.ORANGE);
        colors.add(Color.PURPLE);

        FireworkEffect.Builder builder = FireworkEffect.builder()
                .flicker(true)
                .withColor(colors)
                .trail(true);

        return builder.build();
    }

    public int getVoteCount(Player player) {
        return voteCounts.getOrDefault(player, 0);
    }

}
