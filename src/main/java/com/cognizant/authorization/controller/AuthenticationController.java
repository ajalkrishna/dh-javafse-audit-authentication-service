package com.cognizant.authorization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.authorization.entity.AuthRequest;
import com.cognizant.authorization.entity.AuthResponse;
import com.cognizant.authorization.entity.IsValidToken;
import com.cognizant.authorization.entity.TokenValidator;
import com.cognizant.authorization.exception.AuthenticationException;
import com.cognizant.authorization.service.CustomUserDetailService;
import com.cognizant.authorization.util.JwtUtil;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/authorize")
public class AuthenticationController {
	
	@Autowired
	private JwtUtil util;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private CustomUserDetailService userDetails;
	
	@PostMapping("/authenticate")
	public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest request) throws AuthenticationException {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		}
		catch(Exception ex) {
			log.info("Something went wrong during authentication");
			return new ResponseEntity<>(new AuthResponse(null),HttpStatus.OK);		
		}
		String token=util.generateToken(request.getUsername());
		log.info("Login successful");
		return new ResponseEntity<>(new AuthResponse(token),HttpStatus.OK);
		
		
	}
	
	@PostMapping("/validate")
	public IsValidToken validateToken(@RequestBody TokenValidator inputToken) {
		UserDetails authorizedUser=null;
		
		try {
			String username = util.extractUsername(inputToken.getToken());
			authorizedUser= userDetails.loadUserByUsername(username);
			log.info("User Available");
		}
		catch(Exception ex) {
			log.info("Something went wrong");
			return new IsValidToken();
			
		}
		Boolean isValid = util.validateToken(inputToken.getToken(), authorizedUser);
		log.info("token is valid");
		return new IsValidToken(isValid);
	}
}
