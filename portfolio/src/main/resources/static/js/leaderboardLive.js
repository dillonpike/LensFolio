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
    // stompClient.debug = null;
    stompClient.connect({}, function () {
        stompClient.subscribe('/webSocketGet/evidence-added', function (eventResponseArg) {
            console.log("Received notification!")
            const eventResponse = JSON.parse(eventResponseArg.body);
            showLeaderboardUpdateToast(eventResponse.type, eventResponse.artefactName, eventResponse.artefactId, eventResponse.username);
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

function showLeaderboardUpdateToast(type, evidenceName, evidenceId, userFullName) {
    let newNotification = new Notification(type, evidenceName, evidenceId, userFullName, "", "", ADDACTION);
    newNotification = addNotification(newNotification);
    newNotification.show();
    newNotification.hideTimed(SECONDS_TILL_HIDE);
}