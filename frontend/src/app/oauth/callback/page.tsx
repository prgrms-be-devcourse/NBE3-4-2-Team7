"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import LoadingScreen from "../../components/LoadingScreen";
import { authService } from "../../auth/services/authService";
import ErrorModal from "../../components/ErrorModal";

const OAuthCallbackPage = () => {
    const router = useRouter();
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const checkLoginStatus = async () => {
            try {
                // 로그인 상태 확인을 위해 잠시 대기
                await new Promise(resolve => setTimeout(resolve, 1000));
                
                const user = await authService.checkLoginStatus();
                if (user) {
                    router.replace("/travels");
                } else {
                    setError("로그인에 실패했습니다. 다시 시도해 주세요.");
                }
            } catch (error) {
                console.error("로그인 처리 중 오류 발생:", error);
                setError("로그인 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
            }
        };

        checkLoginStatus();
    }, [router]);

    const handleErrorClose = () => {
        setError(null);
        router.replace("/login");
    };

    if (error) {
        return <ErrorModal message={error} onClose={handleErrorClose} />;
    }

    return <LoadingScreen />;
};

export default OAuthCallbackPage; 