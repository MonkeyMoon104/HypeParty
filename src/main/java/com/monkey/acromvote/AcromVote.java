package com.monkey.acromvote;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import net.minecraftitalia.votifier.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import com.vexsoftware.votifier.model.Vote;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import static com.monkey.acromvote.Database.*;

public class AcromVote extends JavaPlugin implements Listener {
    private VoteListener voteListener;
    private int maxVotesForVoteParty = 50;
    private final int maxVotesForHypeParty = 5;
    private int countdownLevel1TaskId = -1;
    private final int maxDonationForHypeParty = 1;
    private Map<Player, Integer> voteCounts;
    private Map<Player, Integer> DonationCounts;
    private BossBar votePartyBossBar;
    private BarColor voteColor = BarColor.PURPLE;
    private boolean reverseVote = false;
    private boolean hypePartyActive = false;
    private boolean hypePartyActiveByDatabase = false;
    private boolean hypePartyTimerActive = false;
    private boolean IronRainTask = false;
    private final int hypePartyDuration = 5 * 60;
    private int hypePartyLevel = 1;
    private int voteEffectTaskId = -1;
    private int Countdownlevel1TaskId = -1;
    private int Countdownlevel2TaskId = -1;
    private int Countdownlevel3TaskId = -1;
    private int Countdownlevel4TaskId = -1;
    private int maxDonationForVoteParty = 5;
    private final int maxVotesLevel2 = 10;
    private final int maxDonationsLevel2= 2;
    private final int maxVotesLevel3 = 15;
    private final int maxDonationsLevel3= 3;
    private final int maxVotesLevel4 = 20;
    private final int maxDonationsLevel4= 4;
    private Set<Player> hiddenWordRevealers = new HashSet<>();
    private int minigameTask;
    private int minigameTaskId2;
    private int minigameTaskId3;
    private int minigameTaskIdOperazioniMatematiche;
    private int minigameTaskRisolvi;
    private int minigameTaskOperazioniMatematiche;
    private boolean pluginCanExecuteHiddenWordCommand = true;
    private final String[] hiddenWords = {
            "adventure", "armor", "axe", "bat", "beacon",
            "biome", "blaze", "boat", "bones", "bow",
            "cave", "chicken", "clay", "coal", "cocoa",
            "compass", "crafting", "creeper", "diamond", "dungeon",
            "emerald", "enchanting", "enderdragon", "enderman", "experience",
            "farm", "fish", "flowers", "ghast", "gold",
            "horse", "iron", "jungle", "lava", "magma",
            "map", "milk", "mining", "mooshroom", "nether",
            "obsidian", "ocean", "ore", "pig", "pickaxe",
            "pillager", "portal", "potions", "pumpkin", "quartz",
            "rabbit", "redstone", "sand", "sheep", "shovel",
            "silverfish", "skeleton", "slime", "spawner", "spider",
            "sponge", "squid", "steve", "stone", "sugar",
            "sword", "temple", "torch", "tree", "villager",
            "water", "wheat", "witch", "wither", "wolf",
            "wood", "zombie", "cactus", "carrot", "chest",
            "cow", "dragon", "dye", "farmland", "feather",
            "fishing", "flint", "flower", "furnace", "leather",
            "melon", "minecart", "monster", "mushroom", "planks",
            "rail", "stick"
    };
    private String currentHiddenWord;
    private Player winnerMinigameIndovina;
    private Player winnerMinigameRisolvi;
    private Player winnerMinigameOperazioniMatematiche;
    private boolean minigameRunning = false;
    private boolean minigameRunningRisolvi = false;
    private boolean minigameRunningOperazioniMatematiche = false;
    private long minigameStartTime;
    private long minigameEndTime;
    private long minigameStartTimeRisolvi;
    private long minigameEndTimeRisolvi;
    private long minigameStartTimeOperazioniMatematiche;
    private long minigameEndTimeOperazioniMatematiche;
    private Random random = new Random();
    private BukkitTask ironRainTask;
    private String welcomeMessage;

    private int countdownLevel1TaskPrize = -1;
    private int countdownLevel2TaskPrize = -1;
    private int countdownLevel3TaskPrize = -1;
    private int countdownLevel4TaskPrize = -1;
    private int countdownLevel5TaskPrize = -1;

    private String selectRandomPrizeLevel1() {
        double randomNumber = random.nextDouble() * 100;
        if (randomNumber < 10) {
            return "Money 20k";
        } else if (randomNumber < 10 + 8) {
            return "Pioggia Di Ferro";
        } else if (randomNumber < 10 + 8 + 15) {
            return "Chiave Epica";
        } else if (randomNumber < 10 + 8 + 15 + 18) {
            return "Chiave Comune";
        } else if (randomNumber < 10 + 8 + 15 + 18 + 10) {
            return "Spawner di Creeper";
        } else if (randomNumber < 10 + 8 + 15 + 18 + 10 + 5) {
            return "Chiave Leggendaria";
        } else if (randomNumber < 10 + 8 + 15 + 18 + 10 + 5 + 7) {
            return "Spawner di Blaze";
        } else if (randomNumber < 10 + 8 + 15 + 18 + 10 + 5 + 7 + 7) {
            return "Money 50k";
        } else if (randomNumber < 10 + 8 + 15 + 18 + 10 + 5 + 7 + 7 + 5) {
            return "Money 70k";
        } else {
            return "Uovo di Villico";
        }
    }

    private String selectRandomPrizeLevel2() {
        double randomNumber = random.nextDouble() * 100;
        if (randomNumber < 18) {
            return "Chiave Comune";
        } else if (randomNumber < 18 + 17) {
            return "Chiave Epica";
        } else if (randomNumber < 18 + 17 + 15) {
            return "Chiave Epica";
        } else if (randomNumber < 18 + 17 + 15 + 10) {
            return "Chiave Comune";
        } else if (randomNumber < 18 + 17 + 15 + 10 + 10) {
            return "Faro";
        } else if (randomNumber < 18 + 17 + 15 + 10 + 10 + 10) {
            return "Chiave Leggendaria";
        } else if (randomNumber < 18 + 17 + 15 + 10 + 10 + 10 + 7) {
            return "Faro";
        } else if (randomNumber < 18 + 17 + 15 + 10 + 10 + 10 + 7 + 7) {
            return "Chiave Spawner";
        } else if (randomNumber < 18 + 17 + 15 + 10 + 10 + 10 + 7 + 7 + 5) {
            return "Uovo di villico";
        } else {
            return "Chiave Spawner";
        }
    }

    private String selectRandomPrizeLevel3() {
        double randomNumber = random.nextDouble() * 100;
        if (randomNumber < 18) {
            return "Spawner di iron golem";
        } else if (randomNumber < 18 + 17) {
            return "Chiave Spawner";
        } else if (randomNumber < 18 + 17 + 15) {
            return "Money 500k";
        } else if (randomNumber < 18 + 17 + 15 + 10) {
            return "4 Netherite Ingot";
        } else if (randomNumber < 18 + 17 + 15 + 10 + 10) {
            return "Chiave Leggendaria";
        } else if (randomNumber < 18 + 17 + 15 + 10 + 10 + 10) {
            return "Chiave Spawner";
        } else if (randomNumber < 18 + 17 + 15 + 10 + 10 + 10 + 7) {
            return "Penitenza: Niente";
        } else if (randomNumber < 18 + 17 + 15 + 10 + 10 + 10 + 7 + 7) {
            return "Elytra";
        } else if (randomNumber < 18 + 17 + 15 + 10 + 10 + 10 + 7 + 7 + 5) {
            return "Mucca spawner";
        } else {
            return "Spawner di iron golem";
        }
    }

    private String selectRandomPrizeLevel4() {
        double randomNumber = random.nextDouble() * 100;
        if (randomNumber < 18) {
            return "Spawner di Blaze";
        } else if (randomNumber < 18 + 17) {
            return "Chiave Epica";
        } else if (randomNumber < 18 + 17 + 10) {
            return "Chiave Voto";
        } else if (randomNumber < 18 + 17 + 10 + 10) {
            return "Chiave Divina";
        } else if (randomNumber < 18 + 17 + 10 + 10 + 7) {
            return "Chiave Leggendaria";
        } else if (randomNumber < 18 + 17 + 10 + 10 + 7 + 7) {
            return "Money 500k";
        } else {
            return "Pioggia di ferro";
        }
    }

    private String selectRandomPrizeLevel5() {
        double randomNumber = random.nextDouble() * 100;
        if (randomNumber < 18) {
            return "Chiave Divina";
        } else if (randomNumber < 18 + 17) {
            return "Money 1M";
        } else if (randomNumber < 18 + 17 + 10) {
            return "Netherite ingot 5";
        } else if (randomNumber < 18 + 17 + 10 + 10) {
            return "Spawner di enderman";
        } else if (randomNumber < 18 + 17 + 10 + 10 + 10) {
            return "Spawner di iron golem x2";
        } else if (randomNumber < 18 + 17 + 10 + 10 + 10 + 7) {
            return "Chiave Divina";
        } else if (randomNumber < 18 + 17 + 10 + 10 + 10 + 7 + 7) {
            return "Chiave Leggendaria x3";
        } else {
            return "Chiave Epica";
        }
    }







    String green = "\u001B[38;2;15;252;3m";
    String purple = "\u001B[38;2;128;0;128m";;
    String yellow = "\u001B[38;2;255;255;0m";
    String red = "\u001B[38;2;255;0;0m";
    String orange = "\u001B[38;2;255;165;0m";
    String blue = "\u001B[38;2;0;0;255m";
    private boolean hypePartyRunning = false;


