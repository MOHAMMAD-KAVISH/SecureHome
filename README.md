# 🛡️ SecureHomePlus – Home Safety Assessment App

A smart Android application built using **Kotlin + MVVM + Room + Google Maps** to help users check and improve their home security.

---

## 🚀 Features

### 🔐 Home Safety Check
- Simple question-based assessment  
- Weighted scoring system  
- Auto-generated safety score & recommendations  

### 📝 Offline Reports
- Save reports using **Room Database**  
- View / update / delete reports  
- 100% offline report access  

### 🗺️ Live Safety Map
- Google Maps + Fused Location  
- Shows Police, Fire, Hospital nearby  
- Floating Safety Index  
- Bottom sheet + recenter button  

### 🔔 Smart Reminders
- Daily/weekly reminder notifications  
- Built using AlarmManager / WorkManager  

### 👤 User System
- Login / Register  
- Input validation  
- Local session handling  

### 🎨 Modern UI
- Lottie splash screen  
- Material Design 3  
- Smooth animations & clean dashboard  

---

## 🏗️ Tech Stack

- **Kotlin**  
- **MVVM Architecture**  
- **Room Database**  
- **Coroutines**  
- **Google Maps SDK**  
- **Material Components**  
- **WorkManager / AlarmManager**  

---

## 📂 Project Structure (Short)

ui/ (Splash, Login, Dashboard, Survey, Map, Reports)
data/ (Room DB: DAO, Entity, Database)
service/ (ReminderReceiver)
utils/

---

## 🔧 Setup

Clone the project:
git clone https://github.com/MOHAMMAD-KAVISH/SecureHomePlus


Add your Google Maps API key in `AndroidManifest.xml`:
```xml
<meta-data
  android:name="com.google.android.geo.API_KEY"
  android:value="YOUR_API_KEY"/>
Open in Android Studio → Run App.

🧩 Future Scope
IoT integration

Cloud backup

Multi-user support

AI-based risk detection

👨‍💻 Developers
Mohammad Kavish – Lead Android Developer


⭐ Support
If you like this project, please give the repository a star ⭐.

---
