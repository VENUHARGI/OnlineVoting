# ================================

# OFFLINE SCHEMA CREATION GUIDE

# ================================

## Overview

This application now uses offline schema management. The database schema must be created independently before running the application.

## Database Schema Setup

### 1. Connect to Oracle Database

```sql
-- Connect as SYSTEM or DBA user
sqlplus system/password@your_database
```

### 2. Create Application User (if not exists)

```sql
CREATE USER VOTINGAPP IDENTIFIED BY VotingApp123
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP;

GRANT CONNECT, RESOURCE TO VOTINGAPP;
GRANT CREATE SESSION TO VOTINGAPP;
GRANT CREATE TABLE TO VOTINGAPP;
GRANT CREATE SEQUENCE TO VOTINGAPP;
GRANT CREATE VIEW TO VOTINGAPP;
GRANT UNLIMITED TABLESPACE TO VOTINGAPP;
```

### 3. Create Schema

```sql
-- Connect as the application user
CONNECT VOTINGAPP/VotingApp123@your_database

-- Execute the schema creation script
@src/main/resources/database/schema.sql
```

### 4. Load Sample Data (Optional)

```sql
-- Load constituencies and parties data
@src/main/resources/database/constituencies-data.sql
@src/main/resources/database/parties-data.sql
@src/main/resources/database/sample-data.sql
```

## Application Configuration

### 1. Database Connection

Update `application.yml` with your database details:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@your_host:1521:your_service
    username: VOTINGAPP
    password: VotingApp123
```

### 2. Start Application

```bash
mvn spring-boot:run
```

## Testing Database Setup

### 1. Check Database Health

```bash
curl http://localhost:8080/voting/api/admin/database/health
```

### 2. Load Sample Data (if needed)

```bash
curl -X POST http://localhost:8080/voting/api/admin/database/load-sample-data
```

## Benefits of This Approach

1. **Faster Startup** - No Hibernate schema validation hanging
2. **Better Control** - Database migrations handled separately
3. **Production Ready** - Schema managed by DBAs
4. **Clear Separation** - Application logic separate from schema management
5. **Easier Debugging** - Database issues isolated from application startup

## Schema Files Location

- Main Schema: `src/main/resources/database/schema.sql`
- Sample Data: `src/main/resources/database/*.sql`

## Troubleshooting

### Schema Not Found Errors

- Verify schema is created: Check tables exist in database
- Check permissions: Ensure VOTINGAPP user has proper rights
- Test connectivity: Use database health endpoint

### Connection Issues

- Verify Oracle database is running
- Check connection parameters in application.yml
- Test with SQL client first
