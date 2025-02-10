"use client";

import React from "react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { authService } from "../auth/services/authService";

const LoginPage: React.FC = () => {
    const router = useRouter();
    
    const handleKakaoLogin = () => {
        authService.loginWithKakao();
    };

    const handleGoogleLogin = () => {
        authService.loginWithGoogle();
    };

    const handleGithubLogin = () => {
        authService.loginWithGithub();
    };

    const handleGuestAccess = () => {
        router.push('/travels');
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h1 style={styles.title}>
                    <span style={styles.titleText}>Login</span>
                </h1>
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
                            style={{ objectFit: "contain" }}
                        />
                    </button>
                    <button
                        onClick={handleGoogleLogin}
                        style={styles.loginButton}
                    >
                        <Image
                            src="/images/google-login-logo.png"
                            alt="구글 로그인"
                            width={300}
                            height={45}
                            style={{ objectFit: "contain" }}
                        />
                    </button>
                    <button
                        onClick={handleGithubLogin}
                        style={styles.githubButton}
                    >
                        <Image
                            src="/images/github-mark-white.png"
                            alt=" GitHub로 시작하기 "
                            width={24}
                            height={24}
                            priority
                        />
                        <span style={styles.githubText}>GitHub로 시작하기</span>
                    </button>
                    <button
                        onClick={handleGuestAccess}
                        style={styles.guestButton}
                    >
                        비회원으로 시작하기
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
        alignItems: "flex-start",
        minHeight: "100vh",
        backgroundColor: "#E3F2FD",
        paddingTop: "15vh",
        paddingRight: "2rem",
        paddingBottom: "2rem",
        paddingLeft: "2rem",
        overflow: "auto",
    },
    card: {
        backgroundColor: "#FFFFFF",
        borderRadius: "8px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
        padding: "2rem",
        width: "100%",
        minWidth: "400px",
        maxWidth: "500px",
        textAlign: "center",
    },
    title: {
        marginBottom: "2rem",
        whiteSpace: "nowrap",
    },
    titleText: {
        fontSize: "2rem",
        color: "#1E88E5",
        fontWeight: "bold",
    },
    buttonContainer: {
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        gap: "1rem",
        marginTop: "1rem",
    },
    /* 공통 버튼 스타일 */
    buttonContent: {
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        gap: "10px",
    },
    buttonText: {
        fontSize: "16px",
        fontWeight: "bold",
    },
    kakaoButton: {
        width: "300px",
        height: "45px",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#FEE500",
        border: "none",
        borderRadius: "4px",
        color: "#000",
        fontSize: "16px",
        fontWeight: "bold",
        cursor: "pointer",
        transition: "opacity 0.2s ease",
    },
    googleButton: {
        width: "300px",
        height: "45px",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#FFFFFF",
        border: "1px solid #ccc",
        borderRadius: "4px",
        color: "#000",
        fontSize: "16px",
        fontWeight: "bold",
        cursor: "pointer",
        transition: "opacity 0.2s ease",
    },
    githubButton: {
        width: "300px",
        height: "45px",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#333",
        border: "none",
        borderRadius: "4px",
        color: "#FFF",
        fontSize: "16px",
        fontWeight: "bold",
        cursor: "pointer",
        transition: "opacity 0.2s ease",
    },
    guestButton: {
        width: "300px",
        height: "45px",
        padding: "0",
        backgroundColor: "#FFFFFF",
        border: "2px solid #1E88E5",
        borderRadius: "4px",
        color: "#1E88E5",
        fontSize: "16px",
        fontWeight: "bold",
        cursor: "pointer",
        transition: "all 0.2s ease",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
    },
};

export default LoginPage; 