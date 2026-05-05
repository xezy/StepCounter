# Step Counter

A modern Android step tracking application built with Jetpack Compose and Kotlin.

## Features

- **Real-time Step Detection** - Tracks steps using accelerometer sensor with intelligent detection algorithms
- **Distance Calculation** - Estimates distance using stride length derived from height/gender or GPS calibration
- **Background Tracking** - Foreground service keeps tracking when app is minimized
- **Session History** - Stores and displays past walking sessions with detailed statistics
- **GPS Calibration** - Optional GPS-based stride length calibration for improved accuracy
- **Modern UI** - Built with Jetpack Compose and Material Design 3

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: Clean Architecture with MVI/MVVM
- **Dependency Injection**: Hilt
- **Database**: Room (SQLite)
- **Charts**: Vico
- **Build System**: Gradle with Kotlin DSL

## Requirements

- Android 12+ (API 31)
- Compile SDK: 34
- JDK 17

## Building

### Local Build

```bash
./gradlew assembleDebug    # Debug APK
./gradlew assembleRelease  # Release APK
```

### GitHub Actions

The project includes a GitHub Actions workflow that automatically builds the app on push to main/master. The APK is uploaded as an artifact.

## Project Structure

```
app/src/main/java/com/stepcounter/
├── core/                  # Core business logic
│   ├── sensor/           # Accelerometer processing, step detection
│   ├── stride/           # Stride length calculation
│   ├── location/         # GPS tracking
│   └── session/          # Walk session management
├── domain/               # Domain models and use cases
│   ├── model/           # Data models (WalkSession, UserProfile, etc.)
│   └── usecase/         # Business use cases
├── data/                 # Data layer
│   ├── repository/      # Repositories
│   └── database/        # Room database, entities, DAOs
├── ui/                   # UI layer (Compose screens)
│   ├── dashboard/        # Main dashboard
│   ├── history/          # Session history
│   ├── session/          # Session details
│   ├── settings/         # User settings
│   └── navigation/       # Navigation graph
├── di/                   # Dependency injection modules
└── service/              # Background services
```

## Permissions

- `ACTIVITY_RECOGNITION` - Detect steps
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` - GPS tracking
- `FOREGROUND_SERVICE` - Background step tracking
- `RECEIVE_BOOT_COMPLETED` - Restart tracking after reboot

## License

[Add your license here]
