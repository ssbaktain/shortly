package com.ssbaktain.shortly.user.service;

import com.ssbaktain.shortly.user.domain.User;
import com.ssbaktain.shortly.user.exception.UserNotFoundException;
import com.ssbaktain.shortly.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
