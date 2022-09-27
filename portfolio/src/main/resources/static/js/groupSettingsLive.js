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
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/webSocketGet/group-settings-saved', function (GroupSettingsResponseArg) {
            const GroupNotificationResponse = JSON.parse(GroupSettingsResponseArg.body);
            updateSettingsDisplayed(GroupNotificationResponse.sendingGroupId);
        });
        stompClient.subscribe('/webSocketGet/outside-group-settings-saved', function (GroupSettingsResponseArg) {
            const GroupNotificationResponse = JSON.parse(GroupSettingsResponseArg.body);
            updateSettingsDisplayed(GroupNotificationResponse.sendingGroupId);
        });
        stompClient.subscribe('/webSocketGet/group-delete', function (EventResponseArg) {
            const eventResponse = JSON.parse(EventResponseArg.body);
            redirectToGroupPage(eventResponse.artefactId);
        });
        stompClient.subscribe('/webSocketGet/group-change-users', function (twoGroupResponseArg) {
            const twoGroupResponse = JSON.parse(twoGroupResponseArg.body);
            updateUserTables(twoGroupResponse.sendingGroupId, twoGroupResponse.receivingGroupId)
        });
    });
}

function updateSettingsDisplayed(groupId) {
    $.get('groupSettings/refreshGroupSettings?groupId='+groupId).done((result) => {
        showToast(groupShortName, ID, username, userFirstName, userLastName, false, "Group");
        $('#groupSettingContainer').replaceWith(result)
        initialiseCommitsList()

    })
}

/**
 * redirects the user to the group page when a group is deleted.
 * @param groupId the id of the group that was deleted.
 */
function redirectToGroupPage(groupId) {
    if(groupId === ID){
        const url = "/groups";
        document.location.href = url;
    }
}

/**
 * This method updates the user tables on the group settings page whenever a user is added or removed from a group.
 * @param firstGroupId the id of the group that the user was removed from.
 * @param secondGroupId the id of the group that the user was added to.
 */
function updateUserTables(firstGroupId, secondGroupId){
    if(ID === firstGroupId || ID === secondGroupId){
        $.get('/getGroupMembers?groupId='+ID).done((result) => {
            $(`#table_refresh`).replaceWith(result)
        })
    }

}


/**
 * Sends the message that a group's settings has been updated, and sends the id.
 */
function sendIdRefresh(sendingGroupId) {
    stompClient.send("/webSocketPost/save-group-settings", {}, JSON.stringify({
        'sendingGroupId': sendingGroupId,
        'receivingGroupId' : sendingGroupId
    }));
    stompClient.send("/webSocketPost/save-group-settings-outside", {}, JSON.stringify({
        'sendingGroupId': sendingGroupId,
        'receivingGroupId' : sendingGroupId
    }));
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
    let newNotification = new Notification(type, groupName, groupId, username, firstName, lastName, "save");
    newNotification = addNotification(newNotification, listOfNotifications, listOfHTMLToasts);
    newNotification.show();
    newNotification.hideTimed(SECONDS_TILL_HIDE);
}

/**
 * Initialises functions/injections
 */
$(function () {

    connect();

    // Generate list of HTML toasts.
    for (let i = 0; i < NUM_OF_TOASTS; i++) {
        let toastString = "#liveToast" + (i+1);
        let popupTextString = "#popupText" + (i+1);
        let toastTitleString = "#toastTitle" + (i+1);
        listOfHTMLToasts.push({'toast':new bootstrap.Toast($(toastString)), 'text':$(popupTextString), 'title':$(toastTitleString)})
    }

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

