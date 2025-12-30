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
INSERT INTO VOTING_CONSTITUENCIES (ID, NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
(SEQ_VOTING_CONSTITUENCIES.NEXTVAL, 'New Delhi', 'Delhi', 'New Delhi', 'Parliamentary constituency covering central areas of Delhi including Connaught Place, India Gate, and government quarters', 1);

INSERT INTO VOTING_CONSTITUENCIES (ID, NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
(SEQ_VOTING_CONSTITUENCIES.NEXTVAL, 'Mumbai North', 'Maharashtra', 'Mumbai', 'Parliamentary constituency covering northern suburbs of Mumbai including Borivali, Kandivali, and Malad', 1);

INSERT INTO VOTING_CONSTITUENCIES (ID, NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
(SEQ_VOTING_CONSTITUENCIES.NEXTVAL, 'Bangalore Central', 'Karnataka', 'Bangalore Urban', 'Parliamentary constituency covering central Bangalore including Vidhana Soudha, Cubbon Park, and commercial district', 1);

INSERT INTO VOTING_CONSTITUENCIES (ID, NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
(SEQ_VOTING_CONSTITUENCIES.NEXTVAL, 'Chennai Central', 'Tamil Nadu', 'Chennai', 'Parliamentary constituency covering central Chennai including Marina Beach, Fort St. George, and business district', 1);

INSERT INTO VOTING_CONSTITUENCIES (ID, NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
(SEQ_VOTING_CONSTITUENCIES.NEXTVAL, 'Kolkata North', 'West Bengal', 'Kolkata', 'Parliamentary constituency covering northern Kolkata including Shyambazar, Baranagar, and industrial areas', 1);

INSERT INTO VOTING_CONSTITUENCIES (ID, NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
(SEQ_VOTING_CONSTITUENCIES.NEXTVAL, 'Hyderabad', 'Telangana', 'Hyderabad', 'Parliamentary constituency covering Hyderabad city including Charminar, HITEC City, and Old City', 1);

INSERT INTO VOTING_CONSTITUENCIES (ID, NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
(SEQ_VOTING_CONSTITUENCIES.NEXTVAL, 'Pune', 'Maharashtra', 'Pune', 'Parliamentary constituency covering Pune city including Koregaon Park, Aundh, and IT corridors', 1);

INSERT INTO VOTING_CONSTITUENCIES (ID, NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
(SEQ_VOTING_CONSTITUENCIES.NEXTVAL, 'Ahmedabad East', 'Gujarat', 'Ahmedabad', 'Parliamentary constituency covering eastern Ahmedabad including textile mills, commercial areas, and residential sectors', 1);

INSERT INTO VOTING_CONSTITUENCIES (ID, NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
(SEQ_VOTING_CONSTITUENCIES.NEXTVAL, 'Jaipur', 'Rajasthan', 'Jaipur', 'Parliamentary constituency covering Jaipur city including Pink City, Amber Fort area, and modern developments', 1);

INSERT INTO VOTING_CONSTITUENCIES (ID, NAME, STATE, DISTRICT, DESCRIPTION, IS_ACTIVE) VALUES 
(SEQ_VOTING_CONSTITUENCIES.NEXTVAL, 'Lucknow', 'Uttar Pradesh', 'Lucknow', 'Parliamentary constituency covering Lucknow city including Hazratganj, Gomti Nagar, and government offices', 1);

COMMIT;

-- =====================================================
-- SAMPLE PARTIES DATA
-- =====================================================

-- New Delhi Parties
INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Bharatiya Janata Party', 'Lotus', 'Right-wing political party, part of National Democratic Alliance', '/images/logos/bjp-logo.png', '#FF9933', 1);

INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Indian National Congress', 'Hand', 'Centre-left political party, oldest political party in India', '/images/logos/congress-logo.png', '#19AAED', 1);

INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Aam Aadmi Party', 'Broom', 'Anti-corruption political party founded by Arvind Kejriwal', '/images/logos/aap-logo.png', '#0066CC', 1);

-- Mumbai Parties
INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Shiv Sena', 'Bow and Arrow', 'Regional party in Maharashtra advocating for Marathi people', '/images/logos/shivsena-logo.png', '#FF6B35', 1);

INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Nationalist Congress Party', 'Clock', 'Regional political party in Maharashtra', '/images/logos/ncp-logo.png', '#00A658', 1);

-- Bangalore Parties
INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Janata Dal (Secular)', 'Lady Farmer', 'Regional party in Karnataka advocating for secular politics', '/images/logos/jds-logo.png', '#228B22', 1);

INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Karnataka Janata Dal', 'Plant', 'Regional party in Karnataka', '/images/logos/kjd-logo.png', '#32CD32', 1);

-- Chennai Parties
INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Dravida Munnetra Kazhagam', 'Rising Sun', 'Regional party in Tamil Nadu advocating for Tamil rights', '/images/logos/dmk-logo.png', '#DC143C', 1);

INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'All India Anna Dravida Munnetra Kazhagam', 'Two Leaves', 'Regional party in Tamil Nadu', '/images/logos/aiadmk-logo.png', '#FF1493', 1);

-- Kolkata Parties
INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'All India Trinamool Congress', 'Flower', 'Regional party in West Bengal', '/images/logos/aitc-logo.png', '#006600', 1);

INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Communist Party of India (Marxist)', 'Hammer Sickle Star', 'Left-wing political party', '/images/logos/cpim-logo.png', '#FF0000', 1);

-- Hyderabad Parties
INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Telangana Rashtra Samithi', 'Car', 'Regional party in Telangana', '/images/logos/trs-logo.png', '#FF6F00', 1);

-- Pune Parties
INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Maratha Kranti Dal', 'Lion', 'Regional party advocating for Maratha interests', '/images/logos/mkd-logo.png', '#8B4513', 1);

-- Ahmedabad Parties
INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Gujarat Janata Dal', 'Reverse Sun', 'Regional party in Gujarat', '/images/logos/gjd-logo.png', '#FFD700', 1);

-- Jaipur Parties
INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Rajasthan Lok Dal', 'Wheel', 'Regional party in Rajasthan', '/images/logos/rld-logo.png', '#4B0082', 1);

-- Lucknow Parties
INSERT INTO VOTING_PARTIES (ID, NAME, SYMBOL, DESCRIPTION, LOGO_URL, COLOR_CODE, IS_ACTIVE) VALUES 
(SEQ_VOTING_PARTIES.NEXTVAL, 'Samajwadi Party', 'Cycle', 'Regional party in Uttar Pradesh', '/images/logos/sp-logo.png', '#00008B', 1);

COMMIT;

-- =====================================================
-- SAMPLE CANDIDATES DATA
-- =====================================================
-- One candidate per party per constituency
-- =====================================================

-- NEW DELHI CONSTITUENCY CANDIDATES (Constituencies ID=1, Parties IDs: 1,2,3)
INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Rajesh Kumar Singh', 52, 'B.A., Law Degree', 'Senior politician with 20 years of experience in governance and public service. Strong advocate for urban development.', '/images/candidates/rajesh-kumar.png', 1, 1, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Priya Sharma', 45, 'M.A. Political Science', 'Grassroots activist and community organizer. Focuses on education and healthcare for underprivileged sections.', '/images/candidates/priya-sharma.png', 2, 1, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Amit Verma', 38, 'B.Tech, MBA', 'Anti-corruption campaigner with background in social activism. Champion of transparency in governance.', '/images/candidates/amit-verma.png', 3, 1, 1);

-- MUMBAI NORTH CONSTITUENCY CANDIDATES (Constituencies ID=2, Parties IDs: 1,4,5)
INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Vikram Deshmukh', 55, 'B.Sc., Diploma in Business', 'Businessman and industrialist. Known for promoting small and medium enterprises in Maharashtra.', '/images/candidates/vikram-deshmukh.png', 1, 2, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Suresh Patil', 48, 'B.A., M.A. Marathi Literature', 'Cultural advocate and author. Dedicated to preserving Marathi heritage and promoting regional interests.', '/images/candidates/suresh-patil.png', 4, 2, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Sheetal Waghmare', 42, 'M.A. Social Work', 'Social worker with focus on women and child welfare. Advocate for financial inclusion and cooperatives.', '/images/candidates/sheetal-waghmare.png', 5, 2, 1);

-- BANGALORE CENTRAL CONSTITUENCY CANDIDATES (Constituencies ID=3, Parties IDs: 1,6,7)
INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Arun Kumar', 50, 'B.Tech Computer Science, MBA', 'Tech entrepreneur and investor. Advocate for innovation, IT sector growth, and startup ecosystem.', '/images/candidates/arun-kumar.png', 1, 3, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Ramesh Reddy', 56, 'B.Sc. Agriculture, M.Phil', 'Agricultural economist and farmer activist. Champion of farmer welfare and rural development programs.', '/images/candidates/ramesh-reddy.png', 6, 3, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Divya Rao', 40, 'B.A. Psychology, M.A. Public Administration', 'Public servant focused on urban governance and inclusive development policies.', '/images/candidates/divya-rao.png', 7, 3, 1);

-- CHENNAI CENTRAL CONSTITUENCY CANDIDATES (Constituencies ID=4, Parties IDs: 1,8,9)
INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Karthik Iyer', 51, 'B.A. Tamil Literature, Law', 'Lawyer and cultural activist. Advocate for Tamil language preservation and cultural promotion.', '/images/candidates/karthik-iyer.png', 1, 4, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Anitha Ramakrishnan', 44, 'M.A. Economics', 'Economist and women''s rights activist. Works on economic justice and financial literacy.', '/images/candidates/anitha-ramakrishnan.png', 8, 4, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Jayakumar', 47, 'B.Sc. Marine Science', 'Environmental activist focused on coastal conservation and fisheries development.', '/images/candidates/jayakumar.png', 9, 4, 1);

-- KOLKATA NORTH CONSTITUENCY CANDIDATES (Constituencies ID=5, Parties IDs: 1,10,11)
INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Sourav Banerjee', 52, 'B.Sc. Physics, M.Tech', 'Scientist and technology policy expert. Focused on science education and research development.', '/images/candidates/sourav-banerjee.png', 1, 5, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Mamata Dey', 46, 'M.A. Political Science, Ph.D.', 'Political scientist and grassroots organizer. Advocate for Bengali cultural interests and worker rights.', '/images/candidates/mamata-dey.png', 10, 5, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Subhas Chakraborty', 58, 'B.A. History, Law Degree', 'Veteran communist leader with decades of experience in labor rights and socialist movements.', '/images/candidates/subhas-chakraborty.png', 11, 5, 1);

-- HYDERABAD CONSTITUENCY CANDIDATES (Constituencies ID=6, Parties IDs: 1,12)
INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Harish Rao', 49, 'B.E. Electrical, MBA', 'Business executive and technology advocate. Focused on Hyderabad''s development and IT sector growth.', '/images/candidates/harish-rao.png', 1, 6, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Kavya Reddy', 43, 'M.A. Telegu Literature, Social Work', 'Social activist focused on regional interests and cultural preservation in Telangana.', '/images/candidates/kavya-reddy.png', 12, 6, 1);

-- PUNE CONSTITUENCY CANDIDATES (Constituencies ID=7, Parties IDs: 1,4,13)
INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Nitin Gadkari', 54, 'B.Eng Civil Engineering, Law', 'Infrastructure development expert. Advocate for modern transportation and industrial development.', '/images/candidates/nitin-gadkari.png', 1, 7, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Dipak Deshmukh', 47, 'B.A. Marathi, M.A. Regional Studies', 'Regional activist committed to Maratha community interests and cultural preservation.', '/images/candidates/dipak-deshmukh.png', 4, 7, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Meena Thakre', 41, 'M.A. History, Ph.D.', 'Historian and educator. Dedicated to Maratha pride and social justice movements.', '/images/candidates/meena-thakre.png', 13, 7, 1);

-- AHMEDABAD EAST CONSTITUENCY CANDIDATES (Constituencies ID=8, Parties IDs: 1,14)
INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Mukesh Shah', 53, 'B.Comm, MBA Finance', 'Textile industry expert and businessman. Advocate for manufacturing sector and trade development.', '/images/candidates/mukesh-shah.png', 1, 8, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Rajani Patel', 44, 'B.A. Commerce, M.A. Economics', 'Economist and women entrepreneur. Focus on small business development and economic empowerment.', '/images/candidates/rajani-patel.png', 14, 8, 1);

-- JAIPUR CONSTITUENCY CANDIDATES (Constituencies ID=9, Parties IDs: 1,15)
INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Rajesh Singh', 50, 'B.A., Law Degree', 'Lawyer and civil rights activist. Advocate for rural development and agricultural prosperity.', '/images/candidates/rajesh-singh-jaipur.png', 1, 9, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Sunita Sharma', 45, 'M.A. Rajasthani Studies', 'Regional activist. Focused on Rajasthani cultural heritage and farmer welfare programs.', '/images/candidates/sunita-sharma.png', 15, 9, 1);

-- LUCKNOW CONSTITUENCY CANDIDATES (Constituencies ID=10, Parties IDs: 1,16)
INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Akhilesh Yadav', 51, 'B.Tech Civil Engineering', 'Technocrat and administrator. Focused on infrastructure, education, and rural development.', '/images/candidates/akhilesh-yadav.png', 1, 10, 1);

INSERT INTO VOTING_CANDIDATES (ID, NAME, AGE, QUALIFICATION, BIO, PHOTO_URL, PARTY_ID, CONSTITUENCY_ID, IS_ACTIVE) 
VALUES (SEQ_VOTING_CANDIDATES.NEXTVAL, 'Dimple Singh', 43, 'M.A. Political Science, Social Work', 'Social activist from farming community. Advocate for farmers'' rights and grassroots development.', '/images/candidates/dimple-singh.png', 16, 10, 1);

COMMIT;

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
