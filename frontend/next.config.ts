/** @type {import('next').NextConfig} */
const nextConfig = {
    images: {
        domains: [
            'k.kakaocdn.net',  // 카카오 프로필 이미지 도메인
            'img1.kakaocdn.net', // 추가 카카오 이미지 도메인
            'lh3.googleusercontent.com',  // 구글 프로필 이미지 도메인
            'avatars.githubusercontent.com',
            'i.imgur.com' // imgur 이미지 도메인
        ],
    },
}

module.exports = nextConfig