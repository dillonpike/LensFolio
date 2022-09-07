

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
    console.log("worked!")
    $.get('groupSettings?groupId='+groupId).done((result) => {
        //TODO this need to be fixed as this currently doesn't work. It is assumed this will be fixed as part of the live_update_inside_settings_page
        $(`html`).replaceWith(result)
    })
}


function redirectToGroupPage(groupId) {
    if(groupId === ID){
        const url = "/groups";
        document.location.href = url;
    }
}

//TODO comment
function updateUserTables(firstGroupId, secondGroupId){
    if(ID === firstGroupId || ID === secondGroupId){
        $.get('/groups/local?groupId='+ID).done((result) => {
            $(`#table_refresh`).replaceWith(result)
            const temp = document.getElementById('table')
            $(`#table_refresh`).replaceWith(temp)
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
 * Initialises functions/injections
 */
$(function () {
    connect();
});