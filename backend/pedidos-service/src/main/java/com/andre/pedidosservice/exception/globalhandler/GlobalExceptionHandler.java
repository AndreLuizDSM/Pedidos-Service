package com.andre.pedidosservice.exception.globalhandler;

import com.andre.pedidosservice.exception.exceptions.EmailException;
import com.andre.pedidosservice.exception.exceptions.ErrorResponseDTO;
import com.andre.pedidosservice.exception.exceptions.ExternalServiceException;
import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.exception.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlerResourceNotFound(ResourceNotFoundException ex,
                                                                    HttpServletRequest http){
        log.error("Recurso não encontrado na requisição {}", http.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    buildError(ex.getMessage() , HttpStatus.NOT_FOUND.value(), http.getRequestURI() , "Not Found")
            );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handlerUnauthorizedException(UnauthorizedException ex,
                                                                         HttpServletRequest http){
        log.error("Acesso não autorizado na requisição {}", http.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildError(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(), http.getRequestURI(), "Unauthorized"));
    }

    // Requisição inválida do cliente: estoque insuficiente,
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRequest(InvalidRequestException ex,
                                                                 HttpServletRequest http){
        log.error("Requisição inválida na requisição {}", http.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), http.getRequestURI(), "Bad Request"));
    }

    // Falha ao chamar um serviço externo (mensageria)
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleExternalService(ExternalServiceException ex,
                                                                  HttpServletRequest http){
        log.error("Falha em serviço externo na requisição {}", http.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                buildError(ex.getMessage(), HttpStatus.BAD_GATEWAY.value(), http.getRequestURI(), "Bad Gateway"));
    }


    // Exception caso usuário tente passar credênciais inválidas com o que foi definido no DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // Pega todos os erros que o @Valid capita , e retorna uma lista.
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("Falha de validação na requisição {}: {}", request.getRequestURI(), message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        message,
                        HttpStatus.BAD_REQUEST.value(),
                        request.getRequestURI(),
                        "Bad Request"
                ));
    }

    // Rota inexistente (/use em vez de /user) 404 Not Found
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoResourceFound(NoResourceFoundException ex,
                                                                  HttpServletRequest request) {
        log.error("Rota inexistente {}", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildError("Recurso não encontrado", HttpStatus.NOT_FOUND.value(),
                        request.getRequestURI(), "Not Found"));
    }

    // Exception 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex,
                                                          HttpServletRequest request) {

        // Loga o erro real (com stack trace) para diagnóstico; ao cliente devolve só a mensagem genérica.
        log.error("Erro na requisição {}", request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError("Erro interno do servidor", 500, request.getRequestURI(), "Internal Server Error"));
    }

    private ErrorResponseDTO buildError (String message, int status, String path, String erro) {
        // Criar um erroDTO para passar detalhadamente o erro com o Token JWT
        return  ErrorResponseDTO.builder()
                .timeStamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .path(path)
                .erro(erro)
                .build();
    }
}


