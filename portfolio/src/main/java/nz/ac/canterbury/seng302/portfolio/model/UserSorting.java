package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * JPA entity that links a user id to a column index and sort order. Purpose is for persisting how each user sorts
 * the list of users.
 */
@Entity
public class UserSorting {

    /**
     * User id.
     */
    @Id
    private int userId;

    /**
     * Index of the column being sorted by.
     */
    private int columnIndex;

    /**
     * Sort order, either 'asc' for ascending or 'desc' for descending.
     */
    private String sortOrder;

    /**
     * Empty constructor required for JPA.
     */
    public UserSorting() {}

    /**
     * Constructor that creates a UserSorting object with the given userId, and default columnIndex and sortOrder.
     * @param userId user id
     */
    public UserSorting(int userId) {
        this.userId = userId;
        this.columnIndex = 0;
        this.sortOrder = "asc";
    }

    /**
     * Constructor that creates a UserSorting object with the given userId, columnIndex, and sortOrder.
     * @param userId user id
     * @param columnIndex column index
     * @param sortOrder sort order
     */
    public UserSorting(int userId, int columnIndex, String sortOrder) {
        this.userId = userId;
        this.columnIndex = columnIndex;
        this.sortOrder = sortOrder;
    }

    /**
     * Returns the user id.
     * @return user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     * @param userId new user id
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns the column index.
     * @return column index
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * Sets the column index.
     * @param columnIndex new column index
     */
    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * Returns the sort order.
     * 'asc' for ascending and 'desc' for descending.
     * @return sort order
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the sort order.
     * 'asc' for ascending and 'desc' for descending.
     * @param sortOrder new sort order
     */
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
