# Security Policy — NodePhone Android (`nodephone/android`)

Security is a core priority for NodePhone. As NodePhone converts mobile devices into local backend servers, maintaining strict token security, pairing verification, and local network isolation is essential.

---

## 🔒 Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

---

## 🛡️ Security & Privacy Architecture

- **Offline-First Execution**: The embedded server runs locally on the device with zero cloud dependencies or external secret leaks.
- **Short-Lived QR Payloads**: QR pairing tokens expire automatically in under 30 seconds and contain zero database secrets or JWTs.
- **HMAC-SHA256 Session Validation**: Studio pairing handshakes negotiate trusted session tokens with immediate revocation capabilities.
- **Checksum Verification**: All disaster recovery backups are hashed with SHA-256 before extraction.

---

## 🚨 Reporting Vulnerabilities

If you discover a potential security vulnerability within NodePhone Android, please do **NOT** open a public issue.

Instead, please send a security report detailing the issue to:
`security@nodephone.dev`

Include:
1. Description of the vulnerability.
2. Steps to reproduce.
3. Impact assessment.

We will acknowledge receipt within 24 hours and keep you updated on remediation.
