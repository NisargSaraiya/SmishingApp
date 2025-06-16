## Smishing App
### An Android app for detecting Phishing sms
### Techstack - Kotlin with compose for android dev
![image](https://github.com/Arifk-24/Smishing-app/assets/69386772/6bc3b0ca-1854-49b3-b58e-2e36c89cb810)
![image](https://github.com/Arifk-24/Smishing-app/assets/69386772/2c3fadcc-8b6c-492d-8483-9ff122dcfaf0)
![image](https://github.com/Arifk-24/Smishing-app/assets/69386772/ffd37da4-8216-41ac-abb2-74be38df6f06)
![image](https://github.com/Arifk-24/Smishing-app/assets/69386772/d8a1d870-3b60-4d63-8845-4aaac634b5af)

---

# 📖 Project Documentation

## Table of Contents
- [About the App](#about-the-app)
- [Features](#features)
- [Screenshots](#screenshots)
- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Setup & Installation](#setup--installation)
- [Usage](#usage)
- [Permissions](#permissions)
- [Model & Phishing Detection](#model--phishing-detection)
- [Folder Structure](#folder-structure)
- [Contributing](#contributing)
- [License](#license)

## About the App
**Smishing App** is an Android application designed to detect phishing SMS messages (smishing) using machine learning and rule-based heuristics. It helps users identify and manage suspicious messages and block potential threats.

## Features
- Detects phishing (smishing) SMS messages in real-time.
- Classifies messages as spam or ham (not spam).
- Allows users to view, mark, and manage spam/ham messages.
- Blocks senders identified as spammers.
- Modern and clean UI built with Jetpack Compose.
- Uses cloud-based model inference via REST API.
- Handles SMS permissions securely and transparently.

## Screenshots
(See images above for UI examples.)

## Architecture Overview
- **MVVM (Model-View-ViewModel)** architecture.
- **Jetpack Compose** for UI.
- **Kotlin Coroutines** for async tasks.
- **Retrofit** for network communication with the phishing detection API.
- **Permissions** handled with Accompanist.

### Main Components
- `MainActivity.kt`: Entry point, sets up theme and navigation.
- `MessageViewModel.kt`: Handles SMS reading, classification, and state.
- `MesasgeScreen.kt`, `MessageView.kt`: UI for listing and interacting with messages.
- `Permission.kt`: Handles runtime permission requests.
- `UserApi.kt`: Defines Retrofit API for phishing detection.
- `TextClassification.kt`: (Commented code for local ML model support.)

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM
- **Networking:** Retrofit2, Gson
- **Permissions:** Accompanist
- **Testing:** JUnit, Espresso

## Setup & Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/Arifk-24/Smishing-app.git
   ```
2. **Open in Android Studio.**
3. **Build the project:**
   - Ensure you have Android SDK 24+ and Gradle 7+.
   - Sync Gradle and resolve dependencies.
4. **Run on device/emulator:**
   - Grant SMS permissions when prompted.

## Usage
- On first launch, grant the app permission to read SMS.
- The app will scan your SMS inbox and classify messages.
- Navigate between All, Spam, and Ham tabs.
- Mark messages as spam/ham using the menu.
- Block suspicious senders.

## Permissions
- `READ_SMS`: Required to read SMS messages for classification.
- The app explains the need for permissions and requests them at runtime.

## Model & Phishing Detection
- The app sends SMS text to a cloud-based phishing detection API (`https://smishing-service.onrender.com/api`).
- The API returns a score; messages with score ≥ 0.5 are marked as spam.
- (Commented code for on-device TensorFlow Lite model is present for future use.)

## Folder Structure
```
Smishing-app-master/
├── app/
│   ├── src/main/java/com/example/smishingapp/
│   │   ├── MainActivity.kt
│   │   ├── TextClassification.kt
│   │   ├── network/
│   │   │   └── UserApi.kt
│   │   ├── ui/
│   │   │   ├── MesasgeScreen.kt
│   │   │   ├── MessageViewModel.kt
│   │   │   ├── Permission.kt
│   │   │   └── ...
│   ├── res/
│   └── ...
├── build.gradle
├── README.md
└── ...
```

## Contributing
Contributions are welcome! Please open issues or submit pull requests for improvements or bug fixes.

## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

---
