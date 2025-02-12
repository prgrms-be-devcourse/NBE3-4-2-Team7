"use client";

import React, { useState, useEffect, useRef } from "react";
import { useParams } from "next/navigation";
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import axiosInstance from "../../utils/axios";

interface ChatMessage {
  id: string;
  senderName: string;
  sender: string; 
  senderEmail: string;
  senderProfile: string;
  content: string;
  time: string;
  readStatus: boolean;
}

const ChatRoomPage: React.FC = () => {
  const { roomId: roomIdParam } = useParams();
  const roomId = Array.isArray(roomIdParam) ? roomIdParam[0] : roomIdParam;

  const [currentUserEmail, setCurrentUserEmail] = useState<string | null>(null);
  const [currentUserName, setCurrentUserName] = useState<string>("");
  const [receiverEmail, setReceiverEmail] = useState<string | null>(null);
  const [receiverName, setReceiverName] = useState<string>("");
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [newMessage, setNewMessage] = useState("");
  const messageEndRef = useRef<HTMLDivElement>(null);
  const scrollRef = useRef<HTMLDivElement>(null);
  const stompClientRef = useRef<any>(null);

  /** ✅ 사용자 정보 가져오기 */
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const accessToken = document.cookie
          .split("; ")
          .find((cookie) => cookie.startsWith("accessToken="))
          ?.split("=")[1];

        if (!accessToken) {
          console.error("Access token이 없습니다.");
          return;
        }

        const response = await axiosInstance.get("/members/me", {
          headers: { Authorization: `Bearer ${accessToken}` },
        });

        setCurrentUserEmail(response.data.email);
        setCurrentUserName(response.data.name);

        const receiverResponse = await axiosInstance.get(`/chatting-room/receiver`, {
          params: { roomId, currentUserEmail: response.data.email },
        });

        setReceiverEmail(receiverResponse.data.receiver);
        setReceiverName(receiverResponse.data.receiverName || "알 수 없음");
      } catch (error) {
        console.error("❌ 사용자 정보를 가져오는 데 실패했습니다:", error);
      }
    };

    fetchUserData();
  }, [roomId]);

  /** ✅ currentUserEmail 설정 후 WebSocket 연결 */
  useEffect(() => {
    if (currentUserEmail) {
      connectWebSocket();
    }
  }, [currentUserEmail, roomId]);

  /** ✅ WebSocket 연결 */
  const connectWebSocket = () => {
    if (stompClientRef.current) return;
  
    const accessToken = document.cookie
      .split("; ")
      .find((cookie) => cookie.startsWith("accessToken="))
      ?.split("=")[1];
  
    if (!accessToken) {
      console.error("Access token이 없습니다.");
      return;
    }
  
    const socket = new SockJS("http://localhost:8080/chat");
    const stompClient = Stomp.over(socket);
    stompClientRef.current = stompClient;
  
    stompClient.connect({ Authorization: `Bearer ${accessToken.trim()}`, roomId }, () => {
      console.log("🔗 WebSocket 연결 성공");
  
      // ✅ 메시지 수신 구독 (채팅 메시지 받기)
      stompClient.subscribe(`/topic/chat.room.${roomId}`, (message) => {
        const newMessage: ChatMessage = JSON.parse(message.body);
        console.log("📩 실시간 메시지 도착:", newMessage);
        
        setMessages((prevMessages) => {
          const isCurrentUserSender =
            newMessage.senderEmail?.trim().toLowerCase() === currentUserEmail?.trim().toLowerCase();
      
          return [
            ...prevMessages,
            {
              id: newMessage.id,
              sender: newMessage.senderEmail,
              senderName: isCurrentUserSender ? currentUserName : receiverName,
              senderEmail: newMessage.senderEmail,
              senderProfile: newMessage.senderProfile || "/default-profile.png",
              content: newMessage.content,
              time: newMessage.time,
              readStatus: newMessage.readStatus,
            },
          ];
        });
  
        scrollToBottom();
      });
  
      // ✅ 읽음 처리 메시지 수신 (서버가 보낸 읽음 상태 업데이트)
      stompClient.subscribe(`/topic/chat.read.${roomId}`, (message) => {
        const readMessage = JSON.parse(message.body);
        console.log("📩 읽음 처리 메시지 도착:", readMessage);
  
        // ✅ UI에서 읽음 처리 (readStatus 업데이트)
        setMessages((prevMessages) =>
          prevMessages.map((msg) =>
            msg.senderEmail !== readMessage.receiver ? { ...msg, readStatus: true } : msg
          )
        );
      });
  
    });
  };
  
  /** ✅ 기존 메시지 불러오기 */
  useEffect(() => {
    setMessages([]);
    fetchAllMessages();
  }, [roomId]);

  const fetchAllMessages = async () => {
    try {
      console.log("📢 전체 메시지 가져오기 시작");
      const response = await axiosInstance.get(`/chatting-room/${roomId}/messages`);
      const fetchedMessages: ChatMessage[] = response.data;

      console.log("✅ 모든 메시지:", fetchedMessages);

      setMessages(
        fetchedMessages.map((msg) => ({
          id: msg.id,
          sender: msg.sender,
          senderName: msg.senderName,
          senderEmail: msg.senderEmail,
          senderProfile: msg.senderProfile || "/default-profile.png",
          content: msg.content,
          time: msg.time,
          readStatus: msg.readStatus,
        }))
      );
    } catch (error) {
      console.error("❌ 전체 메시지 로드 실패:", error);
    }
  };

  /** ✅ 메시지를 보낸 후 input 초기화 */
  const sendMessage = () => {
    if (!stompClientRef.current || !stompClientRef.current.connected) return;

    const messageDto = {
      roomId,
      sender: currentUserEmail,
      receiver: receiverEmail,
      content: newMessage,
    };

    stompClientRef.current.send(`/pub/chat.message.${roomId}`, {}, JSON.stringify(messageDto));
    setNewMessage("");
  };

  /** ✅ 스크롤 아래로 이동 */
  const scrollToBottom = () => {
    setTimeout(() => {
      messageEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, 0);
  };

  /** ✅ UI 렌더링 */
  return (
    <div className="flex flex-col h-screen">
      <div className="bg-blue-600 text-white text-center py-4 font-bold text-xl">
        {roomId ? `채팅방 ${roomId}` : "유효한 roomId가 필요합니다."}
      </div>

      <div ref={scrollRef} className="flex-1 overflow-y-auto p-4 space-y-2 bg-gray-100">
        {messages.map((message, index) => {
          const isCurrentUserSender = 
          message.senderEmail?.trim().toLowerCase() === currentUserEmail?.trim().toLowerCase();
      

          return (
            <div key={index} className={`flex items-start ${isCurrentUserSender ? "justify-end" : "justify-start"} mb-2`}>
  {!isCurrentUserSender && (
    <img
      src={message.senderProfile || "/default-profile.png"}
      alt={message.senderName}
      className="w-10 h-10 rounded-full mr-3"
    />
  )}

  <div className={`p-3 rounded-lg ${isCurrentUserSender ? "bg-blue-500 text-white" : "bg-gray-300 text-black"} max-w-[75%] break-words`}>
    {!isCurrentUserSender && <div className="text-sm font-bold mb-1">{message.senderName}</div>}

    <div>{message.content}</div>

    <div className="text-xs text-gray-500 mt-1 flex justify-between">
      <span>{message.time}</span>
      {!message.readStatus && <span className="text-red-500 ml-2">1</span>}
    </div>
  </div>
</div>

          );
        })}
        <div ref={messageEndRef} />
      </div>

      <div className="p-4 bg-white border-t flex items-center">
        <input type="text" className="flex-1 border rounded-lg px-3 py-2" value={newMessage} onChange={(e) => setNewMessage(e.target.value)} placeholder="메시지를 입력하세요..." />
        <button onClick={sendMessage} className="ml-3 bg-blue-500 text-white px-4 py-2 rounded-lg">
          전송
        </button>
      </div>
    </div>
  );
};

export default ChatRoomPage;
