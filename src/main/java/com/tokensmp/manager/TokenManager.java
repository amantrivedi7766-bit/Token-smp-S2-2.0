package com.tokensmp.manager;

import com.tokensmp.model.TokenType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TokenManager {
    private final JavaPlugin plugin;
    private final Random random = new Random();
    private final Map<UUID, List<TokenType>> tokens = new HashMap<>();
    private final Map<UUID, TokenType> equipped = new HashMap<>();

    public TokenManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public TokenType rollNormalToken() {
        List<TokenType> normal = TokenType.normalTokens();
        return normal.get(random.nextInt(normal.size()));
    }

    public void grant(Player player, TokenType token) {
        tokens.computeIfAbsent(player.getUniqueId(), id -> new ArrayList<>()).add(token);
        equipped.putIfAbsent(player.getUniqueId(), token);
    }

    public boolean remove(Player player, TokenType token) {
        List<TokenType> list = tokens.getOrDefault(player.getUniqueId(), new ArrayList<>());
        boolean removed = list.remove(token);
        if (removed && Objects.equals(equipped.get(player.getUniqueId()), token)) {
            equipped.put(player.getUniqueId(), list.isEmpty() ? null : list.get(0));
        }
        return removed;
    }

    public List<TokenType> getTokens(Player player) {
        return Collections.unmodifiableList(tokens.getOrDefault(player.getUniqueId(), List.of()));
    }

    public Map<UUID, List<TokenType>> allTokens() {
        return Collections.unmodifiableMap(tokens);
    }

    public TokenType getEquipped(Player player) {
        return equipped.get(player.getUniqueId());
    }

    public void setEquipped(Player player, TokenType token) {
        if (getTokens(player).contains(token)) equipped.put(player.getUniqueId(), token);
    }

    public void save() {
        File file = new File(plugin.getDataFolder(), "players.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, List<TokenType>> entry : tokens.entrySet()) {
            List<String> ids = entry.getValue().stream().map(TokenType::name).toList();
            yaml.set("players." + entry.getKey() + ".tokens", ids);
            TokenType eq = equipped.get(entry.getKey());
            yaml.set("players." + entry.getKey() + ".equipped", eq == null ? null : eq.name());
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save players.yml: " + e.getMessage());
        }
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "players.yml");
        if (!file.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection sec = yaml.getConfigurationSection("players");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            List<String> raw = yaml.getStringList("players." + key + ".tokens");
            List<TokenType> list = raw.stream().map(s -> {
                try { return TokenType.valueOf(s); } catch (Exception e) { return null; }
            }).filter(Objects::nonNull).toList();
            tokens.put(uuid, new ArrayList<>(list));
            String eq = yaml.getString("players." + key + ".equipped");
            if (eq != null) {
                try { equipped.put(uuid, TokenType.valueOf(eq)); } catch (Exception ignored) {}
            }
        }
    }
}
