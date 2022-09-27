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
            const notification = showLeaderboardUpdateToast(eventResponse.artefactType, eventResponse.artefactName, eventResponse.artefactId,
                eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName);
            updateLeaderboard(notification);
        });
        stompClient.subscribe('/webSocketGet/delete-student-role', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body)
            const notification = showLeaderboardUpdateToast(eventResponse.artefactType, eventResponse.artefactName, eventResponse.artefactId,
                eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName);
            updateLeaderboard(notification);
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
 */
function showLeaderboardUpdateToast(type, evidenceName, evidenceId, username, firstName, lastName) {
    let newNotification = new Notification(type, evidenceName, evidenceId, username, firstName, lastName, ADDEVIDENCEACTION);
    newNotification = addNotification(newNotification);
    newNotification.show();
    newNotification.hideTimed(SECONDS_TILL_HIDE);
    return newNotification;
}