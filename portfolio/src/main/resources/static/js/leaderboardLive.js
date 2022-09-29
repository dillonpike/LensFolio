/**
 * Number of toasts generated and can be created at one time. Must be more or equal to NUM_OF_TOASTS in DetailsController.java.
 * @type {number}
 */
const NUM_OF_TOASTS = 3;

/**
 * Stores the stomp client to connect to and send to for WebSockets/SockJS.
 */
let stompClient = null;

/**
 * Holds a list of Notification objects that are, or have been active. Can only be as long as listOfHTMLToasts.
 * @type {[Notification]}
 */
let listOfNotifications = [];

/**
 * List of html toast object pairs that hold a Bootstrap toast object, a body text variable and a title text variable.
 * These can be assigned to Notification objects to display them.
 * @type {[{'toast', 'text', 'title'}]}
 */
let listOfHTMLToasts = [];


/**
 * Connects the stomp client to the setup websocket endpoint.
 * Then subscribes methods to the required endpoints.
 */
function connect() {
    let socket = new SockJS('mywebsockets');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;
    stompClient.connect({}, function () {
        stompClient.subscribe('/webSocketGet/evidence-added', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body)
            if (eventResponse.artefactType === "studentEvidence") {
                const notification = showLeaderboardUpdateToast("Evidence", eventResponse.artefactName, eventResponse.artefactId,
                    eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, ADDEVIDENCEACTION);
                updateLeaderboard(notification, ADDEVIDENCEACTION);
                    eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, ADDEVIDENCEACTION);
                updateLeaderboard(notification, ADDEVIDENCEACTION);
            }
        });
        stompClient.subscribe('/webSocketGet/evidence-deleted', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body)
            if (eventResponse.artefactType === "studentEvidence") {
                const notification = showLeaderboardUpdateToast("Evidence", eventResponse.artefactName, eventResponse.artefactId,
                    eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, DELETEEVIDENCEACTION);
                updateLeaderboard(notification, DELETEEVIDENCEACTION);
            }
        });
        stompClient.subscribe('/webSocketGet/delete-student-role', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body)
            const notification = showLeaderboardUpdateToast(eventResponse.artefactType, eventResponse.artefactName, eventResponse.artefactId,
                eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, DELETEROLEACTION);
            updateLeaderboard(notification, DELETEROLEACTION);
        });
        stompClient.subscribe('/webSocketGet/add-student-role', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body)
            const notification = showLeaderboardUpdateToast(eventResponse.artefactType, eventResponse.artefactName, eventResponse.artefactId,
                eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, ADDROLEACTION);
            updateLeaderboard(notification, ADDROLEACTION);
        });
    });
}

$(function() {
    // Generate list of HTML toasts.
    for (let i = 0; i < NUM_OF_TOASTS; i++) {
        let toastString = "#liveToast" + (i+1);
        let popupTextString = "#popupText" + (i+1);
        let toastTitleString = "#toastTitle" + (i+1);
        listOfHTMLToasts.push({'toast':new bootstrap.Toast($(toastString)), 'text':$(popupTextString), 'title':$(toastTitleString)})
    }
    connect();
})

/**
 * Creates a notification and displays it to the user in a toast.
 * @param type type of notification
 * @param evidenceName name of evidence
 * @param evidenceId id of evidence
 * @param username username of user who added evidence
 * @param firstName first name of user who added evidence
 * @param lastName last name of user who added evidence
 * @param operation operation performed on evidence, such as add or delete
 */
function showLeaderboardUpdateToast(type, evidenceName, evidenceId, username, firstName, lastName, operation) {
    let newNotification = new Notification(type, evidenceName, evidenceId, username, firstName, lastName, operation);
    newNotification = addNotification(newNotification, listOfNotifications, listOfHTMLToasts);
    newNotification.show();
    newNotification.hideTimed(SECONDS_TILL_HIDE);
    return newNotification;
}