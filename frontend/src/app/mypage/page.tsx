"use client";

import React, { useState } from 'react';
import Image from 'next/image';
import { useAuth } from '../contexts/AuthContext';
import { useRouter } from 'next/navigation';
import { IoChevronBack, IoChevronDown, IoChevronUp } from "react-icons/io5";

const MyPage = () => {
    const { user } = useAuth();
    const router = useRouter();
    const [isGuideProfileOpen, setIsGuideProfileOpen] = useState(false);

    // 임시 더미 데이터
    const dummyTravels = [
        {
            id: 1,
            title: "서울 여행",
            status: "WAITING_FOR_MATCHING"
        },
        {
            id: 2,
            title: "부산 여행",
            status: "IN_PROGRESS"
        },
        {
            id: 3,
            title: "제주도 여행",
            status: "MATCHED"
        }
    ];

    // 임시 가이드 데이터
    const guideData = {
        name: "홍길동",
        activityRegion: "서울, 부산",
        introduction: "안녕하세요. 한국의 아름다움을 전달하는 가이드입니다.",
        languages: "한국어, 영어, 일본어",
        experienceYears: 5
    };

    const getStatusText = (status: string) => {
        switch (status) {
            case 'WAITING_FOR_MATCHING':
                return '매칭 대기중';
            case 'IN_PROGRESS':
                return '진행중';
            case 'MATCHED':
                return '매칭 완료';
            default:
                return '알 수 없음';
        }
    };

    const getStatusColor = (status: string) => {
        switch (status) {
            case 'WAITING_FOR_MATCHING':
                return 'text-yellow-500';
            case 'IN_PROGRESS':
                return 'text-blue-500';
            case 'MATCHED':
                return 'text-green-500';
            default:
                return 'text-gray-500';
        }
    };

    const handleEdit = (id: number) => {
        // 수정 로직 구현
        console.log('Edit travel:', id);
    };

    const handleDelete = (id: number) => {
        // 삭제 로직 구현
        console.log('Delete travel:', id);
    };

    return (
        <div className="min-h-screen bg-gray-50 pt-24 pb-8">
            <div className="max-w-4xl mx-auto px-4">
                {/* 뒤로가기 버튼 */}
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

                {/* 프로필 섹션 */}
                <div className="bg-white rounded-lg shadow-md p-8 mb-6">
                    <div className="flex items-center space-x-8">
                        <div className="relative w-32 h-32">
                            <Image
                                src={user?.imageUrl || '/default-profile.png'}
                                alt="프로필 이미지"
                                fill
                                className="rounded-full object-cover pointer-events-none"
                            />
                        </div>
                        <div className="pointer-events-none">
                            <h1 className="text-3xl font-bold text-gray-800 mb-2">
                                {user?.name || '사용자'}
                            </h1>
                            <p className="text-lg text-gray-600">
                                {user?.email || 'email@example.com'}
                            </p>
                        </div>
                    </div>
                </div>

                {/* 가이드 프로필 섹션 */}
                <div className="bg-white rounded-lg shadow-md p-6 mb-6">
                    <button 
                        onClick={() => setIsGuideProfileOpen(!isGuideProfileOpen)}
                        className="w-full flex items-center justify-between text-left"
                    >
                        <h2 className="text-2xl font-bold text-gray-800">가이드 프로필</h2>
                        {isGuideProfileOpen ? 
                            <IoChevronUp className="w-6 h-6 text-gray-600" /> : 
                            <IoChevronDown className="w-6 h-6 text-gray-600" />
                        }
                    </button>
                    
                    {isGuideProfileOpen && (
                        <div className="mt-6 space-y-4 animate-fade-in">
                            <div className="grid grid-cols-2 gap-6">
                                <div>
                                    <h3 className="text-sm font-semibold text-gray-500 mb-1">활동 지역</h3>
                                    <p className="text-gray-800">{guideData.activityRegion}</p>
                                </div>
                                <div>
                                    <h3 className="text-sm font-semibold text-gray-500 mb-1">사용 가능 언어</h3>
                                    <p className="text-gray-800">{guideData.languages}</p>
                                </div>
                                <div>
                                    <h3 className="text-sm font-semibold text-gray-500 mb-1">경력</h3>
                                    <p className="text-gray-800">{guideData.experienceYears}년</p>
                                </div>
                            </div>
                            <div>
                                <h3 className="text-sm font-semibold text-gray-500 mb-1">소개</h3>
                                <p className="text-gray-800">{guideData.introduction}</p>
                            </div>
                            
                            {/* 프로필 수정 버튼 추가 */}
                            <div className="flex justify-end mt-4">
                                <button
                                    onClick={() => router.push('/mypage/guide/${user.id}')}
                                    className="px-4 py-2 bg-blue-600 text-white rounded-lg
                                             hover:bg-blue-700 transition-colors duration-200
                                             flex items-center space-x-2 text-sm font-medium"
                                >
                                    <span>프로필 수정</span>
                                </button>
                            </div>
                        </div>
                    )}
                </div>

                {/* 여행 매칭 목록 섹션 */}
                <div className="bg-white rounded-lg shadow-md p-8">
                    <h2 className="text-2xl font-bold text-gray-800 mb-6">
                        나의 여행 매칭 목록
                    </h2>
                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead>
                                <tr className="border-b-2 border-gray-200">
                                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">No.</th>
                                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">요청 명</th>
                                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">요청상태</th>
                                    <th className="px-6 py-3 text-center text-sm font-semibold text-gray-600">관리</th>
                                </tr>
                            </thead>
                            <tbody>
                                {dummyTravels.map((travel, index) => (
                                    <tr key={travel.id} className="border-b border-gray-200 hover:bg-gray-50">
                                        <td className="px-6 py-4 text-sm text-gray-500">{index + 1}</td>
                                        <td className="px-6 py-4 text-sm text-gray-800">{travel.title}</td>
                                        <td className={`px-6 py-4 text-sm font-medium ${getStatusColor(travel.status)}`}>
                                            {getStatusText(travel.status)}
                                        </td>
                                        <td className="px-6 py-4 text-sm text-center">
                                            <div className="flex justify-center gap-2">
                                                <button 
                                                    className="px-3 py-1.5 bg-blue-50 text-blue-600 rounded-md
                                                             hover:bg-blue-100 transition-colors duration-200
                                                             flex items-center gap-1 text-sm font-medium"
                                                    onClick={() => handleEdit(travel.id)}
                                                >
                                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                                    </svg>
                                                    수정
                                                </button>
                                                <button 
                                                    className="px-3 py-1.5 bg-red-50 text-red-600 rounded-md
                                                             hover:bg-red-100 transition-colors duration-200
                                                             flex items-center gap-1 text-sm font-medium"
                                                    onClick={() => handleDelete(travel.id)}
                                                >
                                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                    </svg>
                                                    삭제
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MyPage;
