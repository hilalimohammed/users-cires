package min.project.users.service;

import min.project.users.data.dto.BatchDto;
import min.project.users.data.dto.TokenDto;
import min.project.users.data.entities.User;
import min.project.users.data.request.LoginRequest;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    public List<User> generate(Integer count);

    public BatchDto batch(MultipartFile file) throws IOException, ParseException, java.text.ParseException;

    ResponseEntity<TokenDto> login(LoginRequest loginRequest);
    
    User getProfile(String username);
}
