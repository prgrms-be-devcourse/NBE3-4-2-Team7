"use client";

import React from "react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { authService } from "../auth/services/authService";

const LoginForm: React.FC<{ onClose?: () => void; onLoginSuccess?: () => void }> = ({ onClose, onLoginSuccess }) => {
    const router = useRouter();
    
    const handleKakaoLogin = () => {
        authService.loginWithKakao();
    };

    const handleGoogleLogin = () => {
        authService.loginWithGoogle();
    };

    const handleGithubLogin = () => {
        authService.loginWithGithub();
    };

    const handleGuestAccess = () => {
        router.push('/travels');
        onClose?.();
    };

    const handleLogin = () => {
        onLoginSuccess?.();
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50 backdrop-blur-sm" 
            onClick={(e) => {
                if (e.target === e.currentTarget) {
                    onClose?.();
                }
            }}
        >
            <div className="relative bg-white rounded-lg shadow-lg p-6 w-full max-w-sm">
                {onClose && (
                    <button
                        onClick={onClose}
                        className="absolute top-4 right-4 text-gray-500 hover:text-gray-700"
                    >
                        ✕
                    </button>
                )}
                <h2 className="text-2xl font-bold text-center mb-2 text-blue-600">로그인</h2>
                <p className="text-center text-gray-600 mb-6">로그인 후 이용 가능합니다.</p>
                <div className="space-y-4">
                    <div className="w-full h-[45px] bg-[#FEE500] rounded-md">
                        <button 
                            onClick={() => { handleKakaoLogin(); handleLogin(); }}
                            className="w-full h-full flex items-center justify-center"
                        >
                            <Image 
                                src="/images/kakao-login-logo.png" 
                                alt="카카오 로그인" 
                                width={300} 
                                height={45} 
                                className="h-[45px] w-[300px] object-contain"
                            />
                        </button>
                    </div>
                    <div className="w-full h-[45px] bg-white border border-gray-300 rounded-md relative">
                    <button 
                            onClick={() => { handleGoogleLogin(); handleLogin(); }}
                            className="w-full h-full flex items-center justify-start pl-4 relative"
                        >
                            <Image 
                                src="/images/google-mark-logo.png" 
                                alt="Google 로그인" 
                                width={24} 
                                height={24} 
                                priority
                                className="ml-3"
                            />
                            <span 
                                className="absolute left-1/2 transform -translate-x-1/2"
                                style={{ color: '#000000', fontFamily: 'Roboto, Arial, sans-serif' }}
                            >
                                Continue with Google
                            </span>
                        </button>
                    </div>
                    <button 
                        onClick={() => { handleGithubLogin(); handleLogin(); }}
                        className="w-full h-[45px] flex items-center justify-start pl-4 bg-gray-800 text-white px-4 py-2 rounded-md hover:bg-gray-700 transition-colors relative"
                    >
                        <Image 
                            src="/images/github-mark-white.png" 
                            alt="GitHub 로그인" 
                            width={24} 
                            height={24} 
                            priority 
                            className="ml-3"
                        />
                        <span 
                            className="absolute left-1/2 transform -translate-x-1/2"
                        >
                            GitHub로 시작하기
                        </span>
                    </button>
                    <button
                        onClick={handleGuestAccess}
                        className="w-full h-[45px] flex items-center justify-center text-blue-600 border border-blue-600 px-4 py-2 rounded-md hover:bg-blue-600 hover:text-white transition-colors"
                    >
                        비회원으로 시작하기
                    </button>
                </div>
            </div>
        </div>
    );
};

export default LoginForm; 