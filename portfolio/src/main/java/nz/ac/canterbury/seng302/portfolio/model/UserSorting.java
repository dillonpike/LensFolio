package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserSorting {


    @Id
    private int userId;

    private int columnIndex;

    private String sortOrder;

    public UserSorting() {
    }

    public UserSorting(int userId, int columnIndex, String sortOrder) {
        this.userId = userId;
        this.columnIndex = columnIndex;
        this.sortOrder = sortOrder;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
