"use client";

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import axiosInstance from '../utils/axios';

interface ChatRoom {
  roomId: string;
  name: string;
  profileImage: string;
  lastMessage: string;
  lastMessageTime: string;
}

const ChatRoomListPage: React.FC = () => {
  const [chatRooms, setChatRooms] = useState<ChatRoom[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [leavingRoom, setLeavingRoom] = useState<string | null>(null);
  const router = useRouter();

  useEffect(() => {
    const fetchChatRooms = async () => {
      try {
        setLoading(true);
        const response = await axiosInstance.get('/chatting-room/my-list', {
          params: {
            page: 0,
            size: 10,
          },
        });
        if (response.data.content.length === 0) {
          setError('참여 중인 채팅방이 없습니다.');
        } else {
          setChatRooms(response.data.content);
        }
      } catch (err) {
        console.error('채팅방 목록 불러오기 실패:', err);
        setError('채팅방 목록을 불러오는 중 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchChatRooms();
  }, []);

  const handleChatRoomClick = (roomId: string) => {
    router.push(`/chat-room/${roomId}`);
  };

  const handleLeaveChatRoom = async (roomId: string) => {
    if (confirm("채팅방에서 나가시겠습니까?")) {
      try {
        setLeavingRoom(roomId);
        await axiosInstance.patch(`/chatting-room/${roomId}/leave`);
        setChatRooms(chatRooms.filter((room) => room.roomId !== roomId));
        alert("채팅방에서 나갔습니다.");
      } catch (err) {
        console.error('채팅방 나가기 실패:', err);
        alert('채팅방 나가기 중 오류가 발생했습니다.');
      } finally {
        setLeavingRoom(null);
      }
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-lg text-gray-600 animate-pulse">채팅방 목록을 불러오는 중...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-lg text-red-500">{error}</p>
      </div>
    );
  }

  return (
    <div className="p-6 min-h-screen bg-gray-50">
      <h2 className="text-2xl font-bold mb-8 text-[#1a237e]">채팅방 목록</h2>
      {chatRooms.length === 0 ? (
        <p className="text-gray-600 text-center">참여 중인 채팅방이 없습니다.</p>
      ) : (
        <ul className="space-y-6">
          {chatRooms.map((room) => (
            <li
              key={room.roomId}
              className="flex items-center p-5 border border-gray-300 rounded-lg shadow-sm cursor-pointer hover:bg-gray-100 transition relative"
            >
              <Image
                src={room.profileImage || '/default-profile.png'}
                alt={`${room.name}의 프로필 이미지`}
                width={60}
                height={60}
                className="rounded-full border border-gray-300 mr-4"
              />
              <div onClick={() => handleChatRoomClick(room.roomId)} className="flex-1">
                <div className="font-semibold text-lg text-[#1a237e]">{room.name}</div>
                <div className="text-gray-600 text-sm truncate w-64">
                  {room.lastMessage.length > 20
                    ? `${room.lastMessage.substring(0, 20)}...`
                    : room.lastMessage}
                </div>
                <div className="text-gray-500 text-xs">{room.lastMessageTime}</div>
              
              </div>
              <button
                onClick={(e) => {
                  e.stopPropagation(); // 채팅방 클릭 방지
                  handleLeaveChatRoom(room.roomId);
                }}
                className={`ml-4 px-4 py-2 text-white rounded-lg ${
                  leavingRoom === room.roomId ? 'bg-gray-500' : 'bg-red-500'
                } hover:bg-red-700 transition`}
                disabled={leavingRoom === room.roomId}
              >
                {leavingRoom === room.roomId ? '나가는 중...' : '나가기'}
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ChatRoomListPage;
