"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

const OAuthCallbackPage = () => {
    const router = useRouter();

    useEffect(() => {
        // 로그인 성공 후 메인 페이지로 리다이렉트
        router.push("/");
    }, [router]);

    return (
        <div className="min-h-screen flex items-center justify-center">
            <div className="text-center">
                <h2 className="text-xl font-semibold">로그인 처리 중...</h2>
                <p className="mt-2 text-gray-600">잠시만 기다려주세요.</p>
            </div>
        </div>
    );
};

export default OAuthCallbackPage; 