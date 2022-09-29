/**
 * Constants used for identifying what kind of notification the toast should be.
 */
const EVENTTYPE = "Event";
const DEADLINETYPE = "Deadline";
const MILESTONETYPE = "Milestone";
const GROUPTYPE = "Group";
const HIGHFIVETYPE = "HighFive"
const EVIDENCETYPE = "Evidence";
const ROLETYPE = "Role";


const ADDACTION = "add";
const SAVEACTION = "save";
const EDITACTION = "edit";
const DELETEACTION = "delete";
const HIGHFIVEACTION = "highfive"
const HIGHFIVEUPDATEACTION = "highfiveUpdate"
const ADDEVIDENCEACTION = "addEvidence";
const DELETEEVIDENCEACTION = "deleteEvidence";
const UPDATELEADERBOARDADDACTION = "updateLeaderboardAdd";
const UPDATELEADERBOARDDELETEACTION = "updateLeaderboardDelete";
const UPDATELEADERBOARDACTION = "updateLeaderboard";
const DELETEROLEACTION = "deleteRole";
const ADDROLEACTION = "addRole";
const ADDROLEUPDATEACTION = "addRoleUpdate";
const DELETEROLEUPDATEACTION = "deleteRoleUpdate";


/**
 * The amount of time in seconds the toast will take before hiding on a timed hide function.
 * @type {number}
 */
const SECONDS_TILL_HIDE = 5;

/**
 * Notification object that holds its title and the users' username, first and last name for an item notification.
 * This object also holds its HTML Bootstrap toast information and can display on this notification object.
 */
class Notification {
    toast;
    toastBodyTextVar;
    toastTitleTextVar;
    titleName = "";
    bodyText = "";
    selectedDate = (new Date(Date.now())).valueOf();
    isHidden = true;
    type = "";
    isWaitingToBeHidden = false;
    action = "";

    id = "";
    id_number = -1;
    name = "";
    username = "";
    firstName = "";
    lastName = "";
    highfivers = [];

