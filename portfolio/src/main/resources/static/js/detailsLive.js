let stompClient = null;

/**
 * Connects the stomp client to the setup websocket endpoint.
 * Then subscribes a method to the events/being-edited endpoint.
 */
function connect() {
    let socket = new SockJS('/mywebsockets');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/events/being-edited', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body)
            showToast(eventResponse.eventName, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName);
        });
        stompClient.subscribe('/events/stop-being-edited', function (ignore) {
            showToast("", "", "", "");
        })
        stompClient.subscribe('/events/save-edit', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body)
            showToastSave(eventResponse.eventName, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName);
        });
    });
}

/**
 * Function that is called when a message is sent to the endpoint. Shows the toast if the message is full.
 * Removes the toast if the message is empty.
 * @param eventName Event message that may or may not be empty.
 * @param username Username of the user making the change
 * @param firstName First name of the user
 * @param lastName Last name of the user
 */
function showToast(eventName, username, firstName, lastName) {
    if (eventName !== "") {
        const toast = new bootstrap.Toast($("#liveToast"));
        toast.autohide = true;
        toast.delay = 5000;
        $("#popupText").text("'" + eventName + "' is being edited by " + firstName + " " + lastName + " (" + username + ").").hidden = false;
        toast.show();
    } else {
        const toast = $("#liveToast")
        $("#popupText").text("").hidden = true;
        toast.hide();
    }
}

/**
 * Function that is called when a message is sent to the endpoint. Shows the toast if the message is full.
 * Removes the toast if the message is empty. This function displays that an event has been updated.
 * @param eventName Event message that may or may not be empty.
 * @param username Username of the user making the change
 * @param firstName First name of the user
 * @param lastName Last name of the user
 */
function showToastSave(eventName, username, firstName, lastName) {
    if (eventName !== "") {
        const toast = new bootstrap.Toast($("#liveToast"));
        toast.autohide = true;
        toast.delay = 5000;
        $("#popupText").text("'" + eventName + "' has been updated by " + firstName + " " + lastName + " (" + username + ").").hidden = false;
        toast.show();
    } else {
        const toast = $("#liveToast")
        $("#popupText").text("").hidden = true;
        toast.hide();
    }
}

/**
 * Initialises functions/injections
 */
$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    connect();
});