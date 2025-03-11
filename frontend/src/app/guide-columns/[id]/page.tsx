"use client";

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Image from 'next/image';
import { useAuth } from '@/hooks/useAuth';

interface GuideColumn {
    id: number;
    guideName: string;
    title: string;
    content: string;
    imageUrls: string[];
    guideId: number;
}

export default function GuideColumnDetailPage() {
    const params = useParams();
    const router = useRouter();
    const { user } = useAuth();
    const [column, setColumn] = useState<GuideColumn | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchColumn = async () => {
            try {
                if (!params.id) return;
                const response = await fetch(`/api/guide-columns/${params.id}`, {
                    credentials: 'include'
                });
                
                if (!response.ok) {
                    throw new Error('칼럼을 불러오는데 실패했습니다.');
                }

                const data = await response.json();
                console.log('칼럼 데이터:', data);
                console.log('현재 유저:', user);
                setColumn(data);
            } catch (error) {
                console.error('Error:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchColumn();
    }, [params.id]);

    const handleEdit = () => {
        router.push(`/guide-columns/${params.id}/edit`);
    };

    const handleDelete = async () => {
        if (!confirm('정말로 이 칼럼을 삭제하시겠습니까?')) {
            return;
        }

        try {
            const response = await fetch(`/api/guide-columns/${params.id}`, {
                method: 'DELETE',
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('칼럼 삭제에 실패했습니다.');
            }

            router.push('/guide-columns');
        } catch (error) {
            console.error('Error:', error);
            alert('칼럼 삭제에 실패했습니다.');
        }
    };

    const renderContent = (content: string) => {
        return (
            <div 
                className="prose max-w-none mb-8"
                dangerouslySetInnerHTML={{ __html: content }}
            />
        );
    };

    if (loading) return (
        <div className="min-h-screen flex items-center justify-center">
            <div className="text-center">
                <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-500 mb-4"></div>
                <p className="text-lg text-gray-600">로딩 중...</p>
            </div>
        </div>
    );

    if (!column) return null;

    return (
        <div className="min-h-screen bg-gray-50 py-12">
            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="bg-white rounded-xl shadow-sm overflow-hidden">
                    <div className="p-8">
                        <h1 className="text-3xl font-bold text-gray-900 mb-4">
                            {column.title}
                        </h1>
                        <div className="text-gray-600 mb-6">
                            작성자: {column.guideName}
                        </div>
                        
                        {/* 이미지 갤러리 */}
                        {column.imageUrls && column.imageUrls.length > 0 && (
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
                                {column.imageUrls.map((url, index) => (
                                    <div key={index} className="relative aspect-video">
                                        <Image
                                            src={url}
                                            alt={`이미지 ${index + 1}`}
                                            fill
                                            unoptimized
                                            className="object-cover rounded-lg"
                                        />
                                    </div>
                                ))}
                            </div>
                        )}

                        <div className="prose max-w-none mb-8">
                            <div 
                                className="text-gray-900 prose prose-lg max-w-none"
                                dangerouslySetInnerHTML={{ __html: column.content }}
                            />
                        </div>
                        
                        {user && user.id === column.guideId && (
                            <div className="flex justify-end gap-4 mt-6">
                                <button
                                    onClick={handleEdit}
                                    className="px-6 py-2.5 bg-blue-500 text-white rounded-lg 
                                             hover:bg-blue-600 transition-colors"
                                >
                                    수정하기
                                </button>
                                <button
                                    onClick={handleDelete}
                                    className="px-6 py-2.5 bg-red-500 text-white rounded-lg 
                                             hover:bg-red-600 transition-colors"
                                >
                                    삭제하기
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
