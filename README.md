# WolfKin - Clothing Brand E-Commerce Platform

A modern full-stack e-commerce application for WolfKin clothing brand, built with Spring Boot backend and React frontend, containerized with Docker for easy development and deployment. Features JWT authentication, Redis caching, RabbitMQ messaging, and comprehensive admin functionality.

## 🚀 Quick Start

### Prerequisites

1. **Docker Desktop** - [Download and install Docker Desktop](https://www.docker.com/products/docker-desktop/)
2. **Git** - For version control

### Setup Instructions

1. **Start Docker Desktop**
   - Launch Docker Desktop and wait for it to start completely
   - Verify Docker is running: `docker --version`

2. **Run the development environment**
   
   **Windows (PowerShell):**
   ```powershell
   .\dev-setup.ps1
   ```
   
   **Manual setup:**
   ```bash
   # Copy environment configuration
   copy .env.docker .env
   
   # Build and start all services
   docker-compose -f docker-compose.yml -f docker-compose.dev.yml up --build
   ```

3. **Access the application**
   - **Frontend**: http://localhost:3000
   - **Backend API**: http://localhost:8080/api
   - **Health Check**: http://localhost:8080/actuator/health

## 🎯 Features

- **Authentication**: JWT-based user authentication with role-based access control
- **Products**: CRUD operations with categories, search, filtering, and pagination
- **Shopping Cart**: Client-side cart management with persistent state
- **Orders**: Complete order management system with order history
- **Admin Panel**: Product management, inventory control, and order management
- **Caching**: Redis caching for improved performance
- **Async Processing**: Email notifications using RabbitMQ
- **File Storage**: Image upload and management (S3 compatible)
- **Payments**: Stripe integration for secure payments
- **Containerization**: Full Docker support with docker-compose
- **CI/CD**: GitHub Actions workflows for automated deployment

## 🛠 Tech Stack

### Backend
- **Framework**: Spring Boot 3.x with Java 17+
- **Database**: PostgreSQL
- **Cache**: Redis
- **Message Queue**: RabbitMQ
- **Security**: Spring Security with JWT
- **Documentation**: OpenAPI/Swagger
- **Testing**: JUnit 5, TestContainers

### Frontend
- **Framework**: React 18 with Vite
- **Routing**: React Router v6
- **State Management**: React Query + Context API
- **UI Framework**: Tailwind CSS
- **HTTP Client**: Axios
- **Testing**: Vitest, React Testing Library

### DevOps
- **Containerization**: Docker & Docker Compose
- **CI/CD**: GitHub Actions
- **Deployment**: AWS ECS/EC2, Heroku ready
- **Monitoring**: Spring Boot Actuator

## 📁 Project Structure

```
E-comm/
├── backend/                 # Spring Boot API
│   ├── src/
│   │   ├── main/java/com/example/ecom/
│   │   │   ├── config/     # Configuration classes
│   │   │   ├── controller/ # REST controllers
│   │   │   ├── dto/        # Data transfer objects
│   │   │   ├── entity/     # JPA entities
│   │   │   ├── repository/ # Data repositories
│   │   │   ├── security/   # Security configuration
│   │   │   ├── service/    # Business logic
│   │   │   └── util/       # Utility classes
│   │   └── resources/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/               # React application
│   ├── src/
│   │   ├── components/     # Reusable components
│   │   ├── pages/          # Page components
│   │   ├── services/       # API services
│   │   ├── contexts/       # React contexts
│   │   ├── hooks/          # Custom hooks
│   │   └── utils/          # Utility functions
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml      # Local development setup
└── .github/workflows/      # CI/CD workflows
```

## 🚦 Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL (for local development without Docker)

### Quick Start with Docker

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd E-comm
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - RabbitMQ Management: http://localhost:15672 (guest/guest)

### Local Development

#### Backend Setup
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

## 📊 Database Schema

### Core Tables
- **users**: User accounts with roles
- **categories**: Product categories
- **products**: Product catalog with inventory
- **orders**: Customer orders
- **order_items**: Order line items

### Key Features
- Uses integer cents for precise money handling
- Optimized indexes for search and filtering
- Audit fields (created_at, updated_at)
- Soft delete support

## 🔐 Security

- JWT-based authentication with configurable expiration
- Role-based access control (USER, ADMIN)
- Password encryption with BCrypt
- CORS configuration for cross-origin requests
- Rate limiting for API endpoints
- Input validation and sanitization

## 📈 Performance

- **Caching**: Redis caching for frequently accessed data
- **Pagination**: Efficient pagination for large datasets
- **Database Indexing**: Optimized queries with proper indexes
- **Connection Pooling**: HikariCP for database connections
- **Async Processing**: Non-blocking operations for emails

## 🧪 Testing

### Backend Testing
```bash
cd backend
./mvnw test
```

### Frontend Testing
```bash
cd frontend
npm run test
```

### Integration Tests
```bash
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

## 🚀 Deployment

### Environment Variables

#### Backend (.env)
```env
DATABASE_URL=jdbc:postgresql://localhost:5432/ecom
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=yourpassword
JWT_SECRET=your-jwt-secret-key
REDIS_HOST=localhost
REDIS_PORT=6379
AWS_ACCESS_KEY_ID=your-aws-key
AWS_SECRET_ACCESS_KEY=your-aws-secret
S3_BUCKET_NAME=your-s3-bucket
```

#### Frontend (.env)
```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_STRIPE_PUBLIC_KEY=your-stripe-public-key
```

### Production Deployment

1. **Build Docker images**
   ```bash
   docker-compose -f docker-compose.prod.yml build
   ```

2. **Deploy to AWS ECS/EC2**
   ```bash
   # Push images to ECR
   docker tag ecom-backend:latest your-account.dkr.ecr.region.amazonaws.com/ecom-backend:latest
   docker push your-account.dkr.ecr.region.amazonaws.com/ecom-backend:latest
   ```

## 🔧 Configuration

### Application Properties
Key configuration options in `application.yml`:
- Database connection settings
- JWT configuration
- Redis connection
- File upload settings
- Email configuration
- Logging levels

## 📚 API Documentation

Once the backend is running, access the API documentation at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙋‍♂️ Support

For support and questions:
- Create an issue in the GitHub repository
- Check the documentation in `/docs`
- Review the API documentation

---

**Built with ❤️ for learning and demonstration purposes**