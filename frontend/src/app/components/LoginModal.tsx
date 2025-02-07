"use client";

import React from 'react';
import Image from 'next/image';
import { authService } from '../auth/services/authService';

interface LoginModalProps {
    onClose: () => void;
}

const LoginModal: React.FC<LoginModalProps> = ({ onClose }) => {
    const handleKakaoLogin = () => {
        authService.loginWithKakao();
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
            {/* 배경 오버레이 */}
            <div 
                className="absolute inset-0 bg-black bg-opacity-50 backdrop-blur-sm"
                onClick={onClose}
            />
            
            {/* 모달 컨텐츠 */}
            <div className="relative bg-white rounded-lg p-8 w-full max-w-md">
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-gray-500 hover:text-gray-700"
                >
                    ✕
                </button>

                <h2 className="text-2xl font-bold text-center mb-2 text-blue-600">
                    로그인
                </h2>
                
                {/* 안내 메시지 추가 */}
                <p className="text-center text-gray-600 mb-6">
                    로그인 후 이용 가능합니다.
                </p>

                <div className="space-y-4">
                    <button
                        onClick={handleKakaoLogin}
                        className="w-full"
                    >
                        <Image 
                            src="/images/kakao-login-logo.png"
                            alt="카카오 로그인" 
                            width={300}
                            height={45}
                            className="mx-auto"
                        />
                    </button>
                </div>
            </div>
        </div>
    );
};

export default LoginModal; 