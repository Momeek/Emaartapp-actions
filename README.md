E-MART is an online shopping platform (e-commerce website) where customers can:
- Browse and search for products
- Add products to a shopping cart
- Place orders with shipping details
- Track their order history
- Register and login to their accounts

### Functionality

The goal of the app is to allow users to browse ,  search for products, place orders with shopping carts, place orders with shipping details, track their order history,register and login to their account.

Administrators can:
- Add, edit, and delete products
- Manage product categories
- View all orders
- Manage the product catalog

# Microservices Architecture

The application is built using **6 independent services** that work together:

```
User Browser
    ↓
nginx Gateway (Port 80)
    ↓
    ├─→ Angular Frontend
    ├─→ Node.js API (Products & Orders)
    └─→ Java API (Books)
         ↓
    MongoDB & MySQL Database

## Technology Stack

| Service | Technology | Purpose |
|---------|-----------|---------|
| Frontend | Angular 12 | User interface |
| API Gateway | nginx | Route traffic |
| Product API | Node.js + Express | Handle products/orders |
| Books API | Spring Boot | Handle books |
| Product Database | MongoDB | Store products/users/orders |
| Books Database | MySQL | Store books |

## Microservice #1: Angular Frontend (Client)

**What it does:**
- Displays the website that users see in their browser
- Shows product listings, shopping cart, and order forms
- Handles user interactions (clicks, form submissions)

**Technology:** Angular 12 + nginx
**Port:** 80

**Think of it as:** The storefront of the shop - what customers see and interact with

---

## Microservice #2: Node.js API (Product & Order Service)

**What it does:**

### Product Management
- Creates new products with images
- Updates product information
- Deletes products
- Searches products by name or category
- Organizes products into categories

### Shopping Cart
- Adds products to user's cart
- Removes products from cart
- Updates quantities
- Calculates total prices

### Order Processing
- Creates orders when users checkout
- Validates credit card numbers
- Checks shipping date availability (max 3 orders per day)
- Tracks order history for each user
- Sends order confirmations

### User Management
- Registers new users
- Logs in users with secure passwords
- Sends welcome emails
- Manages user profiles
- Assigns admin roles

**Technology:** Node.js + Express + MongoDB
**Port:** 5000

**Database:** MongoDB (stores products, users, orders, categories)

**Think of it as:** The main store manager - handles most of the shopping operations

---

## Microservice #3: Java API (Books Service)

**What it does:**
- Manages a separate books database
- Handles book-specific operations
- Provides RESTful API for book data

**Technology:** Spring Boot + MySQL

**Port:** 8080

**Database:** MySQL (stores books data)

**Think of it as:** A specialized department for books within the store

---

## Microservice #4: nginx API Gateway

**What it does:**
- Acts as the single entry point for all requests
- Routes traffic to the correct service:
  - `/` → Sends to Angular frontend
  - `/api/` → Sends to Node.js API
  - `/webapi/` → Sends to Java API
- Balances load across multiple servers
- Handles WebSocket connections

**Technology:** nginx

**Port:** 80 (the only port exposed to the internet)

**Think of it as:** The main entrance and receptionist - directs everyone to the right place

**Think of it as:** The main entrance and receptionist - directs everyone to the right place

---

## Microservice #5: MongoDB Database

**What it does:**
- Stores all product information
- Stores user accounts and profiles
- Stores shopping carts
- Stores order history
- Stores product categories

**Technology:** MongoDB 4

**Port:** 27017

**Database Name:** epoc

**Think of it as:** The main filing cabinet for products and customer data

---

## Microservice #6: MySQL Database

**What it does:**
- Stores books information
- Provides structured data storage
- Ensures data consistency

**Technology:** MySQL 5.7

**Port:** 3306

**Database Name:** books

**Think of it as:** A specialized filing cabinet for books

-
# Development
For deployment it used Docker compose and ubuntu OS/ec2 instance

To run the app in development environment, make sure Docker engine and Docker compose environment is installed on your local machine.

Before running the app environment docker engines and docker compose environment must be installed. Execute commands below to install:
```bash

# Install docker on Ubuntu
sudo apt-get update
   sudo apt-get install \
    ca-certificates \
    curl \
    gnupg \
    lsb-release -y
   curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
   echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install docker-compose
   sudo apt-get update
   sudo apt-get install docker-ce docker-ce-cli containerd.io -y
   sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
   sudo chmod +x /usr/local/bin/docker-compose

# Add ubuntu user into docker group
    sudo usermod -a -G docker ubuntu


Be sure to expose nginx

Start the containers with docker compose up -d

