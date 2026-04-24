package com.tokensmp.manager;

import com.tokensmp.model.TokenType;
import com.tokensmp.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpinManager {
    private final JavaPlugin plugin;
    private final TokenManager tokenManager;

    public SpinManager(JavaPlugin plugin, TokenManager tokenManager) {
        this.plugin = plugin;
        this.tokenManager = tokenManager;
    }

    public void spin(Player player) {
        List<TokenType> options = new ArrayList<>(TokenType.normalTokens());
        Inventory inv = Bukkit.createInventory(null, 27, "§0§lToken Spin");
        player.openInventory(inv);

        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                tick++;
                Collections.shuffle(options);
                for (int i = 10; i <= 16; i++) {
                    TokenType token = options.get(i % options.size());
                    ItemStack item = ItemFactory.make(token.icon(), token.displayName(), List.of("§7Rarity: §f" + token.rarity()));
                    inv.setItem(i, item);
                }

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.8f, 1.2f + (tick * 0.02f));
                player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1, 0), 15, 0.4, 0.4, 0.4, 0.02);

                if (tick >= 30) {
                    TokenType winner = tokenManager.rollNormalToken();
                    tokenManager.grant(player, winner);
                    player.closeInventory();
                    player.sendTitle("§6§lTOKEN UNLOCKED", winner.displayName(), 10, 60, 20);
                    player.sendMessage("§aYou received: " + winner.displayName() + " §7[" + winner.rarity() + "]");
                    player.sendActionBar("§eUnlocked: " + winner.displayName());
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
