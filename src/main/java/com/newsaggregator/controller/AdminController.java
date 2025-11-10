package com.newsaggregator.controller;

import com.newsaggregator.service.HibernateSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Administrative operations")
public class AdminController {
    
    private final HibernateSearchService hibernateSearchService;
    
    @PostMapping("/rebuild-index")
    @Operation(summary = "Rebuild search index", description = "Rebuilds the Hibernate Search index for better search performance")
    @ApiResponse(responseCode = "200", description = "Index rebuild completed successfully")
    @ApiResponse(responseCode = "500", description = "Index rebuild failed")
    public ResponseEntity<Map<String, String>> rebuildSearchIndex() {
        try {
            hibernateSearchService.rebuildSearchIndex();
            log.info("Search index rebuild completed successfully");
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Search index rebuilt successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to rebuild search index", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to rebuild search index: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}