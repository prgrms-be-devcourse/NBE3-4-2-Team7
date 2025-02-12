"use client";

import React, { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axios from "axios";

const CreateReviewPage: React.FC = () => {
    const router = useRouter();
    const searchParams = useSearchParams();
    const [travelId, setTravelId] = useState<number | null>(null);

    useEffect(() => {
        const id = searchParams.get("travelId");
        if (id) {
            setTravelId(Number(id));
        }
    }, [searchParams]);

    const [review, setReview] = useState("");
    const [reviewScore, setReviewScore] = useState<number | "">("");

    // 리뷰 내용 변경 핸들러
    const handleReviewChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setReview(event.target.value);
    };

    // 평점 변경 핸들러
    const handleReviewScoreChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const value = parseFloat(event.target.value);

        if (isNaN(value) || value < 0 || value > 5) {
            alert("평점은 0.0에서 5.0 사이여야 합니다.");
            return;
        }

        setReviewScore(value);
    };

    // 리뷰 제출 핸들러
    const handleSubmit = async () => {
        console.log("제출 데이터:", { travelId, review, reviewScore });

        if (!travelId || review.trim() === "" || reviewScore === "") {
            alert("여행 ID, 리뷰 내용, 평점을 입력해주세요.");
            return;
        }

        const requestData = {
            travelId, // ✅ 여행 ID 추가
            comment: review, // ✅ 백엔드 필드명 `comment`
            reviewScore // ✅ 필드명 `reviewScore`
        };

        try {
            await axios.post("/reviews", requestData);
            alert("리뷰가 작성되었습니다.");
            router.push("/mypage"); // 마이페이지로 이동
        } catch (error) {
            console.error("리뷰 작성 에러:", error);
            if (error.response && error.response.data) {
                alert(error.response.data.errorMessage);
            } else {
                alert("리뷰 작성에 실패했습니다.");
            }
        }
    };

    return (
        <div style={styles.reviewFormContainer}>
            <h2 style={styles.sectionTitle}>리뷰 작성</h2>

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

export default CreateReviewPage;