package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This class is a model which used to replicate the user(s) who has high five to a certain piece of evidence
 */
@Entity
public class HighFivers {
    private String name;

    private Integer userId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer highFiverId;

    /**
     * Basic constructor for high fivers object
     * @param name the name of the user, this consists of first name of the user followed by last name of the user
     * @param id the id of that user
     */
    public HighFivers(String name, int id) {
        this.name = name;
        this.userId = id;
    }

    /**
     * Empty constructor for JPA
     */
    public HighFivers() {

    }

    /**
     * Getter for the high five id
     * @return the id of the high five
     */
    public Integer getHighFiverId() {
        return highFiverId;
    }

    /**
     * Setter for the high five id
     * @param highFiverId the id of the high five
     */
    public void setHighFiverId(Integer highFiverId) {
        this.highFiverId = highFiverId;
    }

    /**
     * get the name of high fiver
     * @return the name of the high fiver
     */
    public String getName() {
        return name;
    }

    /**
     * set the name of the high fiver
     * @param name new/initial name of the high fiver
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * return the id of the high fiver
     * @return an integer which indicates the id of the high fiver
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * set the new/initial id of the high fiver, This is the id of the user that stored in idp
     * @param userId new/initial id of the high fiver
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
