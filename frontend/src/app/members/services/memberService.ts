import axiosInstance from '../../utils/axios';

// 회원 정보 DTO
export interface MemberResponseDTO {
    id: number;
    email: string;
    name: string;
    imageUrl: string | null;
    hasGuideProfile: boolean;
}

// 내 정보 조회 API
export const getMyInfo = async (): Promise<MemberResponseDTO> => {
    const response = await axiosInstance.get<MemberResponseDTO>("/members/me");
    return response.data;
};

// 자신의 가이드 프로필이 존재하는지 검사
export const hasGuideProfile = () => {
    return axiosInstance.get<boolean>(`/members/me/guide`);
};

