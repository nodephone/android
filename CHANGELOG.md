# Changelog — NodePhone Android (`nodephone/android`)

All notable changes to the NodePhone Android application will be documented in this file.

---

## [v1.0.0] - 2026-09-05 — Initial Production Release

### Summary
The initial production release of **NodePhone Android (v1.0.0)** completes all 10 core PRDs, creating a production-ready Android application capable of running NodePhone Server as an embedded background service, exposing local APIs, enabling instant Studio QR pairing, hosting multi-tenant backend projects, managing file storage buckets, performing disaster recovery backups, monitoring realtime events & hardware metrics, and providing native diagnostics control.

### Added Features by Module

#### 1. PRD 001 — Android Foundation & Server Runtime
- Clean Architecture (MVVM + Coroutines StateFlow + Hilt DI + Room Database).
- Material 3 dark-themed UI system (`HomeScreen`, `SettingsScreen`, `SplashScreen`).
- `NodePhoneService` Foreground Service for persistent server background execution with Android notification integration.

#### 2. PRD 002 — Embedded NodePhone Server Engine
- `NodePhoneServerEngine` manager controlling server boot, stop, restart, and automatic boot recovery (`BootReceiver`).
- Live health check polling and hardware resource telemetry loop.

#### 3. PRD 003 — QR Pairing Engine
- Dedicated `PairingScreen` generating dynamic QR payloads containing device identity, server URL, port, and temporary pairing tokens (<30s expiration).
- Android NSD mDNS broadcaster (`_nodephone._tcp`) for instant local network discovery.

#### 4. PRD 004 — Studio Connection & Handshake
- Trusted device pairing handshake endpoints (`/pair/request`, `/pair/verify`, `/pair/trust`, `/pair/revoke`, `/pair/status`).
- `TrustedDevicesScreen` to view, rename, or revoke access for paired Studio laptops.

#### 5. PRD 005 — Projects Engine
- Multi-project isolation engine enabling creation, deletion, duplication, start/stop, and switching of isolated backend environments.
- `ProjectsScreen` with list/grid layouts, search, sorting, and database storage statistics.

#### 6. PRD 006 — Storage & File Manager
- Public and private file buckets manager (`StorageScreen`).
- Temporary signed URL link generator with configurable expiry options (1 minute to 24 hours).
- Background file upload worker (`FileUploadWorker`) using WorkManager.

#### 7. PRD 007 — Backup & Restore Engine
- Portable `.npbackup` archive creation and extraction engine (`BackupArchiveEngine`) with SHA-256 integrity verification.
- Automatic pre-restore safety snapshots and scheduled auto-backups (`BackupWorker`).
- `BackupsScreen` dashboard showing archive history, integrity modal, and restore safeguards.

#### 8. PRD 008 — Realtime Monitoring & Push Notifications
- Local push notification engine (`NotificationEngine`) managing 4 Android channels (`server_health`, `security_alerts`, `storage_alerts`, `system_errors`).
- In-memory live event bus (`RealtimeEventBus`) and hardware telemetry collector (`SystemMonitor`).
- `RealtimeScreen` UI featuring visual hardware gauges, filterable event feed, live event simulator, and connected Studio clients manager.

#### 9. PRD 009 — Device Settings & Diagnostics
- Hardware overview metrics, local ping network diagnostics engine (`NetworkDiagnosticsEngine`), exportable JSON report generator (`SystemDiagnosticsEngine`), and native log viewer with live search/level filter chips (`LogManager`).
- `DiagnosticsScreen`, `PermissionsScreen`, and enhanced `SettingsScreen` with server runtime toggles.

#### 10. PRD 010 — Android Release Audit
- Comprehensive ProGuard / R8 keep rules (`proguard-rules.pro`).
- Full production documentation suite (`README.md`, `LICENSE`, `CHANGELOG.md`, `CONTRIBUTING.md`, `SECURITY.md`).
- Sealed v1.0.0 release build artifacts and Git tag `v1.0.0`.
