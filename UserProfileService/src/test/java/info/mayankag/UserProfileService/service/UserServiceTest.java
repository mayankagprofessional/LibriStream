package info.mayankag.UserProfileService.service;

import info.mayankag.UserProfileService.dto.GetAllUsersResponseDto;
import info.mayankag.UserProfileService.entity.User;
import info.mayankag.UserProfileService.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Value("${pagination-size}")
    private int paginationSize;

    @BeforeEach
    void setUp() {
        paginationSize = 3;

        // Manually set the private paginationSize field value
        ReflectionTestUtils.setField(userService, "paginationSize", paginationSize);
    }

    @Test
    void getAllUsers() {

        // Mock 2 Users
        Page<User> userPage = new PageImpl<>(
                List.of(
                        User.builder()
                                .firstname("John")
                                .lastname("Doe")
                                .email("john@example.com")
                                .age(25)
                                .interests(new ArrayList<>())
                                .build(),
                        User.builder()
                                .firstname("John")
                                .lastname("Doe2")
                                .email("john2@example.com")
                                .age(30)
                                .interests(new ArrayList<>())
                                .build()
                        ));

        // Mock repository to return the page of users when the specific PageRequest is called
        when(userRepository.findAll(PageRequest.of(0, paginationSize)))
                .thenReturn(userPage);

        // Get 1st Page
        Slice<GetAllUsersResponseDto> result = userService.getAllUsers(1);

        // Check whether the data returned is not null
        assertNotNull(result);

        // Check whether the total content is equal to the total users mocked using Page
        assertEquals(userPage.getSize(), result.getContent().size());
    }
}