import axios from 'axios';

// 백엔드 API 기본 URL
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.withCredentials = true;

export interface GuideDto {
    id: number;
    name: string;
    introduction: string;
    activityRegion: string;
    experienceYears: number;
    languages: string;
    isDeleted: boolean;
}
export interface GuideProfileDto {
    id: number;
    name: string;
    introduction: string;
    activityRegion: string;
    experienceYears: number;
    languages: string;
    reviewCount: number;
    averageRating: number;
    reviews: ReviewResponseDto[]; // 리뷰 정보
}
export interface ReviewResponseDto {
    id: number;
    travelId: number;
    guideId: number;
    memberId: number;
    comment: string;
    reviewScore: number;
    createdAt: string;
    updatedAt: string;
}

// 가이드 전체 조회 (목록 불러오기)
export const getGuides = () => {
    return axios.get<GuideDto[]>('/guides');
};

// 가이드 상세 조회 (단일 가이드 정보)
export const getGuideProfile = (guideId: number) => {
    return axios.get<GuideProfileDto>(`/guides/${guideId}`);
};

// 자신의 가이드 프로필 상세 조회
export const getGuideDetailByUser = () => {
    return axios.get<GuideDto>(`/guides/me`);
};


// 가이드 추가
export const createGuide = (data: Partial<GuideDto>) => {
    return axios.post('/guides', data);
};

//가이드 정보 업데이트 (이름, 소개, 활동 지역 변경)
export const updateGuide = (guideId: number, updatedData: Partial<GuideDto>) => {
    return axios.put(`/guides/${guideId}`, updatedData);
};

//가이드 삭제 (Soft Delete 방식)
export const deleteGuide = (guideId: number) => {
    return axios.delete(`/guides/${guideId}`);
};

// ✅ 매칭 요청 상태 업데이트 (수락/거절)
export const updateGuideRequestStatus = (requestId: number, guideId: number, status: 'ACCEPTED' | 'REJECTED') => {
    return axios.patch(`/guide-requests/${requestId}/match`, null, {
        params: {
            guideId,
            status,
        },
    });
};

// 내 가이드 프로필인지 검증
export const verifyMyGuide = (guideId: number) => {
    return axios.get(`/guides/${guideId}/verify`);
}

export const getGuideProfileByUser = () => {
    return axios.get<GuideProfileDto>(`/guides/me`);
};

