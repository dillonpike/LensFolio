let stompClient = null;
/**
 * Connects the stomp client to the setup websocket endpoint.
 * Then subscribes a method to the events/being-edited endpoint.
 */
function connect() {
    let socket = new SockJS('wss://csse-s302g1.canterbury.ac.nz/test/portfolio/mywebsockets');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
    });
}

/**
 * Sends the message that an event is being updated, and sends the events names with it.
 */
function sendName(artefactName, artefactId,userId, userFirstName, userLastName, username, artefactType) {
    stompClient.send("/test/portfolio/app/editing-artefact", {}, JSON.stringify({
        'artefactName': artefactName,
        'artefactId' : artefactId,
        'artefactType' : artefactType,
        'userId': userId,
        'userFirstName': userFirstName,
        'userLastName': userLastName,
        'username': username
    }));
}

/**
 * Sends an empty message so that the other end can realise to close any popups it may have activated.
 */
function sendEmpty(artefactName, artefactId,userId, userFirstName, userLastName, username, artefactType) {
    stompClient.send("/test/portfolio/app/stop-editing-artefact", {}, JSON.stringify({
        'artefactName': artefactName,
        'artefactId' : artefactId,
        'userId': userId,
        'artefactType' : artefactType,
        'userFirstName': userFirstName,
        'userLastName': userLastName,
        'username': username
    }));
}

/**
 * Sends the message that an event has been updated (saved), and sends the events names with it.
 */
function sendNameReload(artefactName, artefactId,userId, userFirstName, userLastName, username, artefactType) {
    stompClient.send("/test/portfolio/app/saved-edited-artefact", {}, JSON.stringify({
        'artefactName': artefactName,
        'artefactId' : artefactId,
        'userId': userId,
        'artefactType' : artefactType,
        'userFirstName': userFirstName,
        'userLastName': userLastName,
        'username': username
    }));
}