import axiosInstance from '../../utils/axios';

export interface GuideDto {
    id: number;
    name: string;
    imageUrl?: string;
    activityRegion: string;
    experienceYears: number;
}

// 가이드 목록 조회
export const getGuides = async () => {
    try {
        const response = await axiosInstance.get('/guides');
        return response;
    } catch (error) {
        console.error('가이드 목록 조회 실패:', error);
        return { data: [] };
    }
};

// 특정 가이드 상세 정보 조회
export const getGuideDetail = async (guideId: number) => {
    try {
        const response = await axiosInstance.get(`/guides/${guideId}`);
        return response;
    } catch (error) {
        console.error('가이드 상세 정보 조회 실패:', error);
        throw error;
    }
};

// 가이드 프로필 수정
export const updateGuideProfile = async (guideId: number, data: Partial<GuideDto>) => {
    try {
        const response = await axiosInstance.patch(`/guides/${guideId}`, data);
        return response;
    } catch (error) {
        console.error('가이드 프로필 수정 실패:', error);
        throw error;
    }
}; 