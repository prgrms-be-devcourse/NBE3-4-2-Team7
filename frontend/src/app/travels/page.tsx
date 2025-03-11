"use client";

import React, {useEffect, useState} from "react";
import {useRouter} from "next/navigation";
import {getTravels, TravelDto} from "../travel/services/travelService";
import {getGuides, GuideDto} from "../guide/services/guideService";
import {useAuth} from '../contexts/AuthContext';
import LoginModal from '../components/LoginModal';
import {IoCalendarClear, IoChevronBack, IoLocationSharp, IoPeople} from "react-icons/io5";
import Image from 'next/image';

const TravelListPage: React.FC = () => {
    const {user} = useAuth();
    const [travels, setTravels] = useState<TravelDto[]>([]);
    const [guides, setGuides] = useState<GuideDto[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>("");
    const router = useRouter();
    const [showLoginModal, setShowLoginModal] = useState(false);

    useEffect(() => {
        setLoading(true);
        Promise.all([
            getTravels().catch(() => ({data: {content: []}})),
            getGuides().catch(() => ({data: []}))
        ])
            .then(([travelResponse, guideResponse]) => {
                setTravels(travelResponse.data.content);
                setGuides(guideResponse.data);
                setError("");
            })
            .catch((error) => {
                console.error("데이터 로딩 에러:", error);
                setError("데이터를 불러오는 데 실패했습니다.");
            })
            .finally(() => setLoading(false));
    }, []);

    const handleTravelRequest = () => {
        if (!user) {
            setShowLoginModal(true);
            return;
        }
        router.push('/travels/create');
    };

    if (loading) return <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
            <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-500 mb-4"></div>
            <p className="text-lg text-gray-600">로딩 중...</p>
        </div>
    </div>;

    if (error) return <div className="min-h-screen flex items-center justify-center">
        <div className="text-center text-red-600">
            <p className="text-lg">{error}</p>
            <button
                onClick={() => window.location.reload()}
                className="mt-4 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
            >
                다시 시도
            </button>
        </div>
    </div>;

    return (
        <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white">
            {/* 배경 이미지 주석 처리 (코드는 유지)
            <div className="absolute inset-0">
                <Image
                    src="/images/travel-main.jpg"
                    alt="여행 배경"
                    fill
                    className="object-cover"
                    priority
                />
                <div className="absolute inset-0 bg-black/30"></div>
            </div>
            */}

            {/* 메인 콘텐츠 */}
            <div className="relative z-10 pt-16">
                <div className="max-w-7xl mx-auto px-4 py-12">
                    {/* 히어로 섹션 */}
                    <div className="bg-white/90 backdrop-blur-sm rounded-2xl shadow-lg p-8 mb-12">
                        <div className="text-center select-none">
                            <h1 className="text-4xl font-bold text-gray-900 mb-4">
                                나만의 특별한 여행을 시작하세요
                            </h1>
                            <p className="text-xl text-gray-700 mb-8">
                                전문 가이드와 함께하는 맞춤형 여행 서비스
                            </p>
                            <div className="flex justify-center">
                                <button
                                    onClick={handleTravelRequest}
                                    className="bg-blue-600 text-white px-8 py-4 rounded-lg text-lg font-semibold
                                             hover:bg-blue-700 transform hover:-translate-y-1 transition-all duration-200
                                             shadow-md hover:shadow-lg"
                                >
                                    여행 요청하기
                                </button>
                                <button
                                    onClick={() => router.push('/guide-columns')}
                                    className="bg-green-600 text-white px-8 py-4 rounded-lg text-lg font-semibold
                                             hover:bg-green-700 transform hover:-translate-y-1 transition-all duration-200
                                             shadow-md hover:shadow-lg ml-4"
                                >
                                    가이드 칼럼
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* 여행 요청 & 가이드 목록 */}
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                        {/* 여행 요청 목록 */}
                        <div className="space-y-6">
                            <div
                                className="flex justify-between items-center mb-6 bg-white/90 backdrop-blur-sm p-4 rounded-xl shadow-sm">
                                <h2 className="text-2xl font-bold text-gray-800 select-none">
                                    진행중인 여행 요청
                                </h2>
                                <button
                                    onClick={() => router.push('/travels/list')}
                                    className="text-blue-600 hover:text-blue-800 font-medium flex items-center"
                                >
                                    <span className="select-none">더보기</span>
                                    <IoChevronBack className="w-5 h-5 rotate-180 ml-1"/>
                                </button>
                            </div>

                            {/* 여행 요청 카드들 */}
                            <div className="space-y-4">
                                {travels.length === 0 ? (
                                    <div
                                        className="bg-white/90 backdrop-blur-sm rounded-xl p-6 text-center text-gray-500">
                                        현재 진행중인 여행 요청이 없습니다.
                                    </div>
                                ) : (
                                    travels.slice(0, 3).map((travel) => (
                                        <div
                                            key={travel.id}
                                            className="bg-white/90 backdrop-blur-sm rounded-xl shadow-sm hover:shadow-md 
                                                     transition-shadow p-6"
                                        >
                                            <div className="flex justify-between items-start mb-4">
                                                <h3 className="text-xl font-semibold text-gray-800 select-none">
                                                    {travel.city}
                                                </h3>
                                                <span className={`px-3 py-1 rounded-full text-sm font-medium select-none
                                                    ${travel.status === 'WAITING_FOR_MATCHING' ? 'bg-yellow-100 text-yellow-800' :
                                                    travel.status === 'IN_PROGRESS' ? 'bg-blue-100 text-blue-800' :
                                                        travel.status === 'COMPLETED' ? 'bg-gray-300 text-gray-800' :
                                                            'bg-green-100 text-green-800'}`}>
                                                    {travel.status === 'WAITING_FOR_MATCHING' ? '매칭 대기중' :
                                                        travel.status === 'IN_PROGRESS' ? '진행중' :
                                                            travel.status === 'COMPLETED' ? '여행 완료' :
                                                                '매칭 완료'}
                                                </span>
                                            </div>
                                            <div className="space-y-2 text-gray-600 select-none">
                                                <div className="flex items-center">
                                                    <IoLocationSharp className="w-5 h-5 mr-2"/>
                                                    <span>{travel.places}</span>
                                                </div>
                                                <div className="flex items-center">
                                                    <IoCalendarClear className="w-5 h-5 mr-2"/>
                                                    <span>{travel.startDate} - {travel.endDate}</span>
                                                </div>
                                                <div className="flex items-center">
                                                    <IoPeople className="w-5 h-5 mr-2"/>
                                                    <span>{travel.participants}명</span>
                                                </div>
                                            </div>
                                            <div className="mt-4 flex justify-end">
                                                <button
                                                    onClick={() => router.push(`/travels/${travel.id}`)}
                                                    className="text-blue-600 hover:text-blue-800 font-medium"
                                                >
                                                    자세히 보기
                                                </button>
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>

                        {/* 추천 가이드 목록 */}
                        <div className="space-y-6">
                            <div
                                className="flex justify-between items-center mb-6 bg-white/90 backdrop-blur-sm p-4 rounded-xl shadow-sm">
                                <h2 className="text-2xl font-bold text-gray-800 select-none">
                                    추천 가이드
                                </h2>
                                <button
                                    onClick={() => router.push('/guides')}
                                    className="text-blue-600 hover:text-blue-800 font-medium flex items-center"
                                >
                                    <span className="select-none">더보기</span>
                                    <IoChevronBack className="w-5 h-5 rotate-180 ml-1"/>
                                </button>
                            </div>

                            {/* 가이드 카드들 */}
                            <div className="space-y-4">
                                {guides.length === 0 ? (
                                    <div
                                        className="bg-white/90 backdrop-blur-sm rounded-xl p-6 text-center text-gray-500">
                                        현재 등록된 가이드가 없습니다.
                                    </div>
                                ) : (
                                    guides.slice(0, 3).map((guide) => (
                                        <div
                                            key={guide.id}
                                            className="bg-white/90 backdrop-blur-sm rounded-xl shadow-sm hover:shadow-md 
                                                     transition-shadow p-6 cursor-pointer"
                                            onClick={() => router.push(`/guides/${guide.id}`)}
                                        >
                                            <div className="flex items-center space-x-4">
                                                <div className="relative w-16 h-16">
                                                    <Image
                                                        src={guide.imageUrl || '/default-profile.png'}
                                                        alt={guide.name}
                                                        fill
                                                        className="rounded-full object-cover"
                                                    />
                                                </div>
                                                <div className="select-none">
                                                    <h3 className="text-xl font-semibold text-gray-800">
                                                        {guide.name}
                                                    </h3>
                                                    <p className="text-gray-600">
                                                        {guide.activityRegion}
                                                    </p>
                                                    <p className="text-sm text-gray-500">
                                                        경력 {guide.experienceYears}년
                                                    </p>
                                                </div>
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {showLoginModal && (
                <LoginModal onClose={() => setShowLoginModal(false)}/>
            )}
        </div>
    );
};

export default TravelListPage;
