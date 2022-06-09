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

    name = "";
    username = "";
    firstName = "";
    lastName = "";

    constructor(type, name, username, firstName, lastName, hasBeenSaved) {
        this.hasBeenSaved = hasBeenSaved;
        this.name = name;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        if (type === EventType) {
            this.titleName = "Event Activity";
        } else if (type === DeadlineType) {
            this.titleName = "Deadline Activity";
        } else {
            this.titleName = "Activity";
        }
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

    set username(username) {
        this.username = username;
    }
    set firstName(firstName) {
        this.firstName = firstName;
    }
    set lastName(lastName) {
        this.lastName = lastName;
    }

    show = function () {
        this.isHidden = false;
        selectedDate = (new Date(Date.now())).valueOf();
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
        if (timeInSeconds <= 0) {
            timeInSeconds = 1;
        }
        setTimeout(() => {
            let currentTime = (new Date(Date.now())).valueOf();
            if (currentTime >= this.selectedDate + (timeInSeconds * 1000) - 50) {
                this.toast.hide();
                this.isHidden = true;
            }
        }, timeInSeconds * 1000);
    }

    setToast = function (toast, textVar) {
        this.toast = toast;
        this.toastBodyTextVar = textVar;
        toast.hide();
        this.isHidden = true;
    }

}

let listOfToasts = [];

function addToast(toast) {
    listOfToasts.add(toast)
    while (listOfToasts.length > 3) {
        listOfToasts.shift();
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
            showToast(eventResponse.eventName, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, false);
        });
        stompClient.subscribe('/events/stop-being-edited', function (eventResponseArg) {
            const eventResponse = JSON.parse(eventResponseArg.body);
            showToast(eventResponse.eventName, eventResponse.username, eventResponse.userFirstName, eventResponse.userLastName, true);
        })
        stompClient.subscribe('/events/save-edit', function (eventResponseArg) {
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
 * @param hide Whether the toast should be hidden or not
 */
function showToast(eventName, username, firstName, lastName, hide) {
    let newToast = new Toast("Event", eventName, username, firstName, lastName, false);
    newToast.setToast(toast1, $("#popupText"));
    if (!hide) {
        newToast.show();
    } else {
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
function showToastSave(eventName, username, firstName, lastName) {
    if (eventName !== "") {
        let newToast = new Toast("Event", eventName, username, firstName, lastName, true);
        newToast.setToast(toast1, $("#popupText"));
        newToast.showTimed(5);
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
    connect();
    // Checks if there should be a live update, and shows a toast if needed.
    let eventInformation = $("#toastInformation");
    if (eventInformation.text() !== "") {
        showToastSave($("#toastEventName").text(), $("#toastUsername").text(), $("#toastFirstName").text(), $("#toastLastName").text());
        showToastSave("", "", "", "");
    }
});