#  EventFlow Backend – Spring Boot

The **EventFlow** backend is a scalable and secure RESTful API built using **Spring Boot**.  
It powers the event planning platform, enabling users to create events, manage agenda items, invite participants, comment on events, and reset passwords securely.

---
##  Technologies & Tools

- **Java 17 & Spring Boot** – Main framework for RESTful APIs
- **Spring Security & JWT** – Stateless authentication and endpoint protection
- **PostgreSQL** – Relational database for data storage
- **JPA (Hibernate)** – ORM for database interaction
- **Swagger** – API documentation and testing at `/swagger-ui/index.html`
- **JUnit & Mockito** – Unit and integration testing
- **JavaMailSender** – For sending password reset emails

---

##  Key Functionalities

### 👤 User Management
- Register, login, and secure sessions using JWT
- Update profile or delete account
- Password reset via email token

### 📅 Event Management
- Create, update, and delete events with different fields

### 🧾 Agenda Items
- Manage sub-events associated with a main event

### 📨 Invitations
- Invite users to events and share its information with them

### 💬 Comment System
- Post and delete comments on events
- Event creators can moderate comments
---

## ✅ Testing

- Unit tests for core services (e.g., UserService)
- Password reset, registration, and authentication are fully tested
- Test coverage ensures reliability and maintainability

