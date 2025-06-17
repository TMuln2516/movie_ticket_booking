# 🎬 Movie Ticket Booking System

A full-featured movie ticket booking system built with **Spring Boot**, supporting functionalities like ticket reservation, user matching, real-time seat selection, showtime and cinema management, and revenue tracking. Designed for both end-users and cinema administrators.

---

## 🚀 Features

- 📅 **Showtime Management**: Create, update, and delete movie showtimes with room and cinema mapping.
- 🎟️ **Ticket Booking**:
    - Real-time seat selection using **WebSocket**.
    - Prevents overbooking and ensures consistent seat status across clients.
- 🤝 **User Matching**:
    - Smart pairing system that connects users with similar movie preferences, age, or gender.
    - Supports WebSocket-based real-time matching.
- 🎬 **Movie Management**:
    - Add, update, or delete movies with genres, directors, and cast members.
    - Upload movie posters and metadata.
- 🧑‍💼 **Person Management**:
    - Manage actors and directors with their profile, job, and date of birth.
- 🏢 **Cinema & Room Management**:
    - Multi-cinema support.
    - Create rooms within each cinema and assign them to showtimes.
- 📊 **Revenue Statistics**:
    - View revenue per day, week, movie, or showtime.
    - Get top 3 movies by ticket sales in a given time range.
- 🔔 **Real-Time Notifications**:
    - Alerts users via WebSocket if no match is found shortly before the showtime.
- 🔐 **Authentication & Authorization**:
    - JWT-based login and signup.
    - Role-based access control (`USER` and `ADMIN`).
- 📩 **OTP Email Verification**:
    - Email-based OTP for registration and password reset.
    - OTP retry limit and auto-deletion after invalid attempts.
- ❌ **Data Integrity**:
    - Keeps ticket history intact even if a related showtime is deleted.

---

## ⚙️ Getting Started

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/TMuln2516/movie_ticket_booking.git
cd movie_ticket_booking
```

### 2️⃣ Run with Docker Compose

```bash
docker-compose up --build
```