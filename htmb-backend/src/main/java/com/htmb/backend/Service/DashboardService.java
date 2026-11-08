
package com.htmb.backend.Service;

import com.htmb.backend.Model.DashboardResponse;
import com.htmb.backend.Model.User;
import com.htmb.backend.Repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DashboardService {

    private final UserRepository userRepository;

    public DashboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public DashboardResponse getDashboard(long userId) {

        Optional<User> result =
                userRepository.findByUserId(userId);

        if (result.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        User user = result.get();

        return new DashboardResponse(
                "Dashboard loaded successfully.",
                user.getUserId(),
                user.getName(),
                user.getMobileNumber(),
                user.getBalance()
        );
    }
}
