package service;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assetmanager.model.User;
import com.assetmanager.repository.UserRepository;
import com.assetmanager.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService; // The "brain" with the mock inside

    @Test
    @DisplayName("Should successfully Register a user")
    void testRegisterUser() {
        // 1. ARRANGE
        int userId = 100;
        User user = new User(userId, "testuser", "234255");

        when(userRepository.existsById(userId)).thenReturn(false);

        // 2. ACT
        userService.registerNewUser(user);

        // 3. ASSERT
        assertEquals(userId, user.getUserId());

        // 4. VERIFY

        // Verify Registration
        verify(userRepository,times(1)).save(user);

        // Verify that the service checked for existing ID
        verify(userRepository,times(1)).existsById(userId);
    }
    
}
