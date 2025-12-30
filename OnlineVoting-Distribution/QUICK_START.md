# Quick Start Reference

## 📦 Package Info

- **File:** OnlineVoting-v1.0.0.zip (58.93 MB)
- **Version:** 1.0.0
- **Build Date:** 2025-12-30

## ⚡ 5-Minute Setup

### 1️⃣ Extract Package

```bash
unzip OnlineVoting-v1.0.0.zip
cd OnlineVoting-Distribution
```

### 2️⃣ Setup Database (Oracle)

```sql
sqlplus system/password@ORCL
@database/schema.sql
@database/create_sequences.sql
@database/constituencies-data.sql
@database/parties-data.sql
```

### 3️⃣ Configure Application

Create `.env` file or use environment variables:

```properties
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@localhost:1521:ORCL
SPRING_DATASOURCE_USERNAME=your_user
SPRING_DATASOURCE_PASSWORD=your_pass
JWT_SECRET=your_32_char_secret_key
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password
```

### 4️⃣ Run Application

```bash
java -jar app/online-voting-system-1.0.0.jar
```

### 5️⃣ Access Application

```
http://localhost:8080
```

---

## 📋 Package Contents

```
OnlineVoting-Distribution/
├── app/
│   └── online-voting-system-1.0.0.jar
├── database/
│   ├── schema.sql
│   ├── create_sequences.sql
│   ├── constituencies-data.sql
│   └── parties-data.sql
├── docs/
│   ├── README.md
│   ├── DATABASE_SETUP.md
│   └── TESTING_GUIDE.md
├── README.md
└── INSTALL.md
```

---

## 🔧 System Requirements

| Component | Requirement     |
| --------- | --------------- |
| Java      | JDK 17+         |
| Database  | Oracle 21c+     |
| Memory    | 2GB RAM minimum |
| Disk      | 1GB free space  |

---

## ✅ Features

- ✓ Secure JWT Authentication
- ✓ OTP Email Verification
- ✓ Admin Dashboard
- ✓ Voter Management
- ✓ Real-time Voting
- ✓ Results Tracking
- ✓ SQL Injection Protection
- ✓ CSRF Protection

---

## 🚀 Common Commands

```bash
# Run on different port
java -Dserver.port=9090 -jar app/online-voting-system-1.0.0.jar

# Run with console logging
java -jar app/online-voting-system-1.0.0.jar --logging.level.root=DEBUG

# Run in background (Linux/Mac)
nohup java -jar app/online-voting-system-1.0.0.jar > app.log 2>&1 &

# Check if running
curl http://localhost:8080/api/health
```

---

## 🆘 Quick Troubleshooting

| Error                | Solution                                        |
| -------------------- | ----------------------------------------------- |
| DB Connection Failed | Check Oracle is running, verify URL/credentials |
| Port Already in Use  | Use different port: `-Dserver.port=9090`        |
| Email Not Sending    | Verify SMTP settings, check firewall port 587   |
| Java Not Found       | Install JDK 17, add to PATH                     |

---

## 📚 Documentation Files

| File              | Purpose                     |
| ----------------- | --------------------------- |
| README.md         | Overview and features       |
| INSTALL.md        | Detailed installation guide |
| DATABASE_SETUP.md | Database configuration      |
| TESTING_GUIDE.md  | Testing procedures          |

---

## 🔐 Security Tips

⚠️ **Before Production:**

1. Change default credentials
2. Use strong JWT secret (64+ chars)
3. Enable HTTPS/SSL
4. Secure database access
5. Regular backups
6. Monitor logs

---

## 📞 Support

For issues, check:

1. `docs/INSTALL.md` - Installation problems
2. `docs/DATABASE_SETUP.md` - Database issues
3. `docs/TESTING_GUIDE.md` - Functional testing

---

**Version:** 1.0.0 | **Date:** 2025-12-30
