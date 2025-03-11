"use client";

import { useEditor, EditorContent } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import Image from '@tiptap/extension-image';
import { useEffect } from 'react';

interface RichTextEditorProps {
    content: string;
    onChange: (content: string) => void;
    onImageUpload: (file: File) => Promise<string>;
}

const RichTextEditor = ({ content, onChange, onImageUpload }: RichTextEditorProps) => {
    const editor = useEditor({
        extensions: [
            StarterKit,
            Image
        ],
        content: content,
        onUpdate: ({ editor }) => {
            onChange(editor.getHTML());
        }
    }, []);

    useEffect(() => {
        if (editor && content) {
            if (editor.getHTML() !== content) {
                editor.commands.setContent(content);
            }
        }
    }, [content, editor]);

    const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file && editor) {
            try {
                const imageUrl = await onImageUpload(file);
                // 현재 커서 위치에 이미지 삽입
                editor
                    .chain()
                    .focus()
                    .insertContent({
                        type: 'image',
                        attrs: { src: imageUrl }
                    })
                    .run();
            } catch (error) {
                console.error('이미지 업로드 실패:', error);
                alert('이미지 업로드에 실패했습니다.');
            }
        }
    };

    if (!editor) {
        return null;
    }

    return (
        <div className="border rounded-lg overflow-hidden">
            <div className="bg-gray-50 p-2 border-b flex gap-2">
                <input
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    style={{ display: 'none' }}
                    id="image-upload"
                />
                <button
                    type="button"
                    onClick={() => document.getElementById('image-upload')?.click()}
                    className="px-3 py-1 bg-white border rounded hover:bg-gray-50"
                >
                    이미지 추가
                </button>
            </div>
            <EditorContent 
                editor={editor} 
                className="p-4 min-h-[200px] text-gray-900 prose prose-lg"
            />
        </div>
    );
};

export default RichTextEditor;
