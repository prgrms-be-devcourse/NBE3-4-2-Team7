"use client";

import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../auth/services/authService';

interface User {
    name: string;
    email: string;
    imageUrl?: string;
}

interface AuthContextType {
    user: User | null;
    loading: boolean;
    isInitialized: boolean;
    logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);
    const [isInitialized, setIsInitialized] = useState(false);

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const userData = await authService.checkLoginStatus();
                setUser(userData);
            } catch (error) {
                console.error('인증 확인 실패:', error);
                setUser(null);
            } finally {
                setLoading(false);
                setIsInitialized(true);
            }
        };

        checkAuth();
    }, []);

    return (
        <AuthContext.Provider 
            value={{ 
                user, 
                loading, 
                isInitialized, 
                logout: authService.logout
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}; 