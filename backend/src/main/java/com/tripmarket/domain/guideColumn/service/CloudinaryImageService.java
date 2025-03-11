package com.tripmarket.domain.guideColumn.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryImageService {
	private final Cloudinary cloudinary;

	public String uploadImage(MultipartFile file) {
		try {
			Map<String, String> options = new HashMap<>();
			options.put("resource_type", "auto");

			Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
			return uploadResult.get("secure_url").toString();
		} catch (IOException e) {
			log.error("이미지 업로드 실패:", e);
			throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
		}
	}
}