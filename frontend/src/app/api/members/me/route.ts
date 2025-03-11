import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function GET() {
    try {
        const response = await fetch('http://localhost:8080/members/me', {
            headers: {
                Cookie: cookies().toString()
            }
        });

        if (!response.ok) {
            throw new Error('Failed to fetch user data');
        }

        const data = await response.json();
        return NextResponse.json(data);
    } catch (error) {
        console.error('Error fetching user:', error);
        return NextResponse.json(null, { status: 401 });
    }
}
