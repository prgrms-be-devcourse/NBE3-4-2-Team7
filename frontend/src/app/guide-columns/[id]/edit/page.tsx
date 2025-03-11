"use client";

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { useAuth } from '@/hooks/useAuth';
import RichTextEditor from '@/components/editor/RichTextEditor';

interface GuideColumn {
    id: number;
    title: string;
    content: string;
    imageUrls: string[];
    guideId: number;
}

export default function EditGuideColumnPage() {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [loading, setLoading] = useState(false);
    const router = useRouter();
    const { id } = useParams();
    const { user } = useAuth();

    useEffect(() => {
        const fetchColumn = async () => {
            try {
                const response = await fetch(`/api/guide-columns/${id}`);
                if (!response.ok) throw new Error('칼럼 조회 실패');
                
                const data = await response.json();
                setTitle(data.title);
                setContent(data.content);  // HTML 문자열 (이미지 태그 포함)
            } catch (error) {
                console.error('Error:', error);
                alert('칼럼을 불러오는데 실패했습니다.');
                router.push('/guide-columns');
            }
        };

        if (id) {
            fetchColumn();
        }
    }, [id, router]);

    const handleImageUpload = async (file: File): Promise<string> => {
        const formData = new FormData();
        formData.append('data', JSON.stringify({
            title: '',
            content: ''
        }));
        formData.append('images', file);

        const response = await fetch('/api/guide-columns', {
            method: 'POST',
            credentials: 'include',
            body: formData
        });

        if (!response.ok) {
            throw new Error('이미지 업로드 실패');
        }

        const data = await response.json();
        return data.imageUrls[0];
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            const formData = new FormData();
            formData.append('data', JSON.stringify({
                title,
                content
            }));

            const response = await fetch(`/api/guide-columns/${id}`, {
                method: 'PUT',
                credentials: 'include',
                body: formData
            });

            if (!response.ok) {
                throw new Error('칼럼 수정에 실패했습니다.');
            }

            router.push(`/guide-columns/${id}`);
        } catch (error) {
            console.error('Error:', error);
            alert('칼럼 수정에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 py-12">
            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="bg-white rounded-xl shadow-sm overflow-hidden">
                    <div className="p-8">
                        <h1 className="text-3xl font-bold text-gray-900 mb-8">
                            칼럼 수정
                        </h1>
                        <form onSubmit={handleSubmit}>
                            <div className="mb-6">
                                <label className="block text-sm font-medium text-gray-900 mb-2">
                                    제목
                                </label>
                                <input
                                    type="text"
                                    value={title}
                                    onChange={(e) => setTitle(e.target.value)}
                                    className="w-full px-4 py-2 border rounded-lg text-gray-900"
                                />
                            </div>
                            <div className="mb-6">
                                <label className="block text-sm font-medium text-gray-900 mb-2">
                                    내용
                                </label>
                                <RichTextEditor
                                    content={content}  // 기존 내용 (이미지 포함)
                                    onChange={setContent}
                                    onImageUpload={handleImageUpload}
                                />
                            </div>
                            <div className="flex justify-end gap-4">
                                <button
                                    type="button"
                                    onClick={() => router.back()}
                                    className="px-4 py-2 text-gray-700 border border-gray-300 
                                             rounded-lg hover:bg-gray-50"
                                >
                                    취소
                                </button>
                                <button
                                    type="submit"
                                    disabled={loading}
                                    className="px-4 py-2 bg-blue-500 text-white rounded-lg 
                                             hover:bg-blue-600 disabled:bg-blue-300"
                                >
                                    {loading ? '수정 중...' : '수정하기'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}
