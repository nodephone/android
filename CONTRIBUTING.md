# Contributing to NodePhone Android (`nodephone/android`)

Thank you for your interest in contributing to **NodePhone Android**! We welcome contributions, bug fixes, and improvements.

---

## 🛠️ Development Setup

1. **Fork and Clone**:
   ```bash
   git clone https://github.com/nodephone/android.git
   ```
2. **Environment Requirements**:
   - Android Studio Jellyfish (2023.3.1) or newer
   - JDK 17
   - Kotlin 1.9+
3. **Build Codebase**:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📐 Architecture Guidelines

- **Clean Architecture**: Strictly maintain separation between `domain/`, `data/`, and `feature/` layers.
- **MVVM Pattern**: Features must use `ViewModel` exposing immutable `StateFlow<UiState>`.
- **UI Framework**: Use Jetpack Compose and Material 3 components.
- **No Mock/Placeholder Code**: All features must be production-ready and fully implemented.

---

## 📝 Commit Guidelines

- Write simple, clear human-language commit messages describing what was changed (e.g. `add local push notification engine system monitor and realtime event bus`).

---

## 🚀 Submitting Pull Requests

1. Create a feature branch:
   ```bash
   git checkout -b feature/my-improvement
   ```
2. Make your changes adhering to code style.
3. Verify the build completes with zero errors:
   ```bash
   ./gradlew assembleDebug
   ```
4. Push your branch and open a Pull Request against `main`.
