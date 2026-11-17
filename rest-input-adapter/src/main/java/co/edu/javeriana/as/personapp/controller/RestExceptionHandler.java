package co.edu.javeriana.as.personapp.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Centraliza la conversión de excepciones de la aplicación en respuestas HTTP, para que el dominio
 * y los casos de uso no dependan de HTTP y el controlador no necesite bloques try/catch.
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(NoExistException.class)
	public ResponseEntity<Response> notFound(NoExistException e) {
		return build(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(AlreadyExistException.class)
	public ResponseEntity<Response> conflict(AlreadyExistException e) {
		return build(HttpStatus.CONFLICT, e.getMessage());
	}

	@ExceptionHandler({ InvalidOptionException.class, IllegalArgumentException.class })
	public ResponseEntity<Response> badRequest(Exception e) {
		return build(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Response> badPathVariable(MethodArgumentTypeMismatchException e) {
		return build(HttpStatus.BAD_REQUEST, e.getName() + " debe ser un número: " + e.getValue());
	}

	private ResponseEntity<Response> build(HttpStatus status, String message) {
		log.warn(message);
		return ResponseEntity.status(status).body(new Response(status.getReasonPhrase(), message, LocalDateTime.now()));
	}
}
