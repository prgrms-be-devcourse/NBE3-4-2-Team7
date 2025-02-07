"use client";

import React, {useEffect, useState} from "react";
import {useRouter} from "next/navigation";
import {
    getGuideRequestsByRequester,
    getMyTravels,
    GuideRequestDto,
    TravelDto,
    updateGuideRequestStatus,
} from "../travel/services/travelService";
import {getMyInfo, MemberResponseDTO} from "../members/services/memberService";
import {
    getGuideRequestsByGuide,
    getTravelOffersByGuide,
    getTravelOffersForUser,
    TravelOfferDto,
    updateTravelOfferStatus,
} from "../travelOffers/services/travelOfferService";

const MyPage: React.FC = () => {
    const [userInfo, setUserInfo] = useState<MemberResponseDTO | null>(null);
    const [guideRequests, setGuideRequests] = useState<GuideRequestDto[]>([]);
    const [myTravels, setMyTravels] = useState<TravelDto[]>([]);
    const [travelOffersForUser, setTravelOffersForUser] = useState<TravelOfferDto[]>([]);
    const [guideRequestsByGuide, setGuideRequestsByGuide] = useState<GuideRequestDto[]>([]);
    const [travelOffers, setTravelOffers] = useState<TravelOfferDto[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const router = useRouter();

    useEffect(() => {
        setLoading(true);
        getMyInfo()
            .then((userInfoResponse) => {
                setUserInfo(userInfoResponse);
                return Promise.all([
                    getGuideRequestsByRequester(),
                    getMyTravels(),
                    getTravelOffersForUser(), // 사용자에게 온 여행 제안 요청 API 추가
                    userInfoResponse.hasGuideProfile ? getGuideRequestsByGuide() : Promise.resolve({data: []}),
                    userInfoResponse.hasGuideProfile ? getTravelOffersByGuide() : Promise.resolve({data: []}),
                ]);
            })
            .then(([
                       guideRequestsResponse,
                       myTravelsResponse,
                       travelOffersForUserResponse,
                       guideRequestsByGuideResponse,
                       travelOffersResponse,
                   ]) => {
                setGuideRequests(guideRequestsResponse.data);
                setMyTravels(myTravelsResponse.data);
                setTravelOffersForUser(travelOffersForUserResponse.data);
                setGuideRequestsByGuide(guideRequestsByGuideResponse.data);
                setTravelOffers(travelOffersResponse.data);
            })
            .catch(() => {
                setError("데이터를 불러오는 데 실패했습니다.");
            })
            .finally(() => setLoading(false));
    }, []);

    const handleViewProfile = (guideId: number) => {
        router.push(`/guides/${guideId}`);
    };

    const handleViewTravelRequest = (travelId: number) => {
        router.push(`/travels/${travelId}`);
    };

    const handleTravelOfferStatusUpdate = (offerId: number, status: "ACCEPTED" | "REJECTED") => {
        updateTravelOfferStatus(offerId, status)
            .then(() => {
                alert(`요청이 ${status === "ACCEPTED" ? "수락" : "거절"}되었습니다.`);
                setTravelOffersForUser((prevOffers) =>
                    prevOffers.map((offer) => (offer.id === offerId ? {...offer, status} : offer))
                );
            })
            .catch(() => alert("요청 상태를 업데이트하는 데 실패했습니다."));
    };

    const handleUpdateStatus = (requestId: number, guideId: number, status: "ACCEPTED" | "REJECTED") => {
        updateGuideRequestStatus(requestId, guideId, status)
            .then(() => {
                alert(`요청이 ${status === "ACCEPTED" ? "수락" : "거절"}되었습니다.`);

                // 상태를 즉시 업데이트하여 UI 반영
                setGuideRequestsByGuide((prevRequests) =>
                    prevRequests.map((req) =>
                        req.id === requestId ? {...req, status: status} : req
                    )
                );
            })
            .catch(() => alert("요청 상태를 업데이트하는데 실패했습니다."));
    };
    if (loading) return <div style={styles.loading}>로딩 중...</div>;
    if (error) return <div style={styles.error}>{error}</div>;

    const getStatusStyle = (status: string): React.CSSProperties => {
        switch (status) {
            case "ACCEPTED":
                return {backgroundColor: "#16A34A", color: "#FFF", padding: "0.2rem 0.5rem", borderRadius: "4px"};
            case "PENDING":
                return {backgroundColor: "#F59E0B", color: "#FFF", padding: "0.2rem 0.5rem", borderRadius: "4px"};
            case "REJECTED":
                return {backgroundColor: "#DC2626", color: "#FFF", padding: "0.2rem 0.5rem", borderRadius: "4px"};
            default:
                return {backgroundColor: "#6B7280", color: "#FFF", padding: "0.2rem 0.5rem", borderRadius: "4px"};
        }
    };


    return (
        <div style={styles.container}>
            {/* 사용자 정보 섹션 */}
            <div style={styles.userInfoContainer}>
                {userInfo && (
                    <div style={styles.userInfo}>
                        <h1 style={styles.userName}>{userInfo.name}</h1>
                        <p style={styles.userEmail}>{userInfo.email}</p>
                        {userInfo.hasGuideProfile && (
                            <p style={styles.guideStatus}>🌟 가이드 프로필 등록 완료</p>
                        )}
                    </div>
                )}
            </div>


            <div style={styles.mainContent}>
                {/* 사용자 섹션 */}
                <div style={styles.sectionBox}>
                    <h2 style={styles.sectionTitle}>👤 사용자 내역</h2>

                    {/* 내가 요청한 가이드 요청 내역 */}
                    <div style={styles.card}>
                        <h3 style={styles.cardTitle}>📑 사용자(나) {"->"} 가이더 요청 내역 조회</h3>
                        <div style={styles.innerCard}>
                            {guideRequests.length === 0 ? (
                                <p style={styles.noRequests}>요청한 가이드 내역이 없습니다.</p>
                            ) : (
                                guideRequests.map((request) => (
                                    <div key={request.id} style={styles.requestBox}>
                                        <div style={styles.requestDetails}>
                                            <p><b>여행 도시:</b> {request.travelCity}</p>
                                            <p><b>가이드 이름:</b> {request.guideName}</p>
                                            <p><b>상태:</b> <span
                                                style={getStatusStyle(request.status)}>{request.status}</span></p>
                                        </div>
                                        <button
                                            style={styles.viewProfileButton}
                                            onClick={() => handleViewProfile(request.guideId)}
                                        >
                                            🔵 가이드 프로필 보기
                                        </button>
                                    </div>
                                ))
                            )}
                        </div>
                    </div>

                    {/* 내가 작성한 여행 요청 내역 */}
                    <div style={styles.card}>
                        <h3 style={styles.cardTitle}>📍 나의 여행 요청 글 목록</h3>
                        <div style={styles.innerCard}>
                            {myTravels.length === 0 ? (
                                <p style={styles.noRequests}>여행 요청 내역이 없습니다.</p>
                            ) : (
                                myTravels.map((travel) => (
                                    <div key={travel.id} style={styles.requestBox}>
                                        <div style={styles.requestDetails}>
                                            <p><b>여행 도시:</b> {travel.city}</p>
                                            <p><b>여행 기간:</b> {travel.startDate} ~ {travel.endDate}</p>
                                        </div>
                                        <button
                                            style={styles.viewProfileButton}
                                            onClick={() => handleViewTravelRequest(travel.id)}
                                        >
                                            🔵 여행 상세 보기
                                        </button>
                                    </div>
                                ))
                            )}
                        </div>
                    </div>

                    {/* 사용자가 받은 여행 제안 요청 (가이더가 제안한 매칭 요청) */}
                    <div style={styles.card}>
                        <h3 style={styles.cardTitle}>📑 사용자(나) {"<-"} 요청 들어온 가이더 매칭 요청</h3>
                        <div style={styles.innerCard}>
                            {travelOffersForUser.length === 0 ? (
                                <p style={styles.noRequests}>받은 여행 제안 요청이 없습니다.</p>
                            ) : (
                                travelOffersForUser.map((offer) => (
                                    <div key={offer.id} style={styles.requestBox}>
                                        <div style={styles.requestDetails}>
                                            <p><b>여행 도시:</b> {offer.travelCity}</p>
                                            <p><b>가이드 이름:</b> {offer.guideName}</p>
                                            <p><b>상태:</b> <span
                                                style={getStatusStyle(offer.status)}>{offer.status}</span></p>
                                        </div>
                                        {offer.status === "PENDING" && (
                                            <div style={styles.buttonGroup}>
                                                <button
                                                    style={styles.acceptButton}
                                                    onClick={() => handleTravelOfferStatusUpdate(offer.id, "ACCEPTED")}
                                                >
                                                    ✅ 수락
                                                </button>
                                                <button
                                                    style={styles.rejectButton}
                                                    onClick={() => handleTravelOfferStatusUpdate(offer.id, "REJECTED")}
                                                >
                                                    ❌ 거절
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                ))
                            )}
                        </div>
                    </div>
                </div>


                {/* 가이드 섹션 (hasGuideProfile이 true일 경우만 표시) */}
                {userInfo?.hasGuideProfile && (
                    <div style={styles.sectionBox}>
                        <h2 style={styles.sectionTitle}>🧑‍🏫 가이드 내역</h2>

                        {/* 사용자가 나에게 요청한 가이드 요청 내역 */}
                        <div style={styles.card}>
                            <h3 style={styles.cardTitle}>📑 가이더(나) {"<-"} 사용자 매칭 요청</h3>
                            <div style={styles.innerCard}>
                                {guideRequestsByGuide.length === 0 ? (
                                    <p style={styles.noRequests}>받은 요청이 없습니다.</p>
                                ) : (
                                    guideRequestsByGuide.map((request) => (
                                        <div key={request.id} style={styles.requestBox}>
                                            <div style={styles.requestDetails}>
                                                <p><b>여행 도시:</b> {request.travelCity}</p>
                                                <p><b>사용자 이름:</b> {request.memberName}</p>
                                                <p><b>상태:</b> <span
                                                    style={getStatusStyle(request.status)}>{request.status}</span></p>
                                            </div>
                                            <div>
                                                <button
                                                    style={styles.viewProfileButton}
                                                    onClick={() => handleViewTravelRequest(request.guideId)}
                                                    disabled={request.isGuideDeleted}
                                                >
                                                    🔵 여행 요청 글 보기
                                                </button>
                                                {request.status === "PENDING" && (
                                                    <div style={styles.buttonGroup}>
                                                        <button
                                                            style={styles.acceptButton}
                                                            onClick={() =>
                                                                handleUpdateStatus(request.id, request.guideId, "ACCEPTED")
                                                            }
                                                        >
                                                            ✅ 수락
                                                        </button>
                                                        <button
                                                            style={styles.rejectButton}
                                                            onClick={() =>
                                                                handleUpdateStatus(request.id, request.guideId, "REJECTED")
                                                            }
                                                        >
                                                            ❌ 거절
                                                        </button>
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>

                        {/* 내가 사용자에게 보낸 여행 제안 요청 내역 */}
                        <div style={styles.card}>
                            <h3 style={styles.cardTitle}>📍 가이더(나) {"->"} 사용자 매칭 요청 조회 </h3>
                            <div style={styles.innerCard}>
                                {travelOffers.length === 0 ? (
                                    <p style={styles.noRequests}>보낸 여행 제안 요청이 없습니다.</p>
                                ) : (
                                    travelOffers.map((offer) => (
                                        <div key={offer.id} style={styles.requestBox}>
                                            <div style={styles.requestDetails}>
                                                <p><b>여행 도시:</b> {offer.travelCity}</p>
                                                <p><b>사용자 이름:</b> {offer.guideName}</p>
                                                <p><b>상태:</b> <span
                                                    style={getStatusStyle(offer.status)}>{offer.status}</span></p>
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

const styles: { [key: string]: React.CSSProperties } = {
    // container: {padding: "2rem", backgroundColor: "#E3F2FD"},
    // userInfo: {textAlign: "center", marginBottom: "2rem"},

    userInfoContainer: {
        backgroundColor: "#E3F2FD",
        borderRadius: "12px",
        padding: "2rem",
        marginBottom: "2rem",
        boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
        display: "flex",
        alignItems: "flex-start",
        justifyContent: "flex-start",
    },
    userInfo: {
        textAlign: "left",
    },
    userName: {
        fontSize: "2rem",
        fontWeight: "bold",
        color: "#1E88E5",
        marginBottom: "0.5rem",
    },
    userEmail: {
        fontSize: "1.2rem",
        color: "#424242",
        marginBottom: "1rem",
    },
    guideStatus: {
        fontSize: "1rem",
        color: "#4CAF50",
        backgroundColor: "#E8F5E9",
        padding: "0.5rem 1rem",
        borderRadius: "8px",
        display: "inline-block",
    },


    mainContent: {
        padding: "1.5rem",
        backgroundColor: "#F9FAFB",
        borderRadius: "12px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
    },
    sectionBox: {
        marginBottom: "2rem",
        backgroundColor: "#FFFFFF",
        padding: "1.5rem",
        borderRadius: "12px",
        boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
    },
    sectionTitle: {
        fontSize: "1.8rem",
        fontWeight: "bold",
        color: "#1E88E5",
        marginBottom: "1rem",
    },
    card: {
        marginBottom: "1.5rem",
        backgroundColor: "#FFFFFF",
        borderRadius: "8px",
        padding: "1.5rem",
        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
    },
    cardTitle: {
        fontSize: "1.5rem",
        fontWeight: "bold",
        marginBottom: "1rem",
        color: "#374151",
    },
    innerCard: {
        display: "flex",
        flexDirection: "column",
        gap: "1rem",
    },
    requestBox: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        backgroundColor: "#F3F4F6",
        padding: "1rem",
        borderRadius: "8px",
        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.05)",
    },
    requestDetails: {
        flex: 1,
    },
    noRequests: {
        color: "#9CA3AF",
    },
    viewProfileButton: {
        backgroundColor: "#2563EB",
        color: "#FFFFFF",
        padding: "0.5rem 1rem",
        borderRadius: "4px",
        fontSize: "0.9rem",
        border: "none",
        cursor: "pointer",
    },
    buttonGroup: {
        display: "flex",
        gap: "0.5rem",
    },
    acceptButton: {
        backgroundColor: "#16A34A",
        color: "#FFFFFF",
        padding: "0.5rem 1rem",
        borderRadius: "4px",
        fontSize: "0.9rem",
        border: "none",
        cursor: "pointer",
    },
    rejectButton: {
        backgroundColor: "#DC2626",
        color: "#FFFFFF",
        padding: "0.5rem 1rem",
        borderRadius: "4px",
        fontSize: "0.9rem",
        border: "none",
        cursor: "pointer",
    },

    // mainContent: {display: "flex", gap: "2rem"},
    // userSection: {flex: 1, backgroundColor: "#FFF", padding: "1.5rem"},
    // guideSection: {flex: 1, backgroundColor: "#FFF", padding: "1.5rem"},
    // sectionTitle: {fontSize: "1.5rem", fontWeight: "bold"},
    // card: {backgroundColor: "#FFF", padding: "1rem", marginBottom: "1rem"},
    // cardTitle: {fontSize: "1.2rem", fontWeight: "bold"},
    // cardContent: {marginBottom: "0.5rem"},
    // acceptButton: {backgroundColor: "#4CAF50", color: "white", padding: "0.5rem"},
    // rejectButton: {backgroundColor: "#F44336", color: "white", padding: "0.5rem"},
    // noRequests: {color: "#757575"},
};

export default MyPage;
