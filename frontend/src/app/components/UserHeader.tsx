"use client";

import React, { useState, useEffect, useRef } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { useAuth } from '../contexts/AuthContext';
import { useRouter } from 'next/navigation';
import { Playfair_Display } from 'next/font/google';
import { AiOutlineMessage } from 'react-icons/ai';

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
        } catch (error) {
            console.error('로그아웃 실패:', error);
        }
        setIsDropdownOpen(false);
    };

    const handleMyPageClick = () => {
        router.push('/mypage');
        setIsDropdownOpen(false);
    };

    const handleChatClick = () => {
        router.push('/chat-rooms'); // 채팅방 목록 경로로 이동
    };

    return (
        <header className="w-full bg-white shadow-sm">
            <div className="w-full h-16 flex items-center justify-between px-0">
                <div className="flex items-center pl-4">
                    <Link href="/main">
                        <h1 className={`${playfair.className} text-2xl font-bold text-blue-600 tracking-wide
                                        hover:opacity-80 transition-opacity duration-300`}>
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
                        <AiOutlineMessage 
                                    className="text-3xl mr-5 text-blue-700 cursor-pointer hover:opacity-80 transition-opacity" 
                                    onClick={handleChatClick} 
                                    title="채팅방으로 이동"
                                    />
                        <div className="relative">
                            <button
                                ref={buttonRef}
                                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                                className="relative hover:opacity-80 transition-opacity"
                            >
                                <Image
                                    src={user?.imageUrl && user.imageUrl.startsWith('http') 
                                        ? user.imageUrl 
                                        : 'https://i.imgur.com/yCUGLR3.jpeg'}
                                    alt="프로필"
                                    width={40}
                                    height={40}
                                    className="rounded-full"
                                    onError={(e) => {
                                        const target = e.target as HTMLImageElement;
                                        target.src = 'https://i.imgur.com/yCUGLR3.jpeg';
                                    }}
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