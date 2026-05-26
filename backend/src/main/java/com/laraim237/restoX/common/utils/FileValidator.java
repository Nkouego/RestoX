package com.laraim237.restoX.common.utils;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.laraim237.restoX.common.Exception.FileValidationException;

public final class FileValidator {
	
	private static final long MAX_IMAGE_SIZE = 1024*1024*5;
	private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/webp", "image/png");
	
	private FileValidator() {}

	public static void validateImage(MultipartFile file) {
		if( file == null || file.isEmpty() ) {
			throw new FileValidationException("Fichier image vide");
		}
		
		if(!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {			
			throw new FileValidationException("Format du fichier accepté: JPG, PNG, WEBP");
		}
		
		if(file.getSize() > MAX_IMAGE_SIZE) {
			throw new FileValidationException("L'image du fichier ne doit pas depasser 5MB");
		}
	}

}
