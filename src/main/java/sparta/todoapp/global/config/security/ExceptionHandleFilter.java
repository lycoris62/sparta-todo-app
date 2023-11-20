package sparta.todoapp.global.config.security;

import static sparta.todoapp.global.error.ErrorCode.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sparta.todoapp.global.error.ErrorResponse;
import sparta.todoapp.global.error.exception.InvalidTokenException;

/**
 * JWT 예외가 발생하면 이를 처리하는 필터.
 */
public class ExceptionHandleFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try{
			filterChain.doFilter(request, response);
		}catch (InvalidTokenException e){
			//유효하지 않은 토큰
			setResponse(response);
			ErrorResponse errorResponse = new ErrorResponse(INVALID_TOKEN.getMessage());

			try{
				response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
			}catch (IOException ignored){
			}
		}
	}

	private void setResponse(HttpServletResponse response) {
		response.setStatus(INVALID_TOKEN.getHttpStatus().value()); // http status 설정
		response.setContentType(MediaType.APPLICATION_JSON_VALUE); // JSON 설정
		response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // UTF8 설정
	}
}
