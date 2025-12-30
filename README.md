# Online Voting System 🗳️

A secure, scalable online voting application built with Spring Boot, Oracle Database, and modern authentication mechanisms including OTP verification and JWT tokens.

## Features

✨ **Core Features:**

- **User Registration & Authentication** - Secure user signup with email verification via OTP
- **Two-Factor Authentication** - OTP-based login verification for enhanced security
- **Constituency Management** - Support for multiple constituencies across different regions
- **Party Management** - Political party listings with symbols and branding
- **Candidate Management** - Candidate information linked to parties and constituencies
- **Secure Voting** - User-authenticated voting with vote tracking and prevention of duplicate votes
- **Voting Analytics** - Comprehensive voting statistics and results

## Technology Stack

| Component             | Technology                   |
| --------------------- | ---------------------------- |
| **Backend Framework** | Spring Boot 3.2.1            |
| **Language**          | Java 17+                     |
| **Database**          | Oracle SQL                   |
| **Build Tool**        | Maven                        |
| **ORM**               | Hibernate JPA                |
| **Authentication**    | Spring Security, BCrypt, JWT |

## Prerequisites

Before running the application, ensure you have:

- **Java Development Kit (JDK)** 17 or higher
- **Apache Maven** 3.6+
- **Oracle Database** (version 11g or higher)
- **Git** (for cloning the repository)

**Optional:**

- IDE: VS Code, or Eclipse

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/VENUHARGI/OnlineVoting.git
cd OnlineVoting
```

### 2. Configure Environment Variables

Copy the template file and update with your credentials:

```bash
cp .env.example .env
```

Edit `.env` file with your configuration:

```env
# Database Configuration
DB_URL=jdbc:oracle:thin:@hostname:port:database
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password


# Environment
SPRING_PROFILES_ACTIVE=dev
```

### 3. Create Database Schema

Connect to your Oracle database and execute:

```bash
# Create schema and tables
sqlplus username/password@database @src/main/resources/database/01_schema_complete.sql

# Load sample data
sqlplus username/password@database @src/main/resources/database/02_sample_data_complete.sql
```

### Sample data creation

Use the sample data loaded from `02_sample_data_complete.sql`:

- **Constituencies** - 10 major Indian constituencies
- **Political Parties** - 16 major political parties
- **Candidates** - 22 candidate entries (2-3 per constituency)

### 4. Build the Application

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on: **http://localhost:8080/voting**

## Quick Start

### Access the Application

1. **Login Page** - http://localhost:8080/voting/login
2. **Signup Page** - http://localhost:8080/voting/signup
3. **API Documentation** - http://localhost:8080/voting/swagger-ui.html

### Test the Application

1. **Register** - Create a new account with email
2. **Verify OTP** - Use the OTP sent
3. **Login** - Login with registered credentials
4. **Verify OTP** - Complete the login OTP verification
5. **Vote** - Select constituency and vote for your preferred candidate

## Project Structure

```
OnlineVoting/
├── src/main/
│   ├── java/com/voting/system/
│   │   ├── config/           # Spring configuration
│   │   ├── controller/       # REST API endpoints
│   │   ├── model/            # JPA entities
│   │   ├── repository/       # Data access layer
│   │   ├── service/          # Business logic
│   │   └── VotingSystemApplication.java
│   └── resources/
│       ├── application.yml   # Application configuration
│       ├── database/         # SQL scripts
│       └── static/           # HTML, CSS, JS files
├── pom.xml                   # Maven dependencies
├── .env              # Environment template
└── README.md                 # This file
```

## API Endpoints

### Authentication APIs

```
POST   /api/auth/signup                 - User registration
POST   /api/auth/check-email            - Check email availability
POST   /api/auth/login                  - User login
POST   /api/auth/verify-otp             - Verify registration OTP
POST   /api/auth/verify-login-otp       - Verify login OTP
POST   /api/auth/logout                 - User logout
```

### User APIs

```
GET    /api/user/profile/{userId}       - Get user profile
PUT    /api/user/change-password        - Change password
GET    /api/user/voting-history         - Get voting history
```

### Voting APIs

```
GET    /api/voting/constituencies       - List all constituencies
GET    /api/voting/parties              - List all parties
GET    /api/voting/candidates           - Get candidates by constituency
POST   /api/voting/vote                 - Cast a vote
GET    /api/voting/results              - Get voting results
```

## Database Schema

### Main Tables

- **VOTING_USERS** - User accounts and authentication
- **VOTING_OTP_VERIFICATION** - OTP records for verification
- **VOTING_CONSTITUENCIES** - Electoral constituencies
- **VOTING_PARTIES** - Political parties
- **VOTING_CANDIDATES** - Candidate information
- **VOTING_VOTES** - Vote records
- **VOTING_VOTE_SESSIONS** - User voting sessions

## Configuration Files

### application.yml

Main Spring Boot configuration file containing:

- Database connection settings
- JPA/Hibernate configuration
- Security settings
- Email configuration
- OTP settings
- JWT configuration

### .env.example

Template for environment variables. Copy to `.env` and update with your actual credentials.

## Security Considerations

⚠️ **Important:**

1. **Never commit .env file** - It contains sensitive credentials
2. **Use strong JWT secret** - Generate with: `openssl rand -base64 32`
3. **Enable HTTPS in production** - Use valid SSL certificates
4. **Database credentials** - Use strong passwords, limit access
5. **Email credentials** - Use app-specific passwords, not main account password

## Troubleshooting

### Database Connection Error

- Verify Oracle DB is running
- Check DB_URL, DB_USERNAME, DB_PASSWORD in .env
- Ensure database schema is created

### Email Not Sending

- Verify email credentials in .env
- For Gmail: Use app-specific password, enable 2FA
- Check email configuration in application.yml

### Application Won't Start

- Check Java version: `java -version`
- Check Maven: `mvn -version`
- Review logs in console for specific errors

## Future Enhancements

- [ ] Real-time voting results dashboard
- [ ] SMS-based OTP (in addition to email)
- [ ] Vote encryption for enhanced security
- [ ] Audit logging for all voting activities
- [ ] Admin dashboard UI
- [ ] Multiple language support
- [ ] Mobile app integration

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is open source. Please check LICENSE file for details.

## Support

For issues, questions, or suggestions:

- Open an issue on GitHub
- Check existing documentation in `/database` folder
- Review API documentation at `/voting/swagger-ui.html`

## Author

**VENUHARGI**

- GitHub: https://github.com/VENUHARGI
- Repository: https://github.com/VENUHARGI/OnlineVoting

---

**Last Updated:** December 30, 2025

**Application Version:** 1.0.0

**Status:** Production Ready ✅
