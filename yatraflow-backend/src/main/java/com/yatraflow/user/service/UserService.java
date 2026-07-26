package com.yatraflow.user.service;

import com.yatraflow.auth.dto.request.RegisterRequest;
import com.yatraflow.user.entity.User;

public interface UserService {

    // ---------- Registration ----------
    User createUser(User user);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);


    // ---------- Fetch ----------
    User getUserById(Long id);

    User getUserByEmail(String email);

    User getCurrentUser();


    // ---------- Persistence ----------

    User save(User user);



//    Future methods
//
//    updateProfile()
//
//    changePassword()
//
//    deleteAccount()
//
//    uploadProfileImage()
//
//    deactivateAccount()
//
//    lockUser()
//
//    unlockUser()
//
//    getAllUsers()

}
