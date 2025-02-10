"use client";

import React, {useEffect, useState} from "react";
import {useParams} from "next/navigation";
import Link from "next/link";
import {createTravelOffer, getTravelDetail, TravelDto, validateSelfOffer} from "../../travel/services/travelService";
import {hasGuideProfile} from "@/app/members/services/memberService";

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
                .catch((err) => {
                    setError("여행 요청 상세 정보를 불러오는데 실패했습니다.");
                    console.error(err);
                })
                .finally(() => setLoading(false));
            validateSelfOffer(Number(travelId))
                .then((response) => {
                setIsSelfRequest(response.data);
            })
                .catch((err) => {
                    setError("셀프 요청 검증에 실패했습니다.")
                })

            const fetchHasGuideProfile = async () => {
                try {
                    const response = await hasGuideProfile();
                    setIsGuideProfileAvailable(response.data);
                } catch(error){
                    console.error('가이드 프로필 유무 조회 실패', error);
                }
            }
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
                <p style={styles.category}><strong>카테고리:</strong> {travel.categoryName}</p>
                <p style={styles.status}><strong>상태:</strong> {travel.status}</p>
                <p style={styles.content}><strong>관광지:</strong> {travel.places}</p>
                <p style={styles.content}><strong>여행 기간:</strong> {travel.startDate} ~ {travel.endDate}</p>
                <p style={styles.content}><strong>참여 인원:</strong> {travel.participants}명</p>
                <p style={styles.content}><strong>상세 내용:</strong> {travel.content}</p>
                <p style={styles.date}><strong>작성일:</strong> {new Date(travel.createdAt).toLocaleString()}</p>
                <p style={styles.date}><strong>수정일:</strong> {new Date(travel.updatedAt).toLocaleString()}</p>

                <div style={styles.buttonContainer}>
                    {!isSelfRequest && isGuideProfileAvailable && (
                        <button
                            style={styles.guideRequestButton}
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
        borderRadius: "8px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
        padding: "2rem",
        width: "100%",
        maxWidth: "600px",
        textAlign: "center",
    },
    title: {
        fontSize: "2rem",
        color: "#1E88E5",
        marginBottom: "1rem",
    },
    category: {
        fontSize: "1.2rem",
        color: "#424242",
        marginBottom: "0.5rem",
    },
    status: {
        fontSize: "1.1rem",
        fontWeight: "bold",
        color: "#1565C0",
        marginBottom: "1rem",
    },
    content: {
        fontSize: "1rem",
        color: "#424242",
        marginBottom: "0.5rem",
    },
    date: {
        fontSize: "0.9rem",
        color: "#757575",
        marginBottom: "0.5rem",
    },
    buttonContainer: {
        display: "flex",
        justifyContent: "space-between",
        marginTop: "1.5rem",
    },
    guideRequestButton: {
        backgroundColor: "#66BB6A",
        color: "#FFFFFF",
        padding: "0.75rem 1rem",
        border: "none",
        borderRadius: "4px",
        cursor: "pointer",
        fontWeight: "bold",
    },
    backButton: {
        backgroundColor: "#1E88E5",
        color: "#FFFFFF",
        padding: "0.75rem 1rem",
        border: "none",
        borderRadius: "4px",
        cursor: "pointer",
        fontWeight: "bold",
    },
    statusMessage: {
        marginTop: "1rem",
        fontSize: "1rem",
        color: "#1565C0",
        fontWeight: "bold",
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
