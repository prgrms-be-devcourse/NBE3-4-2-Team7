"use client";

import React, { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axios from "axios";

const ReviewEditPage: React.FC = () => {
    const router = useRouter();
    const searchParams = useSearchParams();
    const travelId = searchParams.get("travelId");
    const reviewId = searchParams.get("reviewId");

    const [review, setReview] = useState("");
    const [reviewScore, setReviewScore] = useState<number | null>(null);

    useEffect(() => {
        const fetchReview = async () => {
            try {
                const response = await axios.get(`/reviews/travel/${travelId}`); // ✅ travelId로 조회
                if (response.data.length > 0) {
                    setReview(response.data[0].comment);
                    setReviewScore(response.data[0].reviewScore);
                } else {
                    alert("해당 여행에 대한 리뷰가 없습니다.");
                }
            } catch (error) {
                alert("리뷰 정보를 불러오는 데 실패했습니다.");
            }
        };

        if (travelId) fetchReview();
    }, [travelId]);

    // 리뷰 내용 변경 핸들러
    const handleReviewChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setReview(event.target.value);
    };

    // 평점 변경 핸들러 (0.5 단위 조정)
    const handleReviewScoreChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const value = parseFloat(event.target.value);

        if (isNaN(value) || value < 0 || value > 5) {
            alert("평점은 0.0에서 5.0 사이여야 합니다.");
            return;
        }

        setReviewScore(value);
    };

    // 리뷰 수정 핸들러
    const handleUpdateReview = async () => {
        if (!reviewId || review.trim() === "" || reviewScore === null) {
            alert("리뷰 내용과 평점을 입력해주세요.");
            return;
        }

        const requestData = {
            comment: review,
            reviewScore
        };

        console.log("📡 PATCH 요청 전송:", `/reviews/${reviewId}/update`, requestData);

        try {
            const response = await axios.patch(`/reviews/${reviewId}/update`, requestData);
            console.log("✅ PATCH 응답:", response);
            alert("리뷰가 수정되었습니다.");
            router.push("/mypage"); // 마이페이지로 이동
        } catch (error) {
            console.error("❌ 리뷰 수정 에러:", error);
            if (error.response) {
                console.error("🔥 서버 응답 데이터:", error.response.data);
            }
            alert("리뷰 수정에 실패했습니다.");
        }
    };

    return (
        <div style={styles.reviewFormContainer}>
            <h2 style={styles.sectionTitle}>✏️ 리뷰 수정</h2>

            {/* 평점 입력 */}
            <div style={styles.ratingContainer}>
                <label style={styles.ratingLabel}>평점 (0.0 ~ 5.0):</label>
                <input
                    type="number"
                    min="0"
                    max="5"
                    step="0.5"
                    value={reviewScore}
                    onChange={handleReviewScoreChange}
                    style={styles.ratingInput}
                />
            </div>

            {/* 리뷰 내용 입력 */}
            <textarea
                style={styles.reviewInput}
                placeholder="여행에 대한 리뷰를 수정해주세요."
                value={review}
                onChange={handleReviewChange}
            />

            {/* 버튼 그룹 */}
            <div style={styles.buttonGroup}>
                <button style={styles.submitButton} onClick={handleUpdateReview}>수정 완료</button>
                <button style={styles.cancelButton} onClick={() => router.back()}>취소</button>
            </div>
        </div>
    );
};

// ✅ 리뷰 생성 페이지와 동일한 스타일 적용
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
    reviewInput: {
        width: "100%",
        minHeight: "120px",
        padding: "10px",
        borderRadius: "5px",
        border: "1px solid #ccc",
        fontSize: "14px",
        resize: "none",
    },
    ratingContainer: {
        display: "flex",
        alignItems: "center",
        gap: "10px",
    },
    ratingLabel: {
        fontSize: "16px",
        fontWeight: "bold",
    },
    ratingInput: {
        width: "50px",
        padding: "5px",
        fontSize: "14px",
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

export default ReviewEditPage;