

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
    });
}

function updateSettingsDisplayed(groupId) {
    $.get('groupSetting').done((result) => {
        $(`#html`).replaceWith(result)
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
}


/**
 * Initialises functions/injections
 */
$(function () {
    connect();
});