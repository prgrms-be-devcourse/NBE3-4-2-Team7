import axios from "axios";

// 백엔드 API 기본 URL
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

// Axios 기본 설정
axios.defaults.baseURL = API_BASE_URL;
axios.defaults.withCredentials = true;

// 📌 여행 제안 요청 DTO
export interface TravelOfferDto {
    id: number;
    travelId: number;
    travelCity: string;
    isTravelDeleted: boolean;
    guideId: number;
    guideName: string;
    isGuideDeleted: boolean;
    status: string;
    guideEmail: string;    // 가이드 이메일 추가
    userEmail: string;    // 사용자 이메일 추가
}

// 📌 가이드 요청 DTO
export interface GuideRequestDto {
    id: number;
    travelId: number;
    travelCity: string;
    isTravelDeleted: boolean;
    guideId: number;
    guideName: string;
    isGuideDeleted: boolean;
    memberName: string;
    status: string;
    guideEmail: string;    // 가이드 이메일 추가
    userEmail: string;    // 사용자 이메일 추가
}

// 📌 가이드 : 내가 사용자에게 보낸 여행 제안 요청 내역
export const getTravelOffersByGuide = () => {
    return axios.get<TravelOfferDto[]>(`${API_BASE_URL}/members/me/matchings/travel-offers`);
    // 응답 데이터가 배열 형태임
};

// 📌 가이드 : 사용자가 나에게 요청한 가이드 요청 내역
export const getGuideRequestsByGuide = () => {
    return axios.get<GuideRequestDto[]>(`${API_BASE_URL}/members/me/matchings/guide`);
    // 응답 데이터가 배열 형태임
};

// 📌 여행 제안 요청 상태 업데이트 API
export const updateTravelOfferStatus = async (
    requestId: number,
    status: "ACCEPTED" | "REJECTED"
): Promise<void> => {
    try {
        await axios.patch(`${API_BASE_URL}/travel-offers/${requestId}/match`, null, {
            params: {
                status, // 요청 상태 (ACCEPTED or REJECTED)
            },
        });
    } catch (error) {
        console.error("Failed to update travel offer status", error);
        throw error;
    }
};

export const getTravelOffersForUser = () => {
    return axios.get<TravelOfferDto[]>(`${API_BASE_URL}/members/me/matchings/travel-offers/received`);
    // 응답 데이터가 배열 형태임
};