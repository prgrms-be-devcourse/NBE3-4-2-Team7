"use client";

import React from 'react';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import { Playfair_Display } from 'next/font/google';

const playfair = Playfair_Display({ subsets: ['latin'] });

const MainPage = () => {
    const router = useRouter();

    return (
        <div className="relative min-h-screen">
            {/* 배경 이미지 */}
            <div className="absolute inset-0">
                <div className="relative w-full h-full">
                    <Image
                        src="/images/start-image.jpg"
                        alt="여행 배경"
                        fill
                        sizes="100vw"
                        style={{
                            objectFit: 'cover',
                        }}
                        priority
                    />
                </div>
            </div>

            {/* 메인 콘텐츠 */}
            <div className="relative z-50 flex flex-col items-center justify-center min-h-screen text-white px-4">
                {/* 메인 텍스트 */}
                <h1 className="text-7xl mb-24 font-bold select-none
                             text-white transform -translate-y-20
                             drop-shadow-[2px_2px_2px_rgba(0,0,0,0.6)]
                             [text-shadow:_2px_2px_10px_rgb(0_0_0_/_100%)]
                             pointer-events-none">
                    새로운 여행을 선사합니다
                </h1>
                
                {/* 클릭 가능한 영역 */}
                <div 
                    onClick={() => router.push('/travels')}
                    className="cursor-pointer group w-full max-w-2xl py-10
                             border-2 border-white rounded-lg 
                             backdrop-blur-sm bg-black/20 
                             hover:bg-black/30 transition-all duration-300"
                >
                    <div className="text-center">
                        <p className="text-5xl mb-4 font-bold select-none
                                  drop-shadow-[2px_2px_2px_rgba(0,0,0,0.6)]
                                  [text-shadow:_2px_2px_10px_rgb(0_0_0_/_100%)]">
                            나의 여행 시작하기
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MainPage; 