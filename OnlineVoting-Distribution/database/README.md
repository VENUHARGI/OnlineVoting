# Database Scripts - Readme

## Overview
This folder contains all database setup scripts for the Online Voting System.

## Files

### Main Setup
- **01_schema_complete.sql** - MAIN FILE
  - Complete database schema with all tables, sequences, and triggers
  - Includes all schema fixes and alterations in one file
  - Creates 7 tables: VOTING_USERS, VOTING_OTP_VERIFICATION, VOTING_CONSTITUENCIES, VOTING_PARTIES, VOTING_CANDIDATES, VOTING_VOTES, VOTING_VOTE_SESSIONS
  - Creates all required sequences and triggers
  - Creates performance indexes
  - Includes verification queries
  - **Run this first**

- **02_sample_data_complete.sql** - MAIN FILE
  - Complete sample/test data for development and testing
  - Consolidated from multiple data files
  - 10 sample constituencies
  - 17 sample political parties with color codes
  - **Run after 01_schema_complete.sql**

### Legacy Files (Kept for Reference)
- schema.sql - Original schema file
- create_sequences.sql - Legacy sequence creation
- add_status_column.sql - Legacy column addition (now in 01_schema_complete.sql)
- fix_otp_table.sql - Legacy OTP table fix (now in 01_schema_complete.sql)
- fix_voting_votes_schema.sql - Legacy votes schema fix (now in 01_schema_complete.sql)
- constituencies-data.sql - Legacy constituencies data (now in 02_sample_data_complete.sql)
- parties-data.sql - Legacy parties data (now in 02_sample_data_complete.sql)
- schema_simple.sql - Simplified schema version
- drop_schema.sql - Utility to drop all tables

## Quick Start

### Using Consolidated Files (Recommended)
```bash
# 1. Connect to Oracle
sqlplus username/password@database

# 2. Execute schema
@01_schema_complete.sql

# 3. Load sample data
@02_sample_data_complete.sql

# 4. Verify
SELECT COUNT(*) FROM VOTING_CONSTITUENCIES;
SELECT COUNT(*) FROM VOTING_PARTIES;
```

### Using Legacy Files (Alternative)
```bash
# Execute in this order:
@schema.sql
@create_sequences.sql
@add_status_column.sql
@fix_otp_table.sql
@fix_voting_votes_schema.sql
@constituencies-data.sql
@parties-data.sql
```

## Database Schema

### Tables Created
1. **VOTING_USERS** - User accounts and authentication
2. **VOTING_OTP_VERIFICATION** - OTP records for verification
3. **VOTING_CONSTITUENCIES** - Election constituencies
4. **VOTING_PARTIES** - Political parties
5. **VOTING_CANDIDATES** - Candidates per party/constituency
6. **VOTING_VOTES** - Cast votes with status tracking
7. **VOTING_VOTE_SESSIONS** - Voting session management

### Sequences Created
- SEQ_VOTING_USERS
- SEQ_VOTING_OTP_VERIFICATION
- SEQ_VOTING_CONSTITUENCIES
- SEQ_VOTING_PARTIES
- SEQ_VOTING_CANDIDATES
- SEQ_VOTING_VOTES
- SEQ_VOTING_VOTE_SESSIONS

## Sample Data

### Constituencies (10 total)
- New Delhi, Delhi
- Mumbai North, Maharashtra
- Bangalore Central, Karnataka
- Chennai Central, Tamil Nadu
- Kolkata North, West Bengal
- Hyderabad, Telangana
- Pune, Maharashtra
- Ahmedabad East, Gujarat
- Jaipur, Rajasthan
- Lucknow, Uttar Pradesh

### Political Parties (17 total)
- Bharatiya Janata Party (BJP)
- Indian National Congress (INC)
- Aam Aadmi Party (AAP)
- Shiv Sena
- Nationalist Congress Party (NCP)
- Janata Dal (Secular)
- And many more regional parties

## Features Included

✅ Auto-increment ID generation via sequences  
✅ Automatic UPDATED_AT timestamp management  
✅ Foreign key constraints for data integrity  
✅ Unique constraints to prevent duplicate records  
✅ Performance indexes on frequently queried columns  
✅ Check constraints for data validation  
✅ OTP expiry and usage tracking  
✅ Vote status management  
✅ Session-based voting verification  

## Troubleshooting

### Error: "Table already exists"
**Solution:** Run DROP schema script first or use newer file
```sql
@drop_schema.sql
@01_schema_complete.sql
```

### Error: "Sequence already exists"
**Solution:** Already handled in 01_schema_complete.sql, use that file

### Error: "OTP table missing column"
**Solution:** Use 01_schema_complete.sql which includes USED_AT column

### Error: "Votes table has wrong structure"
**Solution:** Use 01_schema_complete.sql with correct CANDIDATE_ID foreign key

## Performance Indexes

The schema includes 24 indexes for performance optimization:
- Email lookups (VOTING_USERS, VOTING_OTP_VERIFICATION)
- Status and state queries
- Timestamp-based searches
- Session tracking
- Vote verification

## Important Notes

1. **Order Matters:** Execute 01_schema_complete.sql before 02_sample_data_complete.sql
2. **Idempotency:** The consolidated files are designed to be run once
3. **Backup:** Always backup your database before running scripts
4. **Users:** Run as a user with CREATE TABLE and CREATE SEQUENCE privileges
5. **Compatibility:** Requires Oracle Database 21c or higher

## Cleanup

To remove all tables and start fresh:
```sql
@drop_schema.sql
```

Then rerun:
```sql
@01_schema_complete.sql
@02_sample_data_complete.sql
```

## Migration from Legacy Files

If you have old data from legacy files:
1. Backup current database
2. Run drop_schema.sql
3. Run 01_schema_complete.sql
4. Run 02_sample_data_complete.sql
5. Verify with provided queries

## Support

For issues:
- Check Oracle error messages in console
- Verify database user privileges
- Ensure Oracle version is 21c or higher
- Review table structure: `DESC VOTING_USERS;`
- Check triggers: `SELECT trigger_name FROM user_triggers;`
