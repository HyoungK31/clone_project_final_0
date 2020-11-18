package com.clone.baemin.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidatorAdvice {
	
		private Logger LOG = LoggerFactory.getLogger(ValidatorAdvice.class);

	
	   @ExceptionHandler
	   public ResponseEntity<Object> BadRequestException(final IllegalArgumentException ex) {
	      LOG.warn("error", ex);
	      return ResponseEntity.badRequest().body(ex.getMessage());
	   }

}
