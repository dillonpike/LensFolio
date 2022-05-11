package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private Project project;

    @BeforeEach
    void setUp() {
        Date start = new Date();
        Date end = new Date();
        project = new Project("Testing","This is testing", start, end);
    }


    @Test
    void stringToDate() {
        Date date = new Date();
        date.setTime(-43200000);
        String stringDate = "01/Jan/1970";
        Date testDate = Project.stringToDate(stringDate);
        assertEquals(date, testDate);
    }

    @Test
    void dateToString() {
        Date date = new Date();
        date.setTime(-43200000);
        String expectedStringDate = "01/Jan/1970";
        String testDate = Project.dateToString(date);
        assertEquals(expectedStringDate, testDate);
    }
}