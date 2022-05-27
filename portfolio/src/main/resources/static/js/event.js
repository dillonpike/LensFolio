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
        // stompClient.subscribe('/events/being-edited', function (eventResponseArg) {
        //     const eventResponse = JSON.parse(eventResponseArg.body)
        //     showToast(eventResponse.eventName, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName);
        // });
        // stompClient.subscribe('/events/save-edit', function (eventResponseArg) {
        //     const eventResponse = JSON.parse(eventResponseArg.body)
        //     setTimeout(() => {showToastSave(eventResponse.eventName, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName);}, 5000);
        //     showToastSave("", "", "", "")
        // });
    });
}

/**
 * Sends the message that an event is being updated, and sends the events names with it.
 */
function sendName() {
    stompClient.send("/app/editing-event", {}, JSON.stringify({'eventName': $("#eventName").val(), 'userId': $("#userId").val()}));
}

/**
 * Sends an empty message so that the other end can realise to close any popups it may have activated.
 */
function sendEmpty() {
    stompClient.send("/app/editing-event", {}, JSON.stringify({'eventName': "", 'userId': $("#userId").val()}));
}

function sendNameReload() {
    stompClient.send("/app/saved-edited-event", {}, JSON.stringify({'eventName': $("#eventName").val(), 'userId': $("#userId").val()}))
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
    $( "#saveButton").click(function() { sendNameReload(); })
    $( "#cancelButton").click(function() { sendEmpty(); })
});