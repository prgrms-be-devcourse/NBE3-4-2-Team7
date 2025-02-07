import axiosInstance from '../../utils/axios';

// 백엔드 API 기본 URL 설정
axiosInstance.defaults.baseURL = 'http://localhost:8080';
axiosInstance.defaults.withCredentials = true;

export const authService = {
    // 카카오 로그인 URL로 리다이렉트
    loginWithKakao: () => {
        const BACKEND_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';
        window.location.href = `${BACKEND_URL}/oauth2/authorization/kakao`;
    },

    // 로그인 상태 확인
    checkLoginStatus: async () => {
        try {
            const response = await axiosInstance.get('/members/me');
            return response.data;
        } catch (error) {
            if (error.response?.status === 401) {
                return null; // 401 에러는 정상적인 비로그인 상태로 처리
            }
            console.error('로그인 상태 확인 실패:', error);
            return null;
        }
    },

    // 로그아웃
    logout: async () => {
        try {
            // 백엔드 로그아웃 요청
            await axiosInstance.post('/auth/logout');
        } catch (error) {
            console.error('로그아웃 요청 실패:', error);
        } finally {
            // 클라이언트 측 상태 정리
            // 모든 쿠키 삭제
            document.cookie.split(";").forEach(function(c) { 
                document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/"); 
            });
            
            // 로컬 스토리지 및 세션 스토리지 초기화
            localStorage.clear();
            sessionStorage.clear();

            // 바로 /travels로 이동
            window.location.replace('/travels');
        }
    }
}; 