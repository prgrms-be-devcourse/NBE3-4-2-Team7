package com.tripmarket.global.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.tripmarket.domain.chatting.entity.Message;

public class DateUtil {

	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M월 d일");
	private static final DateTimeFormatter AM_PM_FORMATTER = DateTimeFormatter.ofPattern("a h:mm");

	public static String convertDateOrTime(LocalDateTime messageTime) {
		LocalDateTime now = LocalDateTime.now();
		LocalDate messageDate = messageTime.toLocalDate();
		LocalDate today = now.toLocalDate();
		LocalDate yesterday = today.minusDays(1);

		if (messageDate.equals(today)) {
			return messageTime.format(timeFormatter);
		} else if (messageDate.equals(yesterday)) {
			return "어제";
		} else {
			return messageTime.format(dateFormatter);
		}
	}

	public static String convertTime(LocalDateTime dateTime) {
		String formattedTime = dateTime.format(AM_PM_FORMATTER);
		return formattedTime.replace("AM", "오전").replace("PM", "오후");
	}

	public static LocalDateTime getTime(Message message) {
		return Instant.ofEpochMilli(message.getId().getTimestamp() * 1000L)
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
	}
}
