package com.tripmarket.domain.match.util;

import com.tripmarket.domain.match.enums.MatchRequestStatus;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.enums.TravelStatus;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

public class MatchRequestStatusUpdater {

	public static void updateStatus(MatchRequestStatus currentStatus, MatchRequestStatus newStatus, Travel travel) {
		if (currentStatus == MatchRequestStatus.ACCEPTED || currentStatus == MatchRequestStatus.REJECTED) {
			throw new CustomException(ErrorCode.REQUEST_ALREADY_PROCESSED);
		}

		switch (newStatus) {
			case ACCEPTED -> travel.updateTravelStatus(TravelStatus.MATCHED);
			case REJECTED -> travel.updateTravelStatus(TravelStatus.WAITING_FOR_MATCHING);
			case PENDING -> throw new CustomException(ErrorCode.INVALID_REQUEST_STATUS);
		}
	}
}
