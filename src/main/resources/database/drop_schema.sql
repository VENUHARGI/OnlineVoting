-- =====================================================
-- ONLINE VOTING SYSTEM - DROP SCHEMA SCRIPT  
-- =====================================================
-- Oracle SQL Script to Drop/Reverse All Objects Created by schema.sql
-- Version: 1.0.0
-- Purpose: Cleanup/Reset database for fresh installation
-- =====================================================

-- WARNING: This script will permanently delete all voting system data!
-- Use with caution - backup your data before running this script

-- =====================================================
-- DROP TABLES (In reverse order due to foreign keys)
-- =====================================================

-- Drop tables that have foreign key dependencies first
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE VOTING_VOTE_SESSIONS CASCADE CONSTRAINTS';
    DBMS_OUTPUT.PUT_LINE('Table VOTING_VOTE_SESSIONS dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN -- Table does not exist
            DBMS_OUTPUT.PUT_LINE('Error dropping VOTING_VOTE_SESSIONS: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Table VOTING_VOTE_SESSIONS does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE VOTING_VOTES CASCADE CONSTRAINTS';
    DBMS_OUTPUT.PUT_LINE('Table VOTING_VOTES dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping VOTING_VOTES: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Table VOTING_VOTES does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE VOTING_PARTIES CASCADE CONSTRAINTS';
    DBMS_OUTPUT.PUT_LINE('Table VOTING_PARTIES dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping VOTING_PARTIES: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Table VOTING_PARTIES does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE VOTING_CANDIDATES CASCADE CONSTRAINTS';
    DBMS_OUTPUT.PUT_LINE('Table VOTING_CANDIDATES dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping VOTING_CANDIDATES: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Table VOTING_CANDIDATES does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE VOTING_CONSTITUENCIES CASCADE CONSTRAINTS';
    DBMS_OUTPUT.PUT_LINE('Table VOTING_CONSTITUENCIES dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping VOTING_CONSTITUENCIES: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Table VOTING_CONSTITUENCIES does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE VOTING_OTP_VERIFICATION CASCADE CONSTRAINTS';
    DBMS_OUTPUT.PUT_LINE('Table VOTING_OTP_VERIFICATION dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping VOTING_OTP_VERIFICATION: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Table VOTING_OTP_VERIFICATION does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE VOTING_USERS CASCADE CONSTRAINTS';
    DBMS_OUTPUT.PUT_LINE('Table VOTING_USERS dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping VOTING_USERS: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Table VOTING_USERS does not exist.');
        END IF;
END;
/

-- =====================================================
-- DROP SEQUENCES
-- =====================================================

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_VOTING_VOTE_SESSIONS';
    DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_VOTE_SESSIONS dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN -- Sequence does not exist
            DBMS_OUTPUT.PUT_LINE('Error dropping SEQ_VOTING_VOTE_SESSIONS: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_VOTE_SESSIONS does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_VOTING_VOTES';
    DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_VOTES dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping SEQ_VOTING_VOTES: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_VOTES does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_VOTING_PARTIES';
    DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_PARTIES dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping SEQ_VOTING_PARTIES: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_PARTIES does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_VOTING_CANDIDATES';
    DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_CANDIDATES dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping SEQ_VOTING_CANDIDATES: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_CANDIDATES does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_VOTING_CONSTITUENCIES';
    DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_CONSTITUENCIES dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping SEQ_VOTING_CONSTITUENCIES: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_CONSTITUENCIES does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_VOTING_OTP_VERIFICATION';
    DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_OTP_VERIFICATION dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping SEQ_VOTING_OTP_VERIFICATION: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_OTP_VERIFICATION does not exist.');
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_VOTING_USERS';
    DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_USERS dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            DBMS_OUTPUT.PUT_LINE('Error dropping SEQ_VOTING_USERS: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Sequence SEQ_VOTING_USERS does not exist.');
        END IF;
END;
/

-- =====================================================
-- DROP PROCEDURES
-- =====================================================

BEGIN
    EXECUTE IMMEDIATE 'DROP PROCEDURE CLEANUP_EXPIRED_OTPS';
    DBMS_OUTPUT.PUT_LINE('Procedure CLEANUP_EXPIRED_OTPS dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -4043 THEN -- Procedure does not exist
            DBMS_OUTPUT.PUT_LINE('Error dropping CLEANUP_EXPIRED_OTPS: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Procedure CLEANUP_EXPIRED_OTPS does not exist.');
        END IF;
END;
/

-- =====================================================
-- DROP INDEXES (Explicitly created ones)
-- =====================================================

-- Note: Primary key and foreign key indexes are dropped automatically with tables
-- But we'll drop the explicitly created performance indexes

DECLARE
    index_not_found EXCEPTION;
    PRAGMA EXCEPTION_INIT(index_not_found, -1418); -- ORA-01418: specified index does not exist
BEGIN
    -- Users table indexes
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_USERS_EMAIL';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_USERS_EMAIL dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_USERS_EMAIL does not exist.');
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_USERS_VERIFIED';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_USERS_VERIFIED dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_USERS_VERIFIED does not exist.');
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_USERS_ACTIVE';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_USERS_ACTIVE dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_USERS_ACTIVE does not exist.');
    END;
    
    -- OTP verification indexes
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_OTP_EMAIL';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_OTP_EMAIL dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_OTP_EMAIL does not exist.');
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_OTP_EXPIRY';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_OTP_EXPIRY dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_OTP_EXPIRY does not exist.');
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_OTP_PURPOSE';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_OTP_PURPOSE dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_OTP_PURPOSE does not exist.');
    END;
    
    -- Constituencies indexes
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_CONSTITUENCIES_STATE';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_CONSTITUENCIES_STATE dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_CONSTITUENCIES_STATE does not exist.');
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_CONSTITUENCIES_ACTIVE';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_CONSTITUENCIES_ACTIVE dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_CONSTITUENCIES_ACTIVE does not exist.');
    END;
    
    -- Parties indexes
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_PARTIES_ACTIVE';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_PARTIES_ACTIVE dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_PARTIES_ACTIVE does not exist.');
    END;
    
    -- Votes indexes
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_VOTES_USER_ID';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTES_USER_ID dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTES_USER_ID does not exist.');
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_VOTES_CONSTITUENCY_ID';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTES_CONSTITUENCY_ID dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTES_CONSTITUENCY_ID does not exist.');
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_VOTES_PARTY_ID';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTES_PARTY_ID dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTES_PARTY_ID does not exist.');
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_VOTES_SESSION_ID';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTES_SESSION_ID dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTES_SESSION_ID does not exist.');
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_VOTES_VOTED_AT';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTES_VOTED_AT dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTES_VOTED_AT does not exist.');
    END;
    
    -- Vote sessions indexes
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_VOTE_SESSIONS_SESSION_ID';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTE_SESSIONS_SESSION_ID dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTE_SESSIONS_SESSION_ID does not exist.');
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_VOTE_SESSIONS_USER_ID';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTE_SESSIONS_USER_ID dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTE_SESSIONS_USER_ID does not exist.');
    END;
    
    BEGIN
        EXECUTE IMMEDIATE 'DROP INDEX IDX_VOTING_VOTE_SESSIONS_EXPIRES_AT';
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTE_SESSIONS_EXPIRES_AT dropped successfully.');
    EXCEPTION WHEN index_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Index IDX_VOTING_VOTE_SESSIONS_EXPIRES_AT does not exist.');
    END;
