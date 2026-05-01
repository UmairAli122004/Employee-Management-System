package com.EmployeeManagementSystem.Service.SeviceImpl;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.Entity.Employee;
import com.EmployeeManagementSystem.Mapper.MapperClassToDTO;
import com.EmployeeManagementSystem.Repository.PaginationRepository;
import com.EmployeeManagementSystem.Service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaginationServiceImpl implements PaginationService {

    private final PaginationRepository paginationRepository;
    private final MapperClassToDTO mapperClassToDTO;
    private final String CACHE_PAGE = "employeePage";

    @Cacheable(
            value = CACHE_PAGE,
            key = "'page=' + #pageable.pageNumber + '-size=' + #pageable.pageSize + '-sort=' + #pageable.sort"
    )
    @Override
    public Page<EmployeeDTO> findByPageNumber(Pageable pageable) {
        if (pageable.getPageNumber() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number cannot be negative"
            );
        }

        if (pageable.getPageSize() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be greater than 0"
            );
        }

        Page<Employee> page = paginationRepository.findByPageNumber(pageable);

        List<EmployeeDTO> list = page.stream()
                .map(mapperClassToDTO::entityToDTO)
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }


    @Cacheable(
            value = CACHE_PAGE,
            key = "'list-page=' + #pageable.pageNumber + '-size=' + #pageable.pageSize + '-sort=' + #pageable.sort"
    )
    @Override
    public List<EmployeeDTO> findEmployeeByPageNumber(Pageable pageable) {
        return paginationRepository.findByPageNumber(pageable)
                .stream()
                .map(mapperClassToDTO::entityToDTO)
                .toList();
    }
}