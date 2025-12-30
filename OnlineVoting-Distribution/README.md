# Online Voting System - Distribution Package

## Overview

A secure online voting system with OTP authentication built with Spring Boot and Oracle Database.

**Version:** 1.0.0  
**Java Version:** 17  
**Spring Boot:** 3.2.1

---

## Package Contents

```
OnlineVoting-Distribution/
├── app/
│   └── online-voting-system-1.0.0.jar    (Executable application)
├── database/
│   ├── schema.sql                        (Database schema)
│   ├── create_sequences.sql              (Create sequences)
│   ├── constituencies-data.sql           (Sample data)
│   ├── parties-data.sql                  (Sample data)
│   └── [other migration scripts]
├── docs/
│   ├── DATABASE_SETUP.md                 (Database setup guide)
│   ├── TESTING_GUIDE.md                  (Testing documentation)
│   └── README.md                         (This file)
└── INSTALL.md                            (Installation instructions)
```

---

## Quick Start

### Prerequisites

- **Java 17+** (JDK)
- **Oracle Database 21c+** (or compatible)
- **Maven 3.8+** (if building from source)

### Installation Steps

1. **Setup Database:**

   - Connect to your Oracle Database
   - Execute the SQL scripts in the `database/` folder in this order:
     - `schema.sql`
     - `create_sequences.sql`
     - `constituencies-data.sql`
     - `parties-data.sql`

2. **Configure Environment:**
   Create a `.env` file in the application directory with:

   ```
   SPRING_DATASOURCE_URL=jdbc:oracle:thin:@<host>:<port>:<sid>
   SPRING_DATASOURCE_USERNAME=<username>
   SPRING_DATASOURCE_PASSWORD=<password>
   JWT_SECRET=<your-secret-key>
   MAIL_USERNAME=<your-email>
   MAIL_PASSWORD=<your-app-password>
   ```

3. **Run Application:**

   ```bash
   java -jar app/online-voting-system-1.0.0.jar
   ```

4. **Access Application:**
   - URL: `http://localhost:8080`
   - Default port: **8080**

---

## Features

✅ **Secure Authentication:** JWT + OTP verification  
✅ **Email Integration:** OTP sent via email  
✅ **Admin Dashboard:** Manage constituencies, parties, and results  
✅ **Voter Management:** Register and authenticate voters  
✅ **Voting:** Real-time voting with security checks  
✅ **Results:** Live result tracking and reports

---

## Configuration

### application.yml Properties

- `spring.datasource.url` - Oracle DB connection URL
- `spring.datasource.username` - Database user
- `spring.datasource.password` - Database password
- `jwt.secret` - JWT signing secret (minimum 32 characters)
- `spring.mail.username` - Email sender address
- `spring.mail.password` - Email password/token

### Recommended Security

- Use a strong JWT secret (minimum 32 characters)
- Store credentials in environment variables
- Use HTTPS in production
- Enable database encryption

---

## API Endpoints

### Authentication

- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/verify-otp` - Verify OTP

### Admin

- `GET /api/admin/dashboard` - View dashboard
- `POST /api/admin/manage-voting` - Manage voting

### Voting

- `GET /api/voting/candidates` - Get candidates
- `POST /api/voting/cast-vote` - Cast a vote
- `GET /api/voting/results` - Get results

---

## Troubleshooting

### Database Connection Error

- Verify Oracle Database is running
- Check connection URL, username, and password
- Ensure database scripts have been executed

### Port Already in Use

```bash
java -jar -Dserver.port=9090 app/online-voting-system-1.0.0.jar
```

### Email Configuration Issues

- Verify email credentials are correct
- For Gmail, use app-specific passwords (not account password)
- Check firewall/antivirus isn't blocking SMTP (port 587)

---

## Testing

See `docs/TESTING_GUIDE.md` for comprehensive testing procedures.

---

## Support & Documentation

- **Database Setup:** See `docs/DATABASE_SETUP.md`
- **Testing Guide:** See `docs/TESTING_GUIDE.md`
- **Spring Boot Docs:** https://spring.io/projects/spring-boot
- **Oracle JDBC:** Use ojdbc11.jar or later

---

## License

This project is provided as-is for educational and voting purposes.

---

## Version History

**v1.0.0** (2025-12-30)

- Initial release
- Core voting functionality
- Admin management features
- OTP authentication

---

For more information, refer to the documentation files included in the `docs/` folder.
