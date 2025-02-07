import axios from 'axios';

// 백엔드 API 기본 URL 설정
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.withCredentials = true;

export const authService = {
    // 카카오 로그인 URL로 리다이렉트
    loginWithKakao: () => {
        window.location.href = `${axios.defaults.baseURL}/oauth2/authorization/kakao`;
    },

    // 로그인 상태 확인
    checkLoginStatus: async () => {
        try {
            const response = await axios.get('/users/me');
            return response.data;
        } catch (error) {
            return null;
        }
    },

    // 로그아웃
    logout: async () => {
        try {
            // 백엔드 로그아웃
            await axios.post('/auth/logout');
            
            // 카카오 로그아웃
            const kakaoLogoutUrl = 'https://kauth.kakao.com/oauth/logout';
            const currentUrl = window.location.origin;
            window.location.href = `${kakaoLogoutUrl}?client_id=${process.env.NEXT_PUBLIC_KAKAO_CLIENT_ID}&logout_redirect_uri=${currentUrl}/login`;
        } catch (error) {
            console.error('로그아웃 실패:', error);
            throw error;
        }
    }
}; 