/**
 * Saves the given sprint in the database.
 */
function saveSprint(sprint) {
    const data = {
        id: sprint.id,
        sprintStartDate: sprint.startStr,
        sprintEndDate: sprint.endStr
    }
    $.post("update-sprint?" + new URLSearchParams(data))
}