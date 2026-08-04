package com.yatraflow.auth.dto.response;

import lombok.*;

@Builder
public record RegisterResponse(

       Long id,

       String firstName,

       String lastName,

       String email,

       String phoneNumber

){

}
