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

// 가이드 전체 조회 (목록 불러오기)
export const getGuides = () => {
    return axios.get<GuideDto[]>('/guides');
};

// 가이드 상세 조회 (단일 가이드 정보)
export const getGuideDetail = (guideId: number) => {
    return axios.get<GuideDto>(`/guides/${guideId}`);
};

// 가이드 추가
export const createGuide = (data: GuideDto) => {
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
