"use client";

import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import { Playfair_Display } from 'next/font/google';

const playfair = Playfair_Display({ subsets: ['latin'] });

const MainPage = () => {
    const router = useRouter();

    useEffect(() => {
        router.push('/main');
    }, [router]);

    return null;
};

export default MainPage;
