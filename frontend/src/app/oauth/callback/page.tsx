"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import LoadingScreen from "../../components/LoadingScreen";

const OAuthCallbackPage = () => {
    const router = useRouter();

    useEffect(() => {
        // 로그인 성공 후 travels 페이지로 리다이렉트
        const timer = setTimeout(() => {
            router.push("/travels");
        }, 1500); // 1.5초 후 리다이렉트

        return () => clearTimeout(timer);
    }, [router]);

    return <LoadingScreen />;
};

export default OAuthCallbackPage; 