package com.tripmarket.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M월 d일");

	public static String convertTime(LocalDateTime messageTime) {
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
}
