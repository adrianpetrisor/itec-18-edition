# 🧠 Studentious — Group Learning Web Platform
**iTEC Edition 18 — Web Development (Advanced)**  
📍 Timișoara, 2025

> A smart web platform for organizing and managing collaborative study sessions between students.

---

## 🧩 Overview

This project was built during the 40-hour iTEC Hackathon, Edition 18 — Advanced Web Development track.  
It aims to facilitate the creation, registration, and participation in educational events, blending real-time features and advanced integrations.

Some features might be **partially functional** due to limited access to 3rd party API keys.

---

## ✨ Features

### 🗓️ Event Management
- Users can create and manage study sessions.
- Others can view and join events.
- Admin panel for session moderation.

### 🔐 Secure Authentication
- Google Sign-In
- Google 2FA (Two Factor Authentication)
- Session validation & timeout protection

### 🧠 AI Integration
- OpenAI-powered functionalities
- Potential for curricula auto-summarization

### 🛡️ File Safety & Integrity
- Uploaded images and documents are scanned via an external API
- Verifies files against malware and phishing signatures

### 📬 Notifications
- Email alerts for session activity
- Web notifications for real-time updates

---

## 🛠 Technologies Used

| Stack | Tech |
|-------|------|
| **Backend** | Spring Boot |
| **Frontend** | Thymeleaf |
| **Authentication** | Google OAuth 2.0 + 2FA |
| **Notifications** | SMTP Email, Web Push |
| **Security APIs** | Malware/File Scanning API |
| **AI Integration** | OpenAI API |

---

## 📦 JavaScript Libraries

- **Three.js** — 3D Elements & Visuals
- **jQuery** — DOM Manipulation
- **Anime.js** — Smooth Animations
- **FullCalendar.js** — Interactive Calendar UI

---

## 🔍 Status

Some external APIs (OpenAI, File Scanning, Mail) are disabled due to missing API Keys.  
Functionality can be restored by supplying valid credentials in the project configuration.

---

## 📌 Notes

- Git is used for version control.
- Project presentation will include a code walkthrough.
- Features were prioritized for a functional MVP.
