/* Make sure to import Notification.js before this file in your HTML. */
/** ************ Notification.js REQUIRED TO RUN THIS FILE ************ */

/**
 * Number of toasts generated and can be created at one time. Must be more or equal to NUM_OF_TOASTS in DetailsController.java.
 * @type {number}
 */
const NUM_OF_TOASTS = 3;

/**
 * Number of milliseconds to wait for artefacts to save to the database.
 * @type {number}
 */
const SAVE_TIME = 1000;

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
        stompClient.subscribe('/webSocketGet/being-edited', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            showToast(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, false, eventResponse.artefactType);
        });
        stompClient.subscribe('/webSocketGet/stop-being-edited', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            showToast(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, true, eventResponse.artefactType);
        });
        stompClient.subscribe('/webSocketGet/artefact-save', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            refreshEvents();
            showToastSave(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, eventResponse.artefactType, SAVEACTION);
        });
        stompClient.subscribe('/webSocketGet/artefact-add', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            refreshEvents();
            showToastSave(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, eventResponse.artefactType, ADDACTION);
        });
        stompClient.subscribe('/webSocketGet/artefact-delete', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            refreshEvents();
            showToastSave(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, eventResponse.artefactType, DELETEACTION);
        });

    });
}

/**
 * Function that is called when a message is sent to the endpoint. Shows the notification/toast if the message is full.
 * Removes the notification/toast if 'hide' is true after delay.
 * This function is used when the 'update' is that it is being edited, rather than saved.
 * @param eventName Event message that may or may not be empty.
 * @param eventId Event id of the event being edited.
 * @param username Username of the user making the change
 * @param firstName First name of the user
 * @param lastName Last name of the user
 * @param hide Whether the toast should be hidden or not
 * @param type they type of the artefact it is either Milestone, Deadline, or event
 */
function showToast(eventName, eventId, username, firstName, lastName, hide, type) {
    let newNotification = new Notification(type, eventName, eventId, username, firstName, lastName, EDITACTION);
    newNotification = addNotification(newNotification);
    if (!hide) {
        newNotification.show();
    } else {
        newNotification.hideTimed(SECONDS_TILL_HIDE);
    }
}

/**
 * Function that is called when a message is sent to the endpoint. Shows the notification/toast for a certain period.
 * This function is used when the 'update' is that it has been saved, rather than being updated.
 * @param eventName Event message that may or may not be empty.
 * @param eventId Event id of the event being edited.
 * @param username Username of the user making the change
 * @param firstName First name of the user
 * @param lastName Last name of the user
 * @param type type of artefact
 * @param action Action that the artefact has done. Can be 'save', 'add', or 'delete'.
 */
function showToastSave(eventName, eventId, username, firstName, lastName, type, action) {
    let newNotification = new Notification(type, eventName, eventId, username, firstName, lastName, action);
    newNotification = addNotification(newNotification);
    newNotification.show();
    newNotification.hideTimed(SECONDS_TILL_HIDE);
}

/**
 * Refresh the DOM after some delay if all modals are closed. Otherwise, set DOM to refresh when this modal is closed,
 * a minimum of SAVE_TIME milliseconds after this function is called.
 */
function refreshEvents() {
    if (isModalOpen()) {
        const modal = getOpenModal();
        modal.addEventListener('hide.bs.modal', reloadAfterDelay);
        setTimeout(() => {
            modal.addEventListener('hide.bs.modal', () => {document.location.reload();})
            modal.removeEventListener('hide.bs.modal', reloadAfterDelay);
        }, SAVE_TIME)
    } else {
        reloadAfterDelay();
    }
}

/**
 * Refresh the DOM after some delay, to account for the saving function completing.
 */
function reloadAfterDelay() {
    setTimeout(() => {
        document.location.reload();
    }, SAVE_TIME);
}

/**
 * Initialises functions/injections
 */
$(function () {

    // Generate list of HTML toasts.
    for (let i = 0; i < NUM_OF_TOASTS; i++) {
        let toastString = "#liveToast" + (i+1);
        let popupTextString = "#popupText" + (i+1);
        let toastTitleString = "#toastTitle" + (i+1);
        listOfHTMLToasts.push({'toast':new bootstrap.Toast($(toastString)), 'text':$(popupTextString), 'title':$(toastTitleString)})
    }

    connect();

    // Checks if there should be a live update, and shows a toast if needed.
    for (let i = 0; i < NUM_OF_TOASTS; i++) {
        let toastInformationString = "#toastInformation" + (i+1);
        let toastArtefactNameString = "#toastArtefactName" + (i+1);
        let toastArtefactIdString = "#toastArtefactId" + (i+1);
        let toastUsernameString = "#toastUsername" + (i+1);
        let toastFirstNameString = "#toastFirstName" + (i+1);
        let toastLastNameString = "#toastLastName" + (i+1);
        let toastAction = "#toastAction" + (i+1);
        let artefactInformation = $(toastInformationString);
        if (artefactInformation.text() !== "") {
            showToastSave($(toastArtefactNameString).text(), $(toastArtefactIdString).text(), $(toastUsernameString).text(), $(toastFirstNameString).text(), $(toastLastNameString).text(), artefactInformation.text(), $(toastAction).text());
        }
    }
});