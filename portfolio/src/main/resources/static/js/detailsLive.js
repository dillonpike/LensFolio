// Make sure to import Notification.js before this file in your HTML.

const NUM_OF_TOASTS = 3;

let stompClient = null;
let toast1 = null;
let toast2 = null;
let toast3 = null;


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
 * Adds Notification objects to the listOfNotifications list if it is new, otherwise updates the existing notification.
 * Then reassigns the toast html objects to the new list.
 * @param newNotification New toast object to add/update to the list.
 * @returns {Notification} updated toast if it already existed, otherwise, returns the parameter 'newToast'.
 */
function addNotification(newNotification) {
    let returnedNotification = newNotification;

    let notificationExists = false;
    let notificationIndex = -1;
    let count = 0;
    for (let item in listOfNotifications) {
        if (listOfNotifications[count].id === newNotification.id) {
            notificationExists = true;
            notificationIndex = count;
            break;
        }
        count += 1;
    }
    if (notificationExists) {
        returnedNotification = listOfNotifications[notificationIndex].updateNotification(newNotification);
    } else {
        listOfNotifications.push(newNotification)
        while (listOfNotifications.length > listOfHTMLToasts.length) {
            listOfNotifications.shift();
        }
    }
    reorderNotifications();
    return returnedNotification;
}

/**
 * Reassigns toast html objects to the toast objects that are active at the moment (in the list 'listOfToasts')
 */
function reorderNotifications() {
    let count = 0;
    for (let item in listOfHTMLToasts) {
        listOfHTMLToasts[count].toast.hide();
        count += 1;
    }
    count = 0;
    for (let item in listOfNotifications) {
        let toastItems = listOfHTMLToasts[count];
        let notification = listOfNotifications[count];
        notification.setToast(toastItems.toast, toastItems.text, toastItems.title);
        count += 1;
    }
}

/**
 * Connects the stomp client to the setup websocket endpoint.
 * Then subscribes methods to the required endpoints.
 */
function connect() {
    let socket = new SockJS('mywebsockets');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/webSocketGet/being-edited', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            showToast(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, false, eventResponse.artefactType);
        });
        stompClient.subscribe('/webSocketGet/stop-being-edited', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            showToast(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, true, eventResponse.artefactType);
        })
        stompClient.subscribe('/webSocketGet/save-edit', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            refreshEvents();

            showToastSave(eventResponse.artefactName, eventResponse.artefactId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, eventResponse.artefactType);

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
    let newNotification = new Notification(type, eventName, eventId, username, firstName, lastName, false);
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
 */
function showToastSave(eventName, eventId, username, firstName, lastName, type) {
    let newNotification = new Notification(type, eventName, eventId, username, firstName, lastName, true);
    newNotification = addNotification(newNotification);
    newNotification.show();
    newNotification.hideTimed(SECONDS_TILL_HIDE);
}

/**
 * Refresh the DOM after some delay, to account for the saving function completing.
 */
function refreshEvents() {
    setTimeout(() => {
        document.location.reload();
    }, 100);
}

/**
 * Initialises functions/injections
 */
$(function () {

    toast1 = new bootstrap.Toast($("#liveToast1"));
    toast2 = new bootstrap.Toast($("#liveToast2"));
    toast3 = new bootstrap.Toast($("#liveToast3"));
    listOfHTMLToasts = [{'toast':toast1, 'text':$("#popupText1"), 'title':$("#toastTitle1")}, {'toast':toast2, 'text':$("#popupText2"), 'title':$("#toastTitle2")}, {'toast':toast3, 'text':$("#popupText3"), 'title':$("#toastTitle3")}];
    connect();
    // Checks if there should be a live update, and shows a toast if needed.
    let eventInformation1 = $("#toastInformation1");
    if (eventInformation1.text() !== "") {
        showToastSave($("#toastArtefactName1").text(), $("#toastArtefactId1").text(), $("#toastUsername1").text(), $("#toastFirstName1").text(), $("#toastLastName1").text(), eventInformation1.text());
    }
    let eventInformation2 = $("#toastInformation2");
    if (eventInformation2.text() !== "") {
        showToastSave($("#toastArtefactName2").text(), $("#toastArtefactId2").text(), $("#toastUsername2").text(), $("#toastFirstName2").text(), $("#toastLastName2").text(), eventInformation1.text());
    }
    let eventInformation3 = $("#toastInformation3");
    if (eventInformation3.text() !== "") {
        showToastSave($("#toastArtefactName3").text(), $("#toastArtefactId3").text(), $("#toastUsername3").text(), $("#toastFirstName3").text(), $("#toastLastName3").text(), eventInformation1.text());
    }
});