package com.yatraflow.auth.dto.response;

import lombok.*;

@Builder
public record RegisterResponse(

        String message,

        String email


){

}
