package com.laraim237.restoX.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.laraim237.restoX.dto.StorageResult;

public interface StorageService {
	
	StorageResult uploadFile(MultipartFile file, String folder) throws IOException;

	void delete(String publicId) throws IOException;
	

}
