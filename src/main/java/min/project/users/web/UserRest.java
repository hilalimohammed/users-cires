package min.project.users.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import min.project.users.Utils.DTOUtils;
import min.project.users.Utils.Utils;
import min.project.users.data.dto.BatchDto;
import min.project.users.data.dto.UserDto;
import min.project.users.data.dto.UserPrinciple;
import min.project.users.data.entities.User;
import min.project.users.service.UserService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRest {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/generate")
    public ResponseEntity<List<User>> generate(@RequestParam(value = "count", defaultValue = "10") Integer count) {

        List<User> data = userService.generate(count);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=users " + new Date().getTime() + ".json").contentType(MediaType.APPLICATION_JSON).body(data);

    }

    @PostMapping(value = "/batch", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public BatchDto batch(@RequestParam(value = "file") MultipartFile file) throws IOException, ParseException, java.text.ParseException {
        return userService.batch(file);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public UserDto me() {
        UserPrinciple userPrinciple = Utils.getUserPrinciple();
        if (userPrinciple != null) {
            User user = userService.getProfile(userPrinciple.getEmail());
            return DTOUtils.map(user, UserDto.class);
        }
        return null;
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserDto findByProfile(@PathVariable String username) {
        User user = userService.getProfile(username);
        return DTOUtils.map(user, UserDto.class);
    }
}
