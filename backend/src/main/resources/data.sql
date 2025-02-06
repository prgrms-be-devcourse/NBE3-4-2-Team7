-- Member 테이블 초기 데이터 삽입
INSERT INTO member (id, name, email, password, role, has_guide_profile, created_at, updated_at)
VALUES (1, 'John Doe', 'john@example.com', 'password123', 'USER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 'Jane Smith', 'jane@example.com', 'password456', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- TravelCategory 테이블 초기 데이터 삽입
INSERT INTO travel_category (name, created_at, updated_at)
VALUES ('힐링', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('자연', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Guide 테이블 초기 데이터 삽입
INSERT INTO guide (name, languages, activity_region, is_deleted, introduction, created_at, updated_at)
VALUES ('Jane Smith', 'English, Korean', 'Seoul', false, 'Experienced guide in Seoul', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- Member 테이블의 guide_id 업데이트
UPDATE member
SET guide_id = 1
WHERE id = 2