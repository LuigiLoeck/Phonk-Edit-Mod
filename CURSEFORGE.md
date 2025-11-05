# CURSEFORGE DESCRIPTION

Transform your Minecraft gameplay into viral YouTube Shorts content! 💀🎵

## What is this?

Phonk Edit Mod recreates the legendary **"Phonk Edit" meme** that's everywhere on YouTube Shorts and TikTok. You know the one - where the video suddenly pauses, phonk music drops, everything goes black & white, and a skull appears with crazy camera effects? Yeah, THAT one!

Fully Customizable! Press **O** to open config menu!

Now it happens IN YOUR GAME! 🔥

![Banner](https://cdn.modrinth.com/data/cached_images/9c0c8db47da4f72bf8733ca69376a0de071beda4_0.webp)

## 🎬 See It In Action

![Demo](https://github.com/LuigiLoeck/Phonk-Edit-Mod/raw/prod/docs/videos/demo.gif)

![Effect in game](https://cdn.modrinth.com/data/cached_images/e0939d7f081f060b7212b04bc2850fa2e1a6f572_0.webp)

---

## ✨ Features

### 🖼️ **Custom Resources** ✨ NEW!

#### Custom Images
- **Add your own images** - Place PNG files in `.minecraft/phonk-edit-mod/custom_images/`
- **Hot-Reload** - Load new images without restarting the game!
- **3 Modes**:
  - **Mod Only**: Uses the 10 included skull images
  - **Mix**: Randomly alternates between mod and custom images
  - **Custom Only**: Uses only your custom images
- **Easy Access** - "Open Images Folder" button in config menu
- **Error Detection** - Toast notifications for invalid files (non-PNG)

#### Custom Audio
- **Add your own audio** - Create a resource pack with custom phonk songs!
- **Resource Pack Based** - Uses Minecraft's native sound system (professional approach)
- **Auto-Generated Pack**: Tutorial resource pack created at `resourcepacks/PhonkEdit-CustomSongs/`
- **3 Modes**:
  - **Mod Only**: Uses the 9 included phonk tracks
  - **Mix**: Randomly alternates between mod and custom audio
  - **Custom Only**: Uses only your custom audio from resource packs
- **Hot-Reload** - Press F3+T or click "Reload Custom Files" button
- **Error Detection** - Toast notifications for invalid audio files (non-OGG)
- **Requirements**: 
  - OGG Vorbis format only
  - Registered in resource pack's `sounds.json` under `"custom/"` keys

### 🎵 **Epic Audio System**
- **9 Phonk Tracks** included (royalty-free)
- Random pitch variation (0.2x - 2.0x speed)
- **Beat-synced effects** - music drives the visuals!

### 🎬 **Cinematic Effects**
- ⚫ **Full Grayscale Filter** - Everything goes B&W (including HUD!)
- 💥 **Radial Blur** - Screen distorts with each beat
- 🔍 **Dynamic Zoom** - Camera pulses (1.0x → 1.3x)
- 📳 **Camera Shake** - Violent screen tremor
- 📱 **Mobile Format** - Black bars on sides (TikTok style)
- 💀 **10 Skull Overlays** - Random colorful skulls
- 📝 **Meme Text** - "SIGMA GRINDSET", "COLD AS ICE", etc.

### ⚡ **Smart Triggers**
The effect activates automatically when you:
- ⚔️ **Attack** an entity (mobs, players)
- ⛏️ **Break** a block
- 🔧 **Interact with blocks** (place, open chests, use doors, etc.)
- 🍖 **Use items** (eat, drink, bow, potions, etc.)
- 💔 **Take damage**
- ⏰ Or just wait (random timer: 30-60s)

### ⚙️ **Fully Customizable**
Press **O** to open config menu:
- Timer intervals (5-180 seconds)
- Effect duration (1-3 seconds)  
- Trigger chance (0-100%)
- Pitch range (0.2x - 2.0x)
- Icon size (16-128px)
- Music volume (0-100%)
- Effect intensities (zoom/blur/shake: 0.5x-2.0x)
- **Image Mode** (Mod Only / Mix / Custom Only) ✨ NEW!
- **Audio Mode** (Mod Only / Mix / Custom Only) ✨ NEW!
- Toggle each trigger ON/OFF
- **Quick Access Buttons**:
  - Open custom images folder
  - Open custom audio folder (resource pack)
  - Reload custom files without restarting

---

## 🎯 How It Works

### Beat Synchronization
The mod calculates BPM from the music pitch:
- **High pitch** (2.0x) = Fast beats, rapid pulses
- **Normal pitch** (1.0x) = Medium pace
- **Low pitch** (0.2x) = Slow, heavy beats

Each beat triggers a **BOOM** effect:
- Zoom pulses in/out
- Blur radiates from center
- Shake intensifies
- Perfect sync with music! 🎶

### Final Chaos (Last 10%)
The last 10% of the effect goes CRAZY:
- Shake becomes **VIOLENT**
- Effects stack and intensify
- Maximum chaos before it ends!

---

## 📦 Requirements

**REQUIRED:**
- Minecraft **1.21**
- Fabric Loader **0.17.3+**
- Fabric API **0.102.0+**
- Java **21**

**INCOMPATIBLE WITH:**
- OptiFine (use Sodium instead)
- Forge (this is Fabric-only)

---

## 🎮 Usage

### Automatic Mode
Just play! The mod triggers automatically.

### Manual Config
1. Press **O** key
2. Adjust settings
3. Save (automatic)

### Config Location
`.minecraft/config/phonk-edit-mod.json`

---

## 🎨 Technical Details

### Performance
- Lightweight (no performance impact)
- GPU shaders (requires decent graphics)
- Tested on mid-range hardware

### Multiplayer
- **Client-side only** (works on servers!)
- Only you see the effect
- No server installation needed

### Shaders
Uses **Satin API** for post-processing:
- Grayscale shader (full-screen B&W)
- Radial blur shader (beat-synced)
- Optimized GLSL code

---

## 📋 Changelog

### Version 1.1.0 (Current)
**Custom Resources Update** ✨
- ✅ Added custom image support (PNG files)
- ✅ Added custom audio support via resource packs (OGG Vorbis)
- ✅ Auto-generated tutorial resource pack
- ✅ Resource pack auto-activation system
- ✅ Hot-reload functionality (F3+T or button in config)
- ✅ Toast notifications for loaded resources
- ✅ Error detection for invalid files (non-PNG/non-OGG)
- ✅ Image Mode selector (Mod Only / Mix / Custom Only)
- ✅ Audio Mode selector (Mod Only / Mix / Custom Only)
- ✅ "Open Images Folder" button in config
- ✅ "Open Audio Folder" button in config (opens resource pack)
- ✅ Achievement-style toast notifications
- ✅ Configurable effect intensities

**Initial Release Features**
- 9 built-in phonk tracks
- 10 skull overlay images
- Beat-synchronized effects (zoom, blur, shake)
- Grayscale filter
- Mobile format black bars
- Meme text overlays
- Smart trigger system
- Fully configurable (Press O)
- Client-side only (works on servers)

---

## 🚀 Planned Features

### Next Update
- 🎯 **Minecraft 1.21.10 Support** - Port mod to latest version

### Future Ideas
- Custom meme text support
- More built-in phonk tracks
- Effect presets (Chill / Normal / Extreme)
- Custom trigger keybind
- Replay system (record and replay effects)

---

## 🤝 Support

- **Issues**: [GitHub Issues](https://github.com/luigi/phonk-edit-mod/issues)
- **Source Code**: [GitHub](https://github.com/luigi/phonk-edit-mod)

---

## 🤖 Development & Trust

**AI-Assisted Development:**
This mod was created using **vibe coding** with **GitHub Copilot** (Claude 3.5 Sonnet)! 🎵✨

**⚠️ Safety & Transparency:**
- ✅ Extensively tested - no bugs or crashes
- ✅ Completely safe - no malicious code
- ✅ Full source code on [GitHub](https://github.com/LuigiLoeck/Phonk-Edit-Mod)
- ✅ Don't trust AI? **Review the code yourself!**

I used AI to accelerate development, but every feature was tested thoroughly. The mod is open-source so you can verify everything yourself if you're concerned about AI-generated code.

## 📜 Credits

- **GitHub Copilot** (Claude 3.5 Sonnet) - AI coding assistance
- **Satin API** by Ladysnake - Shader library
- **Fabric Team** - Modding framework
- **You!** - For downloading ❤️

---

<div align="center">

**"SIGMA GRINDSET" 💀**

Made with ❤️ by luigi

</div>

---

## 🔖 Tags

`meme` `phonk` `edit` `youtube-shorts` `tiktok` `visual-effects` `shaders` `client-side` `configurable` `fabric`
