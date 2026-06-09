# CustomDespawn

A high-performance, completely silent administrative optimization utility for modern Minecraft server software. Engineered explicitly to override vanilla ground-item lifecycles via granular configuration profiles, reducing entity-tick lag and optimizing server MSPT without breaking core gameplay mechanics.

[![Modrinth](https://img.shields.io/badge/Modrinth-00AF5C?style=for-the-badge&logo=modrinth&logoColor=white)](https://modrinth.com/plugin/customdespawn)
[![BuyMeACoffee](https://img.shields.io/badge/Buy_Me_A_Coffee-FFDD00?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black)](https://buymeacoffee.com/muffdogsupreme)

---

## 🛠️ Feature Architecture

* **Native Paper Despawn Rate Injection (`ItemSpawnListener.java`):** Completely bypasses legacy Bukkit age-shifting math (`setTicksLived`) and the vanilla 5-minute hard limit. Leverages modern Paper API (`item.setDespawnRate(int)`) to apply true tick lifespans to item entities directly on spawn.
* **Granular Profile Matching Matrix:** Evaluates ground item materials from top to bottom against customizable configuration layers (`config.yml`). Allows separate lifespan settings for low-value trash items (e.g., 15s lifespans for cobblestone, netherrack, or dirt) while safely protecting high-value assets.
* **Persistent Death Drop Retention:** Automatically tracks player equipment drops during death events using strict metadata assignment loops. Increases the despawn threshold of authentic death item streams (defaulting to a generous 5-minute or longer buffer) so players can retrieve lost items without risking aggressive floor wipes.
* **Delayed Operator Welcome Feed (`PlayerJoinListener.java`):** Identifies incoming server operators (`player.isOp()`) and triggers a delayed message loop. Dispatches an optimization notification exactly 3 seconds (60 ticks) after login, ensuring the notice is clearly visible in the chat feed without getting buried during world loading.
* **Zero-Permission Background Execution:** Operates entirely behind the scenes via a streamlined listener engine. Contains no command mapping footprints, maintaining zero runtime overhead on standard player text routing or permission verification passes.
* **Decoupled Duration Parse Engine (`TimeParser.java`):** Includes a standalone text utility that converts clean human duration syntax (such as `15s`, `2m`, `8m`) into primitive integers (1s = 20t) on the fly. Built with robust configuration fallback parameters that throw clean `IllegalArgumentException` blocks on malformed inputs to protect boot chains from crashing.

---

## ⚙️ Environment Specs

### Compatible Loaders
* **PaperMC (1.21+)** — *Strictly required for native, un-clamped item entity despawn rate manipulation API access.*

### Core Command Matrix
* `/customdespawn reload` (Alias: `/cdespawn reload`) — *Reloads configuration rules dynamically without requiring a server reboot.*
* **Permission:** `customdespawn.reload` (Defaults strictly to server operators).

---

## 📦 Default Configuration (`config.yml`)

```yaml
default-despawn-time: "1m"
honor-player-death-drops: true
death-drop-despawn-time: "5m"
blacklisted-worlds:
  - "creative_world"
  - "test_sandbox"
custom-rules:
  trash-items:
    time: "15s"
    materials: [COBBLESTONE, NETHERRACK, DIRT, SAND, ROTTEN_FLESH, KELP]
  valuable-items:
    time: "8m"
    materials: [DIAMOND, NETHERITE_INGOT, ENCHANTED_GOLDEN_APPLE]
