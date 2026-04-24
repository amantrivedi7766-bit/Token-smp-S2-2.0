package com.tokensmp;

import com.tokensmp.ability.AbilityManager;
import com.tokensmp.command.SpinCommand;
import com.tokensmp.command.TokenInfoCommand;
import com.tokensmp.command.TokenSMPAdminCommand;
import com.tokensmp.command.TokensCommand;
import com.tokensmp.gui.GuiManager;
import com.tokensmp.manager.CooldownManager;
import com.tokensmp.manager.ServerStateManager;
import com.tokensmp.manager.SpinManager;
import com.tokensmp.manager.TokenManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TokenSMPPlugin extends JavaPlugin {
    private TokenManager tokenManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        tokenManager = new TokenManager(this);
        ServerStateManager serverStateManager = new ServerStateManager();
        CooldownManager cooldownManager = new CooldownManager();
        SpinManager spinManager = new SpinManager(this, tokenManager);
        GuiManager guiManager = new GuiManager(tokenManager, serverStateManager);
        AbilityManager abilityManager = new AbilityManager(this, cooldownManager, serverStateManager);

        getServer().getPluginManager().registerEvents(
                new TokenListener(this, tokenManager, spinManager, guiManager, abilityManager, serverStateManager), this);

        getCommand("tokens").setExecutor(new TokensCommand(guiManager));
        getCommand("tokeninfo").setExecutor(new TokenInfoCommand(guiManager));
        getCommand("spin").setExecutor(new SpinCommand(spinManager, getConfig().getBoolean("allow-manual-spin", true)));
        getCommand("tokensmp").setExecutor(new TokenSMPAdminCommand(this, tokenManager, spinManager, guiManager));
    }

    @Override
    public void onDisable() {
        if (tokenManager != null) tokenManager.save();
    }
}
