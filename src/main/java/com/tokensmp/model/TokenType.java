package com.tokensmp.model;

import org.bukkit.Material;

import java.util.List;

public enum TokenType {
    EMBERFANG("emberfang", "§6Emberfang Token", "Mythic", false, Material.BLAZE_POWDER,
            List.of(
                    new AbilityDefinition("lava_lance", "Lava Lance", "Molten spear burst that pierces targets.", 18),
                    new AbilityDefinition("phoenix_bloom", "Phoenix Bloom", "Spiral flames erupt and ignite nearby enemies.", 28),
                    new AbilityDefinition("cinder_vault", "Cinder Vault", "Explosive leap with fiery landing shock.", 22)
            )),
    TIDECALLER("tidecaller", "§bTidecaller Token", "Epic", false, Material.HEART_OF_THE_SEA,
            List.of(
                    new AbilityDefinition("riptide_drive", "Riptide Drive", "Water jet dash that knocks enemies aside.", 15),
                    new AbilityDefinition("abyss_ring", "Abyss Ring", "Rotating tide ring damages and slows projectiles.", 25),
                    new AbilityDefinition("aqua_surge", "Aqua Surge", "Burst heal-wave for allies in range.", 30)
            )),
    STORMWEAVER("stormweaver", "§eStormweaver Token", "Legendary", false, Material.LIGHTNING_ROD,
            List.of(
                    new AbilityDefinition("arc_step", "Arc Step", "Blink forward in electric afterimages.", 14),
                    new AbilityDefinition("thunder_lattice", "Thunder Lattice", "Chain lightning web around target area.", 24),
                    new AbilityDefinition("skybreaker", "Skybreaker", "Summon concentrated strike from above.", 32)
            )),
    VOIDRENDER("voidrender", "§5Voidrender Token", "Mythic", false, Material.ENDER_EYE,
            List.of(
                    new AbilityDefinition("rift_slice", "Rift Slice", "Void crescents cut through multiple enemies.", 17),
                    new AbilityDefinition("graviton_well", "Graviton Well", "Mini singularity pulls and crushes foes.", 27),
                    new AbilityDefinition("phase_shroud", "Phase Shroud", "Temporal phasing reduces incoming damage.", 35)
            )),
    AURORABLOOM("aurorabloom", "§dAurorabloom Token", "Rare", false, Material.AMETHYST_SHARD,
            List.of(
                    new AbilityDefinition("prism_shot", "Prism Shot", "Colorburst shard volley.", 13),
                    new AbilityDefinition("lumen_field", "Lumen Field", "Radiant zone that repairs armor durability slightly.", 29),
                    new AbilityDefinition("dawn_chorus", "Dawn Chorus", "Harmonic pulse that weakens nearby hostiles.", 21)
            )),
    TITANGEAR("titangear", "§7Titangear Token", "Epic", false, Material.NETHERITE_SCRAP,
            List.of(
                    new AbilityDefinition("ram_piston", "Ram Piston", "Mechanical burst dash with stun impact.", 16),
                    new AbilityDefinition("bulwark_grid", "Bulwark Grid", "Deploy kinetic shield grid wall.", 30),
                    new AbilityDefinition("forge_hammer", "Forge Hammer", "Ground slam creating crackwave.", 26)
            )),
    SHADOWMINT("shadowmint", "§2Shadowmint Token", "Legendary", false, Material.ECHO_SHARD,
            List.of(
                    new AbilityDefinition("night_fork", "Night Fork", "Dual shadow trails confuse enemies.", 19),
                    new AbilityDefinition("smoke_mandala", "Smoke Mandala", "Ink bloom obscures enemy vision cone.", 23),
                    new AbilityDefinition("silent_verdict", "Silent Verdict", "Execute marked target under threshold health.", 36)
            )),
    // Admin tokens
    CELESTIAL_CROWN("celestial_crown", "§3Celestial Crown", "Admin", true, Material.NETHER_STAR,
            List.of(
                    new AbilityDefinition("star_edict", "Star Edict", "Admin-only cosmic command pulse.", 10),
                    new AbilityDefinition("orbit_lock", "Orbit Lock", "Admin-only orbital cage.", 10),
                    new AbilityDefinition("astral_reset", "Astral Reset", "Admin-only battlefield reset.", 10)
            )),
    CHRONO_SIGIL("chrono_sigil", "§9Chrono Sigil", "Admin", true, Material.CLOCK,
            List.of(
                    new AbilityDefinition("tick_fracture", "Tick Fracture", "Admin-only timeline fracture.", 10),
                    new AbilityDefinition("rewind_mesh", "Rewind Mesh", "Admin-only temporal rewind.", 10),
                    new AbilityDefinition("epoch_anchor", "Epoch Anchor", "Admin-only time anchor.", 10)
            )),
    OBSIDIAN_DECREE("obsidian_decree", "§8Obsidian Decree", "Admin", true, Material.RECOVERY_COMPASS,
            List.of(
                    new AbilityDefinition("blackwall", "Blackwall", "Admin-only terrain silence wall.", 10),
                    new AbilityDefinition("edict_crush", "Edict Crush", "Admin-only authority shockwave.", 10),
                    new AbilityDefinition("sovereign_null", "Sovereign Null", "Admin-only global null pulse.", 10)
            ));

    private final String id;
    private final String displayName;
    private final String rarity;
    private final boolean adminOnly;
    private final Material icon;
    private final List<AbilityDefinition> abilities;

    TokenType(String id, String displayName, String rarity, boolean adminOnly, Material icon, List<AbilityDefinition> abilities) {
        this.id = id;
        this.displayName = displayName;
        this.rarity = rarity;
        this.adminOnly = adminOnly;
        this.icon = icon;
        this.abilities = abilities;
    }

    public String id() { return id; }
    public String displayName() { return displayName; }
    public String rarity() { return rarity; }
    public boolean adminOnly() { return adminOnly; }
    public Material icon() { return icon; }
    public List<AbilityDefinition> abilities() { return abilities; }

    public static List<TokenType> normalTokens() {
        return List.of(EMBERFANG, TIDECALLER, STORMWEAVER, VOIDRENDER, AURORABLOOM, TITANGEAR, SHADOWMINT);
    }
}
