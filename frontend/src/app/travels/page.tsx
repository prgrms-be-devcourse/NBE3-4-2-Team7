"use client";

import React, {useEffect, useState} from "react";
import Link from "next/link";
import {useRouter} from "next/navigation";
import {getTravels, TravelDto} from "../travel/services/travelService";
import {getGuides, GuideDto} from "../guide/services/guideService";

const TravelListPage: React.FC = () => {
    const [travels, setTravels] = useState<TravelDto[]>([]);
    const [guides, setGuides] = useState<GuideDto[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>("");
    const router = useRouter();

    useEffect(() => {
        setLoading(true);
        Promise.all([getTravels(), getGuides()])
            .then(([travelResponse, guideResponse]) => {
                setTravels(travelResponse.data.content);
                setGuides(guideResponse.data);
            })
            .catch(() => setError("데이터를 불러오는 데 실패했습니다."))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div style={styles.loading}>로딩 중...</div>;
    if (error) return <div style={styles.error}>{error}</div>;

    return (
        <div style={styles.container}>
            {/* 서비스 소개 섹션 */}
            <div style={styles.banner}>
                <h1 style={styles.bannerTitle}>여행 가이드 서비스</h1>
                <p style={styles.bannerText}>
                    원하는 여행을 계획하고 가이드에게 요청하세요! <br/>
                    맞춤형 가이드 서비스를 통해 더욱 즐거운 여행을 경험하세요.
                </p>
                <div style={styles.buttonContainer}>
                    <Link href="/travels/create">
                        <button style={styles.primaryButton}>여행 요청하기</button>
                    </Link>
                    <Link href="/mypage">
                        <button style={styles.secondaryButton}>마이페이지</button>
                    </Link>
                </div>
            </div>

            {/* 여행 요청 & 가이드 목록 */}
            <div style={styles.mainContent}>
                {/* 여행 요청 목록 */}
                <div style={styles.travelSection}>
                    <h2 style={styles.sectionTitle}>여행 요청 목록</h2>
                    <div style={styles.list}>
                        {travels.map((travel) => (
                            <div key={travel.id} style={styles.card}>
                                <h3 style={styles.cardTitle}>{travel.city}</h3>
                                <p style={styles.cardContent}>
                                    {travel.content.length > 50
                                        ? `${travel.content.substring(0, 50)}...`
                                        : travel.content}
                                </p>
                                <Link href={`/travels/${travel.id}`}>
                                    <button style={styles.detailsButton}>상세 보기</button>
                                </Link>
                            </div>
                        ))}
                    </div>
                </div>

                {/* 가이드 목록 */}
                <div style={styles.guideSection}>
                    <h2 style={styles.sectionTitle}>가이드 목록</h2>
                    <div style={styles.list}>
                        {guides.map((guide) => (
                            <div
                                key={guide.id}
                                style={styles.card}
                                onClick={() => router.push(`/guide/${guide.id}`)}
                            >
                                <h3 style={styles.cardTitle}>{guide.name}</h3>
                                <p style={styles.cardContent}>활동 지역: {guide.activityRegion}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

const styles: { [key: string]: React.CSSProperties } = {
    container: {
        backgroundColor: "#E3F2FD",
        minHeight: "100vh",
        padding: "2rem",
    },
    banner: {
        textAlign: "center",
        marginBottom: "2rem",
        backgroundColor: "#1E88E5",
        color: "#FFFFFF",
        padding: "2rem",
        borderRadius: "8px",
    },
    bannerTitle: {
        fontSize: "2rem",
        marginBottom: "0.5rem",
    },
    bannerText: {
        fontSize: "1.25rem",
    },
    buttonContainer: {
        marginTop: "1rem",
        display: "flex",
        gap: "1rem",
        justifyContent: "center",
    },
    primaryButton: {
        backgroundColor: "#FFD700",
        color: "#333",
        border: "none",
        padding: "0.75rem 1.5rem",
        borderRadius: "4px",
        cursor: "pointer",
        fontWeight: "bold",
    },
    secondaryButton: {
        backgroundColor: "#81D4FA",
        color: "#333",
        border: "none",
        padding: "0.75rem 1.5rem",
        borderRadius: "4px",
        cursor: "pointer",
        fontWeight: "bold",
    },
    mainContent: {
        display: "grid",
        gridTemplateColumns: "1fr 1fr", // 좌우 배치
        gap: "2rem",
    },
    travelSection: {
        backgroundColor: "#FFFFFF",
        padding: "1.5rem",
        borderRadius: "8px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
    },
    guideSection: {
        backgroundColor: "#FFFFFF",
        padding: "1.5rem",
        borderRadius: "8px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
    },
    sectionTitle: {
        fontSize: "1.75rem",
        marginBottom: "1rem",
        color: "#1E88E5",
    },
    list: {
        display: "flex",
        flexDirection: "column",
        gap: "1rem",
    },
    card: {
        backgroundColor: "#FFFFFF",
        borderRadius: "8px",
        padding: "1.5rem",
        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
        cursor: "pointer",
        transition: "0.3s",
    },
    cardTitle: {
        fontSize: "1.25rem",
        color: "#1E88E5",
    },
    cardContent: {
        fontSize: "1rem",
        color: "#424242",
    },
    detailsButton: {
        backgroundColor: "#1E88E5",
        color: "#fff",
        border: "none",
        padding: "0.5rem 1rem",
        borderRadius: "4px",
        cursor: "pointer",
        marginTop: "0.5rem",
    },
    loading: {
        fontSize: "1.25rem",
        color: "#1E88E5",
        textAlign: "center",
    },
    error: {
        fontSize: "1.25rem",
        color: "red",
        textAlign: "center",
    },
};

export default TravelListPage;
