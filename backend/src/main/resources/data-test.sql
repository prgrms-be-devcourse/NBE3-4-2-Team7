-- 테이블 초기화 (테스트 실행 전에 기존 데이터 삭제)
DELETE
FROM travel_offer;
DELETE
FROM guide_request;
DELETE
FROM travel;
DELETE
FROM travel_category;
DELETE
FROM guide;
DELETE
FROM member;
ALTER TABLE member
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE travel_category
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE travel
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE guide
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE guide_request
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE travel_offer
    ALTER COLUMN id RESTART WITH 1;

-- 테스트용 회원 데이터
-- 비밀번호 password123!
INSERT INTO member (id, email, password, name, provider, provider_id, image_url, role, has_guide_profile, created_at,
                    updated_at)
VALUES
    -- 일반 로그인 사용자
    (1, 'test@test.com', '$2a$10$uaPcWlFC83UR0u3cp94mxOHQwtWLq41XUEW5l7iFeeLLbTOtD2efa', '테스트유저', 'LOCAL', NULL, NULL,
     'ROLE_USER', false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),

    -- 소셜 로그인 사용자들
    (2, 'kakao@test.com', NULL, '카카오사용자', 'KAKAO', 'kakao123456', 'https://k.kakaocdn.net/dn/sample.jpg', 'ROLE_USER',
     false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (3, 'google@test.com', NULL, '구글사용자', 'GOOGLE', 'google123456', 'https://lh3.googleusercontent.com/sample.jpg',
     'ROLE_USER', false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (4, 'github@test.com', NULL, '깃허브사용자', 'GITHUB', 'github123456', 'https://avatars.githubusercontent.com/sample.jpg',
     'ROLE_USER', false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());