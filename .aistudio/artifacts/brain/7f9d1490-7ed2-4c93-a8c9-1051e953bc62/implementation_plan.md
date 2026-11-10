# GUTIVERSE – 16 Bead Multiplayer Arena (Phase 1 Revision)

GUTIVERSE is an elite esports-inspired 16 Bead (Sholo Guti) digital arena created by Touhidul Hassan Rifat with the tagline **PLAY • CONNECT • CONQUER**. 

This revised Phase 1 blueprint establishes a **100% configurable and testable board & rules engine** (avoiding hardcoded assumptions), a comprehensive **robotic audio engine** (procedural music, distinct action sound effects, and TTS mascot speech), an **11-language localization system** (with RTL support for Arabic and Urdu), and a fully playable local and AI game experience wrapped in a luxury cyber-futuristic UI.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural refinements have been made to address your exact instructions:
> - **Pluggable & Configurable Rules Engine**: The board layout and capture rules are not hardcoded. A clean `BoardConfiguration` and `RulesConfiguration` architecture allows adjusting the 37-node topology, line connections, single-vs-multi jump captures, and forced capture rules on the fly.
> - **Documented Ruleset**: The traditional rules applied by default are explicitly documented in the plan and within the in-game Rules Dialog for transparent player verification.
> - **Comprehensive Audio Engine**: Robotic background music, low-latency sound effects (button clicks, bead selection, movement slides, capture bursts, match start/end), and Text-To-Speech robot voice with automatic music ducking are fully integrated.
> - **Multilingual System (11 Languages)**: Built-in localization support for English, Bengali (বাংলা), Arabic (العربية), Hindi (हिन्दी), Urdu (اردو), Spanish, French, Portuguese, Chinese, Russian, and Turkish, including RTL layout support.
> - **Genuine Functionality**: Phase 1 is not a mockup—it provides a 100% working, interactive 16 Bead game with real move validation, multi-jump captures, win/loss detection, timer, pass-and-play local 2-player mode, and a multi-difficulty heuristic AI robot opponent ("GutiBot").

---

## 1. Overview & Core Concept

- **What It Does**: GUTIVERSE brings the classic 16 Bead strategy game into a cybernetic esports universe. Players command 16 digital beads (Gold vs. Cyan) across a geometric battlegrid, capturing opponent beads by jumping over them into vacant spots.
- **Creator Credit**: `Created by Touhidul Hassan Rifat` | `PLAY • CONNECT • CONQUER` featured prominently across splash, menus, and match screens.
- **Key Value**: A mathematically precise, configurable game engine that feels tactile and responsive, complemented by a charismatic robotic AI mascot and high-production audio.

---

## 2. Documented 16 Bead Rules & Configurable Engine

### Default Traditional Rules (Sholo Guti Standard)

1. **Board Structure (37 Intersecting Points)**:
   - Central grid: 5 horizontal lines × 5 vertical lines (25 intersection points).
   - Two triangular outposts: Top apex outpost (6 points) and Bottom apex outpost (6 points).
   - Total: **37 intersection nodes** connected by **88 bidirectional movement tracks** (horizontal, vertical, and primary diagonal lines).
2. **Initial Setup**:
   - Player 1 (Gold): 16 beads occupying the bottom triangular outpost and the first two rows of the central grid.
   - Player 2 (Cyan / Robot): 16 beads occupying the top triangular outpost and the top two rows of the central grid.
   - Middle neutral row (Row 3, 5 nodes): Starts completely empty.
3. **Movement Rules**:
   - **Step Move**: A bead can move 1 step along any marked line to an adjacent vacant intersection.
   - **Capture Jump**: A bead can jump over an adjacent opposing bead into an immediately following vacant intersection along the same straight line. The jumped bead is removed from the board.
4. **Multi-Jump Chain Captures**:
   - When a capture jump is completed, if the same bead has another legal jump from its new position, it can chain consecutive captures in the same turn.
5. **Winning Conditions**:
   - Capturing all 16 opposing beads, or trapping all remaining opposing beads so they have no legal moves.

### Configurable Engine Parameters (`RulesConfiguration`)

To ensure the engine can be verified and easily adapted to any traditional or regional variant:
```kotlin
data class RulesConfiguration(
    val allowMultiJump: Boolean = true,          // Multi-jump chain captures
    val mandatoryCapture: Boolean = false,       // Force capture if available (Huffing)
    val allowBackwardApexReturn: Boolean = true, // Can beads re-enter starting apex
    val matchTimerSeconds: Int = 300,            // 5 minutes per match
    val turnTimerSeconds: Int = 30               // 30 seconds per turn
)
```

