# 🛍️ WolfKin E-commerce Platform

A modern, full-stack e-commerce platform with advanced features including real-time inventory management, payment gateway integration, admin dashboard, and intelligent caching system. Built with Spring Boot and React, featuring a beautiful bewakoof.com-inspired UI.

![WolfKin](WolfKin.jpeg)

## 🌟 Features

### Customer Features
- 🔍 **Advanced Product Search & Filtering** - Search by name, filter by category, sort by price/popularity
- 🛒 **Real-time Shopping Cart** - Dynamic cart with instant price calculations
- 💳 **Multiple Payment Methods** - Razorpay integration supporting:
  - Credit/Debit Cards
  - UPI
  - Net Banking
  - Wallets
  - Cash on Delivery (COD)
- 🎫 **Coupon System** - Apply discount coupons with real-time validation
- 📦 **Order Tracking** - Complete order history with status tracking
- 👤 **User Profile Management** - Secure authentication with JWT
- 📱 **Responsive Design** - Beautiful bewakoof.com-inspired UI with Tailwind CSS

### Admin Features
- 📊 **Analytics Dashboard** - Real-time statistics and insights
- 📦 **Product Management** - Add, edit, delete products with inventory control
- 🛍️ **Order Management** - View and update order statuses
- 👥 **User Management** - Monitor customer activities
- 📈 **Sales Reports** - Track revenue and order trends

### Technical Features
- ⚡ **Redis Caching** - Lightning-fast product queries with intelligent cache invalidation
- 🔒 **JWT Authentication** - Secure user sessions
- 📨 **RabbitMQ Integration** - Asynchronous order processing
- 🐳 **Docker Containerization** - Easy deployment and scaling
- 🔄 **RESTful API** - Clean and well-documented endpoints
- 💾 **Optimized Database** - PostgreSQL with proper indexing and relationships

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 3.2.0
- **Database:** PostgreSQL 15
- **Cache:** Redis
- **Message Queue:** RabbitMQ
- **Security:** Spring Security + JWT
- **Payment:** Razorpay SDK 1.4.6
- **Build Tool:** Maven

### Frontend
- **Framework:** React 18
- **Build Tool:** Vite
- **Styling:** Tailwind CSS (Amber/Yellow theme)
- **State Management:** Context API
- **HTTP Client:** Axios
- **Routing:** React Router v6
- **Icons:** Lucide React

### DevOps
- **Containerization:** Docker & Docker Compose
- **Deployment:** Railway (Production-ready)
- **Version Control:** Git & GitHub

## 🚀 Quick Start

### Prerequisites
- Docker Desktop
- Git

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/RitesH000027/WolfKin_.git
cd WolfKin_
```

2. **Start all services**
```bash
docker-compose up -d
```

3. **Access the application**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Admin Dashboard: http://localhost:3000/admin/dashboard

### Default Credentials

**Admin Access:**
```
Email: admin@wolfkin.com
Password: admin123
```

**Test User:**
```
Email: user@example.com
Password: password123
```

⚠️ **Important:** Change admin password after first login!

## 📦 Sample Data

The application comes pre-loaded with:
- **17 Products** across multiple categories (T-shirts, Hoodies, Jackets, Jeans, Joggers)
- **3 Coupons:**
  - `WELCOME10` - 10% off (Max ₹100)
  - `SAVE500` - Flat ₹500 off
  - `MEGA20` - 20% off (Max ₹500)
- **Sample Admin and User accounts**

## 🔧 Configuration

### Environment Variables

**Backend** (`.env` in root):
```env
POSTGRES_DB=ecom
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
REDIS_PASSWORD=redis_password
RABBITMQ_DEFAULT_USER=rabbitmq
RABBITMQ_DEFAULT_PASS=rabbitmq
JWT_SECRET=your-super-secret-jwt-key
RAZORPAY_KEY_ID=your_razorpay_key_id
RAZORPAY_KEY_SECRET=your_razorpay_key_secret
```

**Frontend** (`frontend/.env`):
```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_RAZORPAY_KEY_ID=your_razorpay_key_id
```

### Razorpay Setup (Optional)

For full payment functionality:

1. Sign up at [razorpay.com](https://razorpay.com)
2. Get your Test API keys from dashboard
3. Add keys to `.env` files:
   - Root `.env`: Add `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET`
   - Frontend `.env`: Add `VITE_RAZORPAY_KEY_ID`
4. Rebuild containers:
```bash
docker-compose build
docker-compose up -d
```

**Note:** COD (Cash on Delivery) works without Razorpay keys!

## 📖 API Documentation

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Get current user

### Products
- `GET /api/products` - Get all products (with pagination, search, filter, sort)
- `GET /api/products/{id}` - Get product by ID
- `POST /api/admin/products` - Create product (Admin)
- `PUT /api/admin/products/{id}` - Update product (Admin)
- `DELETE /api/admin/products/{id}` - Delete product (Admin)

### Orders
- `POST /api/orders` - Create order
- `GET /api/orders` - Get user orders
- `GET /api/orders/{id}` - Get order details
- `GET /api/orders/admin/all` - Get all orders (Admin)
- `PUT /api/orders/admin/{id}/status` - Update order status (Admin)

### Payments
- `POST /api/payments/create-order` - Create payment order
- `POST /api/payments/verify` - Verify Razorpay payment
- `POST /api/payments/confirm-cod/{orderId}` - Confirm COD order

### Coupons
- `POST /api/coupons/validate` - Validate coupon code

### Categories
- `GET /api/categories` - Get all categories

## 🏗️ Architecture

```
┌─────────────────┐      ┌─────────────────┐
│                 │      │                 │
│  React Frontend │◄────►│  Nginx (Port 3000)
│   (Vite Build)  │      │                 │
│                 │      │                 │
└─────────────────┘      └─────────────────┘
         │
         │ HTTP/REST
         │
         ▼
