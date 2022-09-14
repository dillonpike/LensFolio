/**
 * Function is called when the user clicks the save group settings button.
 * It will send a POST request (saveGroupSettings) to the server with new attributes.
 * If the request is successful, the page will partially reload with the new group settings attributes and commits.
 */
function editGroupSetting() {
    const repoId = document.getElementById('repoId').value;
    if (repoId === "") {
        document.getElementById('repoId').value = 0;
    }
    const data = {
        groupLongName: document.getElementById("longGroupName").value,
        groupShortName: document.getElementById("groupShortName").value,
        repoName: document.getElementById("repoName").value,
        repoID: document.getElementById("repoId").value,
        repoToken: document.getElementById("repoToken").value,
        repoURL: document.getElementById("repoUrl").value,
        groupId: document.getElementById("groupId").value,
        groupSettingsId: document.getElementById("groupSettingsId").value
    }
    $.post('saveGroupSettings?' + new URLSearchParams(data)).done((result) => {
        $(`#groupSettingContainer`).replaceWith(result);
        sendIdRefresh($("#groupId").val());
        initialiseCommitsList()
    }).fail(showError)
}

/**
 * Display the alert banner in the response
 * @param response response containing an alert banner
 */
function showError(response) {
    if (response.responseText.includes("groupLongNameAlertBanner")) {
        $("#groupLongNameAlertBanner").replaceWith(response.responseText)
    } else if (response.responseText.includes("groupSettingsAlertBanner")) {
        $("#groupSettingsAlertBanner").replaceWith(response.responseText)
    }
}
/**
 * Each time a character is typed/pasted will be checked uses a regex validator that are not part of a valid set,
 * replace invalid character with a blank character
 */
function inputValidateCheck() {
    const longNameText = document.getElementById('longGroupName');
    const repoName = document.getElementById('repoName');
    const repoID = document.getElementById('repoId');
    const repoToken = document.getElementById('repoToken');

    longNameText.addEventListener( "input", event => {
        longNameText.value = longNameText.value.replace( /[^a-zA-Z0-9~!@#$%^&*()_+|}{:"?><,./;' ]/gm, '');
    }, false);
    longNameText.addEventListener( "paste", event => {
        longNameText.value = longNameText.value.replace( /[^a-zA-Z0-9~!@#$%^&*()_+|}{:"?><,./;' ]/gm, '');
    }, false);

    repoName.addEventListener( "input", event => {
        repoName.value = repoName.value.replace( /[^a-zA-Z0-9~!@#$%^&*()_+|}{:"?><,./;' ]/gm, '');
    }, false);
    repoName.addEventListener( "paste", event => {
        repoName.value = repoName.value.replace( /[^a-zA-Z0-9~!@#$%^&*()_+|}{:"?><,./;' ]/gm, '');
    }, false);

    // Only allow numbers
    repoID.addEventListener( "input", event => {
        repoID.value = repoID.value.replace(/[^0-9]/g, '');
    }, false);
    repoID.addEventListener( "paste", event => {
        repoID.value = repoID.value.replace(/[^0-9]/g, '');
    }, false);

    repoToken.addEventListener( "input", event => {
        repoToken.value = repoToken.value.replace( /[^a-zA-Z0-9-~!@#$%^&*()_+|}{:"?><,./;' ]/gm, '');
    }, false);
    repoToken.addEventListener( "paste", event => {
        repoToken.value = repoToken.value.replace( /[^a-zA-Z0-9-~!@#$%^&*()_+|}{:"?><,./;' ]/gm, '');
    }, false);

}

/**
 * Check if the name of the item that the user inputted is not an empty string
 * Submit the form and return true if the input is not empty string, otherwise show error banner with the error message and return false
 * @param elementId the ID of the text input HTML element for item's name
 * @param alertBanner the ID of the alert banner HTML element
 * @param alertMessage the ID of the alert banner message HTML element
 * @returns {boolean} true if the input is not empty string, otherwise false
 */
function validateModalName(elementId, alertBanner, alertMessage) {
    const nameInput = document.getElementById(elementId).value.trim();
    if (nameInput === "") {
        document.getElementById(alertBanner).hidden = false;
        document.getElementById(alertMessage).innerText = "Name cannot be empty!";
        return false
    } else {
        if (document.getElementById(alertBanner)) {
          document.getElementById(alertBanner).hidden = true;
        }
        return true
    }
}

/**
 * Validate repository settings input
 * 1. Check if repoId is less than 0, if so show error banner with the error message and return false.
 * 2. check if access token is at least 20 characters long, if not show error banner with the error message and return false.
 * 3. check if repository server URL is in correct format, if not show error banner with the error message and return false.
 *
 * @param elementId the ID of the text input HTML element for item's repo Id
 * @param alertBanner the ID of the alert banner HTML element
 * @param alertMessage the ID of the alert banner message HTML element
 * @returns {boolean} true if the input is less than 10 characters, otherwise false
 */
function validateRepoSetting(elementId, alertBanner, alertMessage) {
    const repoId = document.getElementById('repoId').value;
    const token = document.getElementById('repoToken').value;
    const serverUrl = document.getElementById('repoUrl').value;
    if (repoId.toString().length > 10) {
        document.getElementById(alertBanner).hidden = false;
        document.getElementById(alertMessage).innerText = "Invalid Repository ID!";
        return false
    }
    else if (token.toString().trim().length < 20 && token.toString().length >= 1) {
        document.getElementById(alertBanner).hidden = false;
        document.getElementById(alertMessage).innerText = "Invalid Repository Token! Token length should be at least 20 characters.";
        return false
    }
    else if (serverUrl.toString().length >= 1 && !isValidUrl(serverUrl)) {
            document.getElementById(alertBanner).hidden = false;
            document.getElementById(alertMessage).innerText = "Invalid Repository URL!";
            return false
    }
    else {
        if (document.getElementById(alertBanner)) {
            document.getElementById(alertBanner).hidden = true;
        }
        return true
    }
}

/**
 * Checks that the group modal inputs have text entered in them, then submits the group adding/editing form and adds
 * the new group to the page or updates the edited group if the action was successful, otherwise updates the modal
 * with error messages.
 * @returns {Promise<void>} null
 */
async function validateGroupSetting() {
    if (validateModalName('longGroupName', 'groupLongNameAlertBanner', 'groupLongNameAlertMessage') &&
        validateRepoSetting('repoId', 'groupRepoAlertBanner', 'groupRepoAlertMessage')) {
        document.getElementById('groupSettingForm').onsubmit = () => { return false };

        if (document.getElementById('groupSettingForm').action.includes('add')) {
            addGroup()
        } else {
            editGroupSetting()
        }
        document.getElementById('groupSettingForm').onsubmit = () => {validateGroupSetting(); return false}
    }
}

/**
 * Method to use regex to validate a URL String
 * inspired by https://www.freecodecamp.org/news/check-if-a-javascript-string-is-a-url/#:~:text=You%20can%20use%20the%20URLConstructor,given%20URL%20is%20not%20valid.
 * @param urlString the url string to validate
 * @returns {boolean} true if valid, false otherwise
 */
function isValidUrl(urlString) {
    const urlPattern = new RegExp('^(https?:\\/\\/)?'+ // validate protocol
        '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|'+ // validate domain name
        '((\\d{1,3}\\.){3}\\d{1,3}))'+ // validate OR ip (v4) address
        '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*'+ // validate port and path
        '(\\?[;&a-z\\d%_.~+=-]*)?'+ // validate query string
        '(\\#[-a-z\\d_]*)?$','i'); // validate fragment locator
    return !!urlPattern.test(urlString);
}