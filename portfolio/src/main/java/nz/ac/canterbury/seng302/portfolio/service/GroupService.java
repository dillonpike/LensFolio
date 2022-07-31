package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Contains methods for performing operations on Group objects, such as adding and removing group members, and storing
 * groups in the database.
 */
@Service
public class GroupService {

    /**
     * Repository of Group objects.
     */
    @GrpcClient(value = "identity-provider-grpc-server")
    UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountStub;

    /**
     * Returns the group object from the database with the given id.
     * @param id group id
     * @return group object from the database with the given id
     * @throws ObjectNotFoundException when a group with the given id doesn't exist in the database
     */
    public Group getGroupById(Integer id) throws ObjectNotFoundException {
        Optional<Group> group = groupRepository.findById(id);
        if (group.isPresent()) {
            return group.get();
        } else {
            throw new ObjectNotFoundException(id, "Group");
        }
    }

    /**
     * Adds the user identified by the given id to the given group.
     * @param userId id of user to be added
     * @param group group user is added to
     */
    public void addMember(int userId, Group group) {
        group.addMember(userId);
        groupRepository.save(group);
    }

    /**
     * Removes the user identified by the given id to the given group.
     * @param userId id of user to be removed
     * @param group group user is removed from
     */
    public void removeMember(int userId, Group group) {
        group.removeMember(userId);
        groupRepository.save(group);
    }
}
