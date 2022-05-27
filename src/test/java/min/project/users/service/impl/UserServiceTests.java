package min.project.users.service.impl;

import com.google.gson.Gson;
import min.project.users.data.dto.BatchDto;
import min.project.users.data.entities.User;
import min.project.users.service.UserService;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;


    @Test
    public void testGenerateJson() {
        int count = 10;
        List<User> generatedUsers = userService.generate(count);

        assertThat(generatedUsers.size()).isEqualTo(count);
    }

    @Test
    void testImportJsonFileBatch() throws IOException, ParseException, java.text.ParseException {

        List<User> users = userService.generate(10);
        BatchDto result = userService.batch(convertListToMultipart(users));
        BatchDto expected = new BatchDto(10, 0);

        assertThat(result.getSuccess()).isEqualTo(expected.getSuccess());
    }


    @Test
    void testGetMyProfileOrOtherByUsername() throws IOException, ParseException, java.text.ParseException {
        List<User> users = userService.generate(10);
        userService.batch(convertListToMultipart(users));

        String username = users.get(0).getUsername();
        User userDetails = userService.getProfile(username);

        assertThat(userDetails.getUsername()).isEqualTo(users.get(0).getUsername());
    }


    private MultipartFile convertListToMultipart(List<User> users) {
        String json = new Gson().toJson(users);
        return new MockMultipartFile("file.json", "test", "text/plain", json.getBytes());
    }

}
