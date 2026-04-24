package com.tokensmp;

import com.tokensmp.ability.AbilityManager;
import com.tokensmp.gui.GuiManager;
import com.tokensmp.manager.ServerStateManager;
import com.tokensmp.manager.SpinManager;
import com.tokensmp.manager.TokenManager;
import com.tokensmp.model.TokenType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class TokenListener implements Listener {
    private final TokenSMPPlugin plugin;
    private final TokenManager tokenManager;
    private final SpinManager spinManager;
    private final GuiManager gui;
    private final AbilityManager abilityManager;
    private final ServerStateManager state;

    public TokenListener(TokenSMPPlugin plugin, TokenManager tokenManager, SpinManager spinManager, GuiManager gui, AbilityManager abilityManager, ServerStateManager state) {
        this.plugin = plugin;
        this.tokenManager = tokenManager;
        this.spinManager = spinManager;
        this.gui = gui;
        this.abilityManager = abilityManager;
        this.state = state;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean("spin-on-join", true)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> spinManager.spin(event.getPlayer()), 30L);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        TokenType token = tokenManager.getEquipped(player);
        if (token == null) return;
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            abilityManager.execute(player, token, 0);
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            abilityManager.execute(player, token, 1);
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        TokenType token = tokenManager.getEquipped(event.getPlayer());
        if (token == null) return;
        event.setCancelled(true);
        abilityManager.execute(event.getPlayer(), token, 2);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (title.equals(GuiManager.PLAYER_TOKENS)) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) return;
            for (TokenType token : tokenManager.getTokens(player)) {
                if (clicked.getItemMeta().getDisplayName().equals(token.displayName())) {
                    tokenManager.setEquipped(player, token);
                    player.sendMessage("§aEquipped " + token.displayName());
                    gui.openPlayerTokens(player);
                    return;
                }
            }
        }

        if (title.equals(GuiManager.TOKEN_INFO) || title.equals("§0Server Token Ledger")) {
            event.setCancelled(true);
        }

        if (title.equals(GuiManager.ADMIN_PANEL)) {
            event.setCancelled(true);
            if (!player.hasPermission("tokensmp.admin")) return;
            if (event.getCurrentItem() == null) return;
            switch (event.getSlot()) {
                case 10 -> gui.openPlayerSelect(player, "§aGive Token");
                case 12 -> gui.openPlayerSelect(player, "§cRemove Token");
                case 14 -> gui.openPlayerSelect(player, "§bTrigger Spin");
                case 16 -> gui.openServerLedger(player);
                case 28 -> gui.openPlayerSelect(player, "§6Set Bounty");
                case 30 -> gui.openTokenInfo(player);
                case 32 -> {
                    player.sendMessage("§eCooldown status panel is actionbar-driven. Use /tokens to monitor.");
                    player.closeInventory();
                }
                case 34 -> gui.openTokenInfo(player);
            }
        }

        if (title.contains(GuiManager.PLAYER_SELECT)) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
            String name = event.getCurrentItem().getItemMeta().getDisplayName().replace("§f", "");
            Player target = Bukkit.getPlayerExact(name);
            if (target == null) return;
            if (title.startsWith("§aGive Token")) {
                tokenManager.grant(target, tokenManager.rollNormalToken());
                player.sendMessage("§aToken granted to " + target.getName());
            } else if (title.startsWith("§cRemove Token")) {
                TokenType eq = tokenManager.getEquipped(target);
                if (eq != null) {
                    tokenManager.remove(target, eq);
                    player.sendMessage("§aRemoved equipped token from " + target.getName());
                }
            } else if (title.startsWith("§bTrigger Spin")) {
                spinManager.spin(target);
                player.sendMessage("§aForced spin for " + target.getName());
            } else if (title.startsWith("§6Set Bounty")) {
                state.setBounty(target.getUniqueId(), 1000);
                player.sendMessage("§6Bounty of 1000 set on " + target.getName());
            }
            player.closeInventory();
        }
    }
}
