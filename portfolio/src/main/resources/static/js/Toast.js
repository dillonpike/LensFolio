
const Event = "Event";
const Deadline = "Deadline";

/**
 * Toast object that holds its title and the users' username, first and last name.
 */
class Toast {
    toast;
    titleName = "";
    hasBeenSaved = false;
    username = "";
    firstName = "";
    lastName = "";

    constructor(type, username, firstName, lastName, hasBeenSaved) {
        this.hasBeenSaved = hasBeenSaved;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        if (type === Event) {
            this.titleName = "Event Activity";
        } else if (type === Deadline) {
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

}

export default Toast;