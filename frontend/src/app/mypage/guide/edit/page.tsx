"use client";

import React, {useEffect, useState} from 'react';
import { useRouter } from 'next/navigation';
import { IoChevronBack } from "react-icons/io5";
import axios from "axios";
import {getGuideDetailByUser} from "@/app/guides/services/guideService";
import {convertFromGuideDto, convertToGuideDto} from "@/app/utils/converters";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

const GuideEditPage = () => {
    const router = useRouter();
    const [guideData, setGuideData] = useState({});

    useEffect(() => {
        const fetchGuideDetail = async () => {
            try {
                const guideDetail = await getGuideDetailByUser();
                setGuideData(convertFromGuideDto(guideDetail.data));
            } catch (error) {
                console.error('가이드 상세 정보 조회 실패:', error);
            }
        };

        fetchGuideDetail();
    }, []);

    const updateGuideProfile = async () => {
        try {
            await axios.patch(`${API_BASE_URL}/guides`, convertToGuideDto(guideData));
            alert("수정되었습니다.");
            // 업데이트 후 마이페이지로 이동
            router.push('/mypage');
        } catch (error) {
            console.error("가이드 정보 수정 실패", error);
        }
    };
    
    const handleChange = (e) => {
        const { name, value } = e.target;
        setGuideData({
            ...guideData,
            [name]: value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        router.push('/mypage');
    };

    return (
        <div className="min-h-screen bg-gray-50 pt-24 pb-8">
            <div className="max-w-2xl mx-auto px-4">
                <button
                    onClick={() => router.back()}
                    className="flex items-center text-gray-600 hover:text-blue-600 mb-8 group 
                             transition-all duration-300 ease-in-out px-4 py-2 rounded-lg
                             hover:bg-blue-50 border border-transparent hover:border-blue-100"
                >
                    <IoChevronBack className="w-6 h-6 transform group-hover:-translate-x-1 
                                           transition-transform duration-300 ease-in-out" />
                    <span className="ml-2 text-lg font-medium">이전 페이지로</span>
                </button>

                <div className="bg-white rounded-lg shadow-md p-8">
                    <h2 className="text-2xl font-bold text-gray-800 mb-6">가이드 프로필 수정</h2>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="text-sm font-semibold text-gray-600">이름</label>
                            <input
                                type="text"
                                name="name"
                                value={guideData.name}
                                onChange={handleChange}
                                className="w-full mt-1 p-2 border rounded-lg"
                            />
                        </div>
                        <div>
                            <label className="text-sm font-semibold text-gray-600">활동 지역</label>
                            <input
                                type="text"
                                name="activityRegion"
                                value={guideData.activityRegion}
                                onChange={handleChange}
                                className="w-full mt-1 p-2 border rounded-lg"
                            />
                        </div>
                        <div>
                            <label className="text-sm font-semibold text-gray-600">사용 가능 언어</label>
                            <input
                                type="text"
                                name="languages"
                                value={guideData.languages}
                                onChange={handleChange}
                                className="w-full mt-1 p-2 border rounded-lg"
                            />
                        </div>
                        <div>
                            <label className="text-sm font-semibold text-gray-600">경력 (년)</label>
                            <input
                                type="number"
                                name="experienceYears"
                                value={guideData.experienceYears}
                                onChange={handleChange}
                                className="w-full mt-1 p-2 border rounded-lg"
                            />
                        </div>
                        <div>
                            <label className="text-sm font-semibold text-gray-600">소개</label>
                            <textarea
                                name="introduction"
                                value={guideData.introduction}
                                onChange={handleChange}
                                className="w-full mt-1 p-2 border rounded-lg"
                            ></textarea>
                        </div>
                        <div className="flex justify-end">
                            <button
                                onClick={updateGuideProfile}
                                type="submit"
                                className="px-4 py-2 bg-blue-600 text-white rounded-lg
                                         hover:bg-blue-700 transition-colors duration-200"
                            >
                                저장
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default GuideEditPage;
