"use client";

import React, { useState, useEffect, useRef } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { useAuth } from '../contexts/AuthContext';
import { useRouter } from 'next/navigation';
import { Playfair_Display } from 'next/font/google';

const playfair = Playfair_Display({ subsets: ['latin'] });

const UserHeader: React.FC = () => {
    const { user, logout } = useAuth();
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const router = useRouter();
    const dropdownRef = useRef<HTMLDivElement>(null);
    const buttonRef = useRef<HTMLButtonElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (
                dropdownRef.current && 
                buttonRef.current && 
                !dropdownRef.current.contains(event.target as Node) &&
                !buttonRef.current.contains(event.target as Node)
            ) {
                setIsDropdownOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    const handleLogout = async () => {
        try {
            await logout();
            router.push('/main');
        } catch (error) {
            console.error('로그아웃 실패:', error);
        }
        setIsDropdownOpen(false);
    };

    const handleMyPageClick = () => {
        router.push('/mypage');
        setIsDropdownOpen(false);
    };

    return (
        <header className="w-full bg-white shadow-sm">
            <div className="w-full h-16 flex items-center justify-between px-0">
                <div className="flex items-center pl-4">
                    <Link href="/travels">
                        <h1 className={`${playfair.className} text-2xl font-bold text-blue-600 tracking-wide`}>
                            <span className="bg-gradient-to-r from-blue-600 to-blue-800 bg-clip-text text-transparent">
                                내 여행의 완성,{' '}
                            </span>
                            <span className="text-blue-800 italic">
                                Trip Market
                            </span>
                        </h1>
                    </Link>
                </div>
                
                <div className="flex items-center pr-4">
                    <div className="flex items-center">
                        <span className="mr-4 text-[#1a237e] font-medium whitespace-nowrap">
                            {user?.name}님 환영합니다!
                        </span>
                        <div className="relative">
                            <button
                                ref={buttonRef}
                                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                                className="relative hover:opacity-80 transition-opacity"
                            >
                                <Image
                                    src={user?.imageUrl || '/default-profile.png'}
                                    alt="프로필"
                                    width={40}
                                    height={40}
                                    className="rounded-full"
                                />
                            </button>
                            
                            {isDropdownOpen && (
                                <div 
                                    ref={dropdownRef}
                                    className="absolute right-0 top-12 w-48 bg-white rounded-md shadow-lg py-1 z-50"
                                >
                                    <button
                                        onClick={handleMyPageClick}
                                        className="w-full text-left px-4 py-2 hover:bg-gray-100 text-[#1a237e] font-medium"
                                    >
                                        마이페이지
                                    </button>
                                    <button
                                        onClick={handleLogout}
                                        className="w-full text-left px-4 py-2 hover:bg-gray-100 text-[#1a237e] font-medium"
                                    >
                                        로그아웃
                                    </button>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </header>
    );
};

export default UserHeader; 