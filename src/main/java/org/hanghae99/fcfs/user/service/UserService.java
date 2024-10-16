package org.hanghae99.fcfs.user.service;

import lombok.RequiredArgsConstructor;
import org.hanghae99.fcfs.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
