package com.tokensmp.command;

import com.tokensmp.manager.SpinManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpinCommand implements CommandExecutor {
    private final SpinManager spinManager;
    private final boolean allowManual;

    public SpinCommand(SpinManager spinManager, boolean allowManual) {
        this.spinManager = spinManager;
        this.allowManual = allowManual;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!allowManual) {
            player.sendMessage("§cManual spin is disabled.");
            return true;
        }
        if (!player.hasPermission("tokensmp.spin.manual")) {
            player.sendMessage("§cNo permission.");
            return true;
        }
        spinManager.spin(player);
        return true;
    }
}
