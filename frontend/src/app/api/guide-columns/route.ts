import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function GET(request: Request) {
    try {
        const response = await fetch('http://localhost:8080/guide-columns', {
            headers: {
                'Content-Type': 'application/json',
                'Cookie': cookies()
                    .getAll()
                    .map((cookie: { name: string; value: string }) => `${cookie.name}=${cookie.value}`)
                    .join('; ')
            },
            cache: 'no-store'
        });

        if (!response.ok) {
            throw new Error('칼럼을 불러오는데 실패했습니다.');
        }

        const data = await response.json();
        return NextResponse.json(data);
    } catch (error) {
        console.error('Error in GET /api/guide-columns:', error);
        return NextResponse.json(
            { error: '칼럼을 불러오는데 실패했습니다.' },
            { status: 500 }
        );
    }
}

export async function POST(request: Request) {
    try {
        const formData = await request.formData();
        
        const response = await fetch('http://localhost:8080/guide-columns', {
            method: 'POST',
            headers: {
                Cookie: cookies().toString()
            },
            body: formData
        });

        if (!response.ok) {
            throw new Error('Failed to create column');
        }

        const data = await response.json();
        return NextResponse.json(data);
    } catch (error) {
        console.error('Error creating column:', error);
        return NextResponse.json(
            { error: '칼럼 작성에 실패했습니다.' },
            { status: 500 }
        );
    }
} 