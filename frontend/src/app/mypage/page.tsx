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
import {getGuideProfileByUser, GuideProfileDto} from "@/app/guides/services/guideService";
import {convertFromGuideDto} from "@/app/utils/converters";
import axios from "@/app/utils/axios";

const MyPage: React.FC = () => {
    const [userInfo, setUserInfo] = useState<MemberResponseDTO >();
    const [guideRequests, setGuideRequests] = useState<GuideRequestDto[]>([]);
    const [myTravels, setMyTravels] = useState<TravelDto[]>([]);
    const [travelOffersForUser, setTravelOffersForUser] = useState<TravelOfferDto[]>([]);
    const [guideRequestsByGuide, setGuideRequestsByGuide] = useState<GuideRequestDto[]>([]);
    const [travelOffers, setTravelOffers] = useState<TravelOfferDto[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const [guideProfile, setGuideProfile] = useState<GuideProfileDto | null>(null);
    const router = useRouter();
    const [reviewedTravels, setReviewedTravels] = useState<{ [key: number]: { reviewId: number, comment: string, reviewScore: number } }>({});

    useEffect(() => {
        setLoading(true);
        getMyInfo()
            .then((userInfoResponse) => {
                console.log("✅ getMyInfo() 응답:", userInfoResponse); // 디버깅 로그 추가
                setUserInfo(userInfoResponse);
                return Promise.all([
                    getGuideRequestsByRequester(),
                    getMyTravels(),
                    getTravelOffersForUser(),
                    userInfoResponse.hasGuideProfile ? getGuideRequestsByGuide() : Promise.resolve({ data: [] }),
                    userInfoResponse.hasGuideProfile ? getTravelOffersByGuide() : Promise.resolve({ data: [] }),
                    userInfoResponse.hasGuideProfile ? getGuideProfileByUser() : Promise.resolve({ data: null }),
                ]);
            })
            .then(async ([
                             guideRequestsResponse,
                             myTravelsResponse,
                             travelOffersForUserResponse,
                             guideRequestsByGuideResponse,
                             travelOffersResponse,
                             { data: guideProfileData }
                         ]) => {
                console.log("✅ 여행 목록 응답:", myTravelsResponse.data); // 여행 데이터 확인
                console.log("🚀 여행 목록 응답:", myTravelsResponse.data);

// 각 여행 데이터의 상태 확인
                myTravelsResponse.data.forEach((travel: TravelDto) => {
                    console.log(`🛠️ 여행 상태 체크 - ID: ${travel.id}, 상태: ${travel.status}`);
                });
                console.log("✅ 리뷰 응답 시작");

                setGuideRequests(guideRequestsResponse.data);
                setMyTravels(myTravelsResponse.data);
                setTravelOffersForUser(travelOffersForUserResponse.data);
                setGuideRequestsByGuide(guideRequestsByGuideResponse.data);
                setTravelOffers(travelOffersResponse.data);

                if (guideProfileData) {
                    setGuideProfile(convertFromGuideDto(guideProfileData ?? {}));
                }

                // 🚀 여행 목록을 가져온 후, 각 여행의 리뷰 조회
                const reviewResponses = await Promise.all(
                    myTravelsResponse.data.map(async (travel: TravelDto) => {
                        try {
                            const reviewResponse = await axios.get(`/reviews/travel/${travel.id}`);
                            console.log(`✅ 리뷰 응답 [${travel.id}]:`, reviewResponse.data); // 개별 리뷰 데이터 확인
                            if (reviewResponse.data.length > 0) {
                                return {
                                    travelId: travel.id,
                                    reviewId: reviewResponse.data[0].id,
                                    comment: reviewResponse.data[0].comment,
                                    reviewScore: reviewResponse.data[0].reviewScore
                                };
                            }
                        } catch (err) {
                            console.error(`❌ 리뷰 가져오기 실패 [${travel.id}]`, err);
                            return null; // 리뷰가 없는 경우 무시
                        }
                    })
                );
                console.log("🛠️ 최종 정리된 리뷰 데이터:", reviewedTravels);
                myTravels.forEach((travel) => {
                    console.log(`🛠️ 여행 ID: ${travel.id}, 상태: ${travel.status}, 리뷰 존재 여부:`, reviewedTravels[travel.id]);
                });


                // ✅ 유효한 리뷰만 상태에 저장
                const validReviews = reviewResponses.filter((r) => r !== null);
                console.log("✅ 최종 정리된 리뷰 데이터:", validReviews);
                setReviewedTravels(validReviews.reduce((acc, curr) => {
                    if (curr) acc[curr.travelId] = curr;
                    return acc;
                }, {} as { [key: number]: { reviewId: number, comment: string, reviewScore: number } }));

            })
            .catch((error) => {
                console.error("❌ 데이터 불러오기 실패:", error);
                setError("데이터를 불러오는 데 실패했습니다.");
            })
            .finally(() => setLoading(false));
    }, []);

    const handleEditReview = (travelId: number) => {
        const reviewId = reviewedTravels[travelId].reviewId;
        router.push(`/reviews/edit?travelId=${travelId}&reviewId=${reviewId}`);
    };

    const handleDeleteReview = async (travelId: number) => {
        if (!reviewedTravels[travelId]) return;

        try {
            await axios.patch(`/reviews/${reviewedTravels[travelId].reviewId}`);
            alert("리뷰가 삭제되었습니다.");

            // 🔥 삭제 후 상태 업데이트 (리뷰를 목록에서 제거)
            setReviewedTravels(prev => {
                const updated = { ...prev };
                delete updated[travelId]; // 삭제된 리뷰 제거
                return updated;
            });
        } catch (error) {
            alert("리뷰 삭제에 실패했습니다.");
        }
    };

    // 가이드 생성 페이지로 이동
    const handleGuideCreate = () => {
        if(userInfo.hasGuideProfile){
            return;
        }
        router.push("/guides/register");
    }

    const handleViewProfile = (guideId: number) => {
        router.push(`/guides/${guideId}`);
    };

    const handleViewTravelRequest = (travelId: number) => {
        router.push(`/travels/${travelId}`);
    };
    //   가이드 -> 사용자
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
    //  사용자 -> 가이드
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
                {/* 가이드 프로필 섹션 */}

                <div style={styles.sectionBox}>
                    <h2 style={styles.sectionTitle}>👤 내 가이드 정보</h2>
                    {userInfo.hasGuideProfile ? (
                            <div className="mt-6 space-y-4 animate-fade-in">
                                <div className="grid grid-cols-2 gap-6">
                                    <div>
                                        <h3 className="text-sm font-semibold text-gray-500 mb-1">활동 지역</h3>
                                        <p className="text-gray-800">{guideProfile.activityRegion}</p>
                                    </div>
                                    <div>
                                        <h3 className="text-sm font-semibold text-gray-500 mb-1">사용 가능 언어</h3>
                                        <p className="text-gray-800">{guideProfile.languages}</p>
                                    </div>
                                    <div>
                                        <h3 className="text-sm font-semibold text-gray-500 mb-1">경력</h3>
                                        <p className="text-gray-800">{guideProfile.experienceYears}년</p>
                                    </div>
                                </div>
                                <div>
                                    <h3 className="text-sm font-semibold text-gray-500 mb-1">소개</h3>
                                    <p className="text-gray-800">{guideProfile.introduction}</p>
                                </div>

                                {/* 프로필 수정 버튼 추가 */}
                                <div className="flex justify-end mt-4">
                                    <button
                                        onClick={() => router.push('/mypage/guide/edit')}
                                        className="px-4 py-2 bg-blue-600 text-white rounded-lg
                                         hover:bg-blue-700 transition-colors duration-200
                                         flex items-center space-x-2 text-sm font-medium"
                                    >
                                        <span>프로필 수정</span>
                                    </button>
                                </div>
                            </div>
                        ) :
                        (
                            <div style={styles.guideSectionBox}>
                                <button
                                    style={styles.viewProfileButton}
                                    onClick={() => handleGuideCreate()}
                                >
                                    👤 가이드 프로필 생성
                                </button>
                            </div>
                        )
                    }
                </div>
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

                                        <div style={styles.buttonGroup}>
                                            {travel.status?.trim().toUpperCase() === "COMPLETED" ? (
                                                reviewedTravels[travel.id] ? (
                                                    <div style={styles.buttonGroup}>
                                                        <button
                                                            style={styles.editButton}
                                                            onClick={() => handleEditReview(travel.id)}
                                                        >
                                                            ✏️ 리뷰 수정
                                                        </button>
                                                        <button
                                                            style={styles.deleteButton}
                                                            onClick={() => handleDeleteReview(travel.id)}
                                                        >
                                                            ❌ 리뷰 삭제
                                                        </button>
                                                    </div>
                                                ) : (
                                                    <button
                                                        style={styles.reviewButton}
                                                        onClick={() => router.push(`/reviews/create?travelId=${travel.id}`)}
                                                    >
                                                        ✍️ 리뷰 작성하기
                                                    </button>
                                                )
                                            ) : null}

                                            {/* 여행 상세보기 버튼은 항상 표시 */}
                                            <button
                                                style={styles.viewProfileButton}
                                                onClick={() => handleViewTravelRequest(travel.id)}
                                            >
                                                🔵 여행 상세 보기
                                            </button>
                                        </div>
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
    guideSectionBox: {
        marginBottom: "2rem",
        backgroundColor: "#FFFFFF",
        padding: "1.5rem",
        borderRadius: "12px",
        boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
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
    reviewButton: {
        backgroundColor: "#28a745",
        color: "#FFFFFF",
        padding: "0.5rem 1rem",
        borderRadius: "4px",
        fontSize: "0.9rem",
        border: "none",
        cursor: "pointer",
    },
    deleteButton: {
        backgroundColor: "#DC2626",
        color: "#FFFFFF", // ⚪️ 글씨는 흰색
        padding: "0.5rem 1rem",
        borderRadius: "4px",
        fontSize: "0.9rem",
        border: "none",
        cursor: "pointer",
    },
    editButton: {
        backgroundColor: "#F59E0B", // 🟠 주황색 버튼
        color: "#FFFFFF",
        padding: "0.5rem 1rem",
        borderRadius: "4px",
        fontSize: "0.9rem",
        border: "none",
        cursor: "pointer",
    },
};

export default MyPage;