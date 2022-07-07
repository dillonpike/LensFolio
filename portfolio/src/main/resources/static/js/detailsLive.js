let stompClient = null;
let toast1 = null;
let toast2 = null;
let toast3 = null;

const EventType = "Event";
const DeadlineType = "Deadline";
const MilestoneType = "Milestone";

/**
 * The amount of time in seconds the toast will take before hiding on a timed hide function.
 * @type {number}
 */
const SECONDS_TILL_HIDE = 5;

/**
 * Toast object that holds its title and the users' username, first and last name for an item notification.
 * This object also holds its HTML toast information and can display on this toast object.
 */
class Toast {
    toast;
    toastBodyTextVar;
    toastTitleTextVar;
    titleName = "";
    bodyText = "";
    hasBeenSaved = false;
    selectedDate = (new Date(Date.now())).valueOf();
    isHidden = true;
    type = "";
    isWaitingToBeHidden = false;

    id = "";
    id_number = -1;
    name = "";
    username = "";
    firstName = "";
    lastName = "";

    /**
     * Default constructer.
     * @param type Type of item notification. Can be "Event", "Deadline" or "Milestone".
     * @param name Name of item being updated. E.g. "Event 1" or "Homework Deadline".
     * @param id Integer id of the item being updated.
     * @param username Username of the user updating the item.
     * @param firstName Users first name.
     * @param lastName Users last name.
     * @param hasBeenSaved Whether the item has just been saved, rather than just being edited.
     */
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
        } else if (type === MilestoneType) {
            this.titleName = "Milestone Activity";
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

    /**
     * Shows the toast with the correct message and title.
     */
    show = function () {
        this.isHidden = false;
        this.isWaitingToBeHidden = false;
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
        this.isWaitingToBeHidden = false;
        this.toast.hide();
    }

    /**
     * Hides the toast after a timer.
     * @param timeInSeconds Time in seconds for the toast to hide after. Should be equal to 1 or above
     */
    hideTimed = function (timeInSeconds) {
        this.selectedDate = (new Date(Date.now())).valueOf();
        this.isWaitingToBeHidden = true;
        if (timeInSeconds <= 0) {
            timeInSeconds = 1;
        }
        setTimeout((function () {
            let currentTime = (new Date(Date.now())).valueOf();
            if (currentTime >= this.selectedDate + ((timeInSeconds * 1000) - 50) && this.isWaitingToBeHidden) {
                this.toast.hide();
                this.isHidden = true;
                this.isWaitingToBeHidden = false;
            }
        }).bind(this), timeInSeconds * 1000, this);
    }

    /**
     * Sets the objects html toast object, as well as a body text variable and the title text variable to assign relevant text to.
     * @param toast HTML toast object.
     * @param textVar Text variable for body.
     * @param titleVar Text variable for title.
     */
    setToast = function (toast, textVar, titleVar) {
        this.toast = toast;
        this.toastBodyTextVar = textVar;
        this.toastTitleTextVar = titleVar;
        if (this.isHidden) {
            this.hide();
        } else {
            this.show();
            if (this.isWaitingToBeHidden) {
                this.hideTimed(SECONDS_TILL_HIDE);
            }
        }
    }

    /**
     * Updates itself with the given toast object that holds new information.
     * @param newToast New toast that holds updated information about the toast.
     * @returns {Toast} Returns its updated self.
     */
    updateToast = function (newToast) {
        this.name = newToast.name
        this.hasBeenSaved = newToast.hasBeenSaved;

        return this;
    }
}

/**
 * toSting method for use with debugging.
 * @returns {string}
 */
Toast.prototype.toString = function () {
    return this.id + ": " + this.name;
}

/**
 * Holds a list of toast objects that are, or have been active. Can only be as long as listOfHTMLToasts.
 * @type {[Toast]}
 */
let listOfToasts = [];
/**
 * List of html toast object pairs that hold a Bootstrap toast object, a body text variable and a title text variable.
 * These can be assigned to toast objects to display them.
 * @type {[{'toast', 'text', 'title'}]}
 */
