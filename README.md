API GATEWAY AUTHENTICATION USING THE JWT AND RATE LIMITING

📁 Files to Upload (Complete Structure)
Core Project Files:
MAIN FOLDER
├── README.md                   
├── docker-compose.yml         
├── .env                       
├── .gitignore                  
├── package.json                
├── k8s/                         
│   ├── auth-deployment.yaml
│   ├── api-gateway-deployment.yaml
│   ├── movies-deployment.yaml
│   └── ingress.yaml
├── api-gateway/
│   ├── Dockerfile
│   ├── server-simple.js
│   └── .env
├── auth-service/
│   ├── Dockerfile
│   ├── server-working.js
│   ├── routes/
│   ├── models/
│   └── package.json
├── frontend/
│   ├── Dockerfile
│   ├── server.js
│   └── public/
├── movies-service/
├── music-service/
├── anime-service/
├── videos-service/
└── notification-service/

DEPENDENCIES
node_modules/
npm-debug.log*
 


 PROJECT OVERVIEW
Enterprise-grade API Gateway with microservices architecture featuring:
- JWT Authentication & Authorization
- Rate Limiting & Security
- MongoDB Integration
- Kubernetes Deployment
- Real-time User Management
 
BASIC ARCHITECTURE I FOLLOW
Frontend → API Gateway → Auth Service → MongoDB → Movies Service → Music Service → Anime Service → Videos Service → Notification Service



Access URLs
Frontend: http://localhost:3002
API Gateway: http://localhost:3000
Auth Service: http://localhost:3001



Default Credentials
Admin: admin@example.com / admin123
User: user@example.com / user123



Technologies Used
Backend: Node.js, Express.js
Database: MongoDB Atlas
Containerization: Docker, Docker Compose
Orchestration: Kubernetes
Authentication: JWT
Security: Rate Limiting, CORS, bcrypt



FEATURE
JWT-based Authentication
Role-based Access Control
Rate Limiting (1000 req/hr)
MongoDB Integration
Kubernetes Deployment
Real-time Admin Panel
Microservices Architecture
API Gateway Pattern


DOCKER SERVICES
API Gateway (Port 3000)
Auth Service (Port 3001)
Frontend (Port 3002)
Movies Service (Port 3003)
Music Service (Port 3004)
Anime Service (Port 3005)
Videos Service (Port 3006)
Notification Service (Port 3007)



Kubernetes Deployment
bash
kubectl apply -f k8s/



API ENDPOINT
GET /api/movies - Get all movies
POST /api/auth/login - User login
GET /api/auth/admin/users - Admin user management
And OTHERS



SECURITY FEATURE
JWT Token Authentication
Rate Limiting
CORS Protection
Password Hashing
Role-based Permissions
