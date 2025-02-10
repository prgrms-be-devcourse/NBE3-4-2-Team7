"use client";

import React from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Playfair_Display } from 'next/font/google';

const playfair = Playfair_Display({ subsets: ['latin'] });

const GuestHeader: React.FC = () => {
    const router = useRouter();

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
                    <button
                        onClick={() => router.push('/login')}
                        className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors font-medium"
                    >
                        로그인
                    </button>
                </div>
            </div>
        </header>
    );
};

export default GuestHeader; 