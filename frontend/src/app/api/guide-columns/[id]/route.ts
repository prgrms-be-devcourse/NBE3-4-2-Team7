import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function GET(
    request: Request,
    { params }: { params: { id: string } }
) {
    try {
        const response = await fetch(`http://localhost:8080/guide-columns/${params.id}`, {
            headers: {
                'Content-Type': 'application/json',
                'Cookie': cookies()
                    .getAll()
                    .map(cookie => `${cookie.name}=${cookie.value}`)
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
        console.error('Error fetching column:', error);
        return NextResponse.json(
            { error: '칼럼을 불러오는데 실패했습니다.' },
            { status: 500 }
        );
    }
}

export async function PUT(
    request: Request,
    { params }: { params: { id: string } }
) {
    try {
        const formData = await request.formData();
        
        const response = await fetch(`http://localhost:8080/guide-columns/${params.id}`, {
            method: 'PUT',
            headers: {
                Cookie: cookies().toString()
            },
            body: formData
        });

        if (!response.ok) {
            throw new Error('Failed to update column');
        }

        const data = await response.json();
        return NextResponse.json(data);
    } catch (error) {
        console.error('Error updating column:', error);
        return NextResponse.json(
            { error: '칼럼 수정에 실패했습니다.' },
            { status: 500 }
        );
    }
}

export async function DELETE(
    request: Request,
    { params }: { params: { id: string } }
) {
    try {
        const response = await fetch(`http://localhost:8080/guide-columns/${params.id}`, {
            method: 'DELETE',
            headers: {
                Cookie: cookies().toString()
            }
        });

        if (!response.ok) {
            throw new Error('Failed to delete column');
        }

        return new Response(null, { status: 204 });
    } catch (error) {
        console.error('Error deleting column:', error);
        return NextResponse.json(
            { error: '칼럼 삭제에 실패했습니다.' },
            { status: 500 }
        );
    }
}