The board topology (`BoardTopology`) is defined as a coordinate graph with node coordinates (X, Y in 0.0..1.0 range), adjacency sets, and straight-line collinear jump maps, allowing the board geometry to be modified or reconfigured without rewriting the core game loop.

---

## 3. Robotic Audio & Multilingual Systems

### Audio Architecture (`SoundManager.kt`)

- **Synthesized Audio Engine**:
  - Native Android `AudioTrack` and `SoundPool` procedural synthesizer producing clean, zero-external-dependency futuristic cyber tones.
  - **Robotic Background Music (BGM)**: Dynamic cyber arpeggios with deep bass pulse in dark royal purple aesthetic.
  - **Dedicated Sound Effects (SFX)**:
    - Button click: High-frequency crisp cyber tick.
    - Menu transition: Smooth futuristic whoosh.
    - Bead select: Harmonic crystalline chime.
    - Bead move: Spatial slide sound.
    - Bead capture: Energetic laser impact burst.
    - Losing bead: Warning resonant alert.
    - Match start: Deep esports arena gong.
    - Victory: Triumphant cyber fanfare.
    - Defeat: Descending minor cyber tone.
- **Mascot Voice (`TextToSpeech`)**:
  - Android TTS engine configured with robotic pitch and speech rate.
  - Automatic **Music Ducking**: When GutiBot mascot speaks, BGM volume reduces to 25% and returns smoothly upon completion.
  - Default welcome greeting:
    *"Assalamu Alaikum wa Rahmatullahi wa Barakatuh! Welcome to GUTIVERSE! Enter the ultimate 16 Bead gaming arena. Are you ready to play?"*
  - Contextual in-game lines: capture praise, blunder warnings, victory cheers.
- **Audio Control Center (In Settings & In-Game)**:
  - Toggles: BGM ON/OFF, Button Click ON/OFF, Move Sound ON/OFF, Capture Sound ON/OFF, Robot Voice ON/OFF.
  - Sliders: Master Volume, Music Volume, SFX Volume, Voice Volume.

### Multilingual Localization System (`LocalizationManager.kt`)

Phase 1 provides complete in-memory dictionary support for all 11 target languages:
1. English (Default)
2. Bengali (বাংলা)
3. Arabic (العربية - with right-to-left UI mirroring)
4. Hindi (हिन्दी)
5. Urdu (اردو - with right-to-left UI mirroring)
6. Spanish (Español)
7. French (Français)
8. Portuguese (Português)
9. Chinese (中文)
10. Russian (Русский)
11. Turkish (Türkçe)

Every menu title, button, dialog, victory screen, and mascot script is localized. Language changes apply instantly across the entire application.

---

## 4. User Experience & Visual Design

### Visual Identity & Theme Tokens

- **Atmosphere**: Futuristic luxury esports arena.
- **Colors**:
  - `Dark Royal Purple Background`: `#0A0618`
  - `Obsidian Card Surface`: `#140C2E`
  - `Luxury Imperial Gold (Player 1 / Accents)`: `#FFD700`
  - `Cyber Electric Cyan (Player 2 / Robot)`: `#00F0FF`
  - `Laser Crimson (Captures / Warnings)`: `#FF2266`
  - `Muted Lavender Border`: `#453868`
- **Typography**: M3 typography with high-contrast scores, monospace timers, and clear game prompts.
- **Layouts**: Edge-to-edge support with safe drawing insets, responsive 48dp+ interactive touch points.

### Screen Flow & Feature Hubs

