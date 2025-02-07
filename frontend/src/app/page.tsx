"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { authService } from "./auth/services/authService";

interface User {
    name: string;
    email: string;
    imageUrl?: string;
}

export default function Home() {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);
    const router = useRouter();

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const userData = await authService.checkLoginStatus();
                if (userData) {
                    setUser(userData);
                } else {
                    router.push('/login');
                }
            } catch (error) {
                console.error('인증 확인 실패:', error);
                router.push('/login');
            } finally {
                setLoading(false);
            }
        };

        checkAuth();
    }, [router]);

    const handleLogout = async () => {
        try {
            await authService.logout();
        } catch (error) {
            console.error('로그아웃 실패:', error);
            alert('로그아웃에 실패했습니다.');
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <p>로딩 중...</p>
            </div>
        );
    }

    return (
        <div className="min-h-screen p-8">
            <div className="max-w-4xl mx-auto">
                <header className="flex justify-between items-center mb-8">
                    <h1 className="text-2xl font-bold">여행 가이드 매칭 서비스</h1>
                    {user && (
                        <div className="flex items-center gap-4">
                            <span>{user.name}님 환영합니다</span>
                            <button
                                onClick={handleLogout}
                                className="text-sm text-gray-600 hover:text-gray-900"
                            >
                                로그아웃
                            </button>
                        </div>
                    )}
                </header>

                <main>
                    <div className="grid grid-cols-2 gap-8">
                        <div className="p-6 bg-white rounded-lg shadow">
                            <h2 className="text-xl font-semibold mb-4">여행 요청</h2>
                            {/* 여행 요청 관련 컨텐츠 */}
                        </div>
                        <div className="p-6 bg-white rounded-lg shadow">
                            <h2 className="text-xl font-semibold mb-4">가이드 매칭</h2>
                            {/* 가이드 매칭 관련 컨텐츠 */}
                        </div>
                    </div>
                </main>
            </div>
        </div>
    );
}
