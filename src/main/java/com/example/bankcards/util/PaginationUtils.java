package com.example.bankcards.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * The type Pagination utils.
 */
@Component
public class PaginationUtils {

    /**
     * Create pageable pageable.
     *
     * @param page      the page
     * @param size      the size
     * @param sortBy    the sort by
     * @param direction the direction
     * @return the pageable
     */
    public Pageable createPageable(Integer page, Integer size, String sortBy, String direction) {
        if (page == null) page = 0;
        if (size == null) size = 10;
        if (sortBy == null) sortBy = "id";
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        return PageRequest.of(page-1, size, Sort.by(sortDirection, sortBy));
    }

    /**
     * Build pagination response map.
     *
     * @param page the page
     * @return the map
     */
    public Map<String, Object> buildPaginationResponse(Page<?> page) {
        return Map.of(
            "content", page.getContent(),
            "currentPage", page.getNumber(),
            "totalItems", page.getTotalElements(),
            "totalPages", page.getTotalPages(),
            "size", page.getSize(),
            "first", page.isFirst(),
            "last", page.isLast()
        );
    }
}