```
┌─────────────────────────────────────────────────────────────────┐
│                  GUTIVERSE Navigation Hierarchy                 │
│                                                                 │
│ ┌─────────────────────────┐                                     │
│ │   Cinematic Intro       │ ── Mascot Greeting & Logo Animation │
│ └────────────┬────────────┘                                     │
│              │                                                  │
│ ┌────────────▼────────────┐                                     │
│ │    Main Home Screen     │                                     │
│ │  (11 Esports Hubs)      │                                     │
│ └─┬───┬───┬───┬───┬───┬───┘                                     │
│   │   │   │   │   │   │                                         │
│   ├───┼───┼───┼───┼───┼─── 1. Robot Mode (Easy/Medium/Hard)     │
│   │   │   │   │   │   │    2. Vs Computer                       │
│   │   │   │   │   │   │    5. Local Game (2P Pass & Play)       │
│   │   │   │   │   │   │    ───► [ Playable Game Arena ]         │
│   │   │   │   │   │   │                                         │
│   ├───┼───┼───┼───┼───┼─── 3. Play Online (Lobby Preview)       │
│   │   │   │   │   │   │    4. Friends (Friend Code & Requests)  │
│   │   │   │   │   │   │    6. Leaderboard (Global & Rankings)   │
│   │   │   │   │   │   │    7. My Profile (Stats, Win %, Badges) │
│   │   │   │   │   │   │    8. Messages (Chat Inbox)             │
│   │   │   │   │   │   │    9. Notifications (Alerts & News)     │
│   │   │   │   │   │   │   10. Game Shop (Coins & Themes)        │
│   │   │   │   │   │   │                                         │
│   └───┴───┴───┴───┴───┴─── 11. Settings (Audio/Lang/Rules)     │
│                                                                 │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │                      Playable Game Arena                    │ │
│ │  - Top Player Bar (Beads tally, Timer, Side indicator)     │ │
│ │  - Interactive 37-Node Board Canvas (Glowing lines, Halos)  │ │
│ │  - Multi-Jump Chain Resolver                                │ │
│ │  - GutiBot Live Commentary Banner                           │ │
│ │  - Bottom Player Bar (Your tally, Turn state, Surrender)    │ │
│ │  - Match End Celebration Modal (Stats, Rematch, Share)      │ │
│ └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 5. Technical Architecture & Component Structure

```
┌────────────────────────────────────────────────────────────────────────┐
│                          Package Architecture                          │
│                                                                        │
│  com.example.gutiverse                                                 │
│  ├── engine/                                                           │
│  │   ├── BoardTopology.kt         (Configurable 37-node graph & lines) │
│  │   ├── RulesConfiguration.kt    (Configurable multi-jump/timers)     │
│  │   ├── SholoGutiEngine.kt       (Move generation, captures, checks)  │
│  │   └── GutiBotAI.kt             (Minimax & strategic heuristic bot)  │
│  ├── audio/                                                            │
│  │   ├── SoundManager.kt          (Synthesizer SFX & BGM engine)       │
│  │   └── MascotVoiceManager.kt    (Android TTS & music ducking)        │
│  ├── localization/                                                     │
│  │   └── LocalizationManager.kt   (11 Languages + RTL layout helpers)  │
│  ├── ui/                                                               │
│  │   ├── theme/                   (Theme.kt, Color.kt, Type.kt)        │
│  │   ├── components/                                                   │
│  │   │   ├── BoardCanvas.kt       (Custom Canvas rendering 37 nodes)   │
│  │   │   ├── BeadPiece.kt         (Gold/Cyan 3D glowing bead design)   │
│  │   │   └── MascotBubble.kt      (GutiBot commentary pop-up)          │
│  │   └── screens/                                                      │
│  │       ├── CinematicIntroScreen.kt (Esports arena entrance)          │
│  │       ├── HomeScreen.kt           (11 Menu hub dashboard)           │
│  │       ├── GameScreen.kt           (Full match arena & turn loop)    │
│  │       ├── HubScreens.kt           (Profile, Leaderboard, Shop, etc.)│
│  │       └── SettingsScreen.kt       (Audio sliders, Lang, Rules)      │
│  └── viewmodel/                                                        │
│      ├── GameViewModel.kt         (Game loop, AI coroutines, timer)    │
│      └── AppViewModel.kt          (Navigation, settings state, sound)  │
└────────────────────────────────────────────────────────────────────────┘
```

### AI Robot Logic ("GutiBot")
- **Easy**: Random valid moves with occasional obvious captures.
- **Medium**: Greedy heuristic (prioritizes immediate captures and center position).
- **Hard**: Minimax search with alpha-beta pruning (depth 3-4), evaluating piece count differential, capture opportunities, vulnerable beads, and apex control.

---

## 6. Verification & Testing Plan

1. **Compilation Check**: Run `compile_applet` to ensure zero compilation or build errors.
2. **Game Engine Verification**:
   - Verify initial node occupation: exactly 16 Gold beads, 16 Cyan beads, and 5 empty middle nodes.
   - Test step movement to adjacent unoccupied points.
   - Test jump capture over an enemy piece into a vacant node.
   - Test multi-jump chaining sequence.
   - Test capture prevention if jumping over empty space or allied piece.
   - Verify win condition when one side loses all 16 beads.
3. **Audio & Voice Verification**:
   - Verify procedural BGM plays cleanly.
   - Verify SFX triggers on button clicks, bead moves, and captures.
   - Verify TTS greeting triggers with automatic music ducking.
4. **Localization Verification**:
   - Switch language between English, Bengali, Arabic, and ensure labels and text direction update accurately.
