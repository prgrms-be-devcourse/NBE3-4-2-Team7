"use client";

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import RichTextEditor from '@/components/editor/RichTextEditor';
import { useAuth } from '@/hooks/useAuth';

export default function CreateGuideColumnPage() {
    const router = useRouter();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [loading, setLoading] = useState(false);
    const { user } = useAuth();

    const handleImageUpload = async (file: File): Promise<string> => {
        const formData = new FormData();
        formData.append('images', file);
        formData.append('data', JSON.stringify({
            title: '',
            content: ''
        }));

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

            const response = await fetch('/api/guide-columns', {
                method: 'POST',
                credentials: 'include',
                body: formData
            });

            if (!response.ok) {
                throw new Error('칼럼 작성에 실패했습니다.');
            }

            router.push('/guide-columns');
        } catch (error) {
            console.error('Error:', error);
            alert('칼럼 작성에 실패했습니다.');
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
                            새 칼럼 작성
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
                                    className="w-full px-4 py-2 border rounded-lg focus:ring-2 
                                             focus:ring-blue-500 focus:border-blue-500
                                             text-gray-900 placeholder-gray-500"
                                    placeholder="제목을 입력하세요"
                                    required
                                />
                            </div>

                            <div className="mb-6">
                                <label className="block text-sm font-medium text-gray-900 mb-2">
                                    내용
                                </label>
                                <RichTextEditor
                                    content={content}
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
                                    {loading ? '작성 중...' : '작성하기'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}
