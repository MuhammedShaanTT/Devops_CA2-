<p align="center">
  <h1 align="center">📝 SmartNotes AI</h1>
  <p align="center">
    <strong>An AI-Powered Intelligent Note-Taking Web Application</strong>
  </p>
  <p align="center">
    <a href="#features">Features</a> •
    <a href="#tech-stack">Tech Stack</a> •
    <a href="#quick-start">Quick Start</a> •
    <a href="#deployment">Deployment</a>
  </p>
</p>

---

## 📖 About

**SmartNotes AI** is a full-stack Java web application that leverages **Google Gemini AI** to provide intelligent note-taking capabilities. Users can create, manage, and enhance their notes with AI-powered features such as content summarization, grammar correction, and smart suggestions — all within a clean, responsive interface.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 📝 **CRUD Operations** | Create, Read, Update, and Delete notes seamlessly |
| 🤖 **AI Summarization** | Summarize lengthy notes using Google Gemini AI |
| ✅ **Grammar Correction** | Auto-correct grammar and improve writing quality |
| 💡 **Smart Suggestions** | Get AI-powered content enhancement suggestions |
| 🔍 **Search & Filter** | Quickly find notes with search functionality |
| 📱 **Responsive Design** | Works flawlessly on desktop, tablet, and mobile |
| 🐳 **Dockerized** | One-command deployment with Docker Compose |
| 🔄 **CI/CD Pipeline** | Automated build, test, and deploy with GitHub Actions |

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Frontend** | JSP, HTML5, CSS3, JavaScript, Bootstrap 5 |
| **Backend** | Java 17, Jakarta Servlets |
| **Database** | MySQL 8.0 |
| **AI Engine** | Google Gemini API |
| **Build Tool** | Apache Maven 3.9 |
| **App Server** | Apache Tomcat 9 |
| **Containerization** | Docker, Docker Compose |
| **CI/CD** | GitHub Actions |
| **Cloud Deployment** | AWS EC2 |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    GitHub Actions                        │
│              (CI/CD Pipeline - Build,                   │
│               Docker Push, Deploy)                      │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│                   Docker Compose                         │
│  ┌──────────────────┐     ┌──────────────────────────┐  │
│  │  smartnotes-app  │     │    smartnotes-mysql       │  │
│  │  ┌────────────┐  │     │                          │  │
│  │  │  Tomcat 9   │  │────▶│    MySQL 8.0             │  │
│  │  │  (JDK 17)  │  │     │    (smartnotes db)       │  │
│  │  │            │  │     │                          │  │
│  │  │  ROOT.war  │  │     │    Volume: mysql_data    │  │
│  │  └────────────┘  │     └──────────────────────────┘  │
│  │     Port 8080    │              Port 3306             │
│  └──────────────────┘                                   │
│                         smartnotes-network (bridge)      │
└─────────────────────────────────────────────────────────┘
                         │
                         ▼
              ┌─────────────────────┐
              │   Google Gemini AI  │
              │   (External API)    │
              └─────────────────────┘
```

---

## 📋 Prerequisites

Make sure you have the following installed:

| Tool | Version | Download |
|------|---------|----------|
| **Java JDK** | 17+ | [Eclipse Temurin](https://adoptium.net/) |
| **Apache Maven** | 3.9+ | [Maven Downloads](https://maven.apache.org/download.cgi) |
| **Docker** | 24+ | [Docker Desktop](https://www.docker.com/products/docker-desktop/) |
| **Docker Compose** | v2+ | Included with Docker Desktop |
| **Git** | 2.40+ | [Git Downloads](https://git-scm.com/downloads) |

---

## 🚀 Quick Start

### Using Docker Compose (Recommended)

Get up and running in **3 simple steps**:

```bash
# 1. Clone the repository
git clone https://github.com/your-username/smartnotes-ai.git
cd smartnotes-ai

# 2. Create your environment file
cp .env.example .env
# Edit .env and add your GEMINI_API_KEY

# 3. Build and start all services
docker compose up --build
```

🎉 **That's it!** Open [http://localhost:8080](http://localhost:8080) in your browser.

To stop the application:
```bash
docker compose down
```

To stop and remove all data (including database):
```bash
docker compose down -v
```

---

### Local Development (Without Docker)

For development without Docker:

```bash
# 1. Clone the repository
git clone https://github.com/your-username/smartnotes-ai.git
cd smartnotes-ai

# 2. Set up MySQL database
mysql -u root -p
CREATE DATABASE smartnotes;
SOURCE src/main/resources/db/init.sql;

# 3. Set environment variables
export GEMINI_API_KEY=your_api_key_here
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=smartnotes
export DB_USER=root
export DB_PASSWORD=your_password

# 4. Build the WAR file
mvn clean package -DskipTests

