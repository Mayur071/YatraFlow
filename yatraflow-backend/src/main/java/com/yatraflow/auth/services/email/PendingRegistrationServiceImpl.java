package com.yatraflow.auth.services.email;

import com.yatraflow.auth.entity.PendingRegistration;
import com.yatraflow.auth.repository.PendingRegistrationRepository;
import com.yatraflow.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class PendingRegistrationServiceImpl implements PendingRegistrationService{

    private final PendingRegistrationRepository pendingRegistrationRepository;


    @Override
    public PendingRegistration createPendingRegistration(String email, String passwordHash, String firstName, String lastName, String phoneNumber) {

        PendingRegistration pendingRegistration = PendingRegistration.builder()
                .email(email)
                .passwordHash(passwordHash)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        return pendingRegistrationRepository.save(pendingRegistration);
    }

    @Override
    @Transactional(readOnly = true)
    public PendingRegistration getByEmail(String email) {

        return pendingRegistrationRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pending registration not found"
                        )
                );
    }

    @Override
    public void deleteExpiredRegistrations() {

        pendingRegistrationRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
