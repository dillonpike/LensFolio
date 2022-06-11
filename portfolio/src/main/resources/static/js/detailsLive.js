let stompClient = null;
let toast1 = null;
let toast2 = null;
let toast3 = null;
let selectedDate = (new Date(Date.now())).valueOf();

const EventType = "Event";
const DeadlineType = "Deadline";

/**
 * Toast object that holds its title and the users' username, first and last name.
 */
class Toast {
    toast;
    toastBodyTextVar;
    titleName = "";
    bodyText = "";
    hasBeenSaved = false;
    selectedDate = (new Date(Date.now())).valueOf();
    isHidden = true;
    type = "";

    id = "";
    id_number = -1;
    name = "";
    username = "";
    firstName = "";
    lastName = "";

    constructor(type, name, id, username, firstName, lastName, hasBeenSaved) {
        this.hasBeenSaved = hasBeenSaved;
        this.name = name;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        if (type === EventType) {
            this.titleName = "Event Activity";
        } else if (type === DeadlineType) {
            this.titleName = "Deadline Activity";
        } else {
            this.titleName = "Activity";
        }
        this.id = type.toLowerCase() + "_" + username + "_" + id;
        this.id_number = id;
    }

    get username() {
        return this.username
    }
    get firstName() {
        return this.firstName;
    }
    get lastName() {
        return this.lastName;
    }
    get titleName() {
        return this.titleName;
    }
    get hasBeenSaved() {
        return this.hasBeenSaved;
    }
    get id() {
        return this.id;
    }
    get id_number() {
        return this.id_number;
    }
    get type() {
        return this.type;
    }

    set username(username) {
        this.username = username;
    }
    set firstName(firstName) {
        this.firstName = firstName;
    }
    set lastName(lastName) {
        this.lastName = lastName;
    }
    set name(name) {
        this.name = name;
    }

    show = function () {
        this.isHidden = false;
        this.selectedDate = (new Date(Date.now())).valueOf();
        if (!this.hasBeenSaved) {
            this.bodyText = "'" + this.name + "' is being edited by " +
                this.firstName + " " + this.lastName + " (" + this.username + ").";
        } else {
            this.bodyText = "'" + this.name + "' has been updated by " +
                this.firstName + " " + this.lastName + " (" + this.username + ").";
        }
        this.toastBodyTextVar.text(this.bodyText);
        this.toast.show();
    }

    hide = function () {
        this.isHidden = true;
        this.toast.hide();
    }

    /**
     * Hides the toast after a timer.
     * @param timeInSeconds Time in seconds for the toast to show for. Should be equal to 1 or above
     */
    hideTimed = function (timeInSeconds) {
        this.selectedDate = (new Date(Date.now())).valueOf();
        if (timeInSeconds <= 0) {
            timeInSeconds = 1;
        }
        setTimeout((thisToast) => {
            let currentTime = (new Date(Date.now())).valueOf();
            if (currentTime >= thisToast.selectedDate + ((timeInSeconds * 1000) - 50)) {
                thisToast.toast.hide();
                thisToast.isHidden = true;
            }
        }, timeInSeconds * 1000, this);
    }

    setToast = function (toast, textVar) {
        this.toast = toast;
        this.toastBodyTextVar = textVar;
        if (this.isHidden) {
            toast.hide();
        } else {
            this.show();
        }
    }

    updateToast = function (newToast) {
        this.name = newToast.name
        this.hasBeenSaved = newToast.hasBeenSaved;

        return this;
    }
}

Toast.prototype.toString = function () {
    return this.id + ": " + this.name;
}

let listOfToasts = [];
let listOfHTMLToasts = [];

function addToast(newToast) {
    let returnedToast = newToast;

    let toastExists = false;
    let toastIndex = -1;
    let count = 0;
    for (let item in listOfToasts) {
        if (listOfToasts[count].id === newToast.id) {
            toastExists = true;
            toastIndex = count;
            break;
        }
        count += 1;
    }
    if (toastExists) {
        returnedToast = listOfToasts[toastIndex].updateToast(newToast);
    } else {
        listOfToasts.push(newToast)
        while (listOfToasts.length > 3) {
            listOfToasts.shift();
        }
    }
    reorderToasts();

    return returnedToast;
}

function reorderToasts() {
    let count = 0;
    for (let item in listOfToasts) {
        let toastItems = listOfHTMLToasts[count];
        let toast = listOfToasts[count];
        toast.setToast(toastItems.toast, toastItems.text);
        count += 1;
    }
}

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
            const eventResponse = JSON.parse(eventResponseArg.body);
            showToast(eventResponse.eventName, eventResponse.eventId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, false);
        });
        stompClient.subscribe('/events/stop-being-edited', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            showToast(eventResponse.eventName, eventResponse.eventId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, true);
        })
        stompClient.subscribe('/events/save-edit', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            refreshEvents();
            showToastSave(eventResponse.eventName, eventResponse.eventId, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName);
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
 * @param hide Whether the toast should be hidden or not
 */
function showToast(eventName, eventId, username, firstName, lastName, hide) {
    let newToast = new Toast("Event", eventName, eventId, username, firstName, lastName, false);

    newToast = addToast(newToast);

    if (!hide) {
        newToast.show();
    } else {
        newToast.show();
        newToast.hideTimed(5);
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
function showToastSave(eventName, eventId, username, firstName, lastName) {
    if (eventName !== "") {
        let newToast = new Toast("Event", eventName, eventId, username, firstName, lastName, true);
        newToast = addToast(newToast);
        newToast.show();
        newToast.hideTimed(5);
        // $("#popupText2").text("'" + eventName + "' has been updated by " + firstName + " " + lastName + " (" + username + "). ").hidden = false;
        // toast2.show();
        // selectedDate = (new Date(Date.now())).valueOf();
        // setTimeout(() => {
        //     let currentTime = (new Date(Date.now())).valueOf();
        //     if (currentTime >= selectedDate + 4950) {
        //         toast2.hide();
        //     }
        // }, 5000);
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
    toast1 = new bootstrap.Toast($("#liveToast"));
    toast2 = new bootstrap.Toast($("#liveToast2"));
    toast3 = new bootstrap.Toast($("#liveToast3"));
    listOfHTMLToasts = [{'toast':toast1, 'text':$("#popupText")}, {'toast':toast2, 'text':$("#popupText2")}, {'toast':toast3, 'text':$("#popupText3")}];
    connect();
    // Checks if there should be a live update, and shows a toast if needed.
    let eventInformation1 = $("#toastInformation");
    if (eventInformation1.text() !== "") {
        showToastSave($("#toastEventName").text(), $("#toastUsername").text(), $("#toastFirstName").text(), $("#toastLastName").text());
    }
    let eventInformation2 = $("#toastInformation2");
    if (eventInformation2.text() !== "") {
        showToastSave($("#toastEventName2").text(), $("#toastUsername2").text(), $("#toastFirstName2").text(), $("#toastLastName2").text());
    }
    let eventInformation3 = $("#toastInformation3");
    if (eventInformation3.text() !== "") {
        showToastSave($("#toastEventName3").text(), $("#toastUsername3").text(), $("#toastFirstName3").text(), $("#toastLastName3").text());
    }
});