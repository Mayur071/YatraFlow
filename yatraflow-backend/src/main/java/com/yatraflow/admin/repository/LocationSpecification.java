
package com.yatraflow.admin.repository.specification;

import com.yatraflow.admin.entity.Location;
import com.yatraflow.admin.enums.LocationType;

import org.springframework.data.jpa.domain.Specification;

public final class LocationSpecification {

    private LocationSpecification() {
    }

    public static Specification<Location> filters(
            String search,
            LocationType type,
            String country,
            String state,
            Boolean active
    ) {

        Specification<Location> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        // Search by name and display name
        if (search != null && !search.isBlank()) {

            String keyword = "%"
                    + search.trim().toLowerCase()
                    + "%";

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.or(
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("name")
                                            ),
                                            keyword
                                    ),
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("displayName")
                                            ),
                                            keyword
                                    )
                            )
            );
        }

        // Filter by location type
        if (type != null) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("type"),
                                    type
                            )
            );
        }

        // Filter by country
        if (country != null && !country.isBlank()) {

            String normalizedCountry =
                    country.trim().toLowerCase();

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    criteriaBuilder.lower(
                                            root.get("country")
                                    ),
                                    normalizedCountry
                            )
            );
        }

        // Filter by state
        if (state != null && !state.isBlank()) {

            String normalizedState =
                    state.trim().toLowerCase();

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    criteriaBuilder.lower(
                                            root.get("state")
                                    ),
                                    normalizedState
                            )
            );
        }

        // Filter by active/inactive status
        if (active != null) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("active"),
                                    active
                            )
            );
        }

        return specification;
    }
}