END;
/

-- =====================================================
-- VERIFICATION QUERIES - Show what's left
-- =====================================================

PROMPT
PROMPT =====================================================
PROMPT VERIFICATION: Checking for any remaining objects
PROMPT =====================================================

-- Check for remaining tables
SELECT 'Remaining tables:' as MESSAGE FROM DUAL;
SELECT table_name 
FROM user_tables 
WHERE table_name LIKE 'VOTING_%' 
ORDER BY table_name;

-- Check for remaining sequences  
SELECT 'Remaining sequences:' as MESSAGE FROM DUAL;
SELECT sequence_name 
FROM user_sequences 
WHERE sequence_name LIKE 'SEQ_VOTING_%' 
ORDER BY sequence_name;

-- Check for remaining indexes
SELECT 'Remaining custom indexes:' as MESSAGE FROM DUAL;
SELECT index_name 
FROM user_indexes 
WHERE index_name LIKE 'IDX_VOTING_%' 
ORDER BY index_name;

-- Check for remaining procedures
SELECT 'Remaining procedures:' as MESSAGE FROM DUAL;
SELECT object_name 
FROM user_objects 
WHERE object_type = 'PROCEDURE' 
AND object_name LIKE '%OTP%'
ORDER BY object_name;

-- Summary
SELECT CASE 
    WHEN (SELECT COUNT(*) FROM user_tables WHERE table_name LIKE 'VOTING_%') = 0 
    AND (SELECT COUNT(*) FROM user_sequences WHERE sequence_name LIKE 'SEQ_VOTING_%') = 0
    AND (SELECT COUNT(*) FROM user_indexes WHERE index_name LIKE 'IDX_VOTING_%') = 0
    THEN '✅ ALL VOTING SYSTEM OBJECTS SUCCESSFULLY REMOVED'
    ELSE '⚠️  Some objects may still exist - check output above'
END as CLEANUP_STATUS
FROM DUAL;

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================

PROMPT
PROMPT =====================================================
PROMPT DROP SCHEMA SCRIPT COMPLETED
PROMPT =====================================================
PROMPT Database has been reset to pre-installation state
PROMPT You can now run schema.sql for a fresh installation
PROMPT =====================================================