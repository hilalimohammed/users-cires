package min.project.users.web;

import min.project.users.data.dto.TokenDto;
import min.project.users.data.request.LoginRequest;
import min.project.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class AuthenticationRest {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/auth")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }
}
