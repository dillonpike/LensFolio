
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
            console.log("Received notification!")
            // const eventResponse = JSON.parse(eventResponseArg.body);
            // showLeaderboardUpdateToast(eventResponse.type, eventResponse.artefactName, eventResponse.artefactId, eventResponse.username);
        });
    });
}


function sendAddEvidenceNotification() {
    console.log("Helllllloo?")
    stompClient.send("/webSocketSend/evidence-add", {}, JSON.stringify({
        'artefactName': $("#evidenceTitle").val(),
        'artefactId': 1,
        'username': $("#username").val(),
        'userFirstName': $("#firstName").val(),
        'userLastName': $("#lastName").val(),
        'artefactType': "Evidence"
    }));
}