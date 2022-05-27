package min.project.users.Utils;

import min.project.users.data.dto.UserPrinciple;
import min.project.users.data.entities.User;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Utils {

    public static String generateRandomPassword(int from, int to) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        int result = random.nextInt(to - from) + from;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

    public static String generateRandomRole() {
        Random random = new Random();
        int result = random.nextInt(2);
        return result == 0 ? "ROLE_ADMIN" : "ROLE_USER";
    }

    public static UserPrinciple getUserPrinciple() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrinciple) auth.getPrincipal();
    }

    public static User constructUserFromJson(Object json) throws ParseException {
        //1961-08-12T17:07:21.524+00:00
        JSONObject jsonObject = (JSONObject) json;
        String firstName = (String) jsonObject.get("firstName");
        String lastName = (String) jsonObject.get("lastName");
        String avatar = (String) jsonObject.get("avatar");
        String city = (String) jsonObject.get("city");
        String country = (String) jsonObject.get("country");
        String jobPosition = (String) jsonObject.get("jobPosition");
        String mobile = (String) jsonObject.get("mobile");
        String password = (String) jsonObject.get("password");
        String company = (String) jsonObject.get("company");
        String email = (String) jsonObject.get("email");
        String username = (String) jsonObject.get("username");
        Date birthDate = convertDate((String) jsonObject.get("birthDate"));
        String role = (String) jsonObject.get("role");

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAvatar(avatar);
        user.setCity(city);
        user.setCountry(country);
        user.setJobPosition(jobPosition);
        user.setMobile(mobile);
        user.setPassword(password);
        user.setCompany(company);
        user.setEmail(email);
        user.setUsername(username);
        user.setRole(role);
        user.setBirthDate(birthDate);

        return user;
    }

    private static Date convertDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return new Date(date);
        }
    }
}
