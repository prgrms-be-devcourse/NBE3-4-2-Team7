"use client";

import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../auth/services/authService';
import { usePathname, useRouter } from 'next/navigation';
import ErrorModal from '../components/ErrorModal';

interface User {
    name: string;
    email: string;
    imageUrl?: string;
}

interface AuthContextType {
    user: User | null;
    loading: boolean;
    isInitialized: boolean;
    logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType>({
    user: null,
    loading: true,
    isInitialized: false,
    logout: async () => {}
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);
    const [isInitialized, setIsInitialized] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const router = useRouter();
    const pathname = usePathname();

    // 로그인이 필요한 경로들을 정의
    const protectedRoutes = [
        '/travels/create',
        '/mypage',
        // 다른 보호된 경로들 추가
    ];

    useEffect(() => {
        const checkAuth = async () => {
            try {
                // 현재 경로가 보호된 경로인지 확인
                const isProtectedRoute = protectedRoutes.some(route => pathname?.startsWith(route));
                
                const userData = await authService.checkLoginStatus();
                if (userData) {
                    setUser(userData);
                } else {
                    setUser(null);
                    // 보호된 경로에 비로그인 상태로 접근 시도할 경우
                    if (isProtectedRoute) {
                        setError('잘못된 접근입니다. 로그인이 필요한 페이지입니다.');
                        router.replace('/travels');  // 안전한 페이지로 리다이렉트
                    }
                }
            } catch (error: any) {
                console.error('인증 확인 실패:', error);
                setUser(null);
                if (error.response?.status === 401 && protectedRoutes.some(route => pathname?.startsWith(route))) {
                    setError('잘못된 접근입니다. 로그인이 필요한 페이지입니다.');
                    router.replace('/travels');  // 안전한 페이지로 리다이렉트
                }
            } finally {
                setLoading(false);
                setIsInitialized(true);
            }
        };

        checkAuth();
    }, [pathname, router]);

    const handleErrorClose = () => {
        setError(null);
    };

    return (
        <AuthContext.Provider 
            value={{ 
                user, 
                loading, 
                isInitialized,
                logout: authService.logout
            }}
        >
            {children}
            {error && <ErrorModal message={error} onClose={handleErrorClose} />}
        </AuthContext.Provider>
    );
}

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}; 