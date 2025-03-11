/** @type {import('next').NextConfig} */
const nextConfig = {
    images: {
        remotePatterns: [
            {
                protocol: 'https',
                hostname: 'res.cloudinary.com',
            },
        ],
    },
    async rewrites() {
        return [
            {
                source: '/api/:path*',
                destination: 'http://localhost:8080/:path*',
            },
            {
                source: '/oauth2/:path*',
                destination: 'http://localhost:8080/oauth2/:path*',
            },
            {
                source: '/auth/:path*',
                destination: 'http://localhost:8080/auth/:path*',
            },
            {
                source: '/members/:path*',
                destination: 'http://localhost:8080/members/:path*',
            },
            {
                source: '/api/members/:path*',
                destination: 'http://localhost:8080/members/:path*'
            },
            {
                source: '/api/images/:path*',
                destination: 'http://localhost:8080/images/:path*'
            }
        ];
    },
}

module.exports = nextConfig 