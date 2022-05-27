package min.project.users;

import min.project.users.data.dao.UserDao;
import min.project.users.data.dto.UserDto;
import min.project.users.data.entities.User;
import min.project.users.service.JwtService;
import min.project.users.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;


@RunWith(SpringRunner.class)
@WebMvcTest(value = UserService.class)
@WithMockUser
public class UserServiceIntegTests {

    @MockBean
    private UserDao userDao;
    @MockBean
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder encoder;



    @Test
    public void testLogin() {

        UserDto userOptional = new UserDto();
        userOptional.setUsername("amin");
        userOptional.setEmail("amin@gmail.com");
        userOptional.setPassword(encoder.encode("1234567"));
        Optional<UserDto> optional = Optional.of(userOptional);

        Mockito.when(userDao.findByEmailOrUsername("amin@gmail.com", "amin")).thenReturn(optional);
        UserDto user = null;
        if (optional.isPresent()) {
            user = optional.get();
            if (encoder.matches("1234567", user.getPassword())) {
                jwtService.generateJWT(user.getEmail(), user.getUsername(), user.getRole(), 0);
            }
        }

        Mockito.verify(jwtService, Mockito.times(1)).generateJWT(user.getEmail(), user.getUsername(), user.getRole(), 0);
    }
}

