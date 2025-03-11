USE travel;

DELIMITER $$

DROP PROCEDURE IF EXISTS InsertDummyTravels $$

CREATE PROCEDURE InsertDummyTravels()
BEGIN
  DECLARE i INT DEFAULT 1;

  WHILE i <= 1000000 DO
    INSERT INTO travel (
      user_id,
      category_id,
      city,
      places,
      participants,
      start_date,
      end_date,
      content,
      status,
      is_deleted,
      created_at  -- 랜덤한 과거 날짜 추가
    )
    VALUES (
      1,  -- user_id 고정
      1,  -- category_id 고정
      CONCAT('City', FLOOR(RAND() * 100)),  -- 랜덤한 도시 이름
      'Place1, Place2, Place3',  -- 고정된 장소 데이터
      FLOOR(1 + (RAND() * 10)),  -- 참가자 수 (1~10명)
      DATE_ADD('2025-01-01', INTERVAL FLOOR(RAND() * 365) DAY),  -- 랜덤 시작 날짜
      DATE_ADD('2025-01-01', INTERVAL FLOOR(RAND() * 365) DAY),  -- 랜덤 종료 날짜
      'This is a sample travel content.',  -- 여행 내용
      CASE FLOOR(RAND() * 3)
          WHEN 0 THEN 'WAITING_FOR_MATCHING'
          WHEN 1 THEN 'IN_PROGRESS'
          ELSE 'MATCHED'
      END, -- 랜덤한 상태값
      0,  -- isDeleted (false)
      DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY)  -- 최근 1년 내 랜덤한 createdAt
    );

    SET i = i + 1;
  END WHILE;
END $$

DELIMITER ;

-- 프로시저 실행
CALL InsertDummyTravels();
