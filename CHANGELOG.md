# Changelog

All notable changes to Phonk Edit Mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
- Custom beatmap support for precise beat detection
- More phonk tracks
- Additional visual effects (chromatic aberration, vignette)
- Customizable meme texts
- Multiplayer compatibility testing
- Performance optimizations
