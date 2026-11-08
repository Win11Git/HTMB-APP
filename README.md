# HTMB APP

HTMB (High Trust Management Bank) is a banking application project developed for learning and demonstrating mobile application development, backend development, and database integration.

## Project Structure

```text
HTMB Full App/
├── HTMB-app/       # Android application
├── htmb-backend/   # Spring Boot backend
├── database/       # Database schema
└── README.md
```

## Technologies Used

- **Java** – Application and backend programming
- **Android** – Mobile application development
- **Spring Boot** – Backend development
- **MariaDB / MySQL** – Database management
- **JWT** – Authentication
- **Gradle** – Android project build system
- **Maven** – Backend dependency management

## Features

- User registration and login
- Dashboard
- Cash in
- Cash out
- Money transfer
- Transaction history
- Receipt

## Requirements

- Java Development Kit (JDK)
- Android Studio
- XAMPP or another MariaDB/MySQL server
- Git

## Database Setup

1. Start MySQL or MariaDB using XAMPP.
2. Create a database named `htmb_db`.
3. Import `database/schema.sql` using phpMyAdmin or the MySQL command line.

## Backend Configuration

Configure these environment variables before running the Spring Boot backend:

```properties
DB_URL=jdbc:mysql://localhost:3306/htmb_db
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_long_random_secret
```

These are environment variable examples. Spring Boot must receive the variables through your operating system or IDE run configuration; a `.env` file is not automatically loaded.

## Running the Backend

Open the `htmb-backend` folder and run:

```powershell
.\mvnw.cmd spring-boot:run
```

Make sure the database is running and the required environment variables are configured.

## Running the Android App

1. Open the `HTMB-app` folder in Android Studio.
2. Allow Gradle to sync.
3. Configure the backend API address for your device or emulator.
4. Run the application.

## Disclaimer

HTMB is a learning and development project. It is not a real banking service and should not be used to handle real financial transactions or sensitive customer information.

## Author

Developed as a personal software development project.
