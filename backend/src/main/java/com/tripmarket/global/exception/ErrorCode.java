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
	MEMBER_ACCESS_DENIED(FORBIDDEN, "현재 사용자는 매칭 요청을 당한 사용자가 아닙니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
