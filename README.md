# Local Service Marketplace

A full-stack web application connecting customers with local service providers.

## Tech Stack

- **Frontend**: React, Vite, Tailwind CSS
- **Backend**: Java, Spring Boot, Spring Security
- **Database**: PostgreSQL, Spring Data JPA/Hibernate
- **Auth**: JWT
- **Other**: Docker, Git/GitHub

## Features

- User registration & login with JWT authentication
- Password hashing with BCrypt
- Full CRUD REST API for users
- Protected routes (frontend and backend)
- Input validation and centralized error handling

## Getting Started

### Prerequisites
- Java 21+
- Node.js 20+
- PostgreSQL 16+

### Backend Setup
1. Navigate to `backend/`
2. Copy `.env.example` to `.env` and fill in your values
3. Run `./mvnw spring-boot:run`

### Frontend Setup
1. Navigate to `frontend/`
2. Copy `.env.example` to `.env` and fill in your values
3. Run `npm install`
4. Run `npm run dev`