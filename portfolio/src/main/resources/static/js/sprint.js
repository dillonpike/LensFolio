/**
 * Saves the given sprint in the database and displays the alert banner to notify the user.
 */
function saveSprint(sprint) {
    const data = {
        id: sprint.id,
        sprintStartDate: sprint.startStr,
        sprintEndDate: sprint.endStr
    }
    $.post("update-sprint?" + new URLSearchParams(data)).done(function (response) {
        if (response) {
            showAlertBanner(`${sprint.title} updated`);
        }
    });
}