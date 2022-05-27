package min.project.users.data.dao;

import min.project.users.data.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends CrudRepository<User, String> {

    Optional<User> findByEmailOrUsername(String email, String userName);
}
