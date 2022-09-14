/* Make sure to import Notification.js before this file in your HTML. */
/** ************ Notification.js REQUIRED TO RUN THIS FILE ************ */

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
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/webSocketGet/group-being-edited', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            showToast(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, false, eventResponse.artefactType);
        });
        stompClient.subscribe('/webSocketGet/group-stop-being-edited', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            showToast(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, true, eventResponse.artefactType);
        })
        stompClient.subscribe('/webSocketGet/group-save-edit', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            updateGroupList(eventResponse.artefactId, "save");
            showToastSave(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, eventResponse.artefactType, eventResponse.action);
        });
        stompClient.subscribe('/webSocketGet/group-delete', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            updateGroupList(eventResponse.artefactId, "delete");
            showToastSave(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, eventResponse.artefactType, eventResponse.action);
        });
        stompClient.subscribe('/webSocketGet/group-add', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            updateGroupList(eventResponse.artefactId, "add");
            showToastSave(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, eventResponse.artefactType, eventResponse.action);
        });
        stompClient.subscribe('/webSocketGet/group-change-users', function (twoGroupResponseArg) {
            const twoGroupResponse = JSON.parse(twoGroupResponseArg.body);
            updateGroupList(twoGroupResponse.sendingGroupId, "change-users-send");
            updateGroupList(twoGroupResponse.receivingGroupId, "change-users-receive");
        });
        stompClient.subscribe('/webSocketGet/saved-group-settings-outside', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            updateGroupList(eventResponse.sendingGroupId, "save");
        });

    });
}

/**
 * Function that is called when a message is sent to the endpoint. Shows the notification/toast if the message is full.
 * Removes the notification/toast if 'hide' is true after delay.
 * This function is used when the 'update' is that it is being edited, rather than saved.
 * @param groupName Message that may or may not be empty.
 * @param groupId Group id of the event being edited.
 * @param username Username of the user making the change
 * @param firstName First name of the user
 * @param lastName Last name of the user
 * @param hide Whether the toast should be hidden or not
 * @param type they type of the artefact it is either Milestone, Deadline, or event
 */
function showToast(groupName, groupId, username, firstName, lastName, hide, type) {
    let newNotification = new Notification(type, groupName, groupId, username, firstName, lastName, "edit");
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
 * @param groupName Message that may or may not be empty.
 * @param groupId Group id of the event being edited.
 * @param username Username of the user making the change
 * @param firstName First name of the user
 * @param lastName Last name of the user
 * @param type type of artefact
 * @param action Action of the notification
 */
function showToastSave(groupName, groupId, username, firstName, lastName, type, action) {
    let newNotification = new Notification(type, groupName, groupId, username, firstName, lastName, action);
    newNotification = addNotification(newNotification);
    newNotification.show();
    newNotification.hideTimed(SECONDS_TILL_HIDE);
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
    // This is needed in coordination with the relevant controller method for after reloading the page.
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