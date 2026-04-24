package com.tokensmp.command;

import com.tokensmp.gui.GuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TokenInfoCommand implements CommandExecutor {
    private final GuiManager gui;

    public TokenInfoCommand(GuiManager gui) {
        this.gui = gui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) gui.openTokenInfo(player);
        return true;
    }
}