    @Override
    public void onEnable() {

        getLogger().info("");
        getLogger().info("");
        getLogger().info("");
        getLogger().info("");
        String green = "\u001B[38;2;15;252;3m";
        String purple = "\u001B[38;2;128;0;128m";;
        String yellow = "\u001B[38;2;255;255;0m";
        String red = "\u001B[38;2;255;0;0m";
        String orange = "\u001B[38;2;255;165;0m";
        getLogger().info(green + "Abilitazione plugin in corso...");

        getLogger().info("");
        getLogger().info("");
        getLogger().info("");
        getLogger().info("");
        getLogger().info(purple + "    ________  __              _                _    __      __     ");
        getLogger().info(purple + "   / ____/ /_/ /_  ___  _____(_)___  ____     | |  / /___  / /____ ");
        getLogger().info(purple + "  / __/ / __/ __ \\/ _ \\/ ___/ / __ \\/ __ \\    | | / / __ \\/ __/ _ \\");
        getLogger().info(purple + " / /___/ /_/ / / /  __/ /  / / /_/ / / / /    | |/ / /_/ / /_/  __/");
        getLogger().info(purple + "/_____/\\__/_/ /_/\\___/_/  /_/\\____/_/ /_/     |___/\\____/\\__/\\___/ ");
        getLogger().info(purple + "                                                                 ");
        getLogger().info("");
        getLogger().info("");
        getLogger().info("");
        getLogger().info("");


        voteListener = new VoteListener(this);


        getServer().getPluginManager().registerEvents(voteListener, this);

        VotePartyBossbarReset votePartyBossbarReset = new VotePartyBossbarReset(this);
        getCommand("bossbar").setExecutor(votePartyBossbarReset);
        getCommand("bossbar").setTabCompleter(votePartyBossbarReset);

        getServer().getPluginManager().registerEvents(new VoteGUI(this), this);

        getServer().getPluginManager().registerEvents(this, this);

        getCommand("vote").setExecutor(new VoteCommand(this));


        HypePartyCommand hypePartyCommand = new HypePartyCommand(this);
        getCommand("Hype-Party").setExecutor(hypePartyCommand);
        getCommand("Hype-Party").setTabCompleter(hypePartyCommand);

        donationCommand DonationCommand = new donationCommand(this);
        getCommand("donazione-eseguita").setExecutor(DonationCommand);

        VoteTodayCommand voteTodayCommand = new VoteTodayCommand(this);
        getCommand("today_votes").setExecutor(voteTodayCommand);

        saveDefaultConfig();

        FileConfiguration config = getConfig();
        maxVotesForVoteParty = config.getInt("vote_party.max_votes", 100);
        maxDonationForVoteParty = config.getInt("vote_party.max_donations", 10);


        welcomeMessage = getConfig().getString("welcome_message");

        voteCounts = new HashMap<>();
        DonationCounts = new HashMap<>();

        getServer().getPluginManager().registerEvents(this, this);

        try {
            Database.initializeDatabase();
            Database.setMaxVote(maxVotesForVoteParty);
            Database.setMaxDonation(maxDonationForVoteParty);

        } catch (SQLException ex) {
            getLogger().severe(red + "Impossibile collegarsi con il database e creare la tabella");
            ex.printStackTrace();
        }

        try {
            int currentVoteCount = Database.getVoteCountFromDatabase();
            int votesRemaining = Math.max(0, maxVotesForVoteParty - currentVoteCount);
            int currentDonationCounts = Database.getDonationCountFromDatabase();
            int donationsRemaining = Math.max(0, maxDonationForVoteParty - currentDonationCounts);
            votePartyBossBar = Bukkit.createBossBar("§dHype Party: §eMancano  " + votesRemaining + " §evoti o " + donationsRemaining + " Donazioni", voteColor, BarStyle.SEGMENTED_10);
            votePartyBossBar.setVisible(true);
            double progress = (double) votesRemaining / (double) maxVotesForVoteParty;
            votePartyBossBar.setProgress(progress);
            votePartyBossBar.setColor(BarColor.PURPLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        startBossBarUpdateTask();
        startHypeCheckTask();
        startDatabaseConnectionCheckTask();

        getLogger().info("");
        getLogger().info(blue + "Developer: " + orange + "MonkeyMoon104");
        getLogger().info("");
        getLogger().info("");
        getLogger().info(green + "Plugin abilitato con successo!");
        getLogger().info("");
        getLogger().info("");
        getLogger().info("");
        getLogger().info("");

    }

    public int maxVotesForVoteParty() {
        return maxVotesForVoteParty;
    }

    public void updateVotePartyBossBar() {
        try {
            boolean isHypePartyActive = Database.isHypePartyActive();

            if (hypePartyTimerActive) {
                return;
            }

            if (isHypePartyActive) {
                return;
            }

            int currentVoteCount = Database.getVoteCountFromDatabase();
            int currentDonationCounts = Database.getDonationCountFromDatabase();

            int votesRemaining = Math.max(0, maxVotesForVoteParty - currentVoteCount);
            int donationsRemaining = Math.max(0, maxDonationForVoteParty - currentDonationCounts);

            if ((votesRemaining == 0 && !isHypePartyActive) || (donationsRemaining == 0 && !isHypePartyActive)) {
                StartPartyTimer();
                try {
                    resetDonationCountInDatabase();
                } catch (SQLException e) {
                    getLogger().warning("Errore durante il reset del conteggio delle donazioni nel database: " + e.getMessage());
                }
            }

            String votesWord = (votesRemaining == 1) ? "voto" : "voti";
            String donationsWord = (donationsRemaining == 1) ? "donazione" : "donazioni";

            String voteCountText = "§eMancano " + votesRemaining + " " + votesWord + " o " + donationsRemaining + " " + donationsWord;

            votePartyBossBar.setTitle("§dHype Party: " + voteCountText);

            if (votesRemaining >= maxVotesForVoteParty) {
                votePartyBossBar.setProgress(1.0);
                votePartyBossBar.setColor(BarColor.PURPLE);
            } else {
                double progress = (double) votesRemaining / (double) maxVotesForVoteParty;
                votePartyBossBar.setProgress(progress);
                votePartyBossBar.setColor(BarColor.PURPLE);
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("acromvote.partyvote.see")) {
                    votePartyBossBar.addPlayer(player);
                } else {
                    votePartyBossBar.removePlayer(player);
                }
            }
        } catch (SQLException e) {
            getLogger().warning("Errore durante l'aggiornamento della BossBar: " + e.getMessage());
        }
    }




    public void StartPartyTimer() throws SQLException {

        hypePartyTimerActive = true;

        Database.setHypePartyTimerActive(true);

        try {
            int currentHypePartyCount = Database.getHypePartyCountFromDatabase();
            int newHypePartyCount = currentHypePartyCount + 1;

            updateHypePartyCountInDatabase(newHypePartyCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int TimeDuration = 60;

        voteCounts.clear();
        DonationCounts.clear();
        try {
            resetVoteCountInDatabase();
            getLogger().info("");
            resetDonationCountInDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean isHypePartyDiscordMessageSent = Database.isHypePartyDiscordMessageSent();
        if (!isHypePartyDiscordMessageSent) {
            String DiscordMessage = "discordsrv bcast #1274067341289721907 **Annuncio Hype Party!** @\uD83C\uDF89・HypeParty\\n\\nUnisciti all'Hype Party nel server Minecraft!\\n\\n\uD83C\uDF89 Partirà presto l'Hype Party, con vantaggi speciali in-game per tutti:\\n- Ottieni ricompense votando.\\n- Vantaggi esclusivi anche tramite donazioni.\\n\\nPartecipa alla festa e migliora la tua esperienza di gioco!\\n\\n\uD83C\uDFAE IP del Server: `play.acrom.it`\\n\uD83D\uDD17 [Clicca qui per votare](https://minecraft-italia.net/lista/server/acrom)";
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), DiscordMessage);

            try {
                Database.setHypePartyDiscordMessageSent(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        String chatMessage = "&#FD99D6&lʜ&#F685E3&lʏ&#EF72F0&lᴘ&#E85EFD&lᴇ &#A91FFE&lᴘ&#8A00FF&lᴀ&#8102F2&lʀ&#7904E6&lᴛ&#7006D9&lʏ &l&7»&r §esta per iniziare, recati allo spawn.";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !" + chatMessage);


        votePartyBossBar.setTitle("§dHype Party §ein " + TimeDuration + " secondi");
        votePartyBossBar.setProgress(1.0);
        votePartyBossBar.setColor(BarColor.PURPLE);
        votePartyBossBar.setVisible(true);


        for (int i = 0; i < TimeDuration; i++) {
            int SecondS = TimeDuration - i;
            String Timeremaining = String.format("%02d:%02d", SecondS / 60, SecondS % 60);

            Bukkit.getScheduler().runTaskLater(this, () -> {

                if (SecondS == 30) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "titlemsg all &d30");
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#FD99D6&lʜ&#F685E3&lʏ&#EF72F0&lᴘ&#E85EFD&lᴇ &#A91FFE&lᴘ&#8A00FF&lᴀ&#8102F2&lʀ&#7904E6&lᴛ&#7006D9&lʏ &l&7»&r §einizio tra 30 secondi");
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
                } else if (SecondS == 15) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "titlemsg all &d15");
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
                }
                if (SecondS == 1) {
                    votePartyBossBar.setTitle("§dHype Party §ein 1 secondo");
                } else {
                    votePartyBossBar.setTitle("§dHype Party §ein " + Timeremaining + " secondi");
                }

                if (SecondS == 5) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
                } else if (SecondS == 4) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
                } else if (SecondS == 3) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
                } else if (SecondS == 2) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
                } else if (SecondS == 1) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
                }


                double progress = (double) SecondS / TimeDuration;
                votePartyBossBar.setProgress(progress);

                if (SecondS == 60) {
                    votePartyBossBar.setTitle("§dHype Party §ein 1 minuto");
                }


