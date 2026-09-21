package com.yatraflow.auth.services.email;

import com.yatraflow.auth.entity.PendingRegistration;

public interface PendingRegistrationService {

    PendingRegistration createPendingRegistration(
            String email,
            String passwordHash,
            String firstName,
            String lastName,
            String phoneNumber
    );

    PendingRegistration getByEmail(String email);

    void deleteExpiredRegistrations();
}
