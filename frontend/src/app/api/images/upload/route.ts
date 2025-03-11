import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function POST(request: Request) {
    try {
        const formData = await request.formData();
        const image = formData.get('image');
        
        // 이미지만 전송하는 백엔드 엔드포인트로 요청
        const response = await fetch('http://localhost:8080/images/upload', {
            method: 'POST',
            headers: {
                Cookie: cookies().toString()
            },
            body: formData
        });

        if (!response.ok) {
            throw new Error('Failed to upload image');
        }

        const data = await response.json();
        return NextResponse.json({ url: data.url });
    } catch (error) {
        console.error('Error uploading image:', error);
        return NextResponse.json(
            { error: '이미지 업로드에 실패했습니다.' },
            { status: 500 }
        );
    }
}
