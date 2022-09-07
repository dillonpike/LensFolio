

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
    });
}

function updateSettingsDisplayed(groupId) {
    console.log("worked!")
    $.get('groupSettings?groupId='+groupId).done((result) => {
        //TODO this need to be fixed as this currently doesn't work. It is assumed this will be fixed as part of the live_update_inside_settings_page
        $(`html`).replaceWith(result)
    })
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
 * Initialises functions/injections
 */
$(function () {
    connect();
});