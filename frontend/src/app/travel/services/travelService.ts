// app/travel/services/travelService.ts
import axios from 'axios';

// 백엔드 API 기본 URL (필요에 따라 수정)
axios.defaults.baseURL = 'http://localhost:8080';

// 쿠키 포함 설정 (withCredentials)
axios.defaults.withCredentials = true;

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

// 여행 요청 전체 조회 (페이징 및 선택적 카테고리 필터)
export const getTravels = (categoryId?: number, page: number = 0, size: number = 5) => {
    let url = `/travels?page=${page}&size=${size}`;
    if (categoryId) {
        url += `&categoryId=${categoryId}`;
    }
    return axios.get(url);
};

export const getTravelDetail = (travelId: string) => {
    return axios.get(`/travels/${travelId}`);
};


export const createTravel = (data: TravelCreateRequest) => {
    return axios.post(`/travels`, data);
};

// 요청자로서의 매칭 내역 조회
export const getMyGuideRequests = () => {
    return axios.get<GuideRequestDto[]>("/members/me/matchings/requester");
};

export const getMyTravels = () => {
    return axios.get<TravelDto[]>("/members/me/travels");
};