    /**
     * Default constructor.
     * @param type Type of item notification. Can be "Event", "Deadline" or "Milestone".
     * @param name Name of item being updated. E.g. "Event 1" or "Homework Deadline".
     * @param id Integer id of the item being updated.
     * @param username Username of the user updating the item.
     * @param firstName Users first name.
     * @param lastName Users last name.
     * @param action
     */
    constructor(type, name, id, username, firstName, lastName, action) {
        this.name = name;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.highfivers = [];
        if (type === EVENTTYPE) {
            this.titleName = "Event Activity";
        } else if (type === DEADLINETYPE) {
            this.titleName = "Deadline Activity";
        } else if (type === MILESTONETYPE) {
            this.titleName = "Milestone Activity";
        } else if (type === GROUPTYPE) {
            this.titleName = "Group Activity";
        } else if (type === HIGHFIVETYPE) {
            this.titleName = "High Five Activity";
        } else if (type === EVIDENCETYPE) {
            this.titleName = "Evidence Activity";
        } else if (type === ROLETYPE) {
            this.titleName = "Role Activity";
        }
        else {
            this.titleName = "Activity";
        }

        if (type === HIGHFIVETYPE) {
            this.id = type.toLowerCase() + "_" + id;
        } else {
            this.id = type.toLowerCase() + "_" + username + "_" + id;
        }
        this.id_number = id;
        this.action = action;
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
    get id() {
        return this.id;
    }
    get id_number() {
        return this.id_number;
    }
    get type() {
        return this.type;
    }
    get action() {
        return this.action;
    }

    get numOfHighFivers() {
        return this.numOfHighfivers;
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
    set action(action) {
        this.action = action;
    }

    set numOfHighFivers(numOfHighFivers) {
        this.numOfHighfivers = numOfHighFivers;
    }



    /**
     * Shows the notification with the assigned toast with the correct message and title.
     */
    show() {
        this.isHidden = false;
        this.isWaitingToBeHidden = false;
        this.selectedDate = (new Date(Date.now())).valueOf();
        switch(this.action){
          case SAVEACTION:
              this.bodyText = "'" + this.name + "' has been updated by " + this.firstName + " " + this.lastName + " (" + this.username + ").";
              break;
          case EDITACTION:
              this.bodyText = "'" + this.name + "' is being edited by " + this.firstName + " " + this.lastName + " (" + this.username + ").";
              break;
          case ADDACTION:
              this.bodyText = this.firstName + " " + this.lastName + " (" + this.username + ") has added a new " + this.type.toLowerCase() + "."
              break;
          case DELETEACTION:
              this.bodyText = "'" + this.name + "' has been deleted by " + this.firstName + " " + this.lastName + " (" + this.username + ").";
              break;
          case ADDEVIDENCEACTION:
              this.bodyText = this.firstName + " " + this.lastName + " (" + this.username + ") has added a piece of evidence. Updating leaderboard...";
              break;
            case DELETEEVIDENCEACTION:
                this.bodyText = this.firstName + " " + this.lastName + " (" + this.username + ") has deleted a piece of evidence. Updating leaderboard...";
                break;
          case UPDATELEADERBOARDACTION:
              this.bodyText = this.firstName + " " + this.lastName + " (" + this.username + ") has added a piece of evidence. Updated leaderboard!";
              break;
          case ADDROLEUPDATEACTION:
                this.bodyText = this.firstName + " " + this.lastName + " (" + this.username + ") has been added student role. Updated leaderboard!";
                break;
          case DELETEROLEUPDATEACTION:
                this.bodyText = this.firstName + " " + this.lastName + " (" + this.username + ") has had removed student role. Updated leaderboard!";
                break;
          case DELETEROLEACTION:
                this.bodyText = this.firstName + " " + this.lastName + " (" + this.username + ") has had removed student role. Updating leaderboard...";
                break;
          case ADDROLEACTION:
                this.bodyText = this.firstName + " " + this.lastName + " (" + this.username + ") has been added to a student role. Updating leaderboard...";
                break;
          case HIGHFIVEACTION:
                this.bodyText = "'" + this.name + "' has been high fived by " + this.username + ".";
                break;
          case HIGHFIVEUPDATEACTION:
                this.bodyText = "'" + this.name + "' has been high fived by " + this.username + " and " + (this.highfivers.length - 2) + " other user(s).";
                break;
            case UPDATELEADERBOARDDELETEACTION:
                this.bodyText = this.firstName + " " + this.lastName + " (" + this.username + ") has deleted a piece of evidence. Updated leaderboard!";
                break;
            default:
                this.bodyText = "'" + this.name + "' has been changed by " + this.firstName + " " + this.lastName + " (" + this.username + ").";


        }

        this.toastBodyTextVar.text(this.bodyText);
        this.toastTitleTextVar.text(this.titleName);
        this.toast.show();
    }

    /**
     * Hides the notification, including the toast. Resets variables if needed.
     */
    hide() {
        this.isHidden = true;
        this.isWaitingToBeHidden = false;
        this.toast.hide();
        if (this.type === HIGHFIVETYPE) {
            this.highfivers = [];
            this.username = "";
            this.action = HIGHFIVEACTION;
        }
    }


    /**
     * Hides the notification after a timer.
     * @param timeInSeconds Time in seconds for the notification to hide after. Should be equal to 1 or above
     */
    hideTimed(timeInSeconds) {
        if (timeInSeconds <= 0) {
            timeInSeconds = 1;
        }
        this.selectedDate = (new Date(Date.now())).valueOf();
        this.isWaitingToBeHidden = true;
        setTimeout((function (notification) {
            let currentTime = (new Date(Date.now())).valueOf();
            if (currentTime >= notification.selectedDate + ((timeInSeconds * 1000) - 500) && notification.isWaitingToBeHidden) {
                notification.hide();
            }
        }), timeInSeconds * 1000, this);
    }

    /**
     * Sets the objects html toast object, as well as a body text variable and the title text variable to assign relevant text to.
     * @param toast HTML toast object.
     * @param textVar Text variable for body.
     * @param titleVar Text variable for title.
     */
    setToast(toast, textVar, titleVar) {
        this.toast = toast;
        this.toastBodyTextVar = textVar;
        this.toastTitleTextVar = titleVar;
        if (this.isHidden) {
            this.toast.hide();
        } else {
            this.toast.show();
        }
    }

    /**
     * Updates itself with the given Notification object that may hold new information.
     * @param newNotification New notification that holds updated information about the toast.
     * @returns {Notification} Returns its updated self.
     */
    updateNotification(newNotification) {
        if (((this.action === HIGHFIVEACTION) || (this.action === HIGHFIVEUPDATEACTION))
            && !(this.highfivers.includes(newNotification.username))) {
            if (this.highfivers.length === 1) {
                this.username += ", " + newNotification.username
                this.highfivers.push(newNotification.username);
            } else if (this.highfivers.length >= 2) {
                this.action = HIGHFIVEUPDATEACTION
                this.highfivers.push(newNotification.username);
            } else {
                this.username = newNotification.username
                this.highfivers.push(newNotification.username);
            }
        } else {
            this.name = newNotification.name;
            this.action = newNotification.action;
        }
        return this;
    }
}


/**
 * toSting method for use with debugging.
 * @returns {string}
 */
Notification.prototype.toString = function () {
    return this.id + ": " + this.name;
}


/**
 * Adds Notification objects to the listOfNotifications list if it is new, otherwise updates the existing notification.
 * Then reassigns the Bootstrap toast html objects to the new list.
 * @param newNotification New toast object to add/update to the list.
 * @param listOfNotifications List of Notification objects to add/update the new notification to.
 * @param listOfHTMLToasts List of HTML Bootstrap toast objects to assign to the list of Notification objects.
 * @returns {Notification} updated toast if it already existed, otherwise, returns the parameter 'newToast'.
 */
function addNotification(newNotification, listOfNotifications, listOfHTMLToasts) {
    let returnedNotification = newNotification;

    let notificationExists = false;
    let notificationIndex = -1;
    let count = 0;
    for (let item in listOfNotifications) {
        if (listOfNotifications[count].id === newNotification.id) {
            notificationExists = true;
            notificationIndex = count;
            break;
        }
        count += 1;
    }
    if (notificationExists) {
        returnedNotification = listOfNotifications[notificationIndex].updateNotification(newNotification);
    } else {
        listOfNotifications.push(newNotification)
        while (listOfNotifications.length > listOfHTMLToasts.length) {
            listOfNotifications.shift();
        }
    }
    reorderNotifications(listOfNotifications, listOfHTMLToasts);
    return returnedNotification;
}

/**
 * Reassigns toast html objects to the toast objects that are active at the moment (in the list 'listOfToasts')
 */
function reorderNotifications(listOfNotifications, listOfHTMLToasts) {
    let count = 0;
    for (let item in listOfHTMLToasts) {
        listOfHTMLToasts[count].toast.hide();
        count += 1;
    }
    count = 0;
    for (let item in listOfNotifications) {
        let toastItems = listOfHTMLToasts[count];
        let notification = listOfNotifications[count];
        notification.setToast(toastItems.toast, toastItems.text, toastItems.title);
        count += 1;
    }
}

