/**
 * Saves the given sprint in the database and displays the alert toast to notify the user.
 */
function saveSprint(sprint) {
    const data = {
        id: sprint.id,
        sprintStartDate: sprint.startStr,
        sprintEndDate: sprint.endStr
    }
    $.post("update-sprint?" + new URLSearchParams(data)).done(function (response) {
        if (response) {
            showAlertToast(`${sprint.title} updated`);
            stompClient.send("/webSocketGet/sprint-project-calendar-save", {}, "Sprint updated");
        }
    });
}

/**
 * Calls the error toast display
 */
function errorSprints(message) {
    showAlertErrorToast(message);
}