package com.tripmarket.global.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	//Member
	MEMBER_NOT_FOUND(NOT_FOUND, "사용자가 존재하지 않습니다"),

	//Category
	CATEGORY_NOT_FOUND(NOT_FOUND, "카테고리가 존재하지 않습니다."),

	//Travel
	TRAVEL_NOT_FOUND(NOT_FOUND, "여행 글이 존재하지 않습니다."),
	TRAVEL_ACCESS_DENIED(FORBIDDEN, "현재 사용자가 본인이 작성한 여행 글이 아닙니다."),
	TRAVEL_ALREADY_MATCHED(BAD_REQUEST, "이미 매칭된 여행 글입니다."),
	TRAVEL_ALREADY_IN_PROGRESS(BAD_REQUEST, "이미 매칭 진행 중인 여행 요청 글입니다."),

	//Guide
	GUIDE_NOT_FOUND(NOT_FOUND, "가이드가 존재하지 않습니다."),
	SELF_REQUEST_NOT_ALLOWED(FORBIDDEN, "본인의 가이드 프로필에는 매칭 요청할 수 없습니다."),
	GUIDE_PROFILE_NOT_FOUND(NOT_FOUND, "해당 사용자는 가이더로 등록되지 않았습니다."),
	ALREADY_HAS_GUIDE_PROFILE(BAD_REQUEST, "이미 가이더로 등록한 사용자입니다."),

	//GuideRequest
	DUPLICATE_REQUEST(BAD_REQUEST, "해당 사용자는 이미 해당 가이더에게 매칭 요청을 보냈습니다.중복해서 요청할 수 없습니다."),
	GUIDE_REQUEST_NOT_FOUND(NOT_FOUND, "가이드 요청 내역이 존재하지 않습니다."),
	REQUEST_ALREADY_PROCESSED(BAD_REQUEST, "이미 처리된 가이드 요청입니다."),
	GUIDE_ACCESS_DENIED(FORBIDDEN, "현재 가이더가 매칭 요청을 당한 가이더가 아닙니다."),
	INVALID_REQUEST_STATUS(BAD_REQUEST, "PENDING 상태로 요청을 보낼 수 없습니다."),

	//TravelOffer
	SELF_RESPONSE_NOT_ALLOWED(BAD_REQUEST, "본인이 작성한 여행 요청 글에는 매칭 요청할 수 없습니다."),
	DUPLICATE_TRAVEL_OFFER(BAD_REQUEST, "해당 가이더는 해당 사용자에게 이미 요청을 보냈습니다. 중복해서 요청할 수 없습니다"),
	TRAVEL_OFFER_NOT_FOUND(NOT_FOUND, "여행 제안 내역이 존재하지 않습니다."),
	MEMBER_ACCESS_DENIED(FORBIDDEN, "현재 사용자는 매칭 요청을 당한 사용자가 아닙니다."),

	//REVIEW
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다."),
	REVIEW_ACCESS_DENIED(HttpStatus.FORBIDDEN, "리뷰 작성자만 수정 또는 삭제할 수 있습니다."),
	REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "해당 여행에 이미 리뷰를 작성했습니다."),
	INVALID_REVIEW_SCORE(HttpStatus.BAD_REQUEST, "리뷰 점수는 1.0에서 5.0 사이여야 합니다."),
	REVIEW_DELETION_FORBIDDEN(HttpStatus.FORBIDDEN, "리뷰를 삭제할 권한이 없습니다."),
	REVIEW_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "리뷰를 수정할 권한이 없습니다."),
	REVIEW_CREATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "여행이 완료되지 않아 리뷰를 작성할 수 없습니다."),
	REVIEW_DELETE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "리뷰 삭제 권한이 없습니다."),
	REVIEW_UPDATE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "리뷰 수정 권한이 없습니다."),
	REVIEW_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 리뷰 요청입니다."),
	REVIEW_ALREADY_DELETED(HttpStatus.GONE, "삭제된 리뷰입니다."),
	REVIEW_GUIDE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 가이드에 대한 리뷰가 존재하지 않습니다."),
	REVIEW_TRAVEL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 여행에 대한 리뷰가 존재하지 않습니다."),
	REVIEW_STATS_NOT_FOUND(NOT_FOUND, "리뷰를 할 수 있는 상태가 아닙니다."),

	//Chatting
	DUPLICATE_CHAT_ROOM(CONFLICT, "이미 채팅방이 존재합니다."),
	FAIL_MESSAGE_SEND(SERVICE_UNAVAILABLE, "메시지 전송에 실패했습니다."),
	NOT_FOUND_CHAT_ROOM(NOT_FOUND, "채팅방을 찾을 수 없습니다."),
	STOMP_INVALID_HEADER(BAD_REQUEST, "Stomp 헤더값이 누락되었습니다."),
	STOMP_DISCONNECT_ERROR(INTERNAL_SERVER_ERROR, "Stomp DISCONNECT가 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