┌─────────────────────────────────────────┐
│                                         │
│      Spring Boot Backend (Port 8080)    │
│                                         │
│  ┌──────────────────────────────────┐  │
│  │   Controllers (REST Endpoints)    │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │   Services (Business Logic)      │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │   Repositories (Data Access)     │  │
│  └──────────────────────────────────┘  │
│                                         │
└─────────────────────────────────────────┘
         │                    │
         │                    │
         ▼                    ▼
┌─────────────────┐   ┌─────────────────┐
│   PostgreSQL    │   │      Redis      │
│   (Database)    │   │     (Cache)     │
└─────────────────┘   └─────────────────┘
         │
         │
         ▼
┌─────────────────┐
│    RabbitMQ     │
│ (Message Queue) │
└─────────────────┘
```

## 🎨 Design

The UI is inspired by [bewakoof.com](https://bewakoof.com) featuring:
- **Bold Typography** - Eye-catching product names and CTAs
- **Amber/Yellow Theme** - Warm, inviting color scheme (changed from blue)
- **Modern Cards** - Clean product displays with hover effects
- **Uppercase Buttons** - Strong, actionable buttons
- **Responsive Layout** - Works seamlessly on all devices
- **Smooth Animations** - Polished user experience

## 📝 Project Structure

```
WolfKin_/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/example/ecom/
│   │       ├── config/        # Security, CORS config
│   │       ├── controller/    # REST controllers
│   │       ├── dto/           # Data transfer objects
│   │       ├── entity/        # JPA entities
│   │       ├── repository/    # Data repositories
│   │       ├── security/      # JWT, authentication
│   │       └── service/       # Business logic
│   ├── src/main/resources/
│   │   ├── application.yml    # App configuration
│   │   ├── application-dev.yml
│   │   ├── application-docker.yml
│   │   └── application-prod.yml
│   ├── Dockerfile
│   └── pom.xml               # Maven dependencies
│
├── frontend/                  # React frontend
│   ├── src/
│   │   ├── components/       # Reusable components
│   │   │   ├── Admin/        # Admin components
│   │   │   └── Layout/       # Layout components
│   │   ├── contexts/         # Auth, Cart contexts
│   │   ├── pages/            # Page components
│   │   │   ├── Admin/        # Admin pages
│   │   │   ├── Cart.jsx
│   │   │   ├── Checkout.jsx
│   │   │   ├── Payment.jsx
│   │   │   └── ...
│   │   ├── services/         # API services
│   │   └── App.jsx
│   ├── Dockerfile
│   ├── package.json
│   └── tailwind.config.js
│
├── init-scripts/             # Database initialization
│   └── 01-init.sql
├── docker-compose.yml        # Docker orchestration
├── docker-compose.dev.yml    # Development overrides
├── docker-compose.prod.yml   # Production overrides
├── .env.example             # Environment template
├── .gitignore               # Git ignore rules
└── README.md                # This file
```

## 🚢 Deployment

### Docker Compose (Production)

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### Cloud Platforms

**Railway:**
1. Connect your GitHub repository
2. Add PostgreSQL and Redis services
3. Deploy backend and frontend
4. Configure environment variables

**AWS:**
- Use ECS with Docker images
- Set up RDS for PostgreSQL
- ElastiCache for Redis

**Other Options:**
- Google Cloud (Cloud Run, GKE)
- Azure (App Service, AKS)
- DigitalOcean (App Platform)
- Render or Heroku

## 🧪 Testing

### Test Payment Cards (Razorpay Test Mode)

**Credit/Debit Card:**
```
Card Number: 4111 1111 1111 1111
CVV: Any 3 digits
Expiry: Any future date
Name: Any name
```

**Test UPI IDs:**
```
success@razorpay  (Success)
failure@razorpay  (Failure)
```

### Test Coupons
```
WELCOME10 - 10% off (Max ₹100)
SAVE500   - Flat ₹500 off (Min order ₹2000)
MEGA20    - 20% off (Max ₹500, Min order ₹1500)
```

## 🔒 Security Features

- ✅ JWT-based authentication with expiry
- ✅ Password hashing (BCrypt)
- ✅ CORS configuration
- ✅ SQL injection prevention (JPA/Hibernate)
- ✅ XSS protection
- ✅ Payment signature verification (Razorpay)
- ✅ Environment variable secrets
- ✅ Role-based access control (USER, ADMIN)
- ✅ Secure session management

## 🐛 Troubleshooting

### Common Issues

**Backend won't start:**
```bash
# Check logs
docker logs ecom-backend

