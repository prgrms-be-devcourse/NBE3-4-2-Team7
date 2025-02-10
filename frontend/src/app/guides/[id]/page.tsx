"use client";

import React, {useEffect, useState} from "react";
import {useParams} from "next/navigation";
import Link from "next/link";
import {getGuideDetail, GuideDto, verifyMyGuide} from "../services/guideService";
import {getMyTravels, TravelDto} from "../../travel/services/travelService";
import axios from "axios";

const GuideDetailPage: React.FC = () => {
    const {id} = useParams();
    const [guide, setGuide] = useState<GuideDto | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [travels, setTravels] = useState<TravelDto[]>([]);
    const [selectedTravelId, setSelectedTravelId] = useState<number | null>(null);
    const [requestStatus, setRequestStatus] = useState<string | null>(null);
    const [isMyGuide, setIsMyGuide] = useState<boolean | null>(null);

    useEffect(() => {
        setLoading(true);
        // 가이드 상세 정보 가져오기
        getGuideDetail(Number(id))
            .then((response) => setGuide(response.data))
            .catch(() => setError("가이드 정보를 불러오는데 실패했습니다."))
            .finally(() => setLoading(false));

        // 사용자의 여행 요청 목록 가져오기
        getMyTravels()
            .then((response) => setTravels(response.data))
            .catch(() => console.error("여행 목록을 불러오는 데 실패했습니다."));

        // 셀프 요청 검증
        verifyMyGuide(Number(id))
            .then((response) => setIsMyGuide(response.data))
            .catch(() => console.error("가이드 검증에 실패했습니다."));
    }, [id]);

    // 가이드 요청 보내기
    const handleGuideRequest = async () => {
        if (!guide || !selectedTravelId) {
            setRequestStatus("여행 요청을 선택하세요.");
            return;
        }

        try {
            await axios.post(`/guide-requests/${guide.id}`, {
                travelId: selectedTravelId, // ✅ Query Parameter가 아닌 Body로 전달
            });
            setRequestStatus("가이드 요청이 성공적으로 완료되었습니다.");
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
        } catch (error) {
            setRequestStatus("가이드 요청 중 오류가 발생했습니다.");
        }
    };

    if (loading) return <div style={styles.loading}>로딩 중...</div>;
    if (error) return <div style={styles.error}>{error}</div>;

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h1 style={styles.title}>가이드 상세 정보</h1>
                {guide ? (
                    <>
                        <h2 style={styles.guideName}>{guide.name}</h2>
                        <p style={styles.introduction}><strong>소개:</strong> {guide.introduction}</p>
                        <p><strong>활동 지역:</strong> {guide.activityRegion}</p>
                        <p><strong>경험 연수:</strong> {guide.experienceYears}년</p>
                        <p><strong>사용 언어:</strong> {guide.languages}</p>

                        {/* 여행 요청 선택 */}
                        {travels.length > 0 ? (
                            <div style={styles.travelSelectContainer}>
                                <label htmlFor="travelSelect" style={styles.selectLabel}>여행 요청을 선택하세요:</label>
                                <select
                                    id="travelSelect"
                                    value={selectedTravelId || ""}
                                    onChange={(e) => setSelectedTravelId(Number(e.target.value))}
                                    style={styles.selectBox}
                                >
                                    <option value="" disabled>여행 요청을 선택하세요</option>
                                    {travels.map((travel) => (
                                        <option key={travel.id} value={travel.id}>
                                            {travel.city} - {travel.startDate} ~ {travel.endDate}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        ) : (
                            <p style={styles.noTravelMessage}>
                                여행 요청이 없습니다.{" "}
                                <Link href="/travels/create" style={styles.link}>
                                    여행 요청을 먼저 작성해주세요.
                                </Link>
                            </p>

                        )}

                        {!isMyGuide && (
                            <div style={styles.buttonContainer}>
                                <button
                                    style={styles.contactButton}
                                    onClick={handleGuideRequest}
                                    disabled={travels.length === 0}
                                >
                                    가이드 요청하기
                                </button>
                            </div>
                        )}
                        {requestStatus && <p style={styles.statusMessage}>{requestStatus}</p>}
                    </>
                ) : (
                    <p>가이드 정보를 찾을 수 없습니다.</p>
                )}
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
    guideName: {
        fontSize: "1.8rem",
        fontWeight: "bold",
        color: "#1565C0",
        marginBottom: "0.5rem",
    },
    introduction: {
        fontSize: "1.2rem",
        color: "#424242",
        marginBottom: "1rem",
    },
    travelSelectContainer: {
        marginTop: "1rem",
    },
    selectLabel: {
        fontSize: "1rem",
        fontWeight: "bold",
        display: "block",
        marginBottom: "0.5rem",
    },
    selectBox: {
        width: "100%",
        padding: "0.5rem",
        borderRadius: "4px",
        border: "1px solid #ccc",
        fontSize: "1rem",
    },
    noTravelMessage: {
        fontSize: "1rem",
        color: "red",
        marginTop: "1rem",
    },
    link: {
        color: "#1E88E5",
        textDecoration: "none",
        fontWeight: "bold",
    },
    buttonContainer: {
        marginTop: "1.5rem",
    },
    contactButton: {
        backgroundColor: "#66BB6A",
        color: "#FFFFFF",
        padding: "0.75rem 1.5rem",
        border: "none",
        borderRadius: "4px",
        cursor: "pointer",
        fontSize: "1rem",
        fontWeight: "bold",
        transition: "background 0.3s",
    },
    contactButtonHover: {
        backgroundColor: "#4CAF50",
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
};

export default GuideDetailPage;
