package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.HighFivers;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HighFiversRepository  extends CrudRepository<HighFivers, Integer> {
    List<HighFivers> findByUserId(int userId);
}
