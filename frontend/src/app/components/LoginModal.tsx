"use client";

import React from "react";
import { useRouter } from "next/navigation";
import LoginForm from "./LoginForm";

interface LoginModalProps {
    onClose: () => void;
}

const LoginModal: React.FC<LoginModalProps> = ({ onClose }) => {
    const router = useRouter();

    const handleLoginSuccess = () => {
        // 로그인 성공 후 이동할 페이지
        router.push('/travels');
        onClose();
    };

    return (
        <div 
            className="fixed inset-0 z-50 flex items-center justify-center" 
            onClick={onClose}
        >
            <div 
                className="absolute inset-0 bg-black bg-opacity-50 backdrop-blur-sm" 
                onClick={onClose}
            />
            <div 
                className="relative bg-white rounded-lg p-8 w-full max-w-md mx-4" 
                onClick={(e) => e.stopPropagation()}
            >
                <LoginForm onClose={onClose} onLoginSuccess={handleLoginSuccess} />
            </div>
        </div>
    );
};

export default LoginModal;