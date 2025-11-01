package com.newsaggregator.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsSearchRequest {
    @NotBlank(message = "Search keyword is required")
    private String keyword;
    
    @Min(value = 1, message = "Page number must be greater than 0")
    @Builder.Default
    private int page = 1;
    
    @Min(value = 1, message = "Page size must be greater than 0")
    @Builder.Default
    private int pageSize = 10;
    
    private String city;
    
    @Builder.Default
    private boolean offlineMode = false;
}