import axiosInstance from '../../utils/axios';
import { AxiosError } from 'axios';

// 백엔드 API 기본 URL 설정
axiosInstance.defaults.baseURL = 'http://localhost:8080';
axiosInstance.defaults.withCredentials = true;

export const authService = {
    // 카카오 로그인 URL로 리다이렉트
    loginWithKakao: () => {
        const BACKEND_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';
        window.location.href = `${BACKEND_URL}/oauth2/authorization/kakao`;
    },

    // 구글 로그인 추가
    loginWithGoogle: () => {
        const BACKEND_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';
        window.location.href = `${BACKEND_URL}/oauth2/authorization/google`;
    },

    // 로그인 상태 확인
    checkLoginStatus: async () => {
        try {
            const response = await axiosInstance.get('/members/me');
            return response.data;
        } catch (error) {
            if ((error as AxiosError).response?.status === 401) {
                return null;
            }
            console.error('로그인 상태 확인 실패:', error);
            return null;
        }
    },

    // 로그아웃
    logout: async () => {
        try {
            await axiosInstance.post('/auth/logout');
            // 클라이언트 측 상태 정리
            document.cookie.split(";").forEach(c => { 
                document.cookie = c.replace(/^ +/, "")
                    .replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/"); 
            });
            // 로그아웃 후 travels 페이지로 리다이렉트
            window.location.replace('/travels');
        } catch (error) {
            console.error('로그아웃 요청 실패:', error);
            // 에러가 발생해도 travels 페이지로 리다이렉트
            window.location.replace('/travels');
        }
    }
}; 