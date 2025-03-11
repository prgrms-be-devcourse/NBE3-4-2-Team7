"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from '@/hooks/useAuth';
import Image from 'next/image';

interface GuideColumn {
    id: number;
    guideName: string;
    title: string;
    content: string;
    imageUrls: string[];
}

export default function GuideColumnsPage() {
    const router = useRouter();
    const { user } = useAuth();
    const [columns, setColumns] = useState<GuideColumn[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string>("");

    useEffect(() => {
        const fetchColumns = async () => {
            try {
                const response = await fetch('/api/guide-columns', {
                    credentials: 'include'
                });
                
                if (!response.ok) {
                    throw new Error(`서버 에러가 발생했습니다. (${response.status})`);
                }
                
                const data = await response.json();
                if (data && Array.isArray(data.content)) {
                    setColumns(data.content);
                } else {
                    setColumns([]);
                }
            } catch (err) {
                console.error("칼럼 로딩 에러:", err);
                setError(err instanceof Error ? err.message : "칼럼을 불러오는데 실패했습니다.");
            } finally {
                setLoading(false);
            }
        };

        fetchColumns();
    }, []);

    // HTML에서 첫 번째 이미지 URL을 추출하는 함수
    const extractFirstImageUrl = (htmlContent: string): string | null => {
        const match = htmlContent.match(/<img[^>]+src="([^">]+)"/);
        return match ? match[1] : null;
    };

    // 텍스트만 추출하는 함수 (HTML 태그 제거)
    const stripHtml = (html: string) => {
        const tmp = document.createElement('div');
        tmp.innerHTML = html;
        return tmp.textContent || tmp.innerText || '';
    };

    if (loading) return (
        <div className="min-h-screen flex items-center justify-center">
            <div className="text-center">
                <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-500 mb-4"></div>
                <p className="text-lg text-gray-600">로딩 중...</p>
            </div>
        </div>
    );

    return (
        <div className="min-h-screen bg-gray-50 py-12">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between items-center mb-8">
                    <h1 className="text-3xl font-bold text-gray-900">가이드 칼럼</h1>
                    {user && (
                        <button
                            onClick={() => router.push('/guide-columns/create')}
                            className="px-4 py-2 bg-blue-500 text-white rounded-lg 
                                     hover:bg-blue-600 transition-colors"
                        >
                            칼럼 작성하기
                        </button>
                    )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {columns.map((column) => (
                        <div
                            key={column.id}
                            onClick={() => router.push(`/guide-columns/${column.id}`)}
                            className="bg-white rounded-xl shadow-sm overflow-hidden 
                                     cursor-pointer hover:shadow-md transition-shadow"
                        >
                            {(column.imageUrls?.[0] || extractFirstImageUrl(column.content)) && (
                                <div className="relative h-48">
                                    <Image
                                        src={column.imageUrls?.[0] || extractFirstImageUrl(column.content)!}
                                        alt={column.title}
                                        fill
                                        className="object-cover"
                                        unoptimized
                                    />
                                </div>
                            )}
                            <div className="p-6">
                                <h2 className="text-xl font-semibold text-gray-900 mb-2">
                                    {column.title}
                                </h2>
                                <p className="text-gray-600 text-sm mb-4">
                                    작성자: {column.guideName}
                                </p>
                                <p className="text-gray-700 line-clamp-3">
                                    {stripHtml(column.content)}
                                </p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
