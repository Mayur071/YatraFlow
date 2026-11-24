package com.yatraflow.admin.location.repository;

import com.yatraflow.admin.location.entity.LocationIdentifier;
import com.yatraflow.admin.location.enums.LocationCodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationIdentifierRepository extends JpaRepository<LocationIdentifier, Long> {

    Optional<LocationIdentifier> findByCodeAndCodeType(String code, LocationCodeType codeType);
}
