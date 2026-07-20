# Local Service Marketplace

A full-stack web application connecting customers with local service providers — featuring JWT authentication, role-based authorization, bookings, reviews, and multiple AI-powered features including semantic search and a RAG-based chatbot.

## Tech Stack

- **Frontend**: React, Vite, Tailwind CSS
- **Backend**: Java, Spring Boot, Spring Security
- **Database**: PostgreSQL, Spring Data JPA/Hibernate
- **Auth**: JWT (JSON Web Tokens), BCrypt password hashing
- **AI**: Groq (LLM text generation), Cohere (embeddings for semantic search)
- **Testing**: JUnit 5, Mockito, MockMvc
- **Docs**: Swagger / OpenAPI
- **DevOps**: Docker, Docker Compose, Git/GitHub

## Features

### Core
- User registration & login with JWT authentication
- Role-based access control (CUSTOMER / PROVIDER)
- Full CRUD for service listings, with ownership-based authorization
- Booking system with status workflow (PENDING → CONFIRMED → COMPLETED)
- Reviews tied to completed bookings (one review per booking)
- Pagination and keyword/category search on service listings
- Centralized validation and error handling
- Interactive API documentation via Swagger UI

### AI-Powered
- **AI-generated service descriptions** — providers enter keywords, an LLM writes a polished listing description
- **AI review summarization** — condenses all reviews for a service into a short summary
- **Semantic search** — natural-language search using vector embeddings and cosine similarity, matching by meaning rather than exact keywords
- **RAG chatbot** — a floating assistant that retrieves relevant real services from the database and answers customer questions using that grounded context

## Getting Started

### Prerequisites
- Java 21+
- Node.js 20+
- PostgreSQL 16+
- Docker (optional, for containerized setup)
- Free API keys: [Groq](https://console.groq.com/keys), [Cohere](https://dashboard.cohere.com/api-keys)

### Backend Setup
1. Navigate to `backend/`
2. Copy `.env.example` to `.env` and fill in your values
3. Run `./mvnw spring-boot:run`
4. API available at `http://localhost:8080`
5. Swagger docs at `http://localhost:8080/swagger-ui/index.html`

### Frontend Setup
1. Navigate to `frontend/`
2. Copy `.env.example` to `.env` and fill in your values
3. Run `npm install`
4. Run `npm run dev`
5. App available at `http://localhost:5173`

### Docker Setup (Backend + Database)
1. From the project root, copy `.env.example` values into a root-level `.env`
2. Run `docker compose up --build`

### Running Tests
```bash
cd backend
./mvnw test
```

## Architecture

The backend follows a layered architecture: `Controller → Service → Repository → Database`, with DTOs separating internal entities from API request/response shapes. Authentication uses stateless JWT tokens validated on every request via a custom Spring Security filter. Authorization combines role-based checks (`@PreAuthorize`) with resource-ownership checks (e.g., only a service's provider can delete it).

## Future Improvements
- Image uploads for service listings
- Admin role and moderation dashboard
- Password reset via email
- Payment integration
- Refresh token support