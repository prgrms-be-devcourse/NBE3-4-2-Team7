import type { Metadata } from "next";
import { Playfair_Display, Noto_Sans_KR } from "next/font/google";
import "./globals.css";
import { AuthProvider } from './contexts/AuthContext';
import HeaderWrapper from './components/HeaderWrapper';

// 로고용 폰트
const playfair = Playfair_Display({ subsets: ['latin'] });

// 기본 폰트
const notoSansKr = Noto_Sans_KR({
  subsets: ['latin'],
  weight: ['400', '500', '700'],  // regular, medium, bold
});

export const metadata: Metadata = {
  title: "Trip Market",
  description: "내 여행의 완성, Trip Market",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko">
      <body className={notoSansKr.className}>
        <AuthProvider>
          <HeaderWrapper />
          <main>{children}</main>
        </AuthProvider>
      </body>
    </html>
  );
}
