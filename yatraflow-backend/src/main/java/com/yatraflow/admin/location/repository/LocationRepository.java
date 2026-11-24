package com.yatraflow.admin.location.repository;

import com.yatraflow.admin.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {

    boolean existsByParentLocation_IdAndActiveTrue(Long parentLocationId);
}
