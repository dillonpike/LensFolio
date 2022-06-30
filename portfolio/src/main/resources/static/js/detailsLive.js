let stompClient = null;
let toast = null;
let selectedDate = (new Date(Date.now())).valueOf();

/**
 * Connects the stomp client to the setup websocket endpoint.
 * Then subscribes a method to the events/being-edited endpoint.
 */
function connect() {
    let socket = new SockJS('/test/portfolio/mywebsockets');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/test/portfolio/events/being-edited', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            showToast(eventResponse.eventName, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName);
        });
        stompClient.subscribe('/test/portfolio/events/stop-being-edited', function (ignore) {
            showToast("", "", "", "");
        })
        stompClient.subscribe('/test/portfolio/events/save-edit', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            refreshEvents();
            showToastSave(eventResponse.eventName, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName);
        });
    });
}

/**
 * Function that is called when a message is sent to the endpoint. Shows the toast if the message is full.
 * Removes the toast if the message is empty after delay.
 * @param eventName Event message that may or may not be empty.
 * @param username Username of the user making the change
 * @param firstName First name of the user
 * @param lastName Last name of the user
 */
function showToast(eventName, username, firstName, lastName) {
    if (eventName !== "") {
        $("#popupText").text("'" + eventName + "' is being edited by " + firstName + " " + lastName + " (" + username + ").").hidden = false;
        toast.show();
        selectedDate = (new Date(Date.now())).valueOf();
    } else {
        setTimeout(() => {
            let currentTime = (new Date(Date.now())).valueOf();
            if (currentTime >= selectedDate + 4950) {
                toast.hide()
            }
        }, 5000);
    }
}

/**
 * Function that is called when a message is sent to the endpoint. Shows the toast if the message is full for a certain period.
 * This function displays that an event has been updated.
 * @param eventName Event message that may or may not be empty.
 * @param username Username of the user making the change
 * @param firstName First name of the user
 * @param lastName Last name of the user
 */
function showToastSave(eventName, username, firstName, lastName) {
    if (eventName !== "") {
        $("#popupText").text("'" + eventName + "' has been updated by " + firstName + " " + lastName + " (" + username + "). ").hidden = false;
        toast.show();
        selectedDate = (new Date(Date.now())).valueOf();
        setTimeout(() => {
            let currentTime = (new Date(Date.now())).valueOf();
            if (currentTime >= selectedDate + 4950) {
                toast.hide();
            }
        }, 5000);
    }
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
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    toast = new bootstrap.Toast($("#liveToast"));
    connect();
    // Checks if there should be a live update, and shows a toast if needed.
    let eventInformation = $("#toastInformation");
    if (eventInformation.text() !== "") {
        showToastSave($("#toastEventName").text(), $("#toastUsername").text(), $("#toastFirstName").text(), $("#toastLastName").text());
        showToastSave("", "", "", "");
    }
});