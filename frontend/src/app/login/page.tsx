"use client";

import React from "react";
import Image from "next/image";
import { authService } from "../auth/services/authService";

const LoginPage: React.FC = () => {
    const handleKakaoLogin = () => {
        authService.loginWithKakao();
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h1 style={styles.title}>여행 가이드 매칭 서비스</h1>
                <p style={styles.subtitle}>
                    카카오 계정으로 간편하게 시작하세요
                </p>
                <div style={styles.buttonContainer}>
                    <button
                        onClick={handleKakaoLogin}
                        style={styles.loginButton}
                    >
                        <Image 
                            src="/images/kakao-login-logo.png"
                            alt="카카오 로그인" 
                            width={300}
                            height={45}
                            priority
                        />
                    </button>
                </div>
            </div>
        </div>
    );
};

const styles: { [key: string]: React.CSSProperties } = {
    container: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "100vh",
        backgroundColor: "#E3F2FD",
        padding: "2rem",
    },
    card: {
        backgroundColor: "#FFFFFF",
        borderRadius: "8px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
        padding: "2rem",
        width: "100%",
        maxWidth: "400px",
        textAlign: "center",
    },
    title: {
        fontSize: "2rem",
        color: "#1E88E5",
        marginBottom: "1rem",
    },
    subtitle: {
        fontSize: "1rem",
        color: "#424242",
        marginBottom: "2rem",
    },
    buttonContainer: {
        display: "flex",
        justifyContent: "center",
        marginTop: "1rem",
    },
    loginButton: {
        border: "none",
        background: "none",
        padding: 0,
        cursor: "pointer",
        transition: "transform 0.2s ease",
        ':hover': {
            transform: "scale(1.02)",
        },
    },
};

export default LoginPage; 