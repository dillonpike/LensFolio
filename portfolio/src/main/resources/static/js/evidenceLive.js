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