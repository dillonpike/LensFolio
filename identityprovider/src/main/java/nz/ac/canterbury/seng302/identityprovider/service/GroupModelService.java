package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupModelService {

    @Autowired
    private GroupRepository repository;

    /**
     * Removes the group with the given id from the database if it exists.
     * Returns true if removed, otherwise false (when the group doesn't exist).
     * @param id id of the group being removed
     * @return true if removed, otherwise false
     */
    public boolean removeGroup(Integer id) {
        Optional<GroupModel> sOptional = repository.findById(id);

        if (sOptional.isPresent()) {
            GroupModel groupUpdate = sOptional.get();
            repository.deleteById(groupUpdate.getGroupId());
            return true;
        }
        return false;
    }

}
