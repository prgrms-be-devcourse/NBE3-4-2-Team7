"use client";

import React, {useEffect, useState} from "react";
import {useRouter} from "next/navigation";
import {getMyGuideRequests, GuideRequestDto} from "../travel/services/travelService";
import {updateGuideRequestStatus} from "../guide/services/guideService";

const MyPage: React.FC = () => {
    const [guideRequests, setGuideRequests] = useState<GuideRequestDto[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const router = useRouter();

    useEffect(() => {
        setLoading(true);
        getMyGuideRequests()
            .then((response) => setGuideRequests(response.data))
            .catch(() => setError("가이드 요청 내역을 불러오는 데 실패했습니다."))
            .finally(() => setLoading(false));
    }, []);

    const handleViewProfile = (guideId: number) => {
        router.push(`/guide/${guideId}`);
    };

    const handleUpdateStatus = (requestId: number, guideId: number, status: "ACCEPTED" | "REJECTED") => {
        updateGuideRequestStatus(requestId, guideId, status)
            .then(() => {
                alert(`요청이 ${status === "ACCEPTED" ? "수락" : "거절"}되었습니다.`);
                setGuideRequests((prevRequests) =>
                    prevRequests.map((req) =>
                        req.id === requestId ? {...req, status} : req
                    )
                );
            })
            .catch(() => alert("요청 상태를 업데이트하는데 실패했습니다."));
    };

    if (loading) return <div>로딩 중...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div style={styles.container}>
            <h1 style={styles.title}>나의 가이드 요청 내역</h1>
            {guideRequests.length === 0 ? (
                <p style={styles.noRequests}>요청한 가이드 내역이 없습니다.</p>
            ) : (
                <div style={styles.list}>
                    {guideRequests.map((request) => (
                        <div key={request.id} style={styles.card}>
                            <h3 style={styles.cardTitle}>
                                여행 도시:{" "}
                                {request.isTravelDeleted ? (
                                    <span style={styles.deleted}>삭제된 여행</span>
                                ) : (
                                    request.travelCity
                                )}
                            </h3>
                            <p style={styles.cardContent}>
                                <strong>가이드:</strong>{" "}
                                {request.isGuideDeleted ? (
                                    <span style={styles.deleted}>삭제된 가이드</span>
                                ) : (
                                    request.guideName
                                )}
                            </p>
                            <p style={styles.cardContent}>
                                <strong>매칭 상태:</strong> {request.status}
                            </p>
                            <button
                                style={styles.button}
                                onClick={() => handleViewProfile(request.guideId)}
                                disabled={request.isGuideDeleted}
                            >
                                가이드 프로필 보기
                            </button>
                            {request.status === "PENDING" && (
                                <div style={styles.buttonGroup}>
                                    <button
                                        style={styles.acceptButton}
                                        onClick={() =>
                                            handleUpdateStatus(request.id, request.guideId, "ACCEPTED")
                                        }
                                    >
                                        수락
                                    </button>
                                    <button
                                        style={styles.rejectButton}
                                        onClick={() =>
                                            handleUpdateStatus(request.id, request.guideId, "REJECTED")
                                        }
                                    >
                                        거절
                                    </button>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

const styles: { [key: string]: React.CSSProperties } = {
    container: {
        backgroundColor: "#E3F2FD",
        minHeight: "100vh",
        padding: "2rem",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
    },
    title: {
        fontSize: "2rem",
        marginBottom: "2rem",
        color: "#1E88E5",
    },
    noRequests: {
        fontSize: "1.25rem",
        color: "#757575",
    },
    list: {
        display: "flex",
        flexDirection: "column",
        gap: "1.5rem",
        width: "100%",
        maxWidth: "600px",
    },
    card: {
        backgroundColor: "#FFFFFF",
        borderRadius: "8px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
        padding: "1.5rem",
    },
    cardTitle: {
        fontSize: "1.5rem",
        color: "#1E88E5",
        marginBottom: "0.5rem",
    },
    cardContent: {
        fontSize: "1rem",
        color: "#424242",
        marginBottom: "0.5rem",
    },
    button: {
        padding: "0.5rem 1rem",
        borderRadius: "4px",
        backgroundColor: "#29B6F6",
        color: "#fff",
        border: "none",
        cursor: "pointer",
        marginTop: "1rem",
    },
    buttonGroup: {
        marginTop: "1rem",
        display: "flex",
        gap: "1rem",
    },
    acceptButton: {
        padding: "0.5rem 1rem",
        borderRadius: "4px",
        backgroundColor: "#66BB6A",
        color: "#fff",
        border: "none",
        cursor: "pointer",
    },
    rejectButton: {
        padding: "0.5rem 1rem",
        borderRadius: "4px",
        backgroundColor: "#EF5350",
        color: "#fff",
        border: "none",
        cursor: "pointer",
    },
    deleted: {
        color: "red",
    },
    loading: {
        fontSize: "1.25rem",
        color: "#1E88E5",
    },
    error: {
        fontSize: "1.25rem",
        color: "red",
    },
};

export default MyPage;
