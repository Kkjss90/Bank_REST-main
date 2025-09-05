//package com.example.bankcards.util;
//
//import com.example.bankcards.dto.response.ApiResponse;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ResponseBuilder {
//
//    public ResponseEntity<ApiResponse> buildSuccessResponse(String message) {
//        return ResponseEntity.ok(ApiResponse.success(message));
//    }
//
//    public ResponseEntity<ApiResponse> buildSuccessResponse(String message, Object data) {
//        return ResponseEntity.ok(ApiResponse.success(message, data));
//    }
//
//    public ResponseEntity<ApiResponse> buildErrorResponse(String message) {
//        return ResponseEntity.badRequest().body(ApiResponse.error(message));
//    }
//
//    public ResponseEntity<ApiResponse> buildNotFoundResponse(String message) {
//        return ResponseEntity.notFound().build();
//    }
//
//    public ResponseEntity<ApiResponse> buildForbiddenResponse() {
//        return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
//    }
//
//    public ResponseEntity<ApiResponse> buildUnauthorizedResponse() {
//        return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
//    }
//}