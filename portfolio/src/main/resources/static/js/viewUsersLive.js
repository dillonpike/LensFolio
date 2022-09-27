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
}


function sendRemoveStudentRoleNotification(firstName, lastName, username) {
    stompClient.send("/webSocketPost/delete-student-role", {}, JSON.stringify({
        'artefactName': $("#evidenceTitle").val(),
        'artefactId': 1,
        'userId': 1,
        'username': username,
        'userFirstName': firstName,
        'userLastName': lastName,
        'artefactType': "Role"
    }));
}

$(function() {
    connect();
})