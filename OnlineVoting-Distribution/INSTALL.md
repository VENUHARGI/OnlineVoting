# Installation & Setup Guide

## System Requirements

| Requirement      | Version               |
| ---------------- | --------------------- |
| Java             | 17 or higher          |
| Oracle Database  | 21c or higher         |
| Operating System | Windows, Linux, macOS |
| RAM              | Minimum 2GB           |
| Disk Space       | Minimum 1GB           |

---

## Step 1: Database Setup

### 1.1 Connect to Oracle Database

```sql
sqlplus username/password@database_name
```

### 1.2 Execute Schema Scripts

Run the SQL scripts in this exact order:

```sql
-- 1. Create schema and tables
@database/schema.sql

-- 2. Create sequences
@database/create_sequences.sql

-- 3. Load sample data (optional)
@database/constituencies-data.sql
@database/parties-data.sql
```

### 1.3 Verify Database Setup

```sql
-- Check tables
SELECT table_name FROM user_tables;

-- Check sequences
SELECT sequence_name FROM user_sequences;
```

---

## Step 2: Environment Configuration

Create a `.env` file in the same directory as the JAR file:

```properties
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@localhost:1521:ORCL
SPRING_DATASOURCE_USERNAME=voting_user
SPRING_DATASOURCE_PASSWORD=secure_password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=oracle.jdbc.OracleDriver

# Server Configuration
SERVER_PORT=8080
SERVER_SERVLET_CONTEXT_PATH=/

# JWT Configuration
JWT_SECRET=your_super_secret_key_minimum_32_characters_long
JWT_EXPIRATION=86400000

# Email Configuration
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLED=true
```

---

## Step 3: Running the Application

### 3.1 From Command Line

```bash
# Navigate to app directory
cd OnlineVoting-Distribution/app

# Run the JAR file
java -jar online-voting-system-1.0.0.jar
```

### 3.2 With Custom Configuration

```bash
# Run with custom properties
java -Dspring.datasource.url=jdbc:oracle:thin:@host:1521:sid \
     -Dspring.datasource.username=user \
     -Dspring.datasource.password=pass \
     -jar online-voting-system-1.0.0.jar
```

### 3.3 Run on Different Port

```bash
java -Dserver.port=9090 -jar online-voting-system-1.0.0.jar
```

---

## Step 4: Access Application

1. **Open Browser:** http://localhost:8080
2. **Login Page** should load
3. **Create Admin Account** (first-time setup)
4. **Login** with your credentials

---

## Step 5: Verify Installation

### Test Checklist

- [ ] Database connection successful (check logs)
- [ ] Application starts without errors
- [ ] Login page loads on http://localhost:8080
- [ ] Can create new user account
- [ ] Can receive OTP via email
- [ ] Can complete voting process

---

## Troubleshooting

### Issue: "Cannot connect to database"

**Solution:**

- Verify Oracle Database is running
- Check SPRING_DATASOURCE_URL is correct
- Verify username and password
- Test connection manually with SQLPlus

### Issue: "Java: command not found"

**Solution:**

```bash
# Install Java 17
# Windows: Download from oracle.com/java/technologies/downloads
# Linux: sudo apt-get install openjdk-17-jdk
# macOS: brew install openjdk@17

# Verify installation
java -version
```

### Issue: "Port 8080 already in use"

**Solution:**

```bash
# Change port to 9090
java -Dserver.port=9090 -jar online-voting-system-1.0.0.jar

# Or find and kill process using port 8080
# Windows: netstat -ano | findstr :8080
# Linux: lsof -i :8080 | kill -9 <PID>
```

### Issue: "Email not sending"

**Solution:**

- For Gmail: Enable "Less secure app access" or use App Passwords
- Check SMTP settings in .env file
- Verify firewall isn't blocking port 587
- Test with a simple SMTP client

### Issue: "OTP table error"

**Solution:**

```sql
-- Run in Oracle Database
@database/fix_otp_table.sql
```

---

## Production Deployment

### Security Considerations

1. **Change Default Credentials:** Never use default passwords
2. **Enable HTTPS:** Use SSL/TLS certificates
3. **Strong JWT Secret:** Use 64+ character random string
4. **Database Backup:** Schedule regular backups
5. **Firewall Rules:** Restrict database access

### Production Commands

```bash
# Build from source (if needed)
mvn clean package -DskipTests

# Run with production settings
java -Xmx2g -Xms1g \
     -Dspring.profiles.active=production \
     -Dserver.ssl.enabled=true \
     -Dserver.ssl.key-store=/path/to/keystore.jks \
     -jar online-voting-system-1.0.0.jar
```

### Docker Deployment (Optional)

```dockerfile
FROM openjdk:17-slim
COPY app/online-voting-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build Docker image
docker build -t online-voting:1.0 .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=... \
  -e SPRING_DATASOURCE_USERNAME=... \
  online-voting:1.0
```

---

## Support Resources

- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **Oracle JDBC Driver:** https://www.oracle.com/database/technologies/appdev/jdbc.html
- **Security Best Practices:** https://owasp.org/

---

**Installation Date:** 2025-12-30  
**Version:** 1.0.0
