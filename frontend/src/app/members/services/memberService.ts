import axios from "axios";

// 백엔드 API 기본 URL
axios.defaults.baseURL = "http://localhost:8080";
axios.defaults.withCredentials = true;

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
    const response = await axios.get<MemberResponseDTO>("/members/me");
    return response.data; // API가 직접 DTO 객체를 반환하므로 `response.data`를 반환
};
