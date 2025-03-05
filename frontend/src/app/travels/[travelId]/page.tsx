"use client";

import React, {useEffect, useState} from "react";
import {useParams} from "next/navigation";
import Link from "next/link";
import {createTravelOffer, getTravelDetail, TravelDto, validateSelfOffer} from "../../travel/services/travelService";
import {hasGuideProfile} from "../../members/services/memberService";

const TravelDetailPage: React.FC = () => {
    const params = useParams();
    const travelId = Array.isArray(params.travelId) ? params.travelId[0] : params.travelId;
    const [travel, setTravel] = useState<TravelDto | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [offerStatus, setOfferStatus] = useState<string | null>(null);
    const [isSelfRequest, setIsSelfRequest] = useState<boolean | null>(null);
    const [isGuideProfileAvailable, setIsGuideProfileAvailable] = useState(false);

    useEffect(() => {
        if (travelId) {
            setLoading(true);
            getTravelDetail(travelId)
                .then((response) => {
                    setTravel(response.data);
                })
                .catch(() => setError("여행 요청 상세 정보를 불러오는데 실패했습니다."))
                .finally(() => setLoading(false));

            validateSelfOffer(Number(travelId))
                .then((response) => setIsSelfRequest(response.data))
                .catch(() => setError("셀프 요청 검증에 실패했습니다."));

            const fetchHasGuideProfile = async () => {
                try {
                    const response = await hasGuideProfile();
                    setIsGuideProfileAvailable(response.data);
                } catch (error) {
                    console.error("가이드 프로필 유무 조회 실패", error);
                }
            };
            fetchHasGuideProfile();
        }
    }, [travelId]);

    const handleTravelOfferRequest = async () => {
        if (!travelId) return;

        try {
            await createTravelOffer(travelId);
            setOfferStatus("여행 제안 요청이 성공적으로 완료되었습니다.");
        } catch (error) {
            setOfferStatus("여행 제안 요청 중 오류가 발생했습니다.");
        }
    };

    if (loading) return <div style={styles.loading}>로딩 중...</div>;
    if (error) return <div style={styles.error}>{error}</div>;
    if (!travel) return <div style={styles.notFound}>여행 요청 글이 없습니다.</div>;

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h1 style={styles.title}>{travel.city}</h1>
                <div style={styles.infoContainer}>
                    <div style={styles.infoItem}>
                        <strong>카테고리:</strong> {travel.categoryName}
                    </div>
                    <div style={styles.infoItem}>
                        <strong>상태:</strong> {travel.status}
                    </div>
                    <div style={styles.infoItem}>
                        <strong>관광지:</strong> {travel.places}
                    </div>
                    <div style={styles.infoItem}>
                        <strong>여행 기간:</strong> {travel.startDate} ~ {travel.endDate}
                    </div>
                    <div style={styles.infoItem}>
                        <strong>참여 인원:</strong> {travel.participants}명
                    </div>
                    <div style={styles.infoItem}>
                        <strong>상세 내용:</strong> {travel.content}
                    </div>
                    <div style={styles.infoItem}>
                        <strong>작성일:</strong> {new Date(travel.createdAt).toLocaleString()}
                    </div>
                    <div style={styles.infoItem}>
                        <strong>수정일:</strong> {new Date(travel.updatedAt).toLocaleString()}
                    </div>
                </div>
                <div style={styles.buttonContainer}>
                    {!isSelfRequest && isGuideProfileAvailable && (
                        <button
                            style={styles.offerButton}
                            onClick={handleTravelOfferRequest}
                        >
                            여행 제안 요청하기
                        </button>
                    )}
                    <Link href="/travels">
                        <button style={styles.backButton}>← 목록으로 돌아가기</button>
                    </Link>
                </div>
                {offerStatus && <p style={styles.statusMessage}>{offerStatus}</p>}
            </div>
        </div>
    );
};

const styles: { [key: string]: React.CSSProperties } = {
    container: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "100vh",
        backgroundColor: "#E3F2FD",
        padding: "2rem",
    },
    card: {
        backgroundColor: "#FFFFFF",
        borderRadius: "12px",
        boxShadow: "0 8px 16px rgba(0, 0, 0, 0.1)",
        padding: "2rem",
        width: "100%",
        maxWidth: "720px",
    },
    title: {
        fontSize: "2rem",
        fontWeight: "bold",
        color: "#1565C0",
        marginBottom: "1.5rem",
        textAlign: "center",
    },
    infoContainer: {
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))",
        gap: "1rem",
        marginBottom: "1.5rem",
    },
    infoItem: {
        backgroundColor: "#F9FAFB",
        padding: "1rem",
        borderRadius: "8px",
        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.05)",
        fontSize: "1rem",
        color: "#424242",
    },
    buttonContainer: {
        display: "flex",
        justifyContent: "space-between",
        marginTop: "1.5rem",
    },
    offerButton: {
        backgroundColor: "#66BB6A",
        color: "#FFFFFF",
        padding: "0.75rem 1.5rem",
        border: "none",
        borderRadius: "8px",
        cursor: "pointer",
        fontWeight: "bold",
    },
    backButton: {
        backgroundColor: "#1E88E5",
        color: "#FFFFFF",
        padding: "0.75rem 1.5rem",
        border: "none",
        borderRadius: "8px",
        cursor: "pointer",
        fontWeight: "bold",
    },
    statusMessage: {
        fontSize: "1rem",
        marginTop: "1rem",
        color: "#1565C0",
    },
    loading: {
        fontSize: "1.5rem",
        color: "#1E88E5",
        textAlign: "center",
    },
    error: {
        fontSize: "1.5rem",
        color: "red",
        textAlign: "center",
    },
    notFound: {
        fontSize: "1.5rem",
        color: "gray",
        textAlign: "center",
    },
};

export default TravelDetailPage;
