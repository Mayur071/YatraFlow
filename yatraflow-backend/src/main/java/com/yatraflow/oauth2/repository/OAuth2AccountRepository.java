package com.yatraflow.oauth2.repository;

import com.yatraflow.oauth2.entity.OAuth2Account;
import com.yatraflow.oauth2.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface OAuth2AccountRepository extends JpaRepository<OAuth2Account, Long> {


    Optional<OAuth2Account> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

    boolean existsByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

    @Query("""
    SELECT DISTINCT oa
    FROM OAuth2Account oa
    JOIN FETCH oa.user u
    LEFT JOIN FETCH u.roles
    WHERE oa.provider = :provider
    AND oa.providerUserId = :providerUserId
""")
    Optional<OAuth2Account> findWithUserAndRolesByProviderAndProviderUserId(
            @Param("provider") AuthProvider provider,
            @Param("providerUserId") String providerUserId
    );
}
