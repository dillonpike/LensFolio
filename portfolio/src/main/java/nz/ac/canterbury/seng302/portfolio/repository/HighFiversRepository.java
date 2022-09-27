package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.HighFivers;
import org.springframework.data.repository.CrudRepository;

public interface HighFiversRepository  extends CrudRepository<HighFivers, Integer> {
    HighFivers findByUserId(int userId);
}