# Restart services
docker-compose restart backend
```

**Frontend shows API errors:**
- Verify backend is running: http://localhost:8080/api/test/health
- Check `VITE_API_BASE_URL` in frontend/.env
- Ensure no CORS errors in browser console

**Database connection issues:**
- Ensure PostgreSQL container is healthy: `docker ps`
- Check database credentials in .env
- Verify database created: `docker exec -it ecom-postgres psql -U postgres -l`

**Payment failures:**
- Verify Razorpay keys are correct in both backend and frontend
- Use test card numbers in test mode
- Check backend logs for Razorpay API errors
- Ensure Razorpay account is in test mode

**Orders not showing:**
- Check browser console for errors
- Verify JWT token is valid
- Clear browser cache and localStorage



## 🛣️ Roadmap

**Completed:**
- ✅ User authentication & authorization
- ✅ Product catalog with search, filter, sort
- ✅ Shopping cart functionality
- ✅ Order management system
- ✅ Admin dashboard
- ✅ Payment gateway integration (Razorpay)
- ✅ Coupon system
- ✅ Redis caching
- ✅ Docker containerization
- ✅ Responsive UI design

**Upcoming:**
- [ ] Email notifications (order confirmation, shipping updates)
- [ ] Product reviews and ratings
- [ ] Wishlist functionality
- [ ] Social authentication (Google, Facebook)
- [ ] Advanced analytics and reports
- [ ] Invoice generation (PDF)
- [ ] Mobile app (React Native)
- [ ] Multi-vendor support
- [ ] Real-time order tracking
- [ ] Chat support integration

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Developer

**Ritesh Gupta**
- GitHub: [@RitesH000027](https://github.com/RitesH000027)
- Email: rtriteshgupta2020@gmail.com
- Repository: [WolfKin_](https://github.com/RitesH000027/WolfKin_)

## 🙏 Acknowledgments

- Design inspiration: [bewakoof.com](https://bewakoof.com)
- Payment gateway: [Razorpay](https://razorpay.com)
- Icons: [Lucide React](https://lucide.dev)
- UI Framework: [Tailwind CSS](https://tailwindcss.com)

## 📊 Project Stats

- **Lines of Code:** 10,000+
- **Files:** 116+
- **Languages:** Java, JavaScript, SQL
- **Docker Services:** 5 (Frontend, Backend, PostgreSQL, Redis, RabbitMQ)
- **API Endpoints:** 25+
- **Database Tables:** 8

---

⭐ **Star this repository if you found it helpful!**

📧 **For queries or suggestions, feel free to reach out!**

🚀 **Live Demo:** [Coming Soon - Deploy to Railway]

---

**Built with ❤️ by Ritesh Gupta**