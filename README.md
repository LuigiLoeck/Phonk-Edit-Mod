# 💀 Phonk Edit Mod

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21-brightgreen.svg)](https://www.minecraft.net/)
[![Fabric API](https://img.shields.io/badge/Fabric%20API-Required-orange.svg)](https://modrinth.com/mod/fabric-api)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A Minecraft Fabric mod that recreates the viral **YouTube Shorts "Phonk Edit" meme**! Transform your gameplay into epic cinematic moments with automatic pauses, phonk music drops, visual effects, and camera shake - all synced to the beat! 🎵💥

![Banner](docs/images/banner.png)

## 🎬 Demo

![Demo](docs/videos/demo.gif)

*Watch the effect in action! The mod activates automatically during gameplay.*

## ✨ Features

### 🎬 **Automatic Meme Triggers**
- **Random Timer**: Activates every 30-60 seconds (configurable)
- **Action-Based Triggers** (30% chance, configurable):
  - Attack entities ⚔️
  - Break blocks ⛏️
  - Interact with blocks 🔧 (place, open chests, use doors, etc.)
  - Use items 🍖 (eat, drink, bow, potions, etc.)
  - Take damage 💔

### 🎵 **Audio System**
- **9 Phonk Sound Tracks** included
- **Random pitch variation** (0.2x - 2.0x speed)
- **Beat-synced effects** - faster music = more intense beats!

### 🎨 **Visual Effects**
- **Grayscale Filter** - Full B&W screen including HUD (Satin API)
- **Radial Blur** - Extreme distortion that pulses with beats
- **Beat-Synced Zoom** - Camera zooms in/out with music (1.0x → 1.3x)
- **Camera Shake** - Violent screen tremor, intensifies at end
- **Mobile Format** - Black bars on sides (9:16 aspect ratio)
- **Random Skull Overlay** - 10 colorful skull images
- **Meme Text** - 15 random phrases at top ("SIGMA GRINDSET", "COLD AS ICE", etc.)

![Effect Screenshot](docs/images/effect.png)
*The full effect in action - grayscale, blur, zoom, skull overlay, and meme text!*

### ⚙️ **Fully Configurable**
- **Config Menu** - Press `O` to open settings
- **Adjustable Parameters**:
  - Timer intervals (5-120s, 10-180s)
  - Effect duration (1-3 seconds)
  - Action trigger chance (0-100%)
  - Delay before activation (milliseconds)
  - Pitch range (0.2x - 2.0x)
  - Music volume (0-100%)
  - Icon size (16-128px)
  - Effect intensities (zoom/blur/shake: 0.5x-2.0x)
- **Toggle Switches** for triggers and individual effects
- Settings saved to `config/phonk-edit-mod.json`

![Config Menu](docs/images/craftingtable.png)
*Easy-to-use configuration menu - press `O` to customize everything!*

### 🎯 **Beat Synchronization**
The mod calculates BPM based on pitch and creates **rhythmic pulses**:
- Each beat triggers a **zoom + blur combo**
- Higher pitch = faster beats, more pulses
- Lower pitch = slower, heavier beats
- Final 10% = EXTREME shake and chaos

## 📦 Installation

### Requirements
- **Minecraft 1.21**
- **Fabric Loader 0.17.3+** ([Download](https://fabricmc.net/use/installer/))
- **Fabric API 0.102.0+** ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api) | [Modrinth](https://modrinth.com/mod/fabric-api))
- **Java 21**

### Steps
1. Install [Fabric Loader](https://fabricmc.net/use/installer/)
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) and place in `mods` folder
3. Download **Phonk Edit Mod** from [Releases](https://github.com/LuigiLoeck/Phonk-Edit-Mod/releases)
4. Place the mod JAR in your `.minecraft/mods` folder
5. Launch Minecraft with Fabric profile

## 🎮 Usage

### Automatic Mode
Just play normally! The mod will trigger randomly or when you perform actions.

### Configuration
1. Press **`O`** key to open config menu
2. Scroll through settings with mouse wheel
3. Adjust sliders and toggles
4. Settings save automatically

### Config File Location
`<minecraft_folder>/config/phonk-edit-mod.json`

## 🛠️ For Developers

### Building from Source
```bash
git clone https://github.com/LuigiLoeck/Phonk-Edit-Mod.git
cd phonk-edit-mod
./gradlew build
```
Built JAR will be in `build/libs/`

### Project Structure
```
src/
├── client/
│   ├── java/com/luigi/phonkeditmod/
│   │   ├── PhonkEditModClient.java    # Main client logic
│   │   ├── ConfigScreen.java          # GUI configuration
│   │   ├── ModConfig.java             # Config data model
│   │   ├── InvisiblePauseScreen.java  # Transparent pause
│   │   └── mixin/
│   │       ├── GameRendererMixin.java # Camera effects
│   │       └── CameraAccessor.java    # Camera access
│   └── resources/
│       └── assets/phonk-edit-mod/
│           ├── sounds/                 # 9 phonk tracks
│           ├── textures/gui/           # 10 skull images
│           └── shaders/                # Grayscale & blur shaders
└── main/
    ├── java/com/luigi/phonkeditmod/
    │   └── PhonkEditMod.java          # Common entry point
    └── resources/
        └── fabric.mod.json             # Mod metadata
```

### Dependencies
- **Fabric API** - Required
- **Satin API 2.0.0** - Shader management (bundled)

### Tech Stack
- **Mixins** - Camera manipulation (SpongePowered 0.8.7)
- **Shaders** - GLSL shaders for visual effects
- **Events** - ClientTick, HudRender, AttackEntity, BlockBreak, etc.
- **Concurrency** - ScheduledExecutorService for delays
- **JSON** - Gson for config persistence

## 📝 Changelog

### Version 1.0.0
- Initial release
- 9 phonk sound tracks
- 10 skull overlay images
- Grayscale shader with Satin API
- Beat-synced zoom and blur
- Camera shake effects
- Mobile format bars
- 15 random meme texts
- Full configuration system
- Action triggers with delays
- Pitch-based BPM calculation

## 📜 License

This project is licensed under the **MIT License** - see [LICENSE](LICENSE) file for details.

## 🤖 Development

This mod was developed using **vibe coding** with AI assistance! 🎵✨

**AI-Powered Development:**
- Developed with assistance from **GitHub Copilot** (Claude 3.5 Sonnet)
- Extensively tested and validated by the developer
- All code reviewed and optimized for performance and safety

**⚠️ Transparency & Trust:**
- ✅ Extensively tested in survival and creative modes
- ✅ No malicious code - **completely safe**
- ✅ Full source code available on [GitHub](https://github.com/LuigiLoeck/Phonk-Edit-Mod)
- ✅ If you don't trust AI-assisted code, **review the source yourself!**

The mod is open-source precisely so you can verify everything yourself. AI helped write the code, but I tested every feature thoroughly before release.

## 🙏 Credits

- **GitHub Copilot** (Claude 3.5 Sonnet) - AI coding assistance
- **Satin API** by Ladysnake - Shader management library
- **Fabric** - Modding framework
- **Phonk Music Genre** - For the vibes 🎵
- **YouTube Shorts Editors** - For the meme inspiration

## 📧 Support

- **Issues**: [GitHub Issues](https://github.com/LuigiLoeck/Phonk-Edit-Mod/issues)

---

<div align="center">

**Made with ❤️ by luigi**

*"SIGMA GRINDSET" - "COLD AS ICE ❄️" - "LITERALLY ME"*

[![GitHub](https://img.shields.io/github/stars/LuigiLoeck/Phonk-Edit-Mod?style=social)](https://github.com/LuigiLoeck/Phonk-Edit-Mod)

</div>
