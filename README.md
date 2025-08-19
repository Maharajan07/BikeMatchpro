# BikeMatchPro - Web Application

BikeMatchPro is a Spring Boot + MongoDB application designed to manage and display bike-related data with image storage support. The system allows users to upload, retrieve, and display images from MongoDB while providing a simple interface for interaction.

---

## ✨ Features

📂 Upload and store bike images in MongoDB

🖼️ Retrieve and display images through REST endpoints

🌐 Static UI (index.html) for easy interaction

⚡ Built with Spring Boot for backend services

🧪 Unit tests included with JUnit

---
## 🛠️ Tech Stack

Backend: Java, Spring Boot

Database: MongoDB

Build Tool: Maven

Testing: JUnit

Frontend: Basic HTML (static resources)

---

## 📂 Project Structure
```bash
BikeMatchPro/
 ├── src/main/java/com/example/bikematch/
 │    ├── BikeMatchApplication.java   # Entry point
 │    ├── controller/
 │    │    └── ImageController.java   # Image handling APIs
 │    └── mongo/
 │         ├── MongoDBConnector.java  # MongoDB connection setup
 │         └── MongoDBExample.java    # Example usage
 ├── src/main/resources/
 │    ├── static/index.html           # UI page
 │    └── application.properties      # Configurations
 └── src/test/java/
      └── AppTest.java                # JUnit test
```
---
## 🚀 Getting Started
### 1️⃣ Clone the repository
```bash
git clone https://github.com/your-username/BikeMatchPro.git
cd BikeMatchPro
```

### 2️⃣ Configure MongoDB

Update your application.properties with MongoDB connection details:
```bash
spring.data.mongodb.uri=mongodb://localhost:27017/bikematch
```
### 3️⃣ Run the application
```bash
mvn spring-boot:run
```
---
## 📸 Sample Endpoints
```bash
GET /displayAllImages → Fetch all stored images

GET /getImage/{id} → Retrieve image by ID
```
---
#### Made by Maharajan!
