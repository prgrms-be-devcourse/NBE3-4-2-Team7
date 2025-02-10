import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { useRouter } from 'next/navigation';

const BACKEND_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

const axiosInstance = axios.create({
    baseURL: BACKEND_URL,
    withCredentials: true,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

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
        const originalRequest = error.config;

        // 로그인이 필요하지 않은 public 경로들
        const publicPaths = ['/travels', '/login', '/main'];
        const currentPath = window.location.pathname;

        // 401 에러이고 토큰 갱신 시도를 하지 않은 경우
        if (error.response?.status === 401 && originalRequest && !originalRequest.url?.includes('/auth/refresh')) {
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
                    return axios(originalRequest);
                } catch (refreshError) {
                    // 토큰 갱신 실패 시
                    processQueue(refreshError);
                    
                    // public 경로가 아닌 경우에만 리다이렉트
                    if (!publicPaths.includes(currentPath)) {
                        window.location.replace('/travels');
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
                return axios(originalRequest);
            }).catch((err) => {
                return Promise.reject(err);
            });
        }

        return Promise.reject(error);
    }
);

export default axiosInstance; 