package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface UserModelRepository extends CrudRepository<UserModel, Integer> {

    UserModel findByUserId(int userId);

    boolean existsByUserId(int userId);

    List<UserModel> findByUsername(String username);



}
