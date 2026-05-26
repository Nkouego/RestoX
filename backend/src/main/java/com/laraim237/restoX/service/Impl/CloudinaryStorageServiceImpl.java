package com.laraim237.restoX.service.Impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.laraim237.restoX.common.Exception.StorageException;
import com.laraim237.restoX.common.utils.FileValidator;
import com.laraim237.restoX.dto.StorageResult;
import com.laraim237.restoX.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryStorageServiceImpl implements StorageService {
	
	private final Cloudinary cloudinary;

	@Override
	public StorageResult uploadFile(MultipartFile file, String folder){
		
		FileValidator.validateImage(file);
		
		Map uploadResult;
		try {
			uploadResult = cloudinary.uploader().upload(
				    file.getBytes(),
				    ObjectUtils.asMap(
				    		"upload_preset", "restoX",
				    		"folder", folder
				    )
				);
		} catch (IOException e) {
			log.error("Cloudinary upload failed: {}", e.getMessage(), e);
			throw new StorageException("Échec de l'upload de l'image", e);
		}
		
		return new StorageResult(
				(String) uploadResult.get("secure_url"),
				(String) uploadResult.get("public_id")
				);
	}
	
	 @Override
	    public void delete(String publicId) {
	        try {
				cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
			} catch (IOException e) {
                log.warn("Failed to delete old image: {}", e.getMessage());
            }
	    }
	
	

}
