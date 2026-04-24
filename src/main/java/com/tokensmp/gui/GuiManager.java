package com.tokensmp.gui;

import com.tokensmp.manager.ServerStateManager;
import com.tokensmp.manager.TokenManager;
import com.tokensmp.model.TokenType;
import com.tokensmp.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiManager {
    public static final String PLAYER_TOKENS = "§0§lYour Tokens";
    public static final String TOKEN_INFO = "§0§lToken Encyclopedia";
    public static final String ADMIN_PANEL = "§4§lTokenSMP Admin";
    public static final String PLAYER_SELECT = "§0Select Player";

    private final TokenManager tokenManager;
    private final ServerStateManager stateManager;

    public GuiManager(TokenManager tokenManager, ServerStateManager stateManager) {
        this.tokenManager = tokenManager;
        this.stateManager = stateManager;
    }

    public void openPlayerTokens(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, PLAYER_TOKENS);
        List<TokenType> tokens = tokenManager.getTokens(player);
        for (int i = 0; i < Math.min(tokens.size(), 45); i++) {
            TokenType token = tokens.get(i);
            inv.setItem(i, ItemFactory.make(token.icon(), token.displayName(), List.of(
                    "§7Rarity: §f" + token.rarity(),
                    "§7Abilities:",
                    "§fLMB §8→ §b" + token.abilities().get(0).name(),
                    "§fRMB §8→ §b" + token.abilities().get(1).name(),
                    "§fF-Key §8→ §b" + token.abilities().get(2).name(),
                    "§aClick to equip"
            )));
        }
        inv.setItem(49, ItemFactory.make(Material.BOOK, "§eActive: §f" + (tokenManager.getEquipped(player) == null ? "None" : tokenManager.getEquipped(player).displayName()), List.of("§7Choose a token to equip")));
        player.openInventory(inv);
    }

    public void openTokenInfo(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TOKEN_INFO);
        int i = 0;
        for (TokenType token : TokenType.values()) {
            List<String> lore = new ArrayList<>();
            lore.add("§7Rarity: §f" + token.rarity());
            lore.add(token.adminOnly() ? "§cAdmin Exclusive" : "§aObtainable via spin");
            lore.add("§7Enabled: " + (stateManager.isEnabled(token) ? "§aYes" : "§cNo"));
            lore.add("§8");
            token.abilities().forEach(a -> lore.add("§b• " + a.name() + " §7(" + a.cooldownSeconds() + "s)"));
            inv.setItem(i++, ItemFactory.make(token.icon(), token.displayName(), lore));
        }
        player.openInventory(inv);
    }

    public void openAdminPanel(Player admin) {
        Inventory inv = Bukkit.createInventory(null, 45, ADMIN_PANEL);
        inv.setItem(10, ItemFactory.make(Material.PLAYER_HEAD, "§aGive Token", List.of("§7One-click grant flow")));
        inv.setItem(12, ItemFactory.make(Material.BARRIER, "§cRemove Token", List.of("§7One-click remove flow")));
        inv.setItem(14, ItemFactory.make(Material.NAUTILUS_SHELL, "§bTrigger Spin", List.of("§7Force spin to target player")));
        inv.setItem(16, ItemFactory.make(Material.PAPER, "§eView All Player Tokens", List.of("§7Server-wide token ledger")));
        inv.setItem(28, ItemFactory.make(Material.TARGET, "§6Set Bounty", List.of("§7Set bounty instantly from GUI")));
        inv.setItem(30, ItemFactory.make(Material.LEVER, "§dEnable/Disable Tokens", List.of("§7Toggle token usability")));
        inv.setItem(32, ItemFactory.make(Material.CLOCK, "§9Cooldown Console", List.of("§7Monitor/clear combat cooldown states")));
        inv.setItem(34, ItemFactory.make(Material.NETHER_STAR, "§fToken Encyclopedia", List.of("§7Quick browse all 10 tokens")));
        admin.openInventory(inv);
    }

    public void openPlayerSelect(Player admin, String actionTitle) {
        Inventory inv = Bukkit.createInventory(null, 54, actionTitle + " » " + PLAYER_SELECT);
        int i = 0;
        for (Player online : Bukkit.getOnlinePlayers()) {
            inv.setItem(i++, ItemFactory.make(Material.PLAYER_HEAD, "§f" + online.getName(), List.of(
                    "§7Tokens: §f" + tokenManager.getTokens(online).size(),
                    "§7Bounty: §6" + stateManager.getBounty(online.getUniqueId())
            )));
            if (i >= 54) break;
        }
        admin.openInventory(inv);
    }

    public void openServerLedger(Player admin) {
        Inventory inv = Bukkit.createInventory(null, 54, "§0Server Token Ledger");
        int i = 0;
        for (Map.Entry<UUID, List<TokenType>> e : tokenManager.allTokens().entrySet()) {
            String name = Bukkit.getOfflinePlayer(e.getKey()).getName();
            inv.setItem(i++, ItemFactory.make(Material.WRITABLE_BOOK, "§f" + (name == null ? e.getKey() : name), List.of(
                    "§7Token Count: §f" + e.getValue().size(),
                    "§8" + e.getValue().stream().map(TokenType::displayName).limit(4).toList()
            )));
            if (i >= 54) break;
        }
        admin.openInventory(inv);
    }
}
