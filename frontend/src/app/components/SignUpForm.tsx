"use client";

import React, { useState } from 'react';
import { authService } from '../auth/services/authService';
import { useForm } from 'react-hook-form';

interface SignUpFormData {
    email: string;
    password: string;
    name: string;
}

interface SignUpFormProps {
    onClose: () => void;
    onSignUpSuccess: () => void;
}

const SignUpForm: React.FC<SignUpFormProps> = ({ onClose, onSignUpSuccess }) => {
    const { register, handleSubmit, formState: { errors }, setError: setFormError } = useForm<SignUpFormData>();
    const [serverError, setServerError] = useState('');

    const onSubmit = async (data: SignUpFormData) => {
        try {
            await authService.signup(data);
            onSignUpSuccess();
        } catch (error: any) {
            // CustomException의 errorMessage를 표시
            setServerError(error.response?.data?.errorMessage || '회원가입에 실패했습니다.');
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6 text-center">회원가입</h2>
            <form onSubmit={handleSubmit(onSubmit)}>
                <div className="mb-4">
                    <label className="block text-gray-700 mb-2">이메일</label>
                    <input
                        type="email"
                        {...register('email', {
                            required: '이메일은 필수입니다.',
                            pattern: {
                                value: /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/,
                                message: '이메일은 영문자와 숫자로 작성해야 합니다.'
                            }
                        })}
                        className="w-full p-2 border rounded text-gray-900"
                    />
                    {errors.email && <span className="text-red-500">{errors.email.message}</span>}
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 mb-2">이름</label>
                    <input
                        type="text"
                        {...register('name', {
                            required: '이름은 필수입니다.',
                            pattern: {
                                value: /^[가-힣a-zA-Z]{2,}$/,
                                message: '이름은 한글 또는 영어만 가능하며, 최소 2자 이상이어야 합니다.'
                            }
                        })}
                        className="w-full p-2 border rounded text-gray-900"
                    />
                    {errors.name && <span className="text-red-500">{errors.name.message}</span>}
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 mb-2">비밀번호</label>
                    <input
                        type="password"
                        {...register('password', {
                            required: '비밀번호는 필수입니다.',
                            pattern: {
                                value: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
                                message: '비밀번호는 8자 이상이며, 영문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다.'
                            }
                        })}
                        className="w-full p-2 border rounded text-gray-900"
                    />
                    {errors.password && <span className="text-red-500">{errors.password.message}</span>}
                </div>
                <div className="mb-6">
                    <label className="block text-gray-700 mb-2">비밀번호 확인</label>
                    <input
                        type="password"
                        className="w-full p-2 border rounded text-gray-900"
                        required
                    />
                </div>
                {serverError && <p className="text-red-500 mb-4">{serverError}</p>}
                <button
                    type="submit"
                    className="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600"
                >
                    가입하기
                </button>
                <button
                    type="button"
                    onClick={onClose}
                    className="mt-4 w-full border border-gray-300 p-2 rounded hover:bg-gray-100 text-gray-900"
                >
                    돌아가기
                </button>
            </form>
        </div>
    );
};

export default SignUpForm;