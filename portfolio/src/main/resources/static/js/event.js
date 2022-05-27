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
            showToast(eventResponse.eventName);
        });
    });
}

/**
 * Sends the message that an event is being updated, and sends the events names with it.
 */
function sendName() {
    stompClient.send("/app/editing-event", {}, JSON.stringify({'eventName': $("#eventName").val(), 'eventId': $("#eventId").val()}));
}

/**
 * Sends an empty message so that the other end can realise to close any popups it may have activated.
 */
function sendEmpty() {
    stompClient.send("/app/editing-event", {}, JSON.stringify({'eventName': "", 'eventId': $("#sprintId").val()}));
}

/**
 * Function that is called when a message is sent to the endpoint. Shows the toast if the message is full.
 * Removes the toast if the message is empty.
 * @param eventName Event message that may or may not be empty.
 */
function showToast(eventName) {
    const toast = new bootstrap.Toast($("#liveToast"));
    toast.autohide = false;
    if (eventName !== "") {
        $("#popupText").text(eventName + " is being edited.").hidden = false;
        toast.show();
    } else {
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
    $( "#send" ).click(function() { sendName(); });
    $( "#saveButton").click(function() { sendEmpty(); })
    $( "#cancelButton").click(function() { sendEmpty(); })
});