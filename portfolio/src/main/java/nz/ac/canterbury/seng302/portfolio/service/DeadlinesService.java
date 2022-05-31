package nz.ac.canterbury.seng302.portfolio.service;

import io.grpc.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Deadlines;
import nz.ac.canterbury.seng302.portfolio.repository.DeadlinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeadlinesService {

    @Autowired
    private DeadlinesRepository repository;

    /**
     * Updates a deadline
     * @param deadline deadline to update it to
     * @return Newly edited event
     */
    public Deadlines updateDeadline(Deadlines deadline) {
        Optional<Deadlines> sOptional = repository.findById((Integer) deadline.getId());

        if (sOptional.isPresent()) {
            Deadlines deadlineUpdate = sOptional.get();
            deadlineUpdate.setDeadlineName(deadline.getDeadlineName());
            deadlineUpdate.setDeadlineDate(deadline.getDeadlineDate());


            deadlineUpdate = repository.save(deadlineUpdate);
            return deadlineUpdate;
        } else {
            return deadline;
        }
    }

    /**
     * Get deadline by Id
     * @param id id of event
     * @return deadline with the id that is the input
     * @throws Exception If event can't be found
     */
    public Deadlines getEventById(Integer id) throws Exception {
        Optional<Deadlines> deadline = repository.findById(id);
        if(deadline.isPresent()) {
            return deadline.get();
        } else {

            throw new Exception("Event not found");
        }
    }


}
