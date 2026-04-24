# TokenSMP (Paper/Spigot 1.21.4)

Advanced Token SMP plugin with premium spin animation, 10-token ecosystem, and GUI-first admin control.

## Core design
- **Total tokens: 10**
  - **7 normal tokens** (spin rewards, duplicates allowed)
  - **3 admin-only tokens** (visible in info GUI, never in spin pool)
- **3 unique abilities per token** with independent cooldowns and actionbar indicators.
- **Join spin system** with animated GUI, particles, sounds, title/subtitle reveal.
- **Admin control panel GUI** for give/remove/spin, token ledger, bounty setting, token toggles.

## Commands
### Player
- `/tokens` — open your token collection and equip token.
- `/tokeninfo` — browse all 10 tokens and all abilities.
- `/spin` — run manual spin when enabled by config.

### Admin
- `/tokensmp reload`
- `/tokensmp give <player> <token_enum>`
- `/tokensmp remove <player> <token_enum>`
- `/tokensmp gui`

## Ability controls
- **Left click** → ability #1
- **Right click** → ability #2
- **Swap hand key (F)** → ability #3

## Build
```bash
mvn package
```

GitHub Actions workflow is included at `.github/workflows/build.yml` and uploads the built jar as artifact.
