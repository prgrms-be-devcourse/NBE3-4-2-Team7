"use client";

import Image from "next/image";
import React, {useEffect, useState} from "react";
import {GuideDto} from "@/app/guides/services/guideService";
import {getGuides} from "@/app/guide/services/guideService";
import {useRouter} from "next/navigation";


const GuideList = () => {
    const [guides, setGuides] = useState<GuideDto[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string>("");
    const router = useRouter();

    useEffect(() => {
        setLoading(true);
        Promise.all([
            getGuides().catch(() => ({ data: [] }))
        ])
            .then(([guideResponse]) => {
                setGuides(guideResponse.data);
                setError("");
            })
            .catch((error) => {
                console.error("가이드 데이터 로딩 에러:", error);
                setError("가이드 데이터를 불러오는 데 실패했습니다.");
            })
            .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-500 mb-4"></div>
                    <p className="text-lg text-gray-600">로딩 중...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="text-center text-red-600">
                    <p className="text-lg">{error}</p>
                    <button
                        onClick={() => window.location.reload()}
                        className="mt-4 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
                    >
                        다시 시도
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 py-12">
                <h1 className="text-4xl font-bold text-gray-900 mb-8 text-center">
                    모든 가이드 목록
                </h1>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                    {guides.length === 0 ? (
                        <div className="col-span-full text-center text-gray-500">
                            현재 등록된 가이드가 없습니다.
                        </div>
                    ) : (
                        guides.map((guide) => (
                            <div
                                key={guide.id}
                                className="bg-white shadow-md rounded-lg overflow-hidden cursor-pointer transform hover:scale-105 transition-transform"
                                onClick={() => router.push(`/guides/${guide.id}`)}
                            >
                                <div className="relative w-full h-48">
                                    <Image
                                        src={guide.imageUrl || "/default-profile.png"}
                                        alt={guide.name}
                                        layout="fill"
                                        objectFit="cover"
                                    />
                                </div>
                                <div className="p-4">
                                    <h2 className="text-xl font-semibold text-gray-800">
                                        {guide.name}
                                    </h2>
                                    <p className="text-gray-600">{guide.activityRegion}</p>
                                    <p className="text-sm text-gray-500">경력 {guide.experienceYears}년</p>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
};

export default GuideList;