package com.EmployeeManagementSystem.SpecificationClass;

import com.EmployeeManagementSystem.Enum.SearchOperation;
import com.EmployeeManagementSystem.DTO.SearchRequestDTO;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class EmployeeSpecification<T> {

    //Whitelisted fields (Security)
    private static final Set<String> ALLOWED_FIELDS = Set.of(
            "id", "firstName", "lastName", "address",
            "phoneNumber", "salary", "joiningDateTime", "active",
            "department.id", "department.department_name"
    );

    public Specification<T> filter(List<SearchRequestDTO> requests, String condition) {
        return (root, query, cb) -> {

            if (requests == null || requests.isEmpty()) {
                return cb.conjunction(); // no filter
            }

            List<Predicate> predicates = new ArrayList<>();

            for (SearchRequestDTO request : requests) {

                validateColumn(request.getColumn());

                Path<?> path = getPath(root, request.getColumn());
                Class<?> fieldType = path.getJavaType();

                Object value = castValue(fieldType, request.getValue());
                Object valueTo = castValue(fieldType, request.getValueTo());

                SearchOperation operation = request.getOperation();
                if (operation == null) {
                    throw new RuntimeException("Operation cannot be null");
                }

                switch (operation) {

                    case EQUAL -> predicates.add(cb.equal(path, value));

                    case LIKE -> predicates.add(
                            cb.like(
                                    cb.lower(path.as(String.class)),
                                    "%" + value.toString().toLowerCase() + "%"
                            )
                    );

                    case GT -> predicates.add(
                            cb.greaterThan(path.as(Comparable.class), (Comparable) value)
                    );

                    case LT -> predicates.add(
                            cb.lessThan(path.as(Comparable.class), (Comparable) value)
                    );

                    case BETWEEN -> predicates.add(
                            cb.between(
                                    path.as(Comparable.class),
                                    (Comparable) value,
                                    (Comparable) valueTo
                            )
                    );

                    case IN -> {
                        CriteriaBuilder.In<Object> inClause = cb.in(path);
                        for (Object val : request.getValues()) {
                            inClause.value(castValue(fieldType, val));
                        }
                        predicates.add(inClause);
                    }
                }
            }

            return "OR".equalsIgnoreCase(condition)
                    ? cb.or(predicates.toArray(new Predicate[0]))
                    : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    //Column validation
    private void validateColumn(String column) {
        if (!ALLOWED_FIELDS.contains(column)) {
            throw new RuntimeException("Invalid column: " + column);
        }
    }

    //Nested field support (department.id)
    private Path<?> getPath(Root<?> root, String column) {
        if (column.contains(".")) {
            String[] parts = column.split("\\.");
            Path<?> path = root.get(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                path = path.get(parts[i]);
            }
            return path;
        }
        return root.get(column);
    }

    //Type conversion (CRITICAL)
    private Object castValue(Class<?> type, Object value) {
        if (value == null) return null;

        if (type.equals(Long.class)) {
            return Long.valueOf(value.toString());
        } else if (type.equals(Double.class)) {
            return Double.valueOf(value.toString());
        } else if (type.equals(LocalDateTime.class)) {
            return LocalDateTime.parse(value.toString());
        } else if (type.isEnum()) {
            return Enum.valueOf((Class<Enum>) type, value.toString());
        }
        return value.toString();
    }
}