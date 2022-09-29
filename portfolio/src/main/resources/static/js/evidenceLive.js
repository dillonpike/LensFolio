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
    stompClient.connect({}, function () {
        stompClient.subscribe('/webSocketGet/evidence-added', function (eventResponseArg) {
            let url = "account?userId=" + document.getElementById('userId').value
            setTimeout(function() {
                $("#evidence").load(url+" #evidence>*","");
            }, 10);
        });
        stompClient.subscribe('/webSocketGet/evidence-deleted', function (eventResponseArg) {
            let url = "account?userId=" + document.getElementById('userId').value
            setTimeout(function() {
                $("#evidence").load(url+" #evidence>*","");
            }, 10);
        });
    });
}

/**
 * Sends a notification to the websocket endpoint to notify the server that a new piece of evidence has been added.
 * This is so other users' pages can be updated.
 */
function sendAddEvidenceNotification() {
    const type = roles.includes("STUDENT") ? 'studentEvidence' : 'nonStudentEvidence';
    stompClient.send("/webSocketPost/evidence-add", {}, JSON.stringify({
        'artefactName': $("#evidenceTitle").val(),
        'artefactId': 1,
        'userId': 1,
        'username': $("#username").text(),
        'userFirstName': $("#firstName").val(),
        'userLastName': $("#lastNameInput").val(),
        'artefactType': type
    }));
}

/**
 * Sends a notification to the websocket endpoint to notify the server that a new piece of evidence has been added.
 * This is so other users' pages can be updated.
 */
function sendDeleteEvidenceNotification() {
    const type = roles.includes("STUDENT") ? 'studentEvidence' : 'nonStudentEvidence';
    stompClient.send("/webSocketPost/evidence-delete", {}, JSON.stringify({
        'artefactName': $("#evidenceTitle").val(),
        'artefactId': 1,
        'userId': 1,
        'username': $("#username").text(),
        'userFirstName': $("#firstName").val(),
        'userLastName': $("#lastNameInput").val(),
        'artefactType': type
    }));
}

$(function() {
    connect();
})