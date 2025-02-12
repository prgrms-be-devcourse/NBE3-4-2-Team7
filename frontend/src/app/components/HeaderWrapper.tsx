"use client";

import { usePathname } from 'next/navigation';
import Header from './Header';

export default function HeaderWrapper() {
    const pathname = usePathname();
    
    // /main 페이지에서는 헤더를 표시하지 않음
    if (pathname === '/main') {
        return null;
    }

    return <Header />;
} 