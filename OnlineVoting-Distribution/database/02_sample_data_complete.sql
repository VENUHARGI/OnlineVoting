-- =====================================================
-- ONLINE VOTING SYSTEM - SAMPLE DATA
-- =====================================================
-- Complete sample data for testing
-- Version: 1.0.0
-- Run AFTER executing schema_complete.sql
-- =====================================================

-- =====================================================
-- SAMPLE CONSTITUENCIES DATA
-- =====================================================
INSERT INTO VOTING_CONSTITUENCIES (NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
('New Delhi', 'Delhi', 'New Delhi', 'Parliamentary constituency covering central areas of Delhi including Connaught Place, India Gate, and government quarters', 1);

INSERT INTO VOTING_CONSTITUENCIES (NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
('Mumbai North', 'Maharashtra', 'Mumbai', 'Parliamentary constituency covering northern suburbs of Mumbai including Borivali, Kandivali, and Malad', 1);

INSERT INTO VOTING_CONSTITUENCIES (NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
('Bangalore Central', 'Karnataka', 'Bangalore Urban', 'Parliamentary constituency covering central Bangalore including Vidhana Soudha, Cubbon Park, and commercial district', 1);

INSERT INTO VOTING_CONSTITUENCIES (NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
('Chennai Central', 'Tamil Nadu', 'Chennai', 'Parliamentary constituency covering central Chennai including Marina Beach, Fort St. George, and business district', 1);

INSERT INTO VOTING_CONSTITUENCIES (NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
('Kolkata North', 'West Bengal', 'Kolkata', 'Parliamentary constituency covering northern Kolkata including Shyambazar, Baranagar, and industrial areas', 1);

INSERT INTO VOTING_CONSTITUENCIES (NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
('Hyderabad', 'Telangana', 'Hyderabad', 'Parliamentary constituency covering Hyderabad city including Charminar, HITEC City, and Old City', 1);

INSERT INTO VOTING_CONSTITUENCIES (NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
('Pune', 'Maharashtra', 'Pune', 'Parliamentary constituency covering Pune city including Koregaon Park, Aundh, and IT corridors', 1);

INSERT INTO VOTING_CONSTITUENCIES (NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
('Ahmedabad East', 'Gujarat', 'Ahmedabad', 'Parliamentary constituency covering eastern Ahmedabad including textile mills, commercial areas, and residential sectors', 1);

INSERT INTO VOTING_CONSTITUENCIES (NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
('Jaipur', 'Rajasthan', 'Jaipur', 'Parliamentary constituency covering Jaipur city including Pink City, Amber Fort area, and modern developments', 1);

INSERT INTO VOTING_CONSTITUENCIES (NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
('Lucknow', 'Uttar Pradesh', 'Lucknow', 'Parliamentary constituency covering Lucknow city including Hazratganj, Gomti Nagar, and government offices', 1);

COMMIT;

-- =====================================================
-- SAMPLE PARTIES DATA
-- =====================================================

-- New Delhi Parties
INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Bharatiya Janata Party', 'Lotus', 'Right-wing political party, part of National Democratic Alliance', '/images/logos/bjp-logo.png', '#FF9933', 1);

INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Indian National Congress', 'Hand', 'Centre-left political party, oldest political party in India', '/images/logos/congress-logo.png', '#19AAED', 1);

INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Aam Aadmi Party', 'Broom', 'Anti-corruption political party founded by Arvind Kejriwal', '/images/logos/aap-logo.png', '#0066CC', 1);

-- Mumbai Parties
INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Shiv Sena', 'Bow and Arrow', 'Regional party in Maharashtra advocating for Marathi people', '/images/logos/shivsena-logo.png', '#FF6B35', 1);

INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Nationalist Congress Party', 'Clock', 'Regional political party in Maharashtra', '/images/logos/ncp-logo.png', '#00A658', 1);

-- Bangalore Parties
INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Janata Dal (Secular)', 'Lady Farmer', 'Regional party in Karnataka advocating for secular politics', '/images/logos/jds-logo.png', '#228B22', 1);

INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Karnataka Janata Dal', 'Plant', 'Regional party in Karnataka', '/images/logos/kjd-logo.png', '#32CD32', 1);

-- Chennai Parties
INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Dravida Munnetra Kazhagam', 'Rising Sun', 'Regional party in Tamil Nadu advocating for Tamil rights', '/images/logos/dmk-logo.png', '#DC143C', 1);

INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('All India Anna Dravida Munnetra Kazhagam', 'Two Leaves', 'Regional party in Tamil Nadu', '/images/logos/aiadmk-logo.png', '#FF1493', 1);

-- Kolkata Parties
INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('All India Trinamool Congress', 'Flower', 'Regional party in West Bengal', '/images/logos/aitc-logo.png', '#006600', 1);

INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Communist Party of India (Marxist)', 'Hammer Sickle Star', 'Left-wing political party', '/images/logos/cpim-logo.png', '#FF0000', 1);

-- Hyderabad Parties
INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Telangana Rashtra Samithi', 'Car', 'Regional party in Telangana', '/images/logos/trs-logo.png', '#FF6F00', 1);

-- Pune Parties
INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Maratha Kranti Dal', 'Lion', 'Regional party advocating for Maratha interests', '/images/logos/mkd-logo.png', '#8B4513', 1);

-- Ahmedabad Parties
INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Gujarat Janata Dal', 'Reverse Sun', 'Regional party in Gujarat', '/images/logos/gjd-logo.png', '#FFD700', 1);

-- Jaipur Parties
INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Rajasthan Lok Dal', 'Wheel', 'Regional party in Rajasthan', '/images/logos/rld-logo.png', '#4B0082', 1);

-- Lucknow Parties
INSERT INTO VOTING_PARTIES (NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
('Samajwadi Party', 'Cycle', 'Regional party in Uttar Pradesh', '/images/logos/sp-logo.png', '#00008B', 1);

COMMIT;

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Verify constituencies inserted
SELECT COUNT(*) as TOTAL_CONSTITUENCIES FROM VOTING_CONSTITUENCIES;

-- Verify parties inserted
SELECT COUNT(*) as TOTAL_PARTIES FROM VOTING_PARTIES;

-- Display all constituencies
SELECT ID, NAME, STATE, DISTRICT FROM VOTING_CONSTITUENCIES ORDER BY ID;

-- Display all parties
SELECT ID, NAME, SYMBOL, COLOR_CODE FROM VOTING_PARTIES ORDER BY ID;

-- Show summary
SELECT 
    'Constituencies' as DATA_TYPE, COUNT(*) as RECORD_COUNT 
FROM VOTING_CONSTITUENCIES
UNION ALL
SELECT 
    'Parties', COUNT(*) 
FROM VOTING_PARTIES;
