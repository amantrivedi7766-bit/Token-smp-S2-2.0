package com.tokensmp.command;

import com.tokensmp.gui.GuiManager;
import com.tokensmp.manager.SpinManager;
import com.tokensmp.manager.TokenManager;
import com.tokensmp.model.TokenType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TokenSMPAdminCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final TokenManager tokenManager;
    private final SpinManager spinManager;
    private final GuiManager gui;

    public TokenSMPAdminCommand(JavaPlugin plugin, TokenManager tokenManager, SpinManager spinManager, GuiManager gui) {
        this.plugin = plugin;
        this.tokenManager = tokenManager;
        this.spinManager = spinManager;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tokensmp.admin")) {
            sender.sendMessage("§cAdmin only.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§e/tokensmp reload|give|remove|gui");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reloadConfig();
                sender.sendMessage("§aTokenSMP reloaded.");
            }
            case "gui" -> {
                if (sender instanceof Player player) gui.openAdminPanel(player);
            }
            case "give" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /tokensmp give <player> <token>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage("§cPlayer offline.");
                    return true;
                }
                try {
                    TokenType token = TokenType.valueOf(args[2].toUpperCase());
                    tokenManager.grant(target, token);
                    sender.sendMessage("§aGranted " + token.displayName() + " §ato " + target.getName());
                } catch (Exception ex) {
                    sender.sendMessage("§cInvalid token enum name.");
                }
            }
            case "remove" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /tokensmp remove <player> <token>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage("§cPlayer offline.");
                    return true;
                }
                try {
                    TokenType token = TokenType.valueOf(args[2].toUpperCase());
                    sender.sendMessage(tokenManager.remove(target, token) ? "§aToken removed." : "§cToken not found.");
                } catch (Exception ex) {
                    sender.sendMessage("§cInvalid token enum name.");
                }
            }
            case "spin" -> {
                if (args.length < 2) return true;
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target != null) spinManager.spin(target);
            }
        }
        return true;
    }
}
