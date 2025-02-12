"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import LoadingScreen from "../../../components/LoadingScreen";

const LogoutCallbackPage = () => {
    const router = useRouter();

    useEffect(() => {
        const timer = setTimeout(() => {
            router.push("/main");  // 이미 '/main'으로 설정되어 있음을 확인
        }, 1500);

        return () => clearTimeout(timer);
    }, [router]);

    return <LoadingScreen />;
};

export default LogoutCallbackPage; 