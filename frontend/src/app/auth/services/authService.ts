import axiosInstance from '../../utils/axios';
import { AxiosError } from 'axios';

interface LoginCredentials {
    email: string;
    password: string;
}

// 백엔드 API 기본 URL 설정
axiosInstance.defaults.baseURL = 'http://localhost:8080';
axiosInstance.defaults.withCredentials = true;
axiosInstance.defaults.headers.common['Access-Control-Allow-Origin'] = '*';

export const authService = {
    // 일반 로그인 메서드 추가
    login: async (email: string, password: string) => {
        try {
            const response = await axiosInstance.post('/auth/login', {
                email,
                password
            });
            
            // 로그인 성공 시 응답 반환
            return response.data;
        } catch (error) {
            if (error instanceof AxiosError) {
                if (error.response?.status === 401) {
                    throw new Error('이메일 또는 비밀번호가 올바르지 않습니다.');
                }
                throw new Error(error.response?.data?.message || '로그인 중 오류가 발생했습니다.');
            }
            throw error;
        }
    },

    // 카카오 로그인 URL로 리다이렉트
    loginWithKakao: () => {
        const BACKEND_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';
        window.location.href = `${BACKEND_URL}/oauth2/authorization/kakao`;
    },

    loginWithGoogle: () => {
        const BACKEND_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';
        window.location.href = `${BACKEND_URL}/oauth2/authorization/google`; // 구글 로그인 URL
    },

    loginWithGithub: () => {
        const BACKEND_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';
        window.location.href = `${BACKEND_URL}/oauth2/authorization/github`;
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