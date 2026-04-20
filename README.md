# Emaart - E-Commerce Application

A full-stack e-commerce microservices application deployed on AWS ECS with a CI/CD pipeline using GitHub Actions.

---

## Architecture

```
Browser
   ↓
AWS ALB (Application Load Balancer)
   ├── /*        → Client (Angular)       - ECS Service - Port 80
   ├── /api/*    → Node.js API            - ECS Service - Port 5000
   └── /webapi/* → Java API (Spring Boot) - ECS Service - Port 8080
                        ↓                        ↓
                  MongoDB Atlas            AWS RDS MySQL
```

---

## Services

| Service | Tech | Port | Database |
|---|---|---|---|
| `client` | Angular 12 + Nginx | 80 | - |
| `nodeapi` | Node.js + Express | 5000 | MongoDB Atlas |
| `javaapi` | Spring Boot 2.3 | 8080 | AWS RDS MySQL |

---

## Tech Stack

**Frontend:**
- Angular 12
- Nginx (serves static files)

**Backend:**
- Node.js + Express (products, orders, users, authentication)
- Spring Boot Java (books)
- JWT authentication
- Nodemailer (email notifications)

**Infrastructure:**
- AWS ECS Fargate (container orchestration)
- AWS ALB (load balancing + routing)
- AWS RDS MySQL 5.7 (books database)
- MongoDB Atlas (products, orders, users database)
- AWS ECR (container registry)
- AWS CloudWatch (logging + monitoring)

**CI/CD:**
- GitHub Actions
- Docker
- SonarQube (code quality analysis)
- Trivy (vulnerability scanning)

---

## Project Structure

```
Emaartapp-actions/
├── client/          # Angular frontend
├── nodeapi/         # Node.js REST API
│   ├── config/      # Database config
│   ├── models/      # Mongoose models
│   ├── routes/      # API routes
│   └── server.js    # Entry point
├── javaapi/         # Spring Boot API
│   └── src/main/java/com/springwork/bookwork/
│       ├── controller/    # REST controllers
│       ├── model/         # JPA entities
│       ├── repository/    # JPA repositories
│       └── HealthController.java
└── docker-compose.yaml    # Local development only
```

---

## Local Development

### Prerequisites
- Docker and Docker Compose
- Node.js 14+
- Java 8+
- Maven

### Run locally with Docker Compose
```bash
docker-compose up
```
Access the app at `http://localhost:80`

### Run services individually

**Client:**
```bash
cd client
npm install
npm start
```

**Node API:**
```bash
cd nodeapi
npm install
npm start
```

**Java API:**
```bash
cd javaapi
./mvnw spring-boot:run
```

---

## Environment Variables

### nodeapi (ECS Task Definition)
| Variable | Description |
|---|---|
| `MONGO_URI` | MongoDB Atlas connection string |
| `SECRET_KEY` | JWT signing secret |
| `EMAIL_USER` | Gmail SMTP username |
| `EMAIL_PASS` | Gmail SMTP password |

### javaapi (ECS Task Definition)
| Variable | Description |
|---|---|
| `MYSQL_URI` | RDS MySQL JDBC connection string |
| `MYSQL_USER` | RDS master username |
| `MYSQL_PASSWORD` | RDS master password |

---

## CI/CD Pipeline

The pipeline runs on GitHub Actions with two triggers:
- `pull_request` to main → runs Build, Security Scan and SonarQube only
- `push` to main (merge) → runs full pipeline including Docker build and ECS deploy

### Pipeline Stages

```
Push to main
     ↓
Build → Trivy Security Scan → SonarQube Scan → Docker Build → Push to ECR → Deploy to ECS
```

### Jobs Breakdown

| Job | Trigger | What it does |
|---|---|---|
| `Build` | PR + Push | Installs dependencies for all 3 services |
| `Security-Scan` | PR + Push | Runs Trivy vulnerability scan on all 3 services, uploads reports as artifacts |
| `SonarQube-Scan` | PR + Push | Runs SonarQube code quality analysis on all 3 services |
| `Build_And_Deploy` | Push to main only | Builds Docker images, pushes to ECR, deploys to ECS |

### Trivy Scan
- Scans all three services for `CRITICAL` and `HIGH` vulnerabilities
- Scan type: filesystem (`fs`)
- Reports uploaded as GitHub Actions artifacts for review
- `exit-code: 0` — pipeline continues even if vulnerabilities found

### SonarQube Scan
- Analyses code quality across all services
- Requires `SONAR_TOKEN` secret

### GitHub Secrets Required
| Secret | Description |
|---|---|
| `ACCESS_KEY_ID` | IAM user access key |
| `SECRET_ACCESS_KEY` | IAM user secret key |
| `AWS_REGION` | AWS region (e.g. us-east-1) |
| `SONAR_TOKEN` | SonarQube authentication token |

---

## Health Checks

Each service has a dedicated DB-aware health endpoint:

| Service | Health Check Path | Checks |
|---|---|---|
| `client` | `/` | Nginx serving static files |
| `nodeapi` | `/health` | MongoDB Atlas connection |
| `javaapi` | `/health` | RDS MySQL connection |

ALB uses these endpoints to verify container health every 30 seconds.

---

## API Endpoints

### Node API (`/api`)
| Method | Path | Description | Auth |
|---|---|---|---|
| POST | `/api/user/register` | Register user | No |
| POST | `/api/user/login` | Login user | No |
| GET | `/api/shop/products` | Get all products | Yes |
| GET | `/api/shop/category` | Get all categories | Yes |
| GET | `/api/shop/orders` | Get all orders | Yes |
| GET | `/api/shop/info` | Get shop stats | No |

### Java API (`/webapi`)
| Method | Path | Description |
|---|---|---|
| GET | `/webapi/books` | Get all books |
| GET | `/webapi/books/{id}` | Get book by ID |
| POST | `/webapi/books` | Create book |
| PUT | `/webapi/books/{id}` | Update book |
| DELETE | `/webapi/books/{id}` | Delete book |
| GET | `/webapi/books/published` | Get published books |

---

## AWS Infrastructure

- VPC with public and private subnets
- ECS Cluster running on Fargate
- ALB in public subnets with path-based routing rules
- ECS tasks in private subnets
- RDS MySQL in private subnets
- ECS deployment circuit breaker enabled with automatic rollback

### ALB Listener Rules
| Priority | Path | Target Group | Port |
|---|---|---|---|
| 1 | `/api/*` | Nodeapi-TG | 5000 |
| 2 | `/webapi/*` | Javaapi-TG | 8080 |
| 3 | `/*` | Client-TG | 80 |

---

## Migration Notes

This project was originally built as a monolith using docker-compose with:
- Nginx as reverse proxy routing between all services
- All services in one docker-compose file
- Hardcoded container names (`emongo`, `emartdb`) as hostnames
- Environment variables and configs hardcoded in the codebase
- nodeapi serving Angular static files directly

### Changes made for ECS microservices deployment:
- Removed nginx — replaced with ALB path-based routing
- Each service has its own ECS task definition and ECS service
- Replaced hardcoded DB hostnames with environment variables
- Moved all secrets and config to ECS task definition environment variables
- Removed static file serving from nodeapi — client has its own ECS service
- Added dedicated DB-aware health check endpoints to nodeapi and javaapi
- Each service scales independently
