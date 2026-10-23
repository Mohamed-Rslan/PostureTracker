# 🧘 PostureTracker — On-Device Real-Time AI Posture & Ergonomics Monitor

[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2024%2B)-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2F%20Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![MediaPipe](https://img.shields.io/badge/ML%20Engine-Google%20MediaPipe%20Vision-FF6F00?style=for-the-badge&logo=google&logoColor=white)](https://developers.google.com/mediapipe)
[![CameraX](https://img.shields.io/badge/Camera-Jetpack%20CameraX-00599C?style=for-the-badge)](https://developer.android.com/training/camerax)

An intelligent, on-device Android application engineered in **Kotlin** and **Jetpack Compose** that continuously tracks posture, computes ergonomic health scores, alerts users to slouching or forward-head tilt, and guides users through targeted physical therapy exercises. Powered by **Google MediaPipe Tasks Vision** for zero-latency, private, on-device 3D skeletal pose landmarking.

---

## 📱 App Interface & Visual Showcase

| 📊 Health Dashboard | 📷 Live AI Pose Tracking | 🏋️ Corrective Exercises | ⚙️ Sensitivity & Settings |
|:---:|:---:|:---:|:---:|
| <img src=""screenshots/Dashboard.jpg"" width=""220"" alt=""Posture Score & Streaks""/> | <img src=""screenshots/PostureTracking.jpg"" width=""220"" alt=""33-Point Skeleton HUD""/> | <img src=""screenshots/Excercises.jpg"" width=""220"" alt=""Ergonomic Workouts""/> | <img src=""screenshots/Settings.jpg"" width=""220"" alt=""Sensitivity Calibration""/> |
| *Ergonomic score, streaks, and posture trends* | *Real-time skeletal overlay and angle feedback* | *Interactive routines with countdown timer* | *Adaptive sensitivity and notification thresholds* |

---

## 🔬 Core Capabilities & Architecture

### 1. ⚡ On-Device Computer Vision Pipeline
- **Zero-Cloud Privacy**: All image analysis runs 100% locally on-device. No video streams or camera frames ever leave the phone.
- **MediaPipe Pose Landmarker**: Real-time extraction of 33 normalized 3D body landmarks (nose, eyes, ears, shoulders, hips, spine) with GPU acceleration (DELEGATE_GPU).
- **Jetpack CameraX Integration**: Streams zero-copy camera frames via ImageAnalysis.Analyzer seamlessly tied to the Android lifecycle.

### 2. 📐 Biomechanical Ergonomic Analysis
The custom PostureAnalyzer executes trigonometric vector mathematics across landmark coordinates in real time:
- **Craniovertebral & Neck Tilt Angle**: Computes the angular displacement between the shoulder midpoint and the ear midpoint using $\text{atan2}(\Delta x, \Delta y)$ against calibrated thresholds.
- **Forward Head Posture ("Text Neck")**: Tracks horizontal deviation $|\text{earMid}_X - \text{shoulderMid}_X|$ to identify forward cranial drift.
- **Shoulder Asymmetry**: Measures vertical delta $|\text{leftShoulder}_Y - \text{rightShoulder}_Y|$ to flag slouching or unlevel spine alignment.
- **Spinal Curvature**: Compares shoulder and hip midpoints to estimate spinal sagittal posture.
- **Dynamic Sensitivity Scaling**: Scales sensitivity thresholds proportionally using an adaptive multiplier: $\text{factor} = 1.5 - (\text{sensitivity} / 100.0)$.

### 3. 🎯 Targeted Ergonomic Exercise Workflows
- Curated physical therapy exercises: **Chin Tucks**, **Shoulder Blade Squeezes**, **Chest Openers**, and **Neck Stretches**.
- Interactive timer modal with step-by-step guidance, rest intervals, and rep completion tracking.

### 4. 💾 Local Persistence & State Management
- **Jetpack Room Database**: SQLite-backed structured storage for daily posture tracking sessions, duration, slouch timestamps, and streak calculations.
- **Jetpack DataStore (Preferences)**: Coroutine-backed key-value persistence for sensitivity settings, vibration alerts, and theme preferences.
- **Reactive UI Flow**: Unidirectional data flow (UDF) powered by Kotlin StateFlow and SharedFlow.

---

## 🏗️ System Architecture

`	ext
[ CameraX Preview Stream ]
           │
           ▼ (ImageProxy frames)
[ PoseLandmarkerHelper ] ──(GPU Acceleration)──► [ 33-Point 3D Landmarks ]
                                                           │
                                                           ▼
                                                [ PostureAnalyzer ]
                                                 ├── Neck Tilt Angle
                                                 ├── Forward Head Drift
                                                 └── Shoulder Alignment
                                                           │
                                                           ▼ (PostureResult)
                                                [ CameraViewModel ]
                                                           │
                                                           ▼ (StateFlow)
                                            [ Jetpack Compose UI ]
                                             ├── CameraScreen (HUD Overlay)
                                             ├── DashboardScreen
                                             ├── ExercisesScreen
                                             └── SettingsScreen
`

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Language** | Kotlin 2.0+ (100% Kotlin Coroutines & Flow) |
| **User Interface** | Jetpack Compose, Material Design 3 (Material You), Compose Navigation |
| **Machine Learning** | Google MediaPipe Tasks Vision (	asks.vision), GPU/CPU Delegates |
| **Camera Feed** | Android Jetpack CameraX (Core, Camera2, Lifecycle, View) |
| **Local Persistence** | Jetpack Room ORM, Jetpack DataStore Preferences |
| **Architecture** | MVVM (Model-View-ViewModel), Unidirectional Data Flow (UDF) |
| **Target Platforms** | Android 7.0+ (API 24 to API 35) |

---

## 📂 Project Structure

`	ext
PostureTracker/
├── app/
│   ├── src/main/
│   │   ├── java/com/mobile/posturetracker/
│   │   │   ├── data/
│   │   │   │   ├── AppDatabase.kt          # Room Database instance
│   │   │   │   ├── PostureSession.kt       # Posture session entity
│   │   │   │   ├── PostureSessionDao.kt    # Data Access Object for metrics
│   │   │   │   ├── PostureRepository.kt    # Repository for session telemetry
│   │   │   │   └── SettingsRepository.kt   # DataStore settings repository
│   │   │   ├── ui/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── DashboardScreen.kt  # Score, goal progress, streaks & trend charts
│   │   │   │   │   ├── CameraScreen.kt     # Live AI camera with HUD overlay
│   │   │   │   │   ├── ExercisesScreen.kt  # Corrective workouts & countdown timers
│   │   │   │   │   ├── SettingsScreen.kt   # Sensitivity sliders & alert options
│   │   │   │   │   └── GlassCard.kt        # Glassmorphic Compose card component
│   │   │   │   └── theme/
│   │   │   │       └── PostureTrackerTheme.kt
│   │   │   ├── viewmodel/
│   │   │   │   ├── CameraViewModel.kt      # Camera & ML landmark inference state
│   │   │   │   ├── PostureViewModel.kt     # Dashboard analytics & sessions state
│   │   │   │   └── SettingsViewModel.kt    # User configuration state
│   │   │   ├── AppNavigation.kt            # Compose Navigation routes
│   │   │   ├── MainActivity.kt             # Single activity host
│   │   │   ├── PostureAnalyzer.kt          # Biomechanical angle calculation engine
│   │   │   └── PoseLandmarkerHelper.kt     # MediaPipe Vision wrapper & delegates
│   │   ├── res/                            # Vector drawables, themes, strings
│   │   └── AndroidManifest.xml
│   └── build.gradle                        # App dependencies & NDK packaging
├── screenshots/
│   ├── Dashboard.jpg
│   ├── PostureTracking.jpg
│   ├── Excercises.jpg
│   └── Settings.jpg
├── .gitignore
├── build.gradle                            # Root build config
└── settings.gradle
`

---

## 🚀 Building & Running Locally

### Prerequisites
- **Android Studio** (Hedgehog / Iguana / Ladybug or newer)
- **JDK 17** or **JDK 21**
- **Android SDK** (API 35 installed via SDK Manager)
- Physical Android device with a camera (recommended for MediaPipe GPU inference) or an emulator with webcam passthrough.

### Build Commands

`ash
# Clone the repository
git clone https://github.com/Mohamed-Rslan/PostureTracker.git
cd PostureTracker

# Build the debug APK using Gradle wrapper
# Windows:
gradlew.bat assembleDebug
# macOS/Linux:
# ./gradlew assembleDebug

# Install directly to a connected device:
gradlew.bat installDebug
`

---

## 🔒 Permissions & Privacy

The app requires only:
- ndroid.permission.CAMERA: Exclusively for real-time local camera frames during tracking sessions.
- ndroid.permission.VIBRATE: For subtle tactile alerts when poor posture is detected.

No account creation, network permissions, or external telemetry are requested.

---

## 👤 Author

**Mohamed Rslan**
- GitHub: [@Mohamed-Rslan](https://github.com/Mohamed-Rslan)
- Email: [m.rslan4488@gmail.com](mailto:m.rslan4488@gmail.com)
