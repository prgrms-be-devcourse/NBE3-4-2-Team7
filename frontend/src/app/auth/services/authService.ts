import axios from 'axios';

// 백엔드 API 기본 URL 설정
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.withCredentials = true;

export const authService = {
    // 카카오 로그인 URL로 리다이렉트
    loginWithKakao: () => {
        const BACKEND_URL = 'http://localhost:8080';
        window.location.href = `${BACKEND_URL}/oauth2/authorization/kakao`;
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
            
            // 카카오 연결 끊기 (unlink)
            const KAKAO_UNLINK_URL = 'https://kapi.kakao.com/v1/user/unlink';
            try {
                await axios.post(KAKAO_UNLINK_URL);
            } catch (e) {
                console.log('카카오 연결 끊기 실패 (무시 가능)', e);
            }

            // 모든 쿠키 삭제
            document.cookie.split(";").forEach(function(c) { 
                document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/"); 
            });
            
            // 로컬 스토리지 및 세션 스토리지 초기화
            localStorage.clear();
            sessionStorage.clear();
            
            // 메인 페이지로 리다이렉트
            window.location.href = '/main';
            
        } catch (error) {
            console.error('로그아웃 실패:', error);
            throw error;
        }
    }
}; 