# Smishing Detection App

## Overview
Smishing Detection App is an Android application designed to help users identify, understand, and report **smishing attacks** (SMS phishing).  
The project aims to improve users’ awareness of SMS-based scams and provide practical tools for detection, risk assessment, reporting, and cybersecurity education.

This app combines SMS-related analysis, risk scanning, community reporting, news updates, educational content, and an AI-style chat assistant to create a more complete anti-smishing support platform.

---

## Features

### 1. Smishing Detection
- Detect suspicious SMS-related behaviours and possible phishing attempts.
- Provide users with a safer way to review suspicious messages.
- Display detection-related information in the application interface.

### 2. Risk Scanner
- Evaluate the user’s potential exposure to smishing risks.
- Assess multiple factors such as suspicious SMS behaviour, device/security-related conditions, and other risk indicators.
- Show a risk level result such as **Low Risk**, **Moderate Risk**, or **High Risk**.

### 3. SMS Viewing and Analysis
- Read and display SMS messages from the device (with permission).
- Allow users to inspect message details.
- Support the review of suspicious SMS content within the app.

### 4. Community Reporting
- Let users report suspicious phone numbers or SMS scams.
- Maintain community-related posts, comments, and reported numbers.
- Support a shared reporting environment for users to learn from one another.

### 5. News and Awareness Updates
- Fetch and display cybersecurity or scam-related news content.
- Allow users to refresh news and save articles for later reading.
- Notify users about the latest news updates when enabled.

### 6. Educational Support
- Provide learning resources about smishing and digital safety.
- Include educational pages, case studies, FAQs, quizzes, and help content.
- Improve user awareness and scam prevention knowledge.

### 7. Chat Assistant
- Offer a built-in assistant for common smishing and app-usage questions.
- Support quick FAQ-style responses.
- Connect to a backend AI/chat service for extended assistance when needed.

### 8. Notifications and Widgets
- Support app notifications for important updates.
- Include widget-related components for quick access and visibility.
- Improve user engagement and awareness outside the main app interface.

### 9. Feedback and History
- Allow users to submit feedback about their experience.
- Maintain feedback history for review.
- Support continuous improvement of the application.

---

## Tech Stack

- **Language:** Java, Kotlin
- **Platform:** Android
- **Architecture Style:** Activity-based Android application with modular package structure
- **Networking:** Retrofit, OkHttp
- **Local Data Handling:** Local database / helper classes / in-app memory storage
- **UI Components:** RecyclerView, Activities, Adapters, ViewModel (in some modules)
- **Other Integrations:** Notification system, widgets, backend communication, FAQ repository pattern

---

## Project Structure

```text
com.example.smishingdetectionapp
│
├── Community/         # Community reporting, posts, comments, reported numbers
├── DataBase/          # Retrofit interfaces and database-related response models
├── chat/              # Chat assistant, chat adapter, Ollama/backend client, chat history
├── data/              # Login-related data handling and result models
├── detections/        # Detection records, filters, report display
├── models/            # Shared data models
├── network/           # Retrofit instance and API service definitions
├── news/              # News fetching, adapters, bookmarks, saved news
├── notifications/     # Notification helper and notification settings/types
├── repository/        # Repository layer for FAQ and other data modules
├── riskmeter/         # Risk scanner logic, scanning flow, result screens
├── sms/               # SMS extraction, adapters, models, click listeners
├── ui/                # Additional UI screens and support-related activities
└── viewmodel/         # ViewModel classes for FAQ and related features
