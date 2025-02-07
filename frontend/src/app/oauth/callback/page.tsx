"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import LoadingScreen from "../../components/LoadingScreen";
import { authService } from "../../auth/services/authService";

const OAuthCallbackPage = () => {
    const router = useRouter();

    useEffect(() => {
        const checkLoginStatus = async () => {
            try {
                // 로그인 상태 확인을 위해 잠시 대기
                await new Promise(resolve => setTimeout(resolve, 1000));
                
                const user = await authService.checkLoginStatus();
                if (user) {
                    router.push("/travels");
                } else {
                    throw new Error("로그인 실패");
                }
            } catch (error) {
                console.error("로그인 처리 중 오류 발생:", error);
                router.push("/login");
            }
        };

        checkLoginStatus();
    }, [router]);

    return <LoadingScreen />;
};

export default OAuthCallbackPage; 