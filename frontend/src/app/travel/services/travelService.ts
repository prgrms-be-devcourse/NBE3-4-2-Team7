import axios from "axios";

// 백엔드 API 기본 URL
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

// Axios 기본 설정
axios.defaults.baseURL = API_BASE_URL;
axios.defaults.withCredentials = true;

// 📌 여행 관련 데이터 타입
export interface TravelDto {
    id: number;
    city: string;
    places: string;
    participants: number;
    content: string;
    categoryName: string;
    status: string;
    startDate: string;   // "yyyy-MM-dd" 형식
    endDate: string;     // "yyyy-MM-dd" 형식
    createdAt: string;   // "yyyy-MM-dd HH:mm:ss" 형식
    updatedAt: string;   // "yyyy-MM-dd HH:mm:ss" 형식
}

// 📌 여행 요청 생성 타입
export interface TravelCreateRequest {
    categoryId: number;
    city: string;
    places: string;
    travelPeriod: {
        startDate: string;
        endDate: string;
    };
    participants: number;
    content: string;
}

// 📌 가이드 요청 타입
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
}

// 📌 여행 요청 전체 조회 (페이징 및 선택적 카테고리 필터)
export const getTravels = (categoryId?: number, page: number = 0, size: number = 10) => {
    let url = `${API_BASE_URL}/travels?page=${page}&size=${size}`;
    if (categoryId) {
        url += `&categoryId=${categoryId}`;
    }
    return axios.get<TravelDto[]>(url);
};

// 📌 특정 여행 요청 상세 조회
export const getTravelDetail = (travelId: number | string) => {
    return axios.get<TravelDto>(`${API_BASE_URL}/travels/${travelId}`);
};

// 📌 여행 요청 생성
export const createTravel = (data: TravelCreateRequest) => {
    return axios.post(`${API_BASE_URL}/travels`, data);
};

// 📌 내가 요청한 가이드 요청 내역 조회
export const getGuideRequestsByRequester = () => {
    return axios.get<GuideRequestDto[]>(`${API_BASE_URL}/members/me/matchings/requester`);
};

// 📌 내가 작성한 여행 요청 글 조회
export const getMyTravels = () => {
    return axios.get<TravelDto[]>(`${API_BASE_URL}/members/me/travels`);
};

// 📌 가이드가 사용자의 여행 요청 글에 매칭 요청
export const createTravelOffer = (travelId: number | string) => {
    return axios.post(`${API_BASE_URL}/travel-offers/${travelId}`);
};

// 📌 가이드 요청 상태 업데이트 API
export const updateGuideRequestStatus = async (
    requestId: number,
    guideId: number,
    status: "ACCEPTED" | "REJECTED"
) => {
    return axios.patch(`${API_BASE_URL}/guide-requests/${requestId}/match`, null, {
        params: {
            guideId: guideId,
            status: status,
        },
    });
};




