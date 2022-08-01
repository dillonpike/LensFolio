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
     * Removes a group from the database.
     * @param id ID of the group being removed
     */
    public void removeGroup(Integer id) {
        Optional<GroupModel> sOptional = repository.findById(id);

        if (sOptional.isPresent()) {
            GroupModel groupUpdate = sOptional.get();
            repository.deleteById(groupUpdate.getGroupId());
        }
    }

}