                if (SecondS <= 5) {
                    String titleCommand = "titlemsg all &d" + SecondS;
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), titleCommand);
                }
            }, i * 20L);
        }


        Bukkit.getScheduler().runTaskLater(this, () -> {

            Bukkit.getScheduler().cancelTasks(this);

            hypePartyTimerActive = false;

            try {
                Database.setHypePartyTimerActive(false);
                Database.setHypePartyDiscordMessageSent(false);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "effect-start 1");

            startHypeParty();
        }, TimeDuration * 20L);
    }

    public void startHypeParty() {

        hypePartyRunning = true;

        startHypeCheckTask();
        startBossBarUpdateTask();
        startDatabaseConnectionCheckTask();


        try {
            Database.setHypePartyActive(true);
            Database.setHypePartyActiveByDatabase(true);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }


        boolean isHypePartyActive;
        try {
            isHypePartyActive = Database.isHypePartyActive();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (!isHypePartyActive) {

            return;
        }

        try {
            Database.resetVoteCountInDatabase();
            Database.resetDonationCountInDatabase();
            updateVotePartyBossBar();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cmi titlemsg " + playerName + " &d&lL'hype party \\n &d&lE' iniziato");
            }
        }

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound ender_dragon_death");

        startHypePartyEffects();
        startVoteEffect();

        String broadcastMessageHypePartyStart = "&#FD99D6&lʜ&#F685E3&lʏ&#EF72F0&lᴘ&#E85EFD&lᴇ &#A91FFE&lᴘ&#8A00FF&lᴀ&#8102F2&lʀ&#7904E6&lᴛ&#7006D9&lʏ &l&7»&r §erecati allo /spawn per partecipare!";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !" + broadcastMessageHypePartyStart);

        hypePartyLevel = 1;

        HypePartyLevel1();

    }

    public void HypePartyLevel1(){

        voteCounts.clear();
        DonationCounts.clear();

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
        String PassLevel = "&#FD99D6&lʜ&#F685E3&lʏ&#EF72F0&lᴘ&#E85EFD&lᴇ &#A91FFE&lᴘ&#8A00FF&lᴀ&#8102F2&lʀ&#7904E6&lᴛ&#7006D9&lʏ &l&7»&r §ePassato al livello 1";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !" + PassLevel);

        String hypePartyTitle = "§dHype Party Livello 1: §eMancano 5 voti o 1 Donazione per il livello 2";
        votePartyBossBar.setTitle(hypePartyTitle);
        votePartyBossBar.setProgress(1.0);
        votePartyBossBar.setVisible(true);

        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        AtomicLong currentHypePartyDuration = new AtomicLong(hypePartyDuration * 20L);

        int hypePartyDurationSeconds = 5 * 60;
        currentHypePartyDuration.set(hypePartyDurationSeconds * 20L);

        startPrizeLevel1Task();

        Countdownlevel1TaskId = Bukkit.getScheduler().runTaskTimer(this, () -> {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime.get();
            long remainingTime = hypePartyDuration * 1000 - elapsedTime;
            int secondsRemaining = (int) Math.max(0, Math.ceil(remainingTime / 1000.0));
            int minutes = secondsRemaining / 60;
            int seconds = secondsRemaining % 60;

            String timeRemaining;
            if (minutes == 1 && seconds == 0) {
                timeRemaining = "manca 1 minuto";
            } else if (minutes < 1 && seconds <= 10) {
                timeRemaining = "Rimangono " + seconds + " secondi";
            } else if (minutes == 0) {
                timeRemaining = "Rimangono " + seconds + " secondi";
            } else {
                timeRemaining = "Mancano " + String.format("%d:%02d", minutes, seconds) + " minuti alla fine";
            }

            double maxProgress = 1.0;
            double progress = (double) elapsedTime / (hypePartyDuration * 1000);
            double currentProgress = maxProgress - progress;
            votePartyBossBar.setProgress(currentProgress);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("acromvote.partyvote.see")) {
                    player.sendActionBar("§dHype Party: §e" + timeRemaining);
                }
            }

            int totalVotes = voteCounts.values().stream().mapToInt(Integer::intValue).sum();
            int totalDonations = DonationCounts.values().stream().mapToInt(Integer::intValue).sum();

            int votesRemaining = Math.max(0, maxVotesForHypeParty - totalVotes);
            int DonationRemaining = Math.max(0, maxDonationForHypeParty - totalDonations);

            if (votesRemaining > 0 || DonationRemaining > 0) {
                String votesWord = (votesRemaining == 1) ? "voto" : "voti";
                String donationsWord = (DonationRemaining == 1) ? "donazione" : "donazioni";

                String updatedHypePartyTitle = "§dHype Party Livello 1: §eMancano " + votesRemaining + " " + votesWord + " o " + DonationRemaining + " " + donationsWord + " per il livello 2";
                votePartyBossBar.setTitle(updatedHypePartyTitle);

            }
            if (votesRemaining == 0 || DonationRemaining == 0) {
                voteCounts.clear();
                DonationCounts.clear();
                hypePartyLevel++;
                if (Countdownlevel1TaskId != -1) {
                    Bukkit.getScheduler().cancelTask(Countdownlevel1TaskId);
                    Countdownlevel1TaskId = -1;
                    if (IronRainTask) {
                        stopIronRain();
                    }
                    stopPrizeLevel1Task();
                    HypePartyLevel2();
                }

            }
            if (minutes == 3 && seconds == 30) {
                startTrovaLaParolaMinigame();
            }
            if (minutes == 3 && seconds == 0) {
                startRisolviMinigame();
            }
            if (minutes == 4 && seconds == 40) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 4 && seconds == 0) {
                startRisolviMinigame();
            }
            if (minutes == 2 && seconds == 20) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 1 && seconds == 40) {
                startTrovaLaParolaMinigame();
            }
            if (minutes == 1 && seconds == 0) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 0 && seconds == 25) {
                startRisolviMinigame();
            }

        }, 0L, 20L).getTaskId();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (Countdownlevel1TaskId != -1) {
                Bukkit.getScheduler().cancelTask(Countdownlevel1TaskId);
                Countdownlevel1TaskId = -1;
                stopHypeParty();
                stopPrizeLevel1Task();
            }
        }, currentHypePartyDuration.get());

    }

    public void HypePartyLevel2(){

        voteCounts.clear();
        DonationCounts.clear();

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
        String PassLevel = "&#FD99D6&lʜ&#F685E3&lʏ&#EF72F0&lᴘ&#E85EFD&lᴇ &#A91FFE&lᴘ&#8A00FF&lᴀ&#8102F2&lʀ&#7904E6&lᴛ&#7006D9&lʏ &l&7»&r §ePassato al livello 2";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !" + PassLevel);


        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();


        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "panimation circle;c:193,167,96;twist;part:5;r:0.75;pitch:90;move:0,0.1,0;rc:-0.02;target:" + playerName;
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }

        String hypePartyTitle = "§dHype Party Livello 2: §eMancano 10 voti o 2 Donazioni per il livello 3";
        votePartyBossBar.setTitle(hypePartyTitle);
        votePartyBossBar.setProgress(1.0);
        votePartyBossBar.setVisible(true);

        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        AtomicLong currentHypePartyDuration = new AtomicLong(hypePartyDuration * 20L);

        int hypePartyDurationSeconds = 5 * 60;
        currentHypePartyDuration.set(hypePartyDurationSeconds * 20L);

        startPrizeLevel2Task();

        Countdownlevel2TaskId = Bukkit.getScheduler().runTaskTimer(this, () -> {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime.get();
            long remainingTime = hypePartyDuration * 1000 - elapsedTime;
            int secondsRemaining = (int) Math.max(0, Math.ceil(remainingTime / 1000.0));
            int minutes = secondsRemaining / 60;
            int seconds = secondsRemaining % 60;

            String timeRemaining;
            if (minutes == 1 && seconds == 0) {
                timeRemaining = "manca 1 minuto";
            } else if (minutes < 1 && seconds <= 10) {
                timeRemaining = "Rimangono " + seconds + " secondi";
            } else if (minutes == 0) {
                timeRemaining = "Rimangono " + seconds + " secondi";
            } else {
                timeRemaining = "Mancano " + String.format("%d:%02d", minutes, seconds) + " minuti alla fine";
            }

            double maxProgress = 1.0;
            double progress = (double) elapsedTime / (hypePartyDuration * 1000);
            double currentProgress = maxProgress - progress;
            votePartyBossBar.setProgress(currentProgress);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("acromvote.partyvote.see")) {
                    player.sendActionBar("§dHype Party: §e" + timeRemaining);
                }
            }

            int totalVotes = voteCounts.values().stream().mapToInt(Integer::intValue).sum();
            int totalDonations = DonationCounts.values().stream().mapToInt(Integer::intValue).sum();

            int votesRemaining = Math.max(0, maxVotesLevel2 - totalVotes);
            int DonationRemaining = Math.max(0, maxDonationsLevel2 - totalDonations);

            if (votesRemaining > 0 || DonationRemaining > 0) {
                String votesWord = (votesRemaining == 1) ? "voto" : "voti";
                String donationsWord = (DonationRemaining == 1) ? "donazione" : "donazioni";

                String updatedHypePartyTitle = "§dHype Party Livello 2: §eMancano " + votesRemaining + " " + votesWord + " o " + DonationRemaining + " " + donationsWord + " per il livello 3";
                votePartyBossBar.setTitle(updatedHypePartyTitle);

            }
            if (votesRemaining == 0 || DonationRemaining == 0) {
                voteCounts.clear();
                DonationCounts.clear();
                hypePartyLevel++;
                if (Countdownlevel2TaskId != -1) {
                    Bukkit.getScheduler().cancelTask(Countdownlevel2TaskId);
                    Countdownlevel2TaskId = -1;
                    stopPrizeLevel2Task();
                    HypePartyLevel3();
                }

            }

            if (minutes == 3 && seconds == 30) {
                startTrovaLaParolaMinigame();
            }
            if (minutes == 3 && seconds == 0) {
                startRisolviMinigame();
            }
            if (minutes == 4 && seconds == 40) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 4 && seconds == 0) {
                startRisolviMinigame();
            }
            if (minutes == 2 && seconds == 20) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 1 && seconds == 40) {
                startTrovaLaParolaMinigame();
            }
            if (minutes == 1 && seconds == 0) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 0 && seconds == 25) {
                startRisolviMinigame();
            }

        }, 0L, 20L).getTaskId();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (Countdownlevel2TaskId != -1) {
                Bukkit.getScheduler().cancelTask(Countdownlevel2TaskId);
                Countdownlevel2TaskId = -1;
                stopHypeParty();
                stopPrizeLevel2Task();
            }
        }, currentHypePartyDuration.get());


    }

    public void HypePartyLevel3(){

        voteCounts.clear();
        DonationCounts.clear();

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
        String PassLevel = "&#FD99D6&lʜ&#F685E3&lʏ&#EF72F0&lᴘ&#E85EFD&lᴇ &#A91FFE&lᴘ&#8A00FF&lᴀ&#8102F2&lʀ&#7904E6&lᴛ&#7006D9&lʏ &l&7»&r §ePassato al livello 3";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !" + PassLevel);


        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();


        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "panimation circle;c:179,149,43;twist;part:5;r:0.75;pitch:90;move:0,0.1,0;rc:-0.02;target:" + playerName;
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }

        String hypePartyTitle = "§dHype Party Livello 3: §eMancano 15 voti o 3 Donazioni per il livello 4";
        votePartyBossBar.setTitle(hypePartyTitle);
        votePartyBossBar.setProgress(1.0);
        votePartyBossBar.setVisible(true);

        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        AtomicLong currentHypePartyDuration = new AtomicLong(hypePartyDuration * 20L);

        int hypePartyDurationSeconds = 5 * 60;
        currentHypePartyDuration.set(hypePartyDurationSeconds * 20L);

        startPrizeLevel3Task();

        Countdownlevel3TaskId = Bukkit.getScheduler().runTaskTimer(this, () -> {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime.get();
            long remainingTime = hypePartyDuration * 1000 - elapsedTime;
            int secondsRemaining = (int) Math.max(0, Math.ceil(remainingTime / 1000.0));
            int minutes = secondsRemaining / 60;
            int seconds = secondsRemaining % 60;

            String timeRemaining;
            if (minutes == 1 && seconds == 0) {
                timeRemaining = "manca 1 minuto";
            } else if (minutes < 1 && seconds <= 10) {
                timeRemaining = "Rimangono " + seconds + " secondi";
            } else if (minutes == 0) {
                timeRemaining = "Rimangono " + seconds + " secondi";
            } else {
                timeRemaining = "Mancano " + String.format("%d:%02d", minutes, seconds) + " minuti alla fine";
            }

            double maxProgress = 1.0;
            double progress = (double) elapsedTime / (hypePartyDuration * 1000);
            double currentProgress = maxProgress - progress;
            votePartyBossBar.setProgress(currentProgress);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("acromvote.partyvote.see")) {
                    player.sendActionBar("§dHype Party: §e" + timeRemaining);
                }
            }

            int totalVotes = voteCounts.values().stream().mapToInt(Integer::intValue).sum();
            int totalDonations = DonationCounts.values().stream().mapToInt(Integer::intValue).sum();

            int votesRemaining = Math.max(0, maxVotesLevel3 - totalVotes);
            int DonationRemaining = Math.max(0, maxDonationsLevel3 - totalDonations);

            if (votesRemaining > 0 || DonationRemaining > 0) {
                String votesWord = (votesRemaining == 1) ? "voto" : "voti";
                String donationsWord = (DonationRemaining == 1) ? "donazione" : "donazioni";

                String updatedHypePartyTitle = "§dHype Party Livello 3: §eMancano " + votesRemaining + " " + votesWord + " o " + DonationRemaining + " " + donationsWord + " per il livello 4";
                votePartyBossBar.setTitle(updatedHypePartyTitle);

            }
            if (votesRemaining == 0 || DonationRemaining == 0) {
                voteCounts.clear();
                DonationCounts.clear();
                hypePartyLevel++;
                if (Countdownlevel3TaskId != -1) {
                    Bukkit.getScheduler().cancelTask(Countdownlevel3TaskId);
                    Countdownlevel3TaskId = -1;
                    stopPrizeLevel3Task();
                    HypePartyLevel4();
                }

            }

            if (minutes == 3 && seconds == 30) {
                startTrovaLaParolaMinigame();
            }
            if (minutes == 3 && seconds == 0) {
                startRisolviMinigame();
            }
            if (minutes == 4 && seconds == 40) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 4 && seconds == 0) {
                startRisolviMinigame();
            }
            if (minutes == 2 && seconds == 20) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 1 && seconds == 40) {
                startTrovaLaParolaMinigame();
            }
            if (minutes == 1 && seconds == 0) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 0 && seconds == 25) {
                startRisolviMinigame();
            }

        }, 0L, 20L).getTaskId();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (Countdownlevel3TaskId != -1) {
                Bukkit.getScheduler().cancelTask(Countdownlevel3TaskId);
                Countdownlevel3TaskId = -1;
                stopHypeParty();
                stopPrizeLevel3Task();
            }
        }, currentHypePartyDuration.get());
    }

    public void HypePartyLevel4(){

        voteCounts.clear();
        DonationCounts.clear();

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
        String PassLevel = "&#FD99D6&lʜ&#F685E3&lʏ&#EF72F0&lᴘ&#E85EFD&lᴇ &#A91FFE&lᴘ&#8A00FF&lᴀ&#8102F2&lʀ&#7904E6&lᴛ&#7006D9&lʏ &l&7»&r §ePassato al livello 4";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !" + PassLevel);


        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();


        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "panimation circle;c:192,111,64;twist;part:5;r:0.75;pitch:90;move:0,0.1,0;rc:-0.02;target:" + playerName;
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }

        String hypePartyTitle = "§dHype Party Livello 4: §eMancano 20 voti o 4 Donazioni per il livello 5";
        votePartyBossBar.setTitle(hypePartyTitle);
        votePartyBossBar.setProgress(1.0);
        votePartyBossBar.setVisible(true);

        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        AtomicLong currentHypePartyDuration = new AtomicLong(hypePartyDuration * 20L);

        int hypePartyDurationSeconds = 5 * 60;
        currentHypePartyDuration.set(hypePartyDurationSeconds * 20L);

        startPrizeLevel4Task();

        Countdownlevel4TaskId = Bukkit.getScheduler().runTaskTimer(this, () -> {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime.get();
            long remainingTime = hypePartyDuration * 1000 - elapsedTime;
            int secondsRemaining = (int) Math.max(0, Math.ceil(remainingTime / 1000.0));
            int minutes = secondsRemaining / 60;
            int seconds = secondsRemaining % 60;

            String timeRemaining;
            if (minutes == 1 && seconds == 0) {
                timeRemaining = "manca 1 minuto";
            } else if (minutes < 1 && seconds <= 10) {
                timeRemaining = "Rimangono " + seconds + " secondi";
            } else if (minutes == 0) {
                timeRemaining = "Rimangono " + seconds + " secondi";
            } else {
                timeRemaining = "Mancano " + String.format("%d:%02d", minutes, seconds) + " minuti alla fine";
            }

            double maxProgress = 1.0;
            double progress = (double) elapsedTime / (hypePartyDuration * 1000);
            double currentProgress = maxProgress - progress;
            votePartyBossBar.setProgress(currentProgress);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("acromvote.partyvote.see")) {
                    player.sendActionBar("§dHype Party: §e" + timeRemaining);
                }
            }

            int totalVotes = voteCounts.values().stream().mapToInt(Integer::intValue).sum();
            int totalDonations = DonationCounts.values().stream().mapToInt(Integer::intValue).sum();

            int votesRemaining = Math.max(0, maxVotesLevel4 - totalVotes);
            int DonationRemaining = Math.max(0, maxDonationsLevel4 - totalDonations);

            if (votesRemaining > 0 || DonationRemaining > 0) {
                String votesWord = (votesRemaining == 1) ? "voto" : "voti";
                String donationsWord = (DonationRemaining == 1) ? "donazione" : "donazioni";

                String updatedHypePartyTitle = "§dHype Party Livello 4: §eMancano " + votesRemaining + " " + votesWord + " o " + DonationRemaining + " " + donationsWord + " per il livello 5";
                votePartyBossBar.setTitle(updatedHypePartyTitle);

            }
            if (votesRemaining == 0 || DonationRemaining == 0) {
                voteCounts.clear();
                DonationCounts.clear();
                hypePartyLevel++;
                if (Countdownlevel4TaskId != -1) {
                    Bukkit.getScheduler().cancelTask(Countdownlevel4TaskId);
                    Countdownlevel4TaskId = -1;
                    if (IronRainTask) {
                        stopIronRain();
                    }
                    stopPrizeLevel4Task();
                    HypePartyLevel5();
                }

            }

            if (minutes == 3 && seconds == 30) {
                startTrovaLaParolaMinigame();
            }
            if (minutes == 3 && seconds == 0) {
                startRisolviMinigame();
            }
            if (minutes == 4 && seconds == 40) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 4 && seconds == 0) {
                startRisolviMinigame();
            }
            if (minutes == 2 && seconds == 20) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 1 && seconds == 40) {
                startTrovaLaParolaMinigame();
            }
            if (minutes == 1 && seconds == 0) {
                startOperazioniMatematicheMinigame();
            }
            if (minutes == 0 && seconds == 25) {
                startRisolviMinigame();
            }

        }, 0L, 20L).getTaskId();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (Countdownlevel4TaskId != -1) {
                Bukkit.getScheduler().cancelTask(Countdownlevel4TaskId);
                Countdownlevel4TaskId = -1;
                stopHypeParty();
                stopPrizeLevel4Task();
            }
        }, currentHypePartyDuration.get());

    }

    public void HypePartyLevel5(){

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound experience -p:2");
        String PassLevel = "Livello massimo raggiunto!";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#AB9230&lɢ&#AF9D2D&lᴏ&#B4A92A&lʟ&#B8B427&lᴅ&#CBCD1A&lᴇ&#DEE60D&lɴ &#D5DA14&lᴘ&#B8B427&lᴀ&#B4A92A&lʀ&#AF9D2D&lᴛ&#AB9230&lʏ " + PassLevel);

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound exp");

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cmi titlemsg all &e&lGOLDEN &6&lPARTY");

        startPrizeLevel5Task();


        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();


        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "panimation circle;c:255,223,0;twist;part:5;r:0.75;pitch:90;move:0,0.1,0;rc:-0.02;target:" + playerName;
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }

        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        AtomicLong currentHypePartyDuration = new AtomicLong(hypePartyDuration * 20L);

        int hypePartyDurationSeconds = 5 * 60;
        currentHypePartyDuration.set(hypePartyDurationSeconds * 20L);

        String[] goldPartyFrames = {
                "§6GOLDEN PARTY",
                "§eGOLDEN PARTY",
                "§6GOLDEN PARTY",
                "§eGOLDEN PARTY",
                "§6G",
                "§6G§eO",
                "§6G§eO§6L",
                "§6G§eO§6L§eD",
                "§6G§eO§6L§eD§6E",
                "§6G§eO§6L§eD§6E§eN",
                "§6G§eO§6L§eD§6E§eN ",
                "§6G§eO§6L§eD§6E§eN §6P",
                "§6G§eO§6L§eD§6E§eN §6P§eA",
                "§6G§eO§6L§eD§6E§eN §6P§eA§6R",
                "§6G§eO§6L§eD§6E§eN §6P§eA§6R§eT",
                "§6G§eO§6L§eD§6E§eN §6P§eA§6R§eT§6Y"
        };

        int animationInterval = 4;

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            int currentFrameIndex = (int) ((System.currentTimeMillis() / 1000) % goldPartyFrames.length);
            String currentFrame = goldPartyFrames[currentFrameIndex];
            votePartyBossBar.setTitle(currentFrame);
        }, 0L, animationInterval);


        if (voteEffectTaskId != -1) {
            Bukkit.getScheduler().cancelTask(voteEffectTaskId);
            voteEffectTaskId = -1;
        }

        votePartyBossBar.setColor(BarColor.YELLOW);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            long CurrentTime = System.currentTimeMillis();
            long ElapsedTime = CurrentTime - startTime.get();
            long RemainingTime = hypePartyDuration * 1000 - ElapsedTime;
            int SecondsRemaining = (int) Math.max(0, Math.ceil(RemainingTime / 1000.0));
            int Minutes = SecondsRemaining / 60;
            int Seconds = SecondsRemaining % 60;

            String TimeRemaining;
            if (Minutes == 1 && Seconds == 0) {
                TimeRemaining = "manca 1 minuto";
            } else if (Minutes < 1 && Seconds <= 10) {
                TimeRemaining = "Rimangono " + Seconds + " secondi";
            } else if (Minutes == 0) {
                TimeRemaining = "Rimangono " + Seconds + " secondi";
            } else {
                TimeRemaining = "Mancano " + String.format("%d:%02d", Minutes, Seconds) + " minuti alla fine";
            }

            if (Minutes == 0 && Seconds == 10) {
                giveKitMVPplusLv5();
            }

            double MaxProgress = 1.0;
            double Progress = (double) ElapsedTime / (hypePartyDuration * 1000);
            double CurrentProgress = MaxProgress - Progress;
            votePartyBossBar.setProgress(CurrentProgress);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("acromvote.partyvote.see")) {
                    player.sendActionBar("§6GOLDEN PARTY: §e" + TimeRemaining);
                }
            }

        }, 0L, 20L);


        Bukkit.getScheduler().runTaskLater(this, () -> {
            stopHypeParty();
            stopPrizeLevel5Task();
        }, currentHypePartyDuration.get());
    }




    public void startHypePartyEffects() {

        Bukkit.getScheduler().runTask(this, () -> {
            for (World world : Bukkit.getWorlds()) {
                world.setTime(18000L);
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            }
        });


        Bukkit.getScheduler().runTaskTimer(this, () -> {

            generateRandomFireworksAtLocation(-5.500, 90.000, 26.500);
            generateRandomFireworksAtLocation(-5.500, 90.000, 26.500);
            generateRandomFireworksAtLocation(-5.500, 90.000, 26.500);
            generateRandomFireworksAtLocation(-5.500, 90.000, 26.500);
            generateRandomFireworksAtLocation(-5.500, 90.000, 26.500);

            generateRandomFireworksAtLocation(-5.500, 90, 42.500);
            generateRandomFireworksAtLocation(-5.500, 90, 42.500);
            generateRandomFireworksAtLocation(-5.500, 90, 42.500);
            generateRandomFireworksAtLocation(-5.500, 90, 42.500);

            generateRandomFireworksAtLocation(30.500, 90.500, 29.500);
            generateRandomFireworksAtLocation(30.500, 90.500, 29.500);
            generateRandomFireworksAtLocation(30.500, 90.500, 29.500);
            generateRandomFireworksAtLocation(30.500, 90.500, 29.500);

            generateRandomFireworksAtLocation(30.500, 90.500, 39.500);
            generateRandomFireworksAtLocation(30.500, 90.500, 39.500);
            generateRandomFireworksAtLocation(30.500, 90.500, 39.500);
            generateRandomFireworksAtLocation(30.500, 90.500, 39.500);
        }, 0L, 20L * 10L);
    }

    public void stopHypeParty() {

        hypePartyRunning = false;

        try {

            boolean isHypePartyActive;
            try {
                isHypePartyActive = Database.isHypePartyActive();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            if (isHypePartyActive) {

                try {
                    Database.setHypePartyActive(false);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            voteCounts.clear();
            DonationCounts.clear();

            try {
                Database.resetVoteCountInDatabase();
                Database.resetDonationCountInDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            int currentVoteCount = Database.getVoteCountFromDatabase();
            int currentDonationCounts = Database.getDonationCountFromDatabase();

            int votesRemaining = Math.max(0, maxVotesForVoteParty - currentVoteCount);
            int donationsRemaining = Math.max(0, maxDonationForVoteParty - currentDonationCounts);

            votePartyBossBar.setProgress(1.0);
            votePartyBossBar.setColor(BarColor.PURPLE);
            votePartyBossBar.setTitle("§dHype Party: §eMancano " + votesRemaining + " §evoti o " + donationsRemaining + " Donazioni");
            votePartyBossBar.setVisible(true);

            Bukkit.getScheduler().cancelTasks(this);

            Bukkit.getScheduler().runTask(this, () -> {
                for (World world : Bukkit.getWorlds()) {
                    world.setTime(6000L);
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                }
            });

            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

            for (Player player : onlinePlayers) {
                if (isInSpawnRegion(player)) {
                    String playerName = player.getName();
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cmi titlemsg " + playerName + " &d&lL'hype party è terminato! \\n &d&lGrazie");

                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "effect " + playerName + " levitation 5 2");
                }
            }

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sound entity_allay_death");


            for (Player player : onlinePlayers) {
                if (isInSpawnRegion(player)) {
                    String playerName = player.getName();
                    String command = "panimation circle;effect:reddust;dur:5;pitchc:5;part:10;offset:0,1,0;radius:1;yawc:5;color:rs;target:" + playerName;
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
            startBossBarUpdateTask();
            startHypeCheckTask();
            startDatabaseConnectionCheckTask();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropItemsLevel1() {
        try {
            boolean isHypePartyActive = Database.isHypePartyActive();

            if (isHypePartyActive) {
                String prizeCommand = selectRandomPrizeLevel1();

                if (!prizeCommand.equals("Pioggia Di Ferro")) {
                    String PrizeFrase = "§l§dHype Party §7››§r" + ChatColor.YELLOW + " Hai vinto la seguente ricompensa di livello 1: " + ChatColor.AQUA + prizeCommand;
                    String PrizeFraseOff = "§l§dHype Party §7››§r" + ChatColor.YELLOW + " I player allo spawn hanno vinto la seguente ricompensa di livello 1: " + ChatColor.AQUA + prizeCommand;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (isInSpawnRegion(player)) {
                            player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(PrizeFrase));
                        } else {
                            player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(PrizeFraseOff));
                        }
                    }
                } else {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#FD99D6&lʜ&#F685E3&lʏ&#EF72F0&lᴘ&#E85EFD&lᴇ &#A91FFE&lᴘ&#8A00FF&lᴀ&#8102F2&lʀ&#7904E6&lᴛ&#7006D9&lʏ &l&7»&r" +  ChatColor.YELLOW + " Pioggia di ferro allo /spawn!");
                }

                switch (prizeCommand) {
                    case "Spawner di Blaze":
                        giveSpawnerBlazeToPlayers();
                        break;
                    case "Spawner di Creeper":
                        giveSpawnerCreeperToPlayers();
                        break;
                    case "Uovo di Villico":
                        giveVillagerEggToPlayers();
                        break;
                    case "Pioggia Di Ferro":
                        startIronRain();
                        break;
                    case "Chiave Epica":
                        giveChiaveEpica();
                        break;
                    case "Chiave Leggendaria":
                        giveChiaveLeggendariaLv2();
                        break;
                    case "Chiave Comune":
                        giveChiaveComune();
                        break;
                    case "Money 20k":
                        giveSoldi20k();
                        break;
                    case "Money 50k":
                        giveSoldi50k();
                        break;
                    case "Money 70k":
                        giveSoldi70k();
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropItemsLevel2() {
        try {
            boolean isHypePartyActive = Database.isHypePartyActive();

            if (isHypePartyActive) {
                String prizeCommand = selectRandomPrizeLevel2();


                String PrizeFrase = "§l§dHype Party §7››§r" + ChatColor.YELLOW + " Hai vinto la seguente ricompensa di livello 2: " + ChatColor.AQUA + prizeCommand;
                String PrizeFraseOff = "§l§dHype Party §7››§r" + ChatColor.YELLOW + " I player allo spawn hanno vinto la seguente ricompensa di livello 2: " + ChatColor.AQUA + prizeCommand;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isInSpawnRegion(player)) {
                        player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(PrizeFrase));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(PrizeFraseOff));
                    }
                }

                switch (prizeCommand) {
                    case "Chiave Comune":
                        giveChiaveComuneLv2();
                        break;
                    case "Chiave Epica":
                        giveChiaveEpicaLv2();
                        break;
                    case "Chiave Spawner":
                        giveChiaveSpawnerLv3();
                        break;
                    case "Chiave Leggendaria":
                        giveChiaveLeggendariaLv2();
                        break;
                    case "Faro":
                        giveBeaconLv2();
                        break;
                    case "Uovo di Villico":
                        giveVillagerEggToPlayersLv2();
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropItemsLevel3() {
        try {
            boolean isHypePartyActive = Database.isHypePartyActive();

            if (isHypePartyActive) {
                String prizeCommand = selectRandomPrizeLevel3();


                String PrizeFrase = "§l§dHype Party §7››§r" + ChatColor.YELLOW + " Hai vinto la seguente ricompensa di livello 3: " + ChatColor.AQUA + prizeCommand;
                String PrizeFraseOff = "§l§dHype Party §7››§r" + ChatColor.YELLOW + " I player allo spawn hanno vinto la seguente ricompensa di livello 3: " + ChatColor.AQUA + prizeCommand;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isInSpawnRegion(player)) {
                        player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(PrizeFrase));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(PrizeFraseOff));
                    }
                }

                switch (prizeCommand) {
                    case "Money 500k":
                        giveMoney500kLv3();
                        break;
                    case "Chiave Spawner":
                        giveChiaveSpawnerLv3();
                        break;
                    case "Penitenza: Niente":
                        givePenitenzaNienteLv3();
                        break;
                    case "4 Netherite Ingot":
                        giveNetheriteScrapLv3();
                        break;
                    case "Elytra":
                        giveElytraLv3();
                        break;
                    case "Mucca spawner":
                        giveMuccaFungosaSpawnerLv3();
                        break;
                    case "Spawner di iron golem":
                        giveIronGolemSpawnerLv3();
                        break;
                    case "Chiave Leggendaria":
                        giveChiaveLeggendariaLv2();
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropItemsLevel4() {
        try {
            boolean isHypePartyActive = Database.isHypePartyActive();

            if (isHypePartyActive) {
                String prizeCommand = selectRandomPrizeLevel4();

                if (!prizeCommand.equals("Pioggia di ferro")) {
                    String PrizeFrase = "§l§dHype Party §7››§r" + ChatColor.YELLOW + " Hai vinto la seguente ricompensa di livello 4: " + ChatColor.AQUA + prizeCommand;
                    String PrizeFraseOff = "§l§dHype Party §7››§r" + ChatColor.YELLOW + " I player allo spawn hanno vinto la seguente ricompensa di livello 4: " + ChatColor.AQUA + prizeCommand;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (isInSpawnRegion(player)) {
                            player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(PrizeFrase));
                        } else {
                            player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(PrizeFraseOff));
                        }
                    }
                } else {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#FD99D6&lʜ&#F685E3&lʏ&#EF72F0&lᴘ&#E85EFD&lᴇ &#A91FFE&lᴘ&#8A00FF&lᴀ&#8102F2&lʀ&#7904E6&lᴛ&#7006D9&lʏ &l&7»&r" +  ChatColor.YELLOW + " Pioggia di ferro allo /spawn!");
                }

                switch (prizeCommand) {
                    case "Spawner di Blaze":
                        giveSpawnerBlazeToPlayers();
                        break;
                    case "Pioggia di ferro":
                        startIronRain();
                        break;
                    case "Chiave Epica":
                        giveChiaveEpica();
                        break;
                    case "Chiave Divina":
                        giveChiaveDivina();
                        break;
                    case "Chiave Voto":
                        giveChiaveVoto();
                        break;
                    case "Money 500k":
                        giveMoney500kLv3();
                        break;
                    case "Chiave Leggendaria":
                        giveChiaveLeggendariaLv2();
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropItemsLevel5() {
        try {
            boolean isHypePartyActive = Database.isHypePartyActive();

            if (isHypePartyActive) {
                String prizeCommand = selectRandomPrizeLevel5();


                String PrizeFrase = "§eGolden Party §7››§r" + ChatColor.YELLOW + " Hai vinto la seguente ricompensa di livello 5: " + ChatColor.AQUA + prizeCommand;
                String PrizeFraseOff = "§Golden Party §7››§r" + ChatColor.YELLOW + " I player allo spawn hanno vinto la seguente ricompensa di livello 5: " + ChatColor.AQUA + prizeCommand;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isInSpawnRegion(player)) {
                        player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(PrizeFrase));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(PrizeFraseOff));
                    }
                }

                switch (prizeCommand) {
                    case "Chiave Divina":
                        giveChiaveLeggendariaLv2();
                        break;
                    case "Money 1M":
                        giveMoney1MLv5();
                        break;
                    case "Netherite ingot 5":
                        giveNetheriteIngotLv5();
                        break;
                    case "Spawner di enderman":
                        giveEndermanSpawnerLv5();
                        break;
                    case "Spawner di iron golem x2":
                        giveIronGolemSpawnerLv3();
                        giveIronGolemSpawnerLv3();
                        break;
                    case "Chiave Epica":
                        giveChiaveEpica();
                        break;
                    case "Chiave Leggendaria x3":
                        giveChiaveLeggendariaLv5();
                        giveChiaveLeggendariaLv5();
                        giveChiaveLeggendariaLv5();
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isInSpawnRegion(Player player) {
        String hubWorldName = "hub";
        return player.getWorld().getName().equalsIgnoreCase(hubWorldName);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (hypePartyTimerActive || hypePartyRunning) {

            if (isInSpawnRegion(player)) {

                if (player.isFlying()) {
                    player.setFlying(false);
                }
            }
        }
    }


    public void addVote(Player player) throws SQLException {
        try {
            boolean isHypePartyActive = Database.isHypePartyActive();

            if (!isHypePartyActive || hypePartyTimerActive) {
                int currentVoteCount = Database.getVoteCountFromDatabase();
                int newVoteCount = currentVoteCount + 1;

                updateVoteCountInDatabase(newVoteCount);

                int votesRemaining = Math.max(0, maxVotesForVoteParty - currentVoteCount - 1);

                getLogger().info(blue + "Debug: Votes remaining for Hype Party: " + votesRemaining);

                updateVotePartyBossBar();
            } else {
                int totalVotes = voteCounts.values().stream().mapToInt(Integer::intValue).sum();
                int votesRemaining = Math.max(0, maxVotesForHypeParty - totalVotes);

                int votes = voteCounts.getOrDefault(player, 0);
                voteCounts.put(player, votes + 1);

                getLogger().info(blue + "Debug: Votes remaining for Hype Party: " + votesRemaining);

                updateVotePartyBossBar();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDonationFromConsole(Player targetPlayer) throws SQLException {
        try {
            boolean isHypePartyActive = Database.isHypePartyActive();

            if (!isHypePartyActive || hypePartyActive) {
                int currentDonationCount = Database.getDonationCountFromDatabase();
                int newDonationCount = currentDonationCount + 1;

                updateDonationCountInDatabase(newDonationCount);

                int DonationRemaining = Math.max(0, maxDonationForVoteParty - currentDonationCount - 1);

                getLogger().info(blue + "Debug: Donation remaining for Hype Party: " + DonationRemaining);

                updateVotePartyBossBar();
            } else {
                int totalDonations = DonationCounts.values().stream().mapToInt(Integer::intValue).sum();
                int DonationRemaining = Math.max(0, maxDonationForHypeParty - totalDonations);

                int donationCount = DonationCounts.getOrDefault(targetPlayer, 0);
                DonationCounts.put(targetPlayer, donationCount + 1);

                getLogger().info(blue + "Debug: Donation remaining for Hype Party: " + DonationRemaining);

                updateVotePartyBossBar();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();


        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), welcomeMessage.replace("{player}", player.getName()));

        votePartyBossBar.addPlayer(player);
        updateVotePartyBossBar();
    }

    public void startBossBarUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateVotePartyBossBar();
            }
        }.runTaskTimer(this, 0, 20);
    }

    public void startDatabaseConnectionCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Database.checkAndReconnect();
            }
        }.runTaskTimer(this, 0, 20);
    }

    public void startHypeCheckTask() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            try {
                boolean isHypePartyActive = Database.isHypePartyActive();
                boolean isHypePartyActiveByDatabase = Database.isHypePartyActiveByDatabase();
                boolean isHypePartyTimerActive = Database.isHypePartyTimerActive();

                if (!isHypePartyActive && !hypePartyTimerActive) {
                    if (isHypePartyTimerActive) {
                        getLogger().info(yellow + "Funzione richiamata (start timer) !");
                        StartPartyTimer();
                        hypePartyTimerActive = true;
                        Database.setHypePartyTimerActive(true);
                    }

                }
                if (!isHypePartyActive && hypePartyRunning) {
                    if (isHypePartyActiveByDatabase) {
                        getLogger().info(yellow + "funzione richiamata (stop) !");
                        stopHypeParty();
                        hypePartyRunning = false;
                        Database.setHypePartyActiveByDatabase(false);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 0L, 20L);
    }



    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        votePartyBossBar.removePlayer(player);
        updateVotePartyBossBar();
    }

    private void startVoteEffect() {

        voteEffectTaskId = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (reverseVote) {
                voteColor = voteColor.ordinal() - 1 >= 0 ? BarColor.values()[voteColor.ordinal() - 1] : BarColor.values()[BarColor.values().length - 1];
            } else {
                voteColor = voteColor.ordinal() + 1 < BarColor.values().length ? BarColor.values()[voteColor.ordinal() + 1] : BarColor.values()[0];
            }

            if (voteColor == BarColor.RED || voteColor == BarColor.GREEN) {
                reverseVote = !reverseVote;
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                votePartyBossBar.setColor(voteColor);
                votePartyBossBar.addPlayer(player);
            }

        }, 0L, 10L).getTaskId();
    }

    private void startTrovaLaParolaMinigame() {
        if (minigameRunning) {
            return;
        }

        hiddenWordRevealers.clear();
        minigameRunning = true;
        minigameStartTime = System.currentTimeMillis();

        minigameRunning = true;

        String hiddenWord = hiddenWords[new Random().nextInt(hiddenWords.length)];
        currentHiddenWord = hiddenWord;


        TextComponent scrumble = new TextComponent("§l§dHypeGame§r §7››§r " + ChatColor.LIGHT_PURPLE + "Clicca qui per scoprire la " + ChatColor.YELLOW +  "parola" + ChatColor.LIGHT_PURPLE + " nascosta!");
        scrumble.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new BaseComponent[]{new TextComponent(ChatColor.AQUA + hiddenWord)}
        ));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(scrumble);
            hiddenWordRevealers.add(player);
        }


        minigameTask = Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (winnerMinigameIndovina == null) {
                String end = ChatColor.YELLOW + "Nessuno ha trovato la parola nascosta che era: " + ChatColor.RED + currentHiddenWord;
                Bukkit.getScheduler().runTask(this, () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#E37EBC&lʜ&#E869BC&lʏ&#EC54BB&lᴘ&#F13FBB&lᴇ &#FA15BA&lᴘ&#FF00B9&lᴀ&#ED00B0&lʀ&#DA00A6&lᴛ&#C8009D&lʏ &#A3008A&lᴍ&#910081&lɪ&#880077&lɴ&#80006D&lɪ&#770063&lɢ&#6E0058&lᴀ&#66004E&lᴍ&#5D0044&lᴇ &7››&r " + end);
                });
            }

            minigameRunning = false;
            endTrovaLaParolaMinigame();
        }, 20 * 20);


        final Plugin pluginInstance = this;
        minigameTaskId2 = Bukkit.getScheduler().runTaskLater(this, () -> {
            getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
                    if (!minigameRunning) {
                        return;
                    }

                    Player player = event.getPlayer();
                    String message = event.getMessage();


                    if (message.equalsIgnoreCase(currentHiddenWord)) {

                        winnerMinigameIndovina = player;


                        event.setCancelled(true);


                        minigameEndTime = System.currentTimeMillis();
                        long timeElapsed = minigameEndTime - minigameStartTime;
                        long secondsElapsed = timeElapsed / 1000;

                        String winner = ChatColor.YELLOW + "Congratulazioni a " + ChatColor.RED + winnerMinigameIndovina.getName() + ChatColor.YELLOW + " che ha indovinato la parola nascosta in " + ChatColor.AQUA + secondsElapsed + " secondi!";
                        Bukkit.getScheduler().runTask(pluginInstance, () -> {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#E37EBC&lʜ&#E869BC&lʏ&#EC54BB&lᴘ&#F13FBB&lᴇ &#FA15BA&lᴘ&#FF00B9&lᴀ&#ED00B0&lʀ&#DA00A6&lᴛ&#C8009D&lʏ &#A3008A&lᴍ&#910081&lɪ&#880077&lɴ&#80006D&lɪ&#770063&lɢ&#6E0058&lᴀ&#66004E&lᴍ&#5D0044&lᴇ &7››&r " + winner);
                            String prize = "give " + winnerMinigameIndovina.getName() + " beacon 128";
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), prize);
                        });


                        endTrovaLaParolaMinigame();
                    }
                }
            }, pluginInstance);
        }, 1).getTaskId();
    }

    private void endTrovaLaParolaMinigame() {
        if (minigameRunning) {
            minigameRunning = false;
            hiddenWordRevealers.clear();
            Bukkit.getScheduler().cancelTask(minigameTaskId2);


            minigameStartTime = 0;
            minigameEndTime = 0;
        }
    }


    private void startRisolviMinigame() {
        if (minigameRunningRisolvi) {
            return;
        }

        minigameRunningRisolvi = true;
        minigameStartTimeRisolvi = System.currentTimeMillis();

        String hiddenWord = hiddenWords[new Random().nextInt(hiddenWords.length)];
        currentHiddenWord = hiddenWord;


        List<String> letters = new ArrayList<>(Arrays.asList(hiddenWord.split("")));
        Collections.shuffle(letters);
        String shuffledWord = String.join("", letters);


        String message = "§l§dHypeGame§r §7››§r " + ChatColor.LIGHT_PURPLE + "Risolvi la seguente parola: " + ChatColor.YELLOW + shuffledWord;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }

        minigameTaskRisolvi = Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (winnerMinigameRisolvi == null) {
                String endRisolvi = ChatColor.YELLOW + "Nessuno ha trovato la parola mescolata che era: " + ChatColor.RED + currentHiddenWord;
                Bukkit.getScheduler().runTask(this, () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#E37EBC&lʜ&#E869BC&lʏ&#EC54BB&lᴘ&#F13FBB&lᴇ &#FA15BA&lᴘ&#FF00B9&lᴀ&#ED00B0&lʀ&#DA00A6&lᴛ&#C8009D&lʏ &#A3008A&lᴍ&#910081&lɪ&#880077&lɴ&#80006D&lɪ&#770063&lɢ&#6E0058&lᴀ&#66004E&lᴍ&#5D0044&lᴇ &7››&r " + endRisolvi);
                });
            }

            minigameRunningRisolvi = false;
            endRisolviMinigame();
        }, 20 * 20);


        final Plugin pluginInstance = this;
        minigameTaskId3 = Bukkit.getScheduler().runTaskLater(this, () -> {
            getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
                    if (!minigameRunningRisolvi) {
                        return;
                    }

                    Player player = event.getPlayer();
                    String message = event.getMessage();


                    if (message.equalsIgnoreCase(currentHiddenWord)) {

                        winnerMinigameRisolvi = player;


                        event.setCancelled(true);


                        minigameEndTimeRisolvi = System.currentTimeMillis();
                        long timeElapsed = minigameEndTimeRisolvi - minigameStartTimeRisolvi;
                        long secondsElapsed = timeElapsed / 1000;

                        String winnerRisolvi = ChatColor.YELLOW + "Congratulazioni a " + ChatColor.RED + winnerMinigameRisolvi.getName() + ChatColor.YELLOW + " che ha risolto la parola mescolata in " + ChatColor.AQUA + secondsElapsed + " secondi!";
                        Bukkit.getScheduler().runTask(pluginInstance, () -> {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#E37EBC&lʜ&#E869BC&lʏ&#EC54BB&lᴘ&#F13FBB&lᴇ &#FA15BA&lᴘ&#FF00B9&lᴀ&#ED00B0&lʀ&#DA00A6&lᴛ&#C8009D&lʏ &#A3008A&lᴍ&#910081&lɪ&#880077&lɴ&#80006D&lɪ&#770063&lɢ&#6E0058&lᴀ&#66004E&lᴍ&#5D0044&lᴇ &7››&r " + winnerRisolvi);
                            String prize = "give " + winnerMinigameRisolvi.getName() + " netheriteingot 2";
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), prize);
                        });


                        endRisolviMinigame();
                    }
                }
            }, pluginInstance);
        }, 1).getTaskId();
    }

    private void endRisolviMinigame() {
        if (minigameRunningRisolvi) {
            minigameRunningRisolvi = false;
            Bukkit.getScheduler().cancelTask(minigameTaskId3);


            minigameStartTimeRisolvi = 0;
            minigameEndTimeRisolvi = 0;
        }
    }


    private void startOperazioniMatematicheMinigame() {
        if (minigameRunningOperazioniMatematiche) {
            return;
        }

        minigameRunningOperazioniMatematiche = true;
        minigameStartTimeOperazioniMatematiche = System.currentTimeMillis();

        String[] operators = {"+", "-", "*"};
        String operator = operators[new Random().nextInt(operators.length)];
        int operand1 = new Random().nextInt(11);
        int operand2 = new Random().nextInt(11);

        if (operator.equals("-")) {

            operand2 = Math.min(operand1, operand2);
        }

        double result;
        switch (operator) {
            case "+":
                result = operand1 + operand2;
                break;
            case "-":
                result = operand1 - operand2;
                break;
            case "*":
                result = operand1 * operand2;
                break;
            default:
                result = 0;
                break;
        }


        String formattedResult;
        if (result == (long) result) {
            formattedResult = String.format("%d", (long) result);
        } else {
            formattedResult = String.valueOf(result);
        }

        String message = "§l§dHypeGame§r §7››§r " + ChatColor.LIGHT_PURPLE + "Risolvi l'operazione matematica: " + ChatColor.YELLOW + operand1 + " " + operator + " " + operand2;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }

        minigameTaskOperazioniMatematiche = Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (winnerMinigameOperazioniMatematiche == null) {
                String endOperazioniMatematiche = ChatColor.YELLOW + "Nessuno ha risolto l'operazione matematica! Il risultato era: " + ChatColor.RED + formattedResult;
                Bukkit.getScheduler().runTask(this, () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#E37EBC&lʜ&#E869BC&lʏ&#EC54BB&lᴘ&#F13FBB&lᴇ &#FA15BA&lᴘ&#FF00B9&lᴀ&#ED00B0&lʀ&#DA00A6&lᴛ&#C8009D&lʏ &#A3008A&lᴍ&#910081&lɪ&#880077&lɴ&#80006D&lɪ&#770063&lɢ&#6E0058&lᴀ&#66004E&lᴍ&#5D0044&lᴇ &7››&r " + endOperazioniMatematiche);
                });
            }

            minigameRunningOperazioniMatematiche = false;
            endOperazioniMatematicheMinigame();
        }, 20 * 20);

        final Plugin pluginInstance = this;
        minigameTaskIdOperazioniMatematiche = Bukkit.getScheduler().runTaskLater(this, () -> {
            getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
                    if (!minigameRunningOperazioniMatematiche) {
                        return;
                    }

                    Player player = event.getPlayer();
                    String message = event.getMessage();

                    double playerAnswer;
                    try {
                        playerAnswer = Double.parseDouble(message);
                    } catch (NumberFormatException e) {
                        return;
                    }

                    if (playerAnswer == result) {

                        winnerMinigameOperazioniMatematiche = player;

                        event.setCancelled(true);

                        minigameEndTimeOperazioniMatematiche = System.currentTimeMillis();
                        long timeElapsed = minigameEndTimeOperazioniMatematiche - minigameStartTimeOperazioniMatematiche;
                        long secondsElapsed = timeElapsed / 1000;

                        String winnerOperazioniMatematiche = ChatColor.YELLOW + "Congratulazioni a " + ChatColor.RED + winnerMinigameOperazioniMatematiche.getName() + ChatColor.YELLOW + " che ha risolto correttamente l'operazione matematica in " + ChatColor.AQUA + secondsElapsed + " secondi!";
                        Bukkit.getScheduler().runTask(pluginInstance, () -> {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#E37EBC&lʜ&#E869BC&lʏ&#EC54BB&lᴘ&#F13FBB&lᴇ &#FA15BA&lᴘ&#FF00B9&lᴀ&#ED00B0&lʀ&#DA00A6&lᴛ&#C8009D&lʏ &#A3008A&lᴍ&#910081&lɪ&#880077&lɴ&#80006D&lɪ&#770063&lɢ&#6E0058&lᴀ&#66004E&lᴍ&#5D0044&lᴇ &7››&r " + winnerOperazioniMatematiche);
                            String prize = "crate key give " + winnerMinigameOperazioniMatematiche.getName() + " epica 1";
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), prize);
                        });

                        endOperazioniMatematicheMinigame();
                    }
                }
            }, pluginInstance);
        }, 1).getTaskId();
    }

    private void endOperazioniMatematicheMinigame() {
        if (minigameRunningOperazioniMatematiche) {
            minigameRunningOperazioniMatematiche = false;
            Bukkit.getScheduler().cancelTask(minigameTaskIdOperazioniMatematiche);

            minigameStartTimeOperazioniMatematiche = 0;
            minigameEndTimeOperazioniMatematiche = 0;
        }
    }




    private void generateRandomFireworksAtLocation(double x, double y, double z) {
        World world = Bukkit.getWorld("hub");

        Color fireworkColor = getRandomFireworkColor();

        assert world != null;
        Firework firework = (Firework) world.spawnEntity(new Location(world, x, y, z), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().withColor(fireworkColor).with(FireworkEffect.Type.BURST).withTrail().withFlicker().build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }


    private Color getRandomFireworkColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.fromRGB(r, g, b);
    }

    public boolean isHypePartyActive() {
        try {
            return Database.isHypePartyActive();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void setHypePartyLevel(int level) {
        hypePartyLevel = level;
    }

    public void sendVoteToMinecraftItaliaVotifier(Vote vote) {
        Main minecraftItaliaVotifierPlugin = (Main) Bukkit.getPluginManager().getPlugin("MinecraftITALIA-Votifier-addon-v1.2.0");
        if (minecraftItaliaVotifierPlugin != null) {
            boolean forwardingSupport = false;
            CompletableFuture<Void> future = minecraftItaliaVotifierPlugin.onVote(vote, forwardingSupport);
            future.thenAccept(result -> {

            });
        }
    }
    public VoteListener getVoteListener() {
        return voteListener;
    }

    public void startPrizeLevel1Task() {

        countdownLevel1TaskPrize = new BukkitRunnable() {
            @Override
            public void run() {
                dropItemsLevel1();
            }
        }.runTaskTimer(this, 0L, 20L * 60L).getTaskId();
    }

    public void stopPrizeLevel1Task() {
        if (countdownLevel1TaskPrize != -1) {
            Bukkit.getScheduler().cancelTask(countdownLevel1TaskPrize);
            countdownLevel1TaskPrize = -1;
        }
    }

    private void giveSpawnerBlazeToPlayers() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "rosestacker give spawner " + playerName + " blaze 1";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void giveSpawnerCreeperToPlayers() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "rosestacker give spawner  " + playerName + " creeper 1";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void giveVillagerEggToPlayers() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "give "  + playerName + " villager_spawn_egg 1";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void giveChiaveEpica() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key give " + player.getName() + " epica 1");
            }
        }
    }

    private void giveChiaveDivina() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key give " + playerName + " divina 1");
            }
        }
    }

    private void giveChiaveComune() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key give " + playerName + " comune 1");
            }
        }
    }

    private void giveChiaveVoto() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key give " + playerName + " voto 1");
            }
        }
    }

    private void giveSoldi20k() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "money give " + playerName + " 20000");
            }
        }
    }

    private void giveSoldi50k() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "money give " + playerName + " 50000");
            }
        }
    }

    private void giveSoldi70k() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "money give " + playerName + " 70000");
            }
        }
    }

    private void startIronRain() {

        IronRainTask = true;

        ironRainTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            spawnIronInLocation(25.500, 80, 48.500);
            spawnIronInLocation(25.500, 80, 48.500);
            spawnIronInLocation(25.500, 80, 48.500);
            spawnIronInLocation(25.500, 80, 48.500);

            spawnIronInLocation(25.500, 80, 20.500);
            spawnIronInLocation(25.500, 80, 20.500);
            spawnIronInLocation(25.500, 80, 20.500);
            spawnIronInLocation(25.500, 80, 20.500);

            spawnIronInLocation(5.500, 80, 34.500);
            spawnIronInLocation(5.500, 80, 34.500);
            spawnIronInLocation(5.500, 80, 34.500);
            spawnIronInLocation(5.500, 80, 34.500);
        }, 0L, 60L);


        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (IronRainTask) {
                stopIronRain();
            }
        }, 20L * 50L);
    }

    private void spawnIronInLocation(double x, double y, double z) {
        World world = Bukkit.getWorld("hub");
        Location spawnLocation = new Location(world, x, y, z);

        if (world != null) {
            ItemStack ironIngot = new ItemStack(Material.IRON_INGOT);
            Item item = world.dropItem(spawnLocation, ironIngot);
            item.setInvulnerable(false);
        }
    }

    private void stopIronRain() {

        IronRainTask = false;

        ironRainTask.cancel();
        String Stop = ChatColor.GREEN + "Pioggia Di Ferro terminata.";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast !&#FD99D6&lʜ&#F685E3&lʏ&#EF72F0&lᴘ&#E85EFD&lᴇ &#A91FFE&lᴘ&#8A00FF&lᴀ&#8102F2&lʀ&#7904E6&lᴛ&#7006D9&lʏ &l&7»&r " + Stop);
    }

    public void startPrizeLevel2Task() {

        countdownLevel2TaskPrize = new BukkitRunnable() {
            @Override
            public void run() {
                dropItemsLevel2();
            }
        }.runTaskTimer(this, 0L, 20L * 60L).getTaskId();
    }

    public void stopPrizeLevel2Task() {
        if (countdownLevel2TaskPrize != -1) {
            Bukkit.getScheduler().cancelTask(countdownLevel2TaskPrize);
            countdownLevel2TaskPrize = -1;
        }
    }

    private void giveBeaconLv2() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "give " + playerName + " beacon 1";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void giveShulkerboxLv2() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "give " + playerName + " shulkerbox 1";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void giveVillagerEggToPlayersLv2() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "give "  + playerName + " villager_spawn_egg 1";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void giveChiaveEpicaLv2() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key give " + player.getName() + " epica 1");
            }
        }
    }

    private void giveChiaveDivinaLv2() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key give " + player.getName() + " divina 1");
            }
        }
    }

    private void giveChiaveLeggendariaLv2() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key give " + player.getName() + " leggendaria 1");
            }
        }
    }

    private void giveChiaveComuneLv2() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key give " + player.getName() + " comune 1");
            }
        }
    }

    public void startPrizeLevel3Task() {

        countdownLevel3TaskPrize = new BukkitRunnable() {
            @Override
            public void run() {
                dropItemsLevel3();
            }
        }.runTaskTimer(this, 0L, 20L * 60L).getTaskId();
    }

    public void stopPrizeLevel3Task() {
        if (countdownLevel3TaskPrize != -1) {
            Bukkit.getScheduler().cancelTask(countdownLevel3TaskPrize);
            countdownLevel3TaskPrize = -1;
        }
    }

    private void giveMoney500kLv3() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "money give " + playerName + " 500000";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void giveNetheriteScrapLv3() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "give " + playerName + " netheriteingot 4";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void giveChiaveSpawnerLv3() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "crate key give " + playerName + " spawner 1";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void giveGoldenAppleLv3() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "give " + player.getName() + " goldenapple 10");
            }
        }
    }

    private void givePenitenzaNienteLv3() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "");
            }
        }
    }

    private void giveElytraLv3() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "give " + player.getName() + " elytra");
            }
        }
    }

    private void giveMuccaFungosaSpawnerLv3() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rosetacker give spawner " + player.getName() + " cow 1");
            }
        }
    }

    private void giveIronGolemSpawnerLv3() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rosestacker give spawner " + player.getName() + " iron_golem 1");
            }
        }
    }

    public void startPrizeLevel4Task() {

        countdownLevel4TaskPrize = new BukkitRunnable() {
            @Override
            public void run() {
                dropItemsLevel4();
            }
        }.runTaskTimer(this, 0L, 20L * 60L).getTaskId();
    }

    public void stopPrizeLevel4Task() {
        if (countdownLevel4TaskPrize != -1) {
            Bukkit.getScheduler().cancelTask(countdownLevel4TaskPrize);
            countdownLevel4TaskPrize = -1;
        }
    }

    public void startPrizeLevel5Task() {

        countdownLevel5TaskPrize = new BukkitRunnable() {
            @Override
            public void run() {
                dropItemsLevel5();
            }
        }.runTaskTimer(this, 0L, 20L * 60L).getTaskId();
    }

    public void stopPrizeLevel5Task() {
        if (countdownLevel5TaskPrize != -1) {
            Bukkit.getScheduler().cancelTask(countdownLevel5TaskPrize);
            countdownLevel5TaskPrize = -1;
        }
    }

    private void giveMoney1MLv5() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "money give " + playerName + " 1000000";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void giveNetheriteIngotLv5() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                String playerName = player.getName();
                String command = "give " + playerName + " netheriteingot 5";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void giveEndermanSpawnerLv5() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rosestacker give spawner " + player.getName() + " enderman 1");
            }
        }
    }

    private void giveKitMVPplusLv5() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        List<Player> playersInRegion = new ArrayList<>();

        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                playersInRegion.add(player);
            }
        }

        if (!playersInRegion.isEmpty()) {
            int randomIndex = random.nextInt(playersInRegion.size());
            Player luckyPlayer = playersInRegion.get(randomIndex);

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kits give mvp+ " + luckyPlayer.getName());


            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"broadcast !&#AB9230&lɢ&#AF9D2D&lᴏ&#B4A92A&lʟ&#B8B427&lᴅ&#CBCD1A&lᴇ&#DEE60D&lɴ &#D5DA14&lᴘ&#B8B427&lᴀ&#B4A92A&lʀ&#AF9D2D&lᴛ&#AB9230&lʏ&#AB9230&lɢ&#AF9D2D&lᴏ&#B4A92A&lʟ&#B8B427&lᴅ&#CBCD1A&lᴇ&#DEE60D&lɴ &#D5DA14&lᴘ&#B8B427&lᴀ&#B4A92A&lʀ&#AF9D2D&lᴛ&#AB9230&lʏ " + ChatColor.GREEN + "Il giocatore fortunato che ha ricevuto il kit MVP+ è: " + ChatColor.AQUA + luckyPlayer.getName());
        }
    }

    private void giveChiaveLeggendariaLv5() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (isInSpawnRegion(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key give " + player.getName() + " leggendaria 1");
            }
        }
    }



    @Override
    public void onDisable() {

        String green = "\u001B[38;2;15;252;3m";
        String yellow = "\u001B[38;2;255;255;0m";
        String red = "\u001B[38;2;255;0;0m";
        getLogger().info(red + "Disabilitazione plugin in corso...");
        getLogger().info("");
        getLogger().info("");
        getLogger().info("");
        getLogger().info("");

        boolean isHypePartyActive;
        try {
            isHypePartyActive = Database.isHypePartyActive();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (isHypePartyActive) {

            try {
                Database.setHypePartyActive(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        voteCounts.clear();
        DonationCounts.clear();

        String purple = "\u001B[38;2;128;0;128m";

        getLogger().info("");
        getLogger().info("");


        try {
            if (Database.getConnection() != null && !Database.getConnection().isClosed()) {
                Database.getConnection().close();
                getLogger().info("");
                getLogger().info(green + "Connessione al database chiusa correttamente.");
                getLogger().info("");
            }
        } catch (SQLException ex) {
            getLogger().info(red + "Errore nella chiusura della connessione al database");
            ex.printStackTrace();
        }

        getLogger().info("");
        getLogger().info("");
        getLogger().info(purple + "    ________  __              _                _    __      __     ");
        getLogger().info(purple + "   / ____/ /_/ /_  ___  _____(_)___  ____     | |  / /___  / /____ ");
        getLogger().info(purple + "  / __/ / __/ __ \\/ _ \\/ ___/ / __ \\/ __ \\    | | / / __ \\/ __/ _ \\");
        getLogger().info(purple + " / /___/ /_/ / / /  __/ /  / / /_/ / / / /    | |/ / /_/ / /_/  __/");
        getLogger().info(purple + "/_____/\\__/_/ /_/\\___/_/  /_/\\____/_/ /_/     |___/\\____/\\__/\\___/ ");
        getLogger().info(purple + "                                                                 ");
        getLogger().info("");
        getLogger().info("");
        getLogger().info("");
        getLogger().info("");
        getLogger().info(green + "Plugin disabilitato con successo!");
        getLogger().info("");
        getLogger().info("");

    }
}
