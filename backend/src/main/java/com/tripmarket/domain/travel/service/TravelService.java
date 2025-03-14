package com.tripmarket.domain.travel.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.service.MemberService;
import com.tripmarket.domain.travel.dto.TravelDto;
import com.tripmarket.domain.travel.dto.request.TravelCreateRequest;
import com.tripmarket.domain.travel.dto.request.TravelUpdateRequest;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.entity.TravelCategory;
import com.tripmarket.domain.travel.enums.TravelStatus;
import com.tripmarket.domain.travel.repository.TravelRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelService {

	private final MemberService memberService;
	private final TravelCategoryService travelCategoryService;
	private final TravelRepository travelRepository;

	@Transactional
	public TravelDto createTravel(String email, TravelCreateRequest requestDto) {
		Member member = memberService.getMemberByEmail(email);
		TravelCategory category = travelCategoryService.getTravelCategory(requestDto.categoryId());
		Travel travel = requestDto.toEntity(member, category);
		travelRepository.save(travel);
		return TravelDto.of(travel);
	}

	@Transactional
	public TravelDto updateTravel(Long travelId, String email, TravelUpdateRequest requestDto) {
		Member member = memberService.getMemberByEmail(email);
		Travel travel = getTravel(travelId);
		validateOwnership(member, travel);
		validateMatchStatus(travel);
		TravelCategory category = travelCategoryService.getTravelCategory(requestDto.categoryId());

		Travel updateTravel = requestDto.toEntity(travel, category);
		travel.updateTravel(updateTravel, category);
		return TravelDto.of(travel);
	}

	@Transactional(readOnly = true)
	public Page<TravelDto> getTravels(Long categoryId, Pageable pageable) {
		Page<Travel> travelPage = travelRepository.searchTravels(categoryId, pageable);
		return travelPage.map(TravelDto::of);
	}

	@Transactional(readOnly = true)
	public List<TravelDto> getTravelsNoOffset(Long categoryId, LocalDateTime lastCreatedAt, int size) {
		List<Travel> travelList = travelRepository.searchTravelsNoOffset(categoryId, lastCreatedAt, size);
		return travelList.stream().map(TravelDto::of).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public TravelDto getTravelDetail(Long travelId) {
		Travel travel = getTravel(travelId);
		return TravelDto.of(travel);
	}

	@Transactional
	public void deleteTravel(Long travelId, String email) {
		Member member = memberService.getMemberByEmail(email);
		Travel travel = getTravel(travelId);
		validateOwnership(member, travel);
		travel.markAsDeleted();
	}

	public void validateMatchStatus(Travel travel) {
		if (travel.getStatus() == TravelStatus.IN_PROGRESS || travel.getStatus() == TravelStatus.MATCHED) {
			throw new CustomException(ErrorCode.TRAVEL_ALREADY_IN_PROGRESS);
		}
	}

	public void validateOwnership(Member member, Travel travel) {
		Long ownerId = travel.getUser().getId();
		Long requesterId = member.getId();

		if (!Objects.equals(ownerId, requesterId)) {
			throw new CustomException(ErrorCode.TRAVEL_ACCESS_DENIED);
		}
	}

	public Travel getTravel(Long travelId) {
		return travelRepository.findById(travelId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRAVEL_NOT_FOUND));
	}
}
