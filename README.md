# TessieVerse - Victorian Dark Academia Social Network

**TessieVerse** is a modern social network for book lovers, inspired by Victorian aesthetics and Dark Academia culture. Share reviews, track your reading progress, discover new books, and connect with fellow readers in an elegant, immersive environment.

Java 23 | Spring Boot 3.5.7 | React 18 | PostgreSQL | Tailwind CSS | MIT License

---

## Features

### Authentication and Profiles
- JWT-based authentication
- User registration with unique username and email
- Profile editing (name, bio, email)
- Avatar upload support
- Follow/Unfollow system
- User statistics (followers, following, reading stats)

### Book Management
- Add books with cover images, title, author, genre, pages, year, and keywords
- Personal library with reading status:
  - Want to Read (Quero Ler)
  - Currently Reading (Lendo)
  - Already Read (Lido)
- Reading progress tracking with page counter
- Favorite books system

### Reviews and Interactions
- Write and edit book reviews with ratings (1-5 stars)
- Upload images to reviews
- Like/Unlike reviews (toggle system)
- Comment system on reviews
- Delete own comments

### Social Features
- Real-time chat between users
- Private messaging
- Follow users to see their activity
- Suggested users based on reading tastes

### Reading Statistics
- Books read counter
- Total pages read
- Average pages per book
- Top genres analysis
- Genre evolution over time
- Current reading progress
- Reading days statistics

### Discovery and Recommendations
- Explore trending books
- Search for books and users
- Personalized recommendations based on book keywords and tags
- Books of similar genres appear in recommendations

### Design
- Dark Academia aesthetic (ebony, mahogany, moss green, antique gold)
- Victorian ornamentation
- Glassmorphism effects
- Fully responsive layout
- Elegant typography (Cormorant Garamond + Karla)

---

## Tech Stack

### Backend

| Technology | Version |
|------------|---------|
| Java | 23.0.1 |
| Spring Boot | 3.5.7 |
| Spring Security | 6.x |
| Spring Data JPA | 3.x |
| PostgreSQL | 18.3 |
| JWT | 0.11.5 |
| Hibernate | 6.x |
| Maven | 3.9.11 |

### Frontend

| Technology | Version |
|------------|---------|
| React | 18 |
| TypeScript | 5.x |
| Node.js | 24.11.1 |
| npm | 11.6.2 |
| TanStack Router | 1.x |
| TanStack Query | 5.x |
| Tailwind CSS | 3.x |
| Vite | 5.x |


### Development Tools
- Git for version control
- Maven for dependency management
- ESLint and Prettier for code quality
- PostgreSQL for database

---

## API Endpoints Overview

| Category | Endpoint | Method | Description |
|----------|----------|--------|-------------|
| Auth | `/auth/register` | POST | User registration |
| Auth | `/auth/login` | POST | User login |
| Users | `/users/me` | GET | Current user profile |
| Users | `/users/{id}` | GET | User profile by ID |
| Users | `/users/{id}/follow` | POST | Follow user |
| Users | `/users/{id}/unfollow` | POST | Unfollow user |
| Books | `/books` | GET | List all books |
| Books | `/books/recommendations` | GET | Personalized recommendations |
| Books | `/books/{id}` | GET | Book details |
| Library | `/library/me` | GET | User's bookshelf |
| Library | `/library/books/{id}/status` | POST | Update reading status |
| Library | `/library/books/{id}/progress` | PUT | Update reading progress |
| Reviews | `/reviews` | GET | All reviews |
| Reviews | `/reviews/{id}/like` | POST | Like/unlike review |
| Stats | `/stats/me` | GET | Reading statistics |
| Chat | `/chats` | GET | User conversations |
| Chat | `/chats/{id}/messages` | GET | Chat messages |

---

## Key Features Implementation

### Recommendation System
- Based on book keywords and tags
- Analyzes user's reading history
- Jaccard similarity algorithm
- Returns top 5 personalized recommendations

### Reading Progress
- Track pages read versus total pages
- Visual progress bar with percentage
- Start and finish dates auto-recorded
- Updates reading statistics in real-time

### Real-time Chat
- WebSocket implementation (STOMP protocol)
- Private conversations between users
- Read receipts
- Persistent message history

---

## License

This project is licensed under the MIT License.

---

## Author

**Isabelly**  
TessieVerse - Literary Social Network

---

*Scripta Manent - The written word remains.*
