# Cosmic Explorer

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>
  <img src="https://img.shields.io/badge/Theme-Material3-6750A4?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Min%20SDK-24-blue?style=for-the-badge"/>
</p>

<p align="center">
  <b>An immersive space-themed Android app to explore our solar system and test your cosmic knowledge.</b>
</p>

---

## Features

| Screen | What it does |
|---|---|
| Home | Animated title, gradient space background, navigation to Explore and Quiz |
| Explore Universe | Scrollable list of all 8 planets with animated cards |
| Planet Detail | Full info, fun facts, stats, and a glowing planet orb animation |
| Quiz Mode | 10 randomized questions, score tracking, animated transitions |

---

## Screens

```
Home Screen          Explore Screen       Planet Detail        Quiz Screen
+-------------+     +-------------+      +-------------+     +-------------+
|  COSMIC     |     | Explore     |      |             |     | Question 1  |
|  EXPLORER   |     |-------------|      |   Earth     |     |  of 10      |
|             |     | Mercury     |      |-------------|     |-------------|
| Explore     |     | Venus       |      | About Earth |     | O Option A  |
| Universe    |     | Earth       |      | Fun Facts   |     | O Option B  |
|             |     | Mars        |      | Stats       |     | * Option C  |
| Quiz        |     | Jupiter     |      |             |     | O Option D  |
| Mode        |     | Saturn      |      |             |     |-------------|
|             |     | Uranus      |      |             |     | Score: 3    |
+-------------+     +-------------+      +-------------+     +-------------+
```

---

## Architecture and Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (100% — no XML layouts)
- **Navigation:** Intent-based multi-Activity
- **Design System:** Material 3 with custom dark space theme
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36
- **Build System:** Gradle with Version Catalog (`libs.versions.toml`)

---

## Project Structure

```
app/src/main/java/com/example/cosmoq1/
|
+-- MainActivity.kt              # Home screen
+-- ExploreActivity.kt           # Planet list screen
+-- PlanetDetailActivity.kt      # Planet detail screen
+-- QuizActivity.kt              # Quiz screen
|
+-- data/
|   +-- Planet.kt                # Planet data class + 8 planet entries
|   +-- QuizData.kt              # 10 quiz questions
|
+-- ui/
    +-- components/
    |   +-- SpaceBackground.kt   # Reusable gradient backgrounds
    +-- theme/
        +-- Color.kt             # Space color palette
        +-- Theme.kt             # CosmicExplorerTheme
        +-- Type.kt              # Typography
```

---

## Design

The app uses a custom space dark theme built on Material 3.

| Token | Color | Usage |
|---|---|---|
| SpaceBlack | #000010 | Background |
| SpaceDeepBlue | #0A0A2E | Gradient base |
| SpaceCyan | #00D4FF | Primary accent, highlights |
| SpacePurple | #7C3AED | Buttons, secondary |
| SpaceGold | #FFD700 | Score, fun facts, rings |
| StarWhite | #F0F4FF | Text |
| CardBackground | #0F1F4A | Card surfaces |

### Animations

- `animateFloatAsState` + `spring` — bouncy entrance for title and planet orb
- `AnimatedVisibility` + `fadeIn / slideInVertically` — staggered planet cards, feedback banners
- `AnimatedContent` + `slideInHorizontally` — quiz question transitions
- Button press scale with `Spring.DampingRatioMediumBouncy`

---

## Planets Included

| Planet | Moons | Diameter | Distance from Sun |
|---|---|---|---|
| Mercury | 0 | 4,879 km | 57.9M km |
| Venus | 0 | 12,104 km | 108.2M km |
| Earth | 1 | 12,742 km | 149.6M km |
| Mars | 2 | 6,779 km | 227.9M km |
| Jupiter | 95 | 139,820 km | 778.5M km |
| Saturn | 146 | 116,460 km | 1.43B km |
| Uranus | 28 | 50,724 km | 2.87B km |
| Neptune | 16 | 49,244 km | 4.5B km |

Each planet includes a full description, 5 fun facts, and key stats.

---

## Quiz

- 10 randomized multiple-choice questions
- 4 options per question (A / B / C / D)
- Instant correct/wrong feedback with color coding
- Animated progress bar
- Final score screen with rating:
  - 90% and above — Cosmic Master
  - 70% and above — Star Explorer
  - 50% and above — Space Cadet
  - Below 50% — Keep Exploring

---

## How to Build

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 11+
- Android SDK 36

### Run the app

1. Clone the repo:
   ```bash
   git clone https://github.com/Sreejith-nair511/CosmicQ.git
   ```
2. Open in Android Studio
3. Let Gradle sync complete
4. Click Run or press `Shift+F10`

### Build a Debug APK

```
Build > Build Bundle(s) / APK(s) > Build APK(s)
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Build a Release APK

```
Build > Generate Signed Bundle / APK > APK > release
```

### Install via ADB

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Dependencies

```toml
[versions]
agp = "9.0.1"
kotlin = "2.0.21"
composeBom = "2024.09.00"
activityCompose = "1.13.0"
lifecycleRuntimeKtx = "2.10.0"
coreKtx = "1.18.0"
```

All Compose dependencies are managed via the Compose BOM for version consistency.

---

## Contributing

Pull requests are welcome. For major changes, open an issue first to discuss what you would like to change.

1. Fork the repo
2. Create your branch: `git checkout -b feature/amazing-feature`
3. Commit: `git commit -m 'Add amazing feature'`
4. Push: `git push origin feature/amazing-feature`
5. Open a Pull Request

---

## License

```
MIT License — free to use, modify, and distribute.
```

---

<p align="center">Made with passion by <a href="https://github.com/Sreejith-nair511">Sreejith Nair</a></p>
<p align="center">Star this repo if you found it useful.</p>
