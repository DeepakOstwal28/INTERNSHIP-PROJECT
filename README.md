API GATEWAY AUTHENTICATION USING THE JWT AND RATE LIMITING
An enterprise-grade API Gateway with Microservices Architecture that provides secure authentication, centralized routing, and scalable service management.
This project demonstrates a production-style backend system using JWT authentication, rate limiting, Docker containers, and Kubernetes orchestration.



PROJECT OVERVIEW
This system uses an API Gateway pattern where all client requests first pass through a gateway that:
• Verifies JWT tokens  
• Applies rate limiting  
• Handles security checks  
• Routes traffic to microservices  
• Protects internal services  


PROJECT ARCHITECTURE
MAIN FOLDER
│
├── README.md
├── docker-compose.yml
├── .env
├── .gitignore
├── package.json
│
├── k8s/
│   ├── auth-deployment.yaml
│   ├── api-gateway-deployment.yaml
│   ├── movies-deployment.yaml
│   └── ingress.yaml
│
├── api-gateway/
│   ├── Dockerfile
│   ├── server-simple.js
│   └── .env
│
├── auth-service/
│   ├── Dockerfile
│   ├── server-working.js
│   ├── routes/
│   ├── models/
│   └── package.json
│
├── frontend/
│   ├── Dockerfile
│   ├── server.js
│   └── public/
│
├── movies-service/
├── music-service/
├── anime-service/
├── videos-service/
└── notification-service/

---

## 🚀 Features

• JWT-based Authentication  
• Role-based Access Control  
• Rate Limiting (1000 requests/hour)  
• MongoDB Integration  
• API Gateway Pattern  
• Microservices Architecture  
• Docker Containerization  
• Kubernetes Deployment  
• Real-time Admin Panel  
• Secure Password Hashing (bcrypt)  
• CORS Protection  

---

TECH STACK 
FRONTEND       :-Node.js,Express.js
Backend        : Node.js, Express.js  
Database       : MongoDB Atlas  
Authentication : JWT  
Security       : Rate Limiting, CORS, bcrypt  
Containers     : Docker, Docker Compose  
Orchestration  : Kubernetes  




Frontend      : http://localhost:3002  
API Gateway   : http://localhost:3000  
Auth Service  : http://localhost:3001  




FOR LOGIN
Admin  
Email    : admin@example.com  
Password : admin123  

User  
Email    : user@example.com  
Password : user123  



DOCKER SERVICES AND PORT:-
API Gateway           → 3000  
Auth Service          → 3001  
Frontend              → 3002  
Movies Service        → 3003  
Music Service         → 3004  
Anime Service         → 3005  
Videos Service        → 3006  
Notification Service  → 3007  

---

RUN THE DOCKER
docker-compose up --build


KUBERNETES DEVELOPMENT
kubectl apply -f k8s/



POST   /api/auth/login          → User login  
GET    /api/movies              → Get all movies  
GET    /api/auth/admin/users    → Admin user management  



SECURITY FEATURE:-
• JWT Token Authentication  
• Rate Limiting  
• Password Hashing  
• Role-based Permissions  
• CORS Protection  
• Gateway-level Filtering  








