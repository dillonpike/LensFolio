
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
    set toast(toast) {
        this.toast = toast;
    }

    show = function () {
        if (!this.hasBeenSaved) {
            this.bodyText = "'" + this.name + "' is being edited by " +
                this.firstName + " " + this.lastName + " (" + this.username + ").";
        } else {
            this.bodyText = "'" + this.name + "' has been updated by " +
                this.firstName + " " + this.lastName + " (" + this.username + ").";
        }
        this.toastBodyTextVar.text(this.bodyText)
        this.toast.show();
    }

    hide = function () {
        this.toast.hide();
    }

}

export default Toast;