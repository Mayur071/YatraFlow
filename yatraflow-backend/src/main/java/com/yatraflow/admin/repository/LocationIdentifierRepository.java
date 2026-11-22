package com.yatraflow.admin.repository;

import com.yatraflow.admin.entity.LocationIdentifier;
import com.yatraflow.admin.enums.LocationCodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationIdentifierRepository extends JpaRepository<LocationIdentifier, Long> {

    Optional<LocationIdentifier> findByCodeAndCodeType(String code, LocationCodeType codeType);
}
