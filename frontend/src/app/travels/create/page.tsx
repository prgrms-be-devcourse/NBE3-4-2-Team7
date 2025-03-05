"use client";

import React, {useState} from "react";
import {useRouter} from "next/navigation";
import Link from "next/link";
import {createTravel, TravelCreateRequest} from "../../travel/services/travelService";

const categoryOptions = [
    {id: 1, name: "힐링"},
    {id: 2, name: "먹기행"},
    {id: 3, name: "자연 친화"},
    {id: 4, name: "엑티비티"}
];

const CreateTravelPage: React.FC = () => {
    const router = useRouter();
    const [formData, setFormData] = useState<TravelCreateRequest>({
        categoryId: 1,  // 기본값: 힐링 (ID: 1)
        city: "",
        places: "",
        travelPeriod: {
            startDate: "",
            endDate: ""
        },
        participants: 1,
        content: ""
    });
    const [error, setError] = useState<string>("");

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const {name, value} = e.target;
        if (name === "startDate" || name === "endDate") {
            setFormData({
                ...formData,
                travelPeriod: {...formData.travelPeriod, [name]: value}
            });
        } else if (name === "categoryId") {
            setFormData({...formData, categoryId: Number(value)});
        } else if (name === "participants") {
            setFormData({...formData, participants: Number(value)});
        } else {
            setFormData({...formData, [name]: value});
        }
    };

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        createTravel(formData)
            .then(() => {
                alert("여행 요청이 생성되었습니다.");
                router.push("/travels");
            })
            .catch(() => {
                setError("여행 요청 생성에 실패했습니다.");
            });
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <Link href="/travels" style={styles.backLink}>
                    ← 목록으로 돌아가기
                </Link>
                <h1 style={styles.title}>여행 요청 생성</h1>
                {error && <p style={styles.error}>{error}</p>}
                <form onSubmit={handleSubmit} style={styles.form}>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>여행 카테고리:</label>
                        <select
                            name="categoryId"
                            value={formData.categoryId}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        >
                            {categoryOptions.map((category) => (
                                <option key={category.id} value={category.id}>
                                    {category.name}
                                </option>
                            ))}
                        </select>
                    </div>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>도시:</label>
                        <input
                            type="text"
                            name="city"
                            value={formData.city}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>관광지 목록 (쉼표로 구분):</label>
                        <input
                            type="text"
                            name="places"
                            value={formData.places}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>여행 시작 날짜:</label>
                        <input
                            type="date"
                            name="startDate"
                            value={formData.travelPeriod.startDate}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>여행 종료 날짜:</label>
                        <input
                            type="date"
                            name="endDate"
                            value={formData.travelPeriod.endDate}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>참여 인원:</label>
                        <input
                            type="number"
                            name="participants"
                            value={formData.participants}
                            onChange={handleChange}
                            required
                            min={1}
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>상세 요청 내용:</label>
                        <textarea
                            name="content"
                            value={formData.content}
                            onChange={handleChange}
                            required
                            rows={5}
                            style={styles.textarea}
                        />
                    </div>
                    <button type="submit" style={styles.submitButton}>
                        생성
                    </button>
                </form>
            </div>
        </div>
    );
};

const styles: { [key: string]: React.CSSProperties } = {
    container: {
        backgroundColor: '#E0F7FA',
        minHeight: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        padding: '2rem'
    },
    card: {
        backgroundColor: '#fff',
        borderRadius: '8px',
        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
        padding: '2rem',
        width: '100%',
        maxWidth: '600px'
    },
    backLink: {
        display: 'block',
        marginBottom: '1rem',
        textDecoration: 'none',
        color: '#81D4FA',
        fontWeight: 'bold'
    },
    title: {
        textAlign: 'center',
        marginBottom: '1.5rem',
        color: '#333'
    },
    error: {
        color: 'red',
        marginBottom: '1rem',
        textAlign: 'center'
    },
    form: {
        display: 'flex',
        flexDirection: 'column'
    },
    formGroup: {
        display: 'flex',
        flexDirection: 'column',
        marginBottom: '1rem'
    },
    label: {
        marginBottom: '0.5rem',
        fontWeight: 'bold',
        color: '#555'
    },
    input: {
        padding: '0.75rem',
        borderRadius: '4px',
        border: '1px solid #ccc',
        fontSize: '1rem'
    },
    textarea: {
        padding: '0.75rem',
        borderRadius: '4px',
        border: '1px solid #ccc',
        fontSize: '1rem',
        resize: 'vertical'
    },
    submitButton: {
        backgroundColor: '#81D4FA',
        border: 'none',
        padding: '0.75rem',
        borderRadius: '4px',
        cursor: 'pointer',
        color: '#fff',
        fontWeight: 'bold',
        fontSize: '1rem',
        marginTop: '1rem'
    }
};

export default CreateTravelPage;
