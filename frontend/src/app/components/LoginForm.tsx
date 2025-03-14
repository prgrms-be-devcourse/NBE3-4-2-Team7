import React, { useState } from "react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { authService } from "../auth/services/authService";
import { useAuth } from '../contexts/AuthContext';
import SignUpForm from './SignUpForm';  // 새로 만들 회원가입 폼 컴포넌트

const LoginForm: React.FC<{ onClose?: () => void; onLoginSuccess?: () => void }> = ({ onClose, onLoginSuccess }) => {
    const router = useRouter();
    const { login } = useAuth();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [showSignUp, setShowSignUp] = useState(false);  // 회원가입 폼 표시 여부

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await login(email, password);
            onLoginSuccess?.();
        } catch (error: any) {
            console.error('Login error:', error);
            setError(error.message || '로그인에 실패했습니다.');
        }
    };
    
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

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50 backdrop-blur-sm">
            <div className="relative bg-white rounded-lg shadow-lg p-6 w-full max-w-sm">
                {onClose && (
                    <button
                        onClick={onClose}
                        className="absolute top-4 right-4 text-gray-500 hover:text-gray-700"
                    >
                        ✕
                    </button>
                )}
                {!showSignUp ? (
                    <>
                        <h2 className="text-2xl font-bold text-center mb-2 text-blue-600">로그인</h2>
                        <p className="text-center text-gray-600 mb-6">로그인 후 이용 가능합니다.</p>
                        <form onSubmit={handleSubmit} className="space-y-4 mb-6">
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="이메일"
                                className="w-full h-[45px] px-4 border border-gray-300 rounded-md 
                                    focus:outline-none focus:ring-2 focus:ring-blue-500
                                    text-gray-900 placeholder-gray-500"
                                required
                            />
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="비밀번호"
                                className="w-full h-[45px] px-4 border border-gray-300 rounded-md 
                                    focus:outline-none focus:ring-2 focus:ring-blue-500
                                    text-gray-900 placeholder-gray-500"
                                required
                            />
                            {error && (
                                <p className={`text-sm text-center ${
                                    error.startsWith('✅') ? 'text-blue-600' : 'text-red-500'
                                }`}>
                                    {error}
                                </p>
                            )}
                            <button
                                type="submit"
                                className="w-full h-[45px] bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                            >
                                이메일로 로그인
                            </button>
                            <div className="mt-4 text-center">
                                <button
                                    type="button"
                                    onClick={() => setShowSignUp(true)}
                                    className="text-blue-500 hover:text-blue-700"
                                >
                                    회원가입
                                </button>
                            </div>
                        </form>
                        <div className="relative mb-6">
                            <div className="absolute inset-0 flex items-center">
                                <div className="w-full border-t border-gray-300"></div>
                            </div>
                            <div className="relative flex justify-center text-sm">
                                <span className="px-2 bg-white text-gray-500">또는</span>
                            </div>
                        </div>
                        <div className="space-y-4">
                            <div className="w-full h-[45px] bg-[#FEE500] rounded-md">
                                <button
                                    onClick={() => { handleKakaoLogin(); onLoginSuccess?.(); }}
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
                                    onClick={() => { handleGoogleLogin(); onLoginSuccess?.(); }}
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
                                onClick={() => { handleGithubLogin(); onLoginSuccess?.(); }}
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
                    </>
                ) : (
                    <SignUpForm
                        onClose={() => setShowSignUp(false)}
                        onSignUpSuccess={() => {
                            setShowSignUp(false);
                            setError('✅ 회원가입이 완료되었습니다. 로그인해주세요.');
                        }}
                    />
                )}
            </div>
        </div>
    );
};

export default LoginForm; 