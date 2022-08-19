package nz.ac.canterbury.seng302.portfolio.utility;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This class test all the functionality of the EventDic class which purpose is to add event-types to a HashMap
 * and provide a single JSON list out to be used by FullCalendar.
 */
class EventDicTest {

    /**
     * Dictionary Object
     */
    private final EventDic dictionary = new EventDic();
    private Date testDate;

    @BeforeEach
    public void setUp() {
        dictionary.reset(); // Ensures the dic is empty
    }

    /**
     * This tests the ability to store and generate a JSON string for an event.
     * This also tests to ensure that the special HTML characters such as "&" are escaped.
     * This tests to ensure that an event over multiple days has all days added to the final JSON list.
     */
    @Test
    void testEventDicAddMultiDayEvent() {
        testDate = Date.from(Instant.now());
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(testDate);
        calStart.setTime(testDate);
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE,0);
        calStart.set(Calendar.SECOND,0);
        calStart.set(Calendar.MILLISECOND, 0);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(testDate);
        calEnd.add(Calendar.DAY_OF_MONTH, 5);
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);

        Event event = new Event(-1, "Test-Event & <br>", testDate, calEnd.getTime());
        dictionary.add(event);

        String JSON = dictionary.makeJSON();
        while (calStart.compareTo(calEnd) <= 0) {
            Assertions.assertTrue(JSON.contains("{title: '1', start: '"+calStart.getTime().toInstant()+"', type: 'Event', description: '<strong>Events:</strong><br>- Test-Event &amp; &lt;br&gt;'},"));
            calStart.add(Calendar.DATE, 1);
        }
    }

    /**
     * This tests the ability to store and generate a JSON string for a deadline.
     * They have to have their times change to the start of the day to display correctly on the calendar.
     * This also tests to ensure that the special HTML characters such as "&" are escaped.
     */
    @Test
    void testEventDicAddDeadline() {
        testDate = Date.from(Instant.now());

        Deadline deadline = new Deadline(-1, "Test-Deadline & <br>", testDate);
        dictionary.add(deadline);
        String testDateString = updateDateString(deadline.getDeadlineDate());
        String expectJSON = "{title: '1', start: '"+testDateString+"', type: 'Deadline', description: '<strong>Deadlines:</strong><br>- Test-Deadline &amp; &lt;br&gt;'},";
        Assertions.assertEquals(expectJSON, dictionary.makeJSON());
    }

    /**
     * This tests the ability to store and generate a JSON string for a milestone.
     * They have to have their times change to the start of the day to display correctly on the calendar.
     * This also tests to ensure that the special HTML characters such as "&" are escaped.
     */
    @Test
    void testEventDicAddMilestone() {
        testDate = Date.from(Instant.now());
        Milestone milestone = new Milestone(-1, "Test-Milestone & <br>", testDate);
        dictionary.add(milestone);
        String testDateString = updateDateString(milestone.getMilestoneDate());
        String expectJSON = "{title: '1', start: '"+testDateString+"', type: 'Milestone', description: '<strong>Milestones:</strong><br>- Test-Milestone &amp; &lt;br&gt;'},";
        Assertions.assertEquals(expectJSON, dictionary.makeJSON());
    }

    /**
     * This tests to ensure that all objects are hashed correctly so that the final JSON list separate all events based on date and type.
     */
    @Test
    void testEventDicAddOneOfEachEventType() {
        testDate = Date.from(Instant.now());
        Event event = new Event(-1, "Test-Event & <br>", testDate, testDate);
        dictionary.add(event);
        Milestone milestone = new Milestone(-1, "Test-Milestone & <br>", testDate);
        dictionary.add(milestone);
        Deadline deadline = new Deadline(-1, "Test-Deadline & <br>", testDate);
        dictionary.add(deadline);


        ArrayList<String> events = new ArrayList<>();

        String deadlineDateString = updateDateString(deadline.getDeadlineDate());
        events.add("{title: '1', start: '"+deadlineDateString+"', type: 'Deadline', description: '<strong>Deadlines:</strong><br>- Test-Deadline &amp; &lt;br&gt;'},");
        String milestoneDateString = updateDateString(milestone.getMilestoneDate());
        events.add("{title: '1', start: '"+milestoneDateString+"', type: 'Milestone', description: '<strong>Milestones:</strong><br>- Test-Milestone &amp; &lt;br&gt;'},");

        Calendar cal = Calendar.getInstance();
        cal.setTime(event.getEventStartDate());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND, 0);
        Instant eventDate = cal.toInstant();
        events.add("{title: '1', start: '"+eventDate+"', type: 'Event', description: '<strong>Events:</strong><br>- Test-Event &amp; &lt;br&gt;'},");

        String JSON = dictionary.makeJSON();

        for (String eventString : events) {
            Assertions.assertTrue(JSON.contains(eventString));
        }
    }

    /**
     * This is to test that an event-type can have multiple events of that type on a single day.
     * The JSON list should also reflect that in its title.
     */
    @Test
    void testEventDicAddTwoDeadlinesWithSameDateButDifferentTimes() {
        testDate = Date.from(Instant.now());
        Deadline deadline1 = new Deadline(-1, "Test-Deadline-1", testDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(testDate);
        // Adds 5 minutes to the time but if the time would increase the hours which may lead to it increasing
        // the day value its value is set to the start of the minute.
        if (cal.get(Calendar.MINUTE) >= 55) {
            cal.set(Calendar.MINUTE, 0);
        } else {
            cal.add(Calendar.MINUTE, 5);
        }
        testDate = cal.getTime();
        Deadline deadline2 = new Deadline(-1, "Test-Deadline-2", testDate);

        dictionary.add(deadline1);
        dictionary.add(deadline2);
        String testDateString = updateDateString(deadline1.getDeadlineDate());
        // Title is 2 as there are 2 deadlines added.
        String expectJSON = "{title: '2', start: '"+testDateString+"', type: 'Deadline', description: '<strong>Deadlines:</strong><br>- Test-Deadline-1<br>- Test-Deadline-2'},";
        Assertions.assertEquals(expectJSON, dictionary.makeJSON());
    }

    public String updateDateString(Date date) {
        // Changes the date to the start of the day so that it is added to the correct date.
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND, 0);
        return df.format(cal.getTime());
    }

}
