"use client";

import React, {useState} from "react";
import {useRouter} from "next/navigation";
import Link from "next/link";
import axios from "axios";

const GuideRegisterPage: React.FC = () => {
    const router = useRouter();
    const [formData, setFormData] = useState({
        name: "",
        languages: "",
        activityRegion: "",
        experienceYears: 0,
        introduction: "",
    });
    const [error, setError] = useState<string>("");

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setFormData({...formData, [e.target.name]: e.target.value});
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            // 가이더 생성 API 호출
            await axios.post("/guides", formData, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`, // JWT 토큰 포함
                },
            });
            alert("가이더 등록이 완료되었습니다!");
            router.push("/travels"); // 리다이렉트 경로 변경
        } catch (error: any) {
            console.error(error.response || error.message);
            setError(
                error.response?.data?.message || "가이더 등록에 실패했습니다. 다시 시도해주세요."
            );
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <Link href="/" style={styles.backLink}>
                    ← 목록으로 돌아가기
                </Link>
                <h1 style={styles.title}>가이더 등록</h1>
                {error && <p style={styles.error}>{error}</p>}
                <form onSubmit={handleSubmit} style={styles.form}>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>이름:</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>사용 언어 (예: kr, en):</label>
                        <input
                            type="text"
                            name="languages"
                            value={formData.languages}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>활동 지역:</label>
                        <input
                            type="text"
                            name="activityRegion"
                            value={formData.activityRegion}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>경력 (년):</label>
                        <input
                            type="number"
                            name="experienceYears"
                            value={formData.experienceYears}
                            onChange={handleChange}
                            required
                            min={0}
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>소개:</label>
                        <textarea
                            name="introduction"
                            value={formData.introduction}
                            onChange={handleChange}
                            required
                            rows={5}
                            style={styles.textarea}
                        />
                    </div>
                    <button type="submit" style={styles.submitButton}>
                        가이더 등록하기
                    </button>
                </form>
            </div>
        </div>
    );
};

const styles: { [key: string]: React.CSSProperties } = {
    container: {
        backgroundColor: "#E0F7FA",
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: "2rem",
    },
    card: {
        backgroundColor: "#fff",
        borderRadius: "8px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
        padding: "2rem",
        width: "100%",
        maxWidth: "600px",
    },
    backLink: {
        display: "block",
        marginBottom: "1rem",
        textDecoration: "none",
        color: "#81D4FA",
        fontWeight: "bold",
    },
    title: {
        textAlign: "center",
        marginBottom: "1.5rem",
        color: "#333",
    },
    error: {
        color: "red",
        marginBottom: "1rem",
        textAlign: "center",
    },
    form: {
        display: "flex",
        flexDirection: "column",
    },
    formGroup: {
        display: "flex",
        flexDirection: "column",
        marginBottom: "1rem",
    },
    label: {
        marginBottom: "0.5rem",
        fontWeight: "bold",
        color: "#555",
    },
    input: {
        padding: "0.75rem",
        borderRadius: "4px",
        border: "1px solid #ccc",
        fontSize: "1rem",
    },
    textarea: {
        padding: "0.75rem",
        borderRadius: "4px",
        border: "1px solid #ccc",
        fontSize: "1rem",
        resize: "vertical",
    },
    submitButton: {
        backgroundColor: "#FF7043",
        border: "none",
        padding: "0.75rem",
        borderRadius: "4px",
        cursor: "pointer",
        color: "#fff",
        fontWeight: "bold",
        fontSize: "1rem",
        marginTop: "1rem",
    },
};

export default GuideRegisterPage;
