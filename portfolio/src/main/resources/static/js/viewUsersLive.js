/**
 * Stores the stomp client to connect to and send to for WebSockets/SockJS.
 */
let stompClient = null;


/**
 * Connects the stomp client to the setup websocket endpoint.
 * @param firstName user's first name
 * @param lastName user's last name
 * @param username user's username
 */
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