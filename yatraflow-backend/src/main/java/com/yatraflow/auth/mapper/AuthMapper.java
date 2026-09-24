package com.yatraflow.auth.mapper;

import com.yatraflow.auth.dto.request.RegisterRequest;
import com.yatraflow.auth.dto.response.RegisterResponse;
import com.yatraflow.auth.entity.PendingRegistration;
import com.yatraflow.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AuthMapper {

    // RegisterRequest -> User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "accountLocked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(RegisterRequest request);


    // PendingRegistration -> User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "passwordHash")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "emailVerified", constant = "true")
    @Mapping(target = "accountLocked", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)


    User toUser(PendingRegistration pendingRegistration);


    RegisterResponse toRegisterResponse(User user);

}
