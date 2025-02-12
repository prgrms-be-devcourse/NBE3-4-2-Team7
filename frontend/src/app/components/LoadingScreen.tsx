"use client";

import React from 'react';

const LoadingScreen = () => {
    return (
        <div className="fixed inset-0 bg-blue-50 flex items-center justify-center z-50">
            <div className="text-center">
                <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-500 mb-4"></div>
                <div className="text-xl text-blue-600 animate-pulse">
                    로그인 중입니다...
                </div>
            </div>
        </div>
    );
};

export default LoadingScreen; 