package com.monkey.acromvote;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VoteGUI implements Listener {

    private boolean isVoteGUIOpen = false;
    private final JavaPlugin plugin;
    private final List<ChatColor> glassColors = Arrays.asList(
            ChatColor.RED, ChatColor.GREEN, ChatColor.BLUE, ChatColor.YELLOW, ChatColor.AQUA,
            ChatColor.LIGHT_PURPLE, ChatColor.GOLD, ChatColor.GRAY);

    public VoteGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, ChatColor.DARK_PURPLE + "Vote Server");

        ItemStack voteIcon = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta voteIconMeta = voteIcon.getItemMeta();
        voteIconMeta.setDisplayName(ChatColor.YELLOW + "Clicca qui per votare il server e ottenere fantastici premi!");
        voteIcon.setItemMeta(voteIconMeta);

        gui.setItem(13, voteIcon);

        Random random = new Random();
        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                ItemStack glassPanel = getRandomColoredGlass();
                gui.setItem(i, glassPanel);
            }
        }

        player.openInventory(gui);
        startPanelColorChangeEffect(player, gui);
    }

    private ItemStack getRandomColoredGlass() {
        ChatColor randomColor = glassColors.get(new Random().nextInt(glassColors.size()));

        ItemStack glassPanel = new ItemStack(Material.valueOf("LEGACY_STAINED_GLASS_PANE"), 1, (short) randomColor.ordinal());
        ItemMeta glassPanelMeta = glassPanel.getItemMeta();
        glassPanelMeta.setDisplayName(randomColor + "play.acrom.it");
        glassPanel.setItemMeta(glassPanelMeta);

        return glassPanel;
    }

    private void startPanelColorChangeEffect(Player player, Inventory gui) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                    for (int i = 0; i < 27; i++) {
                        if (i != 13) {
                            ItemStack glassPanel = getRandomColoredGlass();
                            gui.setItem(i, glassPanel);
                        }
                    }
                    player.updateInventory();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }


    public void disableItemInteraction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String inventoryTitle = event.getView().getTitle();

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
            if (inventoryTitle.equals(ChatColor.DARK_PURPLE + "Vote Server")) {
                event.setCancelled(true);
                player.updateInventory();
                if (event.getSlot() == 13) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "Puoi votare per noi sui seguenti siti:\n" + ChatColor.AQUA + "https://minecraft-italia.net/lista/server/acrom");
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Player) {
            disableItemInteraction(event);
        }
    }

}



