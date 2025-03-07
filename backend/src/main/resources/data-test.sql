-- 테스트용 회원 데이터
-- 비밀번호 password123!
INSERT INTO member (id, email, password, name, provider, provider_id, image_url, role, has_guide_profile, created_at,
                    updated_at)
VALUES (1, 'test@test.com', '$2a$10$uaPcWlFC83UR0u3cp94mxOHQwtWLq41XUEW5l7iFeeLLbTOtD2efa', '테스트유저', 'LOCAL', NULL,
        NULL, 'ROLE_USER', false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
       (2, 'admin@test.com', '$2a$10$uaPcWlFC83UR0u3cp94mxOHQwtWLq41XUEW5l7iFeeLLbTOtD2efa', '관리자', 'LOCAL', NULL, NULL,
        'ROLE_ADMIN', false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());