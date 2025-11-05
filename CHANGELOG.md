# Changelog

All notable changes to Phonk Edit Mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2025-11-05

### Added
- **Custom Resources System** 🎨
  - **Custom Images Support**:
    - Add your own PNG images to `.minecraft/phonk-edit-mod/custom_images/`
    - Hot-reload functionality (F3+T or reload button)
    - Image Mode selector: Mod Only / Mix / Custom Only
    - "Open Images Folder" button in config menu
    - Error detection for invalid image formats (non-PNG files)
  - **Custom Audio Support**:
    - Resource pack-based audio system (professional approach)
    - Auto-generated tutorial pack at `resourcepacks/PhonkEdit-CustomSongs/`
    - Audio Mode selector: Mod Only / Mix / Custom Only
    - "Open Audio Folder" button in config menu (opens resource pack folder)
    - Error detection for invalid audio formats (non-OGG files)
    - OGG Vorbis format requirement
    - sounds.json integration with `"custom/"` namespace
  - **Toast Notifications**:
    - Achievement-style toast notifications (non-invasive)
    - Success toasts: "X audios loaded", "X images loaded"
    - Error toasts: "X invalid audio (OGG only!)", "X invalid images (PNG only!)"
    - Automatic resource pack activation on first launch
  - **Reload System**:
    - "Reload Custom Files" button in config menu
    - F3+T keyboard shortcut support
    - Detects and counts valid/invalid files
    - Smart toast sequencing (200ms delays to prevent overlap)

## [1.0.0] - 2025-10-21

### Added
- Initial release of Phonk Edit Mod
- **Audio System**:
  - 9 phonk music tracks with random selection
  - Pitch variation system (0.2x - 2.0x speed range)
  - Beat-synchronized effects based on pitch/BPM
- **Visual Effects**:
  - Full-screen grayscale shader (B&W filter) using Satin API
  - Radial blur shader with beat-synced pulses
  - Dynamic zoom effects (1.0x → 1.3x per beat)
  - Camera shake with intensity ramping
  - Mobile format with black bars (9:16 aspect ratio)
- **Assets**:
  - 10 colorful skull overlay images
  - 15 random meme text phrases
- **Trigger System**:
  - Random timer trigger (30-60 seconds default)
  - Action-based triggers:
    - Attack entities (mobs, players)
    - Break blocks
    - Interact with blocks (place, open chests, use doors, etc.)
    - Use items (eat, drink, bow, potions, etc.)
    - Take damage
  - Configurable chance (0-100%, default 30%)
  - Delay system (default 150ms) for animation sync
  - Independent toggles for each trigger type
- **Configuration System**:
  - Config menu accessible via O key
  - Adjustable parameters:
    - Min/max timer intervals (5-180s range)
    - Effect duration (1-3 seconds)
    - Action trigger chance (0-100%)
    - Activation delay (milliseconds)
    - Pitch range (min/max)
    - Icon size (16-128px)
  - Toggle switches for each trigger type
  - Persistent JSON config file
- **Beat Synchronization**:
  - BPM calculation based on pitch
  - Rhythmic zoom/blur pulses
  - Higher pitch = faster beats
  - Final 10% intensity boost
- **Technical Features**:
  - Mixin-based camera manipulation
  - Thread-safe execution with ScheduledExecutorService
  - Event-driven architecture (ClientTick, HudRender, etc.)
  - Custom pause screen for game freezing effect

### Dependencies
- Minecraft 1.21
- Fabric Loader 0.17.3+
- Fabric API 0.102.0+
- Java 21
- Satin API 2.0.0 (bundled)

---

## [Unreleased]

### Planned Features
- **Next Update (1.2.0)**:
  - Minecraft 1.21.10 support
- **Future**:
  - More built-in phonk tracks
  - Additional visual effects (chromatic aberration, vignette)
  - Custom meme text support
  - Effect presets (Chill / Normal / Extreme)
  - Custom trigger keybind
  - Replay system (record and replay effects)
