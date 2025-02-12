"use client";

import React, {useEffect, useState} from "react";
import {useRouter, useSearchParams} from "next/navigation";
import axios from "axios";

const CreateReviewPage: React.FC = () => {
    const router = useRouter();
    const searchParams = useSearchParams();
    const [travelId, setTravelId] = useState<number | null>(null);
    const [review, setReview] = useState("");
    const [reviewScore, setReviewScore] = useState<number>(0); // 기본값 0점
    const [hoverScore, setHoverScore] = useState<number | null>(null); // 별 위에 올릴 때 표시용

    useEffect(() => {
        const id = searchParams.get("travelId");
        if (id) {
            setTravelId(Number(id));
        }
    }, [searchParams]);

    // 리뷰 내용 변경 핸들러
    const handleReviewChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setReview(event.target.value);
    };

    // 별점 클릭 핸들러 (0.5 단위로 반개 선택 가능)
    const handleRatingClick = (score: number) => {
        setReviewScore(score);
    };

    // 별 위에 올릴 때 점수 미리보기 (반개 단위로 선택 가능)
    const handleMouseOver = (score: number) => {
        setHoverScore(score);
    };

    // 마우스가 별에서 벗어나면 원래 점수로 복귀
    const handleMouseLeave = () => {
        setHoverScore(null);
    };

    // 리뷰 제출 핸들러
    const handleSubmit = async () => {
        if (!travelId || review.trim() === "" || reviewScore === 0) {
            alert("여행 ID, 리뷰 내용, 평점을 입력해주세요.");
            return;
        }

        const requestData = {
            travelId,
            comment: review,
            reviewScore
        };

        try {
            await axios.post("/reviews", requestData);
            alert("리뷰가 작성되었습니다.");
            router.push("/mypage");
        } catch (error) {
            console.error("리뷰 작성 에러:", error);
            alert("리뷰 작성에 실패했습니다.");
        }
    };

    return (
        <div style={styles.reviewFormContainer}>
            <h2 style={styles.sectionTitle}>리뷰 작성</h2>

            {/* 별점 선택 */}
            <div style={styles.ratingContainer}>
                <label style={styles.ratingLabel}>평점:</label>
                <div style={styles.stars}>
                    {[1, 2, 3, 4, 5].map((star) => (
                        <span
                            key={star}
                            style={{
                                ...styles.star,
                                color: (hoverScore || reviewScore) >= star ? "#FFD700" : "#ccc"
                            }}
                            onClick={() => handleRatingClick(star)}
                            onMouseOver={() => handleMouseOver(star)}
                            onMouseLeave={handleMouseLeave}
                        >
                            ★
                        </span>
                    ))}
                </div>
            </div>

            {/* 선택된 점수 표시 */}
            <div style={styles.selectedScore}>선택한 점수: {reviewScore}점</div>

            {/* 리뷰 내용 입력 */}
            <textarea
                style={styles.reviewInput}
                placeholder="여행에 대한 리뷰를 작성해주세요."
                value={review}
                onChange={handleReviewChange}
            />

            {/* 버튼 그룹 */}
            <div style={styles.buttonGroup}>
                <button style={styles.submitButton} onClick={handleSubmit}>작성 완료</button>
                <button style={styles.cancelButton} onClick={() => router.back()}>취소</button>
            </div>
        </div>
    );
};

// ✅ 스타일 추가
const styles = {
    reviewFormContainer: {
        maxWidth: "600px",
        margin: "100px auto",
        padding: "20px",
        backgroundColor: "#fff",
        borderRadius: "8px",
        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
        display: "flex",
        flexDirection: "column",
        gap: "10px",
    },
    sectionTitle: {
        fontSize: "20px",
        fontWeight: "bold",
        color: "#1E88E5",
        textAlign: "center",
    },
    ratingContainer: {
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        marginBottom: "10px"
    },
    ratingLabel: {
        fontSize: "16px",
        fontWeight: "bold",
        marginBottom: "5px"
    },
    stars: {
        display: "flex",
        gap: "5px",
        cursor: "pointer"
    },
    star: {
        fontSize: "30px",
        transition: "color 0.2s",
        cursor: "pointer"
    },
    selectedScore: {
        textAlign: "center",
        fontWeight: "bold",
        fontSize: "16px",
        color: "#333"
    },
    reviewInput: {
        width: "100%",
        minHeight: "120px",
        padding: "10px",
        borderRadius: "5px",
        border: "1px solid #ccc",
        fontSize: "14px",
        resize: "none",
    },
    buttonGroup: {
        display: "flex",
        justifyContent: "space-between",
    },
    submitButton: {
        backgroundColor: "#28a745",
        color: "white",
        padding: "10px",
        borderRadius: "4px",
        cursor: "pointer",
        border: "none",
        flex: "1",
        marginRight: "5px",
    },
    cancelButton: {
        backgroundColor: "#DC2626",
        color: "white",
        padding: "10px",
        borderRadius: "4px",
        cursor: "pointer",
        border: "none",
        flex: "1",
        marginLeft: "5px",
    }
};

export default CreateReviewPage;
