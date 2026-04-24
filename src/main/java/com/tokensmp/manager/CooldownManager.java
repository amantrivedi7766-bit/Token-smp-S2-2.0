package com.tokensmp.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public boolean isReady(Player player, String key) {
        long now = System.currentTimeMillis();
        return now >= cooldowns.getOrDefault(player.getUniqueId(), Map.of()).getOrDefault(key, 0L);
    }

    public long remainingSeconds(Player player, String key) {
        long end = cooldowns.getOrDefault(player.getUniqueId(), Map.of()).getOrDefault(key, 0L);
        return Math.max(0L, (end - System.currentTimeMillis()) / 1000L);
    }

    public void setCooldown(Player player, String key, int seconds) {
        cooldowns.computeIfAbsent(player.getUniqueId(), p -> new HashMap<>())
                .put(key, System.currentTimeMillis() + (seconds * 1000L));
    }

    public void clearPlayer(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