# 5. Deploy to Tomcat
cp target/smartnotes-ai.war $CATALINA_HOME/webapps/ROOT.war

# 6. Start Tomcat
$CATALINA_HOME/bin/startup.sh
```

---

## 📁 Project Structure

```
smartnotes-ai/
├── .github/
│   └── workflows/
│       └── ci-cd.yml              # GitHub Actions CI/CD pipeline
├── src/
│   └── main/
│       ├── java/                  # Java source code
│       │   └── com/smartnotes/
│       │       ├── controller/    # Servlet controllers
│       │       ├── model/         # Data models
│       │       ├── dao/           # Database access objects
│       │       ├── service/       # Business logic & AI service
│       │       └── util/          # Utility classes
│       ├── resources/
│       │   └── db/
│       │       └── init.sql       # Database initialization script
│       └── webapp/
│           ├── WEB-INF/
│           │   ├── web.xml        # Servlet configuration
│           │   └── views/         # JSP view files
│           ├── css/               # Stylesheets
│           ├── js/                # JavaScript files
│           └── index.jsp          # Entry point
├── Dockerfile                     # Multi-stage Docker build
├── docker-compose.yml             # Docker Compose orchestration
├── pom.xml                        # Maven project configuration
├── .env.example                   # Environment variables template
├── .gitignore                     # Git ignore rules
└── README.md                      # This file
```

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | Home page — list all notes |
| `GET` | `/notes` | View all notes |
| `GET` | `/notes/new` | New note form |
| `POST` | `/notes/save` | Create or update a note |
| `GET` | `/notes/edit?id={id}` | Edit note form |
| `POST` | `/notes/delete?id={id}` | Delete a note |
| `POST` | `/notes/summarize?id={id}` | AI-summarize a note |
| `POST` | `/notes/improve?id={id}` | AI-improve note content |

---

## 🔄 CI/CD Pipeline

The project uses **GitHub Actions** for continuous integration and deployment:

```
Push to main/master
       │
       ▼
┌──────────────┐    ┌──────────────────┐    ┌──────────────┐
│  Build & Test │───▶│ Docker Build &   │───▶│  Deploy to   │
│  (Maven)      │    │ Push (Docker Hub)│    │  AWS EC2     │
└──────────────┘    └──────────────────┘    └──────────────┘
```

### Pipeline Stages

| Stage | Trigger | Actions |
|-------|---------|---------|
| **Build** | Push / PR | Compile with Maven, upload WAR artifact |
| **Docker** | Push only | Build Docker image, push to Docker Hub |
| **Deploy** | Push to main | SSH into EC2, pull latest image, restart |

### Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `DOCKER_USERNAME` | Docker Hub username |
| `DOCKER_PASSWORD` | Docker Hub password or access token |
| `EC2_HOST` | AWS EC2 public IP address |
| `EC2_KEY` | SSH private key for EC2 instance |
| `GEMINI_API_KEY` | Google Gemini API key |

---

## ☁️ AWS Deployment

### EC2 Instance Setup

```bash
# 1. SSH into your EC2 instance
ssh -i your-key.pem ubuntu@your-ec2-ip

# 2. Install Docker
sudo apt update
sudo apt install -y docker.io docker-compose-v2
sudo usermod -aG docker ubuntu
newgrp docker

# 3. Clone the repository
git clone https://github.com/your-username/smartnotes-ai.git
cd smartnotes-ai

# 4. Create .env file
echo "GEMINI_API_KEY=your_key_here" > .env

# 5. Start the application
docker compose up -d
```

### Security Group Configuration

| Port | Protocol | Source | Purpose |
|------|----------|--------|---------|
| 22 | TCP | Your IP | SSH Access |
| 8080 | TCP | 0.0.0.0/0 | Web Application |
| 3306 | TCP | VPC only | MySQL (internal) |

---

## 🖼️ Screenshots

> 📸 *Screenshots will be added here after deployment.*

<!-- 
![Home Page](screenshots/home.png)
![Create Note](screenshots/create.png)
![AI Features](screenshots/ai-features.png)
-->

---

## 🧪 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `GEMINI_API_KEY` | Google Gemini API key | *Required* |
| `DB_HOST` | MySQL host | `mysql` (Docker) / `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | Database name | `smartnotes` |
| `DB_USER` | Database username | `root` |
| `DB_PASSWORD` | Database password | `root123` |

---

## 👨‍💻 Author

**Muhammed Shaan**

- 🎓 **Course:** B.Tech CSE
- 📘 **Subject:** INT331 — DevOps
- 🏫 **Institution:** Lovely Professional University

---

## 📄 License

This project is developed as part of an academic assignment for **INT331 DevOps** coursework.

---

<p align="center">
  Made with ❤️ by <strong>Muhammed Shaan</strong>
</p>
