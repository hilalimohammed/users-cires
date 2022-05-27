package min.project.users.service.impl;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import min.project.users.Utils.Utils;
import min.project.users.data.dao.UserDao;
import min.project.users.data.dto.BatchDto;
import min.project.users.data.dto.TokenDto;
import min.project.users.data.dto.UserPrinciple;
import min.project.users.data.entities.User;
import min.project.users.data.request.LoginRequest;
import min.project.users.service.JwtService;
import min.project.users.service.UserService;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Override
    public List<User> generate(Integer count) {

        Faker faker = new Faker();
        Gson gson = new Gson();
        List<User> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setAvatar(faker.avatar().image());
            user.setCity(faker.address().city());
            user.setCountry(faker.address().country());
            user.setJobPosition(faker.job().title());
            user.setMobile(faker.phoneNumber().phoneNumber());
            user.setPassword(Utils.generateRandomPassword(6, 10));
            user.setCompany(faker.company().name());
            user.setEmail(faker.bothify("????##@gmail.com"));
            user.setUsername(faker.name().username());
            user.setRole(Utils.generateRandomRole());
            user.setBirthDate(faker.date().birthday(18, 80));
            users.add(user);
        }
        return users;
    }

    @Override
    public BatchDto batch(MultipartFile file) throws IOException, ParseException, java.text.ParseException {
        JSONParser jsonParser = new JSONParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        BatchDto batchDto = new BatchDto(0, 0);

        Object obj = jsonParser.parse(reader);
        JSONArray users = (JSONArray) obj;
        for (Object json : users) {
            User user = Utils.constructUserFromJson(json);
            Optional<User> optional = userDao.findByEmailOrUsername(user.getEmail(), user.getUsername());
            if (!optional.isPresent()) {
                user.setPassword(encoder.encode(user.getPassword()));
                userDao.save(user);
                batchDto.setSuccess(batchDto.getSuccess() + 1);
            } else {
                batchDto.setFailed(batchDto.getFailed() + 1);
            }
        }
        return batchDto;
    }

    @Override
    public ResponseEntity<TokenDto> login(LoginRequest loginRequest) {
        Optional<User> optional = userDao.findByEmailOrUsername(loginRequest.getUsername(), loginRequest.getUsername());
        if (optional.isPresent()) {
            User user = optional.get();
            if (encoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String token = jwtService.generateJWT(user.getEmail(), user.getUsername(), user.getRole(), 0);
                //return new TokenDto(token);
                return ResponseEntity.status(HttpStatus.OK).body(new TokenDto(token));
            }
            else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public User getProfile(String username) {
        Optional<User> optional = userDao.findByEmailOrUsername(username, username);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

}
