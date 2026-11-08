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

### Environment Variables

Configure these environment variables before running the Spring Boot backend:

```properties
DB_URL=jdbc:mysql://localhost:3306/htmb_db
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_base64_encoded_secret
```

These are environment variable examples. Spring Boot must receive the variables through your operating system or IDE run configuration; a `.env` file is not automatically loaded.

### application.properties

Open:

`htmb-backend/src/main/resources/application.properties`

Configure the backend properties:

```properties
spring.application.name=htmb-backend

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

htmb.jwt.secret=${JWT_SECRET}
htmb.jwt.expiration=3600000
```

The values inside `${...}` are supplied by the environment variables configured above.

## Running the Backend

Open the `htmb-backend` folder and run:

```powershell
.\mvnw.cmd spring-boot:run
```

Make sure the database is running and the required environment variables are configured.

## Running the Android App

1. Open the `HTMB-app` folder in Android Studio.
2. Allow Gradle to sync.
3. Configure the backend API address in the API Client.
4. Run the application.

### API Base URL Configuration

Open the API Client file in the Android project and locate the `BASE_URL` variable.

The base URL tells the Android application where to send API requests.

**For Android Emulator:**

```java
private static final String BASE_URL =
        "http://10.0.2.2:8080/";
```

**For a Physical Android Device:**

```java
private static final String BASE_URL =
        "http://YOUR_COMPUTER_LOCAL_IP:8080/";
```

Replace `YOUR_COMPUTER_LOCAL_IP` with your computer's local IP address.

**Important:**
- For an Android Emulator, use `10.0.2.2` to access the backend running on your computer.
- For a physical Android device, connect your phone and computer to the same Wi-Fi network.
- Make sure the Spring Boot backend is running.
- Do not use `localhost` for a physical Android device because it refers to the phone itself.
- Use HTTPS when connecting to a publicly deployed backend.

## Disclaimer

HTMB is a learning and development project. It is not a real banking service and should not be used to handle real financial transactions or sensitive customer information.

## Author

Herwin Magana
