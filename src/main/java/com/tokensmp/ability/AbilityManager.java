package com.tokensmp.ability;

import com.tokensmp.manager.CooldownManager;
import com.tokensmp.manager.ServerStateManager;
import com.tokensmp.model.AbilityDefinition;
import com.tokensmp.model.TokenType;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityManager {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;
    private final ServerStateManager stateManager;
    private final Map<Player, BukkitTask> indicatorTasks = new HashMap<>();

    public AbilityManager(JavaPlugin plugin, CooldownManager cooldownManager, ServerStateManager stateManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.stateManager = stateManager;
    }

    public void execute(Player player, TokenType token, int abilityIndex) {
        if (!stateManager.isEnabled(token)) {
            player.sendMessage("§cThat token is currently disabled by admin.");
            return;
        }
        if (abilityIndex < 0 || abilityIndex >= token.abilities().size()) return;
        AbilityDefinition ability = token.abilities().get(abilityIndex);
        String key = token.id() + ":" + ability.id();
        if (!cooldownManager.isReady(player, key)) {
            player.sendActionBar("§c" + ability.name() + " cooldown: " + cooldownManager.remainingSeconds(player, key) + "s");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, .5f);
            return;
        }

        castEffect(player, token, abilityIndex);
        cooldownManager.setCooldown(player, key, ability.cooldownSeconds());
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.6f);
        startIndicator(player, token);
    }

    private void castEffect(Player player, TokenType token, int index) {
        switch (index) {
            case 0 -> linearBurst(player, token);
            case 1 -> ringBurst(player, token);
            case 2 -> apexMove(player, token);
        }
    }

    private Particle tokenParticle(TokenType token) {
        return switch (token) {
            case EMBERFANG -> Particle.FLAME;
            case TIDECALLER -> Particle.SPLASH;
            case STORMWEAVER -> Particle.ELECTRIC_SPARK;
            case VOIDRENDER -> Particle.PORTAL;
            case AURORABLOOM -> Particle.END_ROD;
            case TITANGEAR -> Particle.CRIT;
            case SHADOWMINT -> Particle.SMOKE;
            default -> Particle.ENCHANT;
        };
    }

    private void linearBurst(Player player, TokenType token) {
        Location origin = player.getEyeLocation();
        Vector dir = origin.getDirection().normalize();
        World world = player.getWorld();
        for (double i = 1; i <= 10; i += 0.6) {
            Location p = origin.clone().add(dir.clone().multiply(i));
            world.spawnParticle(tokenParticle(token), p, 8, 0.12, 0.12, 0.12, 0.01);
            for (LivingEntity e : world.getLivingEntities()) {
                if (e == player) continue;
                if (e.getLocation().distanceSquared(p) < 1.1) e.damage(3.5, player);
            }
        }
        world.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1.25f);
    }

    private void ringBurst(Player player, TokenType token) {
        World world = player.getWorld();
        Location c = player.getLocation().add(0, 1, 0);
        new BukkitRunnable() {
            double radius = 1.0;
            @Override
            public void run() {
                radius += .35;
                for (double t = 0; t < Math.PI * 2; t += Math.PI / 12) {
                    Location p = c.clone().add(Math.cos(t) * radius, 0.15, Math.sin(t) * radius);
                    world.spawnParticle(tokenParticle(token), p, 3, 0.02, 0.02, 0.02, 0.005);
                }
                for (LivingEntity e : world.getLivingEntities()) {
                    if (e == player) continue;
                    if (e.getLocation().distanceSquared(c) < radius * radius + 0.5) {
                        e.damage(1.5, player);
                        e.setVelocity(e.getVelocity().add(e.getLocation().toVector().subtract(c.toVector()).normalize().multiply(0.12)));
                    }
                }
                if (radius > 5.2) cancel();
            }
        }.runTaskTimer(plugin, 0L, 2L);
        world.playSound(c, Sound.ENTITY_WARDEN_SONIC_BOOM, .6f, 1.6f);
    }

    private void apexMove(Player player, TokenType token) {
        Location start = player.getLocation();
        player.setVelocity(start.getDirection().normalize().multiply(1.2).setY(0.6));
        World world = player.getWorld();
        new BukkitRunnable() {
            int ticks;
            @Override
            public void run() {
                ticks++;
                world.spawnParticle(tokenParticle(token), player.getLocation().add(0, 0.3, 0), 18, .25, .05, .25, 0.01);
                if (player.isOnGround() || ticks > 20) {
                    Location land = player.getLocation();
                    world.spawnParticle(Particle.EXPLOSION, land, 1, .1, .1, .1, 0.01);
                    for (LivingEntity e : world.getLivingEntities()) {
                        if (e == player) continue;
                        if (e.getLocation().distanceSquared(land) < 9) {
                            e.damage(5, player);
                            e.setVelocity(e.getLocation().toVector().subtract(land.toVector()).normalize().multiply(0.35).setY(0.4));
                        }
                    }
                    world.playSound(land, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.9f);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void startIndicator(Player player, TokenType token) {
        indicatorTasks.computeIfPresent(player, (p, task) -> { task.cancel(); return null; });
        indicatorTasks.put(player, new BukkitRunnable() {
            int t = 0;
            @Override
            public void run() {
                t++;
                List<AbilityDefinition> abilities = token.abilities();
                StringBuilder bar = new StringBuilder("§b" + token.displayName() + " §8| ");
                for (AbilityDefinition ability : abilities) {
                    String key = token.id() + ":" + ability.id();
                    if (cooldownManager.isReady(player, key)) {
                        bar.append("§a").append(ability.name()).append(" §8• ");
                    } else {
                        bar.append("§c").append(ability.name()).append("(").append(cooldownManager.remainingSeconds(player, key)).append("s) §8• ");
                    }
                }
                player.sendActionBar(bar.toString());
                if (t > 120 || !player.isOnline()) {
                    cancel();
                    indicatorTasks.remove(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L));
    }
}
