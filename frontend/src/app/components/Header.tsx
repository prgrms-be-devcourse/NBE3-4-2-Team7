"use client";

import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import { usePathname } from 'next/navigation';
import UserHeader from './UserHeader';
import GuestHeader from './GuestHeader';

const Header: React.FC = () => {
    const { user, isInitialized } = useAuth();
    const pathname = usePathname();

    // 초기화되지 않았거나 로그인 페이지에서는 헤더를 숨김
    if (!isInitialized || pathname === '/login') {
        return null;
    }

    return user ? (
        <header className="fixed w-full top-0 z-50 bg-white/90 backdrop-blur-sm shadow-sm">
            <UserHeader />
        </header>
    ) : (
        <header className="fixed w-full top-0 z-50 bg-white/90 backdrop-blur-sm shadow-sm">
            <GuestHeader />
        </header>
    );
};

export default Header; 