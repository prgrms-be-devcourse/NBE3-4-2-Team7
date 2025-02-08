"use client";

import React from 'react';
import { useRouter } from 'next/navigation';

interface ErrorModalProps {
    message: string;
    onClose: () => void;
}

const ErrorModal: React.FC<ErrorModalProps> = ({ message, onClose }) => {
    const router = useRouter();

    const handleConfirm = () => {
        onClose();
        router.replace('/travels');  // 안전한 페이지로 리다이렉트
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
            {/* 배경 오버레이 */}
            <div className="absolute inset-0 bg-black bg-opacity-50 backdrop-blur-sm" />
            
            {/* 모달 컨텐츠 */}
            <div className="relative bg-white rounded-lg p-8 w-full max-w-md mx-4 animate-fade-in">
                <h2 className="text-2xl font-bold text-red-600 mb-4">
                    접근 오류
                </h2>
                <p className="text-gray-700 mb-6">
                    {message}
                </p>
                <div className="flex justify-end">
                    <button
                        onClick={handleConfirm}
                        className="px-6 py-2 bg-blue-600 text-white rounded-md 
                                 hover:bg-blue-700 transition-colors font-medium"
                    >
                        확인
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ErrorModal; 