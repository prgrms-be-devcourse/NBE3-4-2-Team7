import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

interface CustomInternalAxiosRequestConfig extends InternalAxiosRequestConfig {
    _retry?: boolean;
}

const BACKEND_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

const getAccessTokenFromCookie = (): string | null => {
    const cookies = document.cookie.split("; ");
    for (let i = 0; i < cookies.length; i++) {
        const [name, value] = cookies[i].split("=");
        if (name === "accessToken") {
            return decodeURIComponent(value);
        }
    }
    return null;
};

const axiosInstance = axios.create({
    baseURL: BACKEND_URL,
    withCredentials: true,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

// 요청 인터셉터 - Authorization 헤더 설정
axiosInstance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const accessToken = getAccessTokenFromCookie();
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 토큰 갱신 진행 중인지 확인하는 플래그
let isRefreshing = false;
// 토큰 갱신 대기 중인 요청들을 저장하는 배열
let failedQueue: { resolve: Function; reject: Function; }[] = [];

// 대기 중인 요청들 처리
const processQueue = (error: any = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve();
        }
    });
    failedQueue = [];
};

// 요청 인터셉터
axiosInstance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 응답 인터셉터
axiosInstance.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
        const originalRequest = error.config as CustomInternalAxiosRequestConfig;

        // 401 에러이고 토큰 갱신 시도를 하지 않은 경우
        if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
            originalRequest._retry = true;

            if (!isRefreshing) {
                isRefreshing = true;

                try {
                    // 토큰 갱신 요청
                    await axios.post(
                        `${BACKEND_URL}/auth/refresh`,
                        {},
                        { withCredentials: true }
                    );

                    // 대기 중인 요청들 처리
                    processQueue();
                    
                    // 원래 요청 재시도
                    return axiosInstance(originalRequest);

                } catch (refreshError) {
                    // 토큰 갱신 실패 시
                    processQueue(refreshError);
                    
                    // 실제 인증 오류인 경우에만 리다이렉트
                    if (axios.isAxiosError(refreshError) && refreshError.response?.status === 401) {
                        // 보호된 경로에서만 리다이렉트
                        const protectedRoutes = ['/travels/create', '/mypage'];
                        if (protectedRoutes.some(route => window.location.pathname.startsWith(route))) {
                            // 로그인 모달을 표시하기 위해 상태 업데이트
                            window.dispatchEvent(new CustomEvent('showLoginModal'));
                            // 안전한 페이지로 리다이렉트
                            window.location.replace('/travels');
                        }
                    }
                    return Promise.reject(refreshError);
                } finally {
                    isRefreshing = false;
                }
            }

            // 토큰 갱신 중인 경우 대기열에 추가
            return new Promise((resolve, reject) => {
                failedQueue.push({ resolve, reject });
            }).then(() => {
                return axiosInstance(originalRequest);
            });
        }

        return Promise.reject(error);
    }
);

export default axiosInstance; 