let listOfHTMLToasts = [];

/**
 * Adds toast objects to the listOfToasts list if it is new, otherwise updates the existing toast. Then reassigns the
 * toast html objects to the new list.
 * @param newToast New toast object to add/update to the list.
 * @returns {Toast} updated toast if it already existed, otherwise, returns the parameter 'newToast'.
 */
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
        while (listOfToasts.length > listOfHTMLToasts.length) {
            listOfToasts.shift();
        }
    }
    reorderToasts();
    return returnedToast;
}

/**
 * Reassigns toast html objects to the toast objects that are active at the moment (in the list 'listOfToasts')
 */
function reorderToasts() {
    let count = 0;
    for (let item in listOfHTMLToasts) {
        listOfHTMLToasts[count].toast.hide();
        count += 1;
    }
    count = 0;
    for (let item in listOfToasts) {
        let toastItems = listOfHTMLToasts[count];
        let toast = listOfToasts[count];
        toast.setToast(toastItems.toast, toastItems.text, toastItems.title);
        count += 1;
    }
}

/**
 * Runs every 10 seconds to make sure that toasts that should be hidden, are hidden.
 */
// setInterval(function () {
//     let count = 0;
//     for (let item in listOfHTMLToasts) {
//         let currentHTMLToast = listOfHTMLToasts[count].toast;
//         console.log(!currentHTMLToast.isHidden + " " + listOfToasts.length.toString(10) !== "0")
//         if (
//             !currentHTMLToast.isHidden &&
//             listOfToasts.length.toString(10) !== "0"
//         ) {
//             currentHTMLToast.hide();
//         }
//     }
// }, 10000);

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
 * @param eventId Event id of the event being edited.
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
        newToast.hideTimed(SECONDS_TILL_HIDE);
    }
}

/**
 * Function that is called when a message is sent to the endpoint. Shows the toast if the message is full for a certain period.
 * This function displays that an event has been updated.
 * @param eventName Event message that may or may not be empty.
 * @param eventId Event id of the event being edited.
 * @param username Username of the user making the change
 * @param firstName First name of the user
 * @param lastName Last name of the user
 */
function showToastSave(eventName, eventId, username, firstName, lastName) {
    let newToast = new Toast("Event", eventName, eventId, username, firstName, lastName, true);
    newToast = addToast(newToast);
    newToast.show();
    newToast.hideTimed(SECONDS_TILL_HIDE);
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
    toast1 = new bootstrap.Toast($("#liveToast1"));
    toast2 = new bootstrap.Toast($("#liveToast2"));
    toast3 = new bootstrap.Toast($("#liveToast3"));
    listOfHTMLToasts = [{'toast':toast1, 'text':$("#popupText1"), 'title':$("#toastTitle1")}, {'toast':toast2, 'text':$("#popupText2"), 'title':$("#toastTitle2")}, {'toast':toast3, 'text':$("#popupText3"), 'title':$("#toastTitle3")}];
    // let count = 0;
    // for (let item in listOfHTMLToasts) {
    //     listOfHTMLToasts[count].toast.hide();
    // }
    connect();
    // Checks if there should be a live update, and shows a toast if needed.
    let eventInformation1 = $("#toastInformation1");
    if (eventInformation1.text() !== "") {
        showToastSave($("#toastEventName1").text(), $("#toastEventId1").text(), $("#toastUsername1").text(), $("#toastFirstName1").text(), $("#toastLastName1").text());
    }
    let eventInformation2 = $("#toastInformation2");
    if (eventInformation2.text() !== "") {
        showToastSave($("#toastEventName2").text(), $("#toastEventId2").text(), $("#toastUsername2").text(), $("#toastFirstName2").text(), $("#toastLastName2").text());
    }
    let eventInformation3 = $("#toastInformation3");
    if (eventInformation3.text() !== "") {
        showToastSave($("#toastEventName3").text(), $("#toastEventId3").text(), $("#toastUsername3").text(), $("#toastFirstName3").text(), $("#toastLastName3").text());
    }
});