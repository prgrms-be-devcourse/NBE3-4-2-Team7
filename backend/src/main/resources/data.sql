-- ✅ 일반 사용자 2명 (가이드 프로필 X, 관리자 X)
INSERT INTO member (name, email_id, password, role, has_guide_profile, created_at)
VALUES ('홍길동', 'user1@example.com', 'password123', 'USER', false, CURRENT_TIMESTAMP),
       ('이순신', 'user2@example.com', 'password123', 'USER', false, CURRENT_TIMESTAMP);

-- ✅ Guide 테이블에 가이드 데이터 추가
INSERT INTO guide (name, languages, activity_region, introduction, experience_years, is_deleted, created_at)
VALUES ('김가이드', 'English, Korean', '서울 지역', '경력 5년', 5, false, CURRENT_TIMESTAMP);

-- ✅ Member 테이블에서 guide_id를 참조하여 가이드와 연결
INSERT INTO member (name, email_id, password, role, has_guide_profile, guide_id, created_at)
VALUES ('김가이드', 'guide1@example.com', 'password123', 'USER', true,
        (SELECT id FROM guide WHERE name = '김가이드'), CURRENT_TIMESTAMP);

-- ✅ 관리자 멤버 1명 (role이 ADMIN)
INSERT INTO member (name, email_id, password, role, has_guide_profile, created_at)
VALUES ('관리자', 'admin@example.com', 'adminpass', 'ADMIN', false, CURRENT_TIMESTAMP);

-- ✅ 1. 여행 카테고리 추가
INSERT INTO travel_category (name, created_at)
VALUES ('도시 탐방', CURRENT_TIMESTAMP),
       ('해변 투어', CURRENT_TIMESTAMP),
       ('자연 탐험', CURRENT_TIMESTAMP),
       ('문화 체험', CURRENT_TIMESTAMP);

-- ✅ 여행 추가 (여행 카테고리와 연결)
-- ✅ 2. 여행 추가 (`travel_category`와 연결)
INSERT INTO travel (user_id, category_id, city, places, participants, start_date, end_date, content, status, is_deleted,
                    created_at)
VALUES ((SELECT id FROM member WHERE email_id = 'user1@example.com'),
        (SELECT id FROM travel_category WHERE name = '도시 탐방'),
        '서울', '경복궁, 남산타워', 2, '2024-02-10', '2024-02-12',
        '서울 주요 명소를 둘러보는 투어입니다.', 'COMPLETED', false, CURRENT_TIMESTAMP),

       ((SELECT id FROM member WHERE email_id = 'user2@example.com'),
        (SELECT id FROM travel_category WHERE name = '해변 투어'),
        '부산', '해운대, 광안대교', 2, '2024-03-05', '2024-03-07',
        '부산의 아름다운 해변과 명소를 둘러보는 투어입니다.', 'IN_PROGRESS', false, CURRENT_TIMESTAMP);

-- ✅ 서울 여행 가이드 요청 (홍길동 → 김가이드)
INSERT INTO guide_request (user_id, guide_id, travel_id, status, created_at)
VALUES (
           (SELECT id FROM member WHERE email_id = 'user1@example.com'),
           (SELECT id FROM guide WHERE name = '김가이드'),
           (SELECT id FROM travel WHERE city = '서울' LIMIT 1),
    'ACCEPTED',
            CURRENT_TIMESTAMP
    );

-- ✅ 부산 여행 가이드 요청 (이순신 → 김가이드)
INSERT INTO guide_request (user_id, guide_id, travel_id, status, created_at)
VALUES (
           (SELECT id FROM member WHERE email_id = 'user2@example.com'),
           (SELECT id FROM guide WHERE name = '김가이드'),
           (SELECT id FROM travel WHERE city = '부산' LIMIT 1),
    'PENDING',
            CURRENT_TIMESTAMP
    );