-- provider 컬럼 제약조건 수정
ALTER TABLE member 
    MODIFY COLUMN provider VARCHAR(10) CHECK (provider IN ('KAKAO', 'GOOGLE', 'GITHUB', 'LOCAL')); 