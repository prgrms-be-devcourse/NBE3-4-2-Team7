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

	public void deleteImage(String imageUrl) {
		try {
			// URL에서 public_id 추출
			String publicId = extractPublicIdFromUrl(imageUrl);

			// 이미지 삭제
			Map<String, String> options = new HashMap<>();
			options.put("resource_type", "image");

			cloudinary.uploader().destroy(publicId, options);
		} catch (IOException e) {
			log.error("이미지 삭제 실패: {}", imageUrl, e);
			throw new CustomException(ErrorCode.IMAGE_DELETE_FAILED);
		}
	}

	private String extractPublicIdFromUrl(String imageUrl) {
		// URL 예시: https://res.cloudinary.com/your-cloud-name/image/upload/v1234567890/folder/image.jpg
		try {
			String[] urlParts = imageUrl.split("/");
			String fileName = urlParts[urlParts.length - 1];
			// 확장자 제거
			return fileName.substring(0, fileName.lastIndexOf('.'));
		} catch (Exception e) {
			log.error("Public ID 추출 실패: {}", imageUrl, e);
			throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
		}
	}
}