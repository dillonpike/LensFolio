function updateCommitsList() {

    const data = {
        groupId: "2",
        branchName : "All Branches",
        userEmail: "All Users"
    }
    $.get('repository-commits?'+new URLSearchParams(data)).done((result) => {
        $(`#commitsListRefresh`).replaceWith(result)
    })

}

updateCommitsList()


function editGroupSetting() {
    const data = {
        groupLongName: document.getElementById("longGroupName").value,
        groupShortName: document.getElementById("groupShortName").value,
        repoName: document.getElementById("repoName").value,
        repoID: document.getElementById("repoID").value,
        repoToken: document.getElementById("repoToken").value,
        groupId: document.getElementById("groupId").value
    }
    $.post('saveGroupSettings?'+new URLSearchParams(data)).done((result) => {
        $(`#groupSettingContainer`).replaceWith(result)
    }).fail(showError)
}


function showError() {

}
/**
 * Each time a character is typed/pasted will be checked uses a regex validator that are not part of a valid set,
 * replace invalid character with a blank character
 */
function inputValidateCheck() {
    const longNameText = document.getElementById('longGroupName');
    const repoName = document.getElementById('repoName');
    const repoID = document.getElementById('repoID');
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
        repoID.value = repoID.value.replace( /[^0-9]/gm, '');
    }, false);
    repoID.addEventListener( "paste", event => {
        repoID.value = repoID.value.replace( /[^0-9]/gm, '');
    }, false);

    repoToken.addEventListener( "input", event => {
        repoToken.value = repoToken.value.replace( /[^a-zA-Z0-9~!@#$%^&*()_+|}{:"?><,./;' ]/gm, '');
    }, false);
    repoToken.addEventListener( "paste", event => {
        repoToken.value = repoToken.value.replace( /[^a-zA-Z0-9~!@#$%^&*()_+|}{:"?><,./;' ]/gm, '');
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
    const nameInput = document.getElementById(elementId).value;
    if (nameInput === "") {
        document.getElementById(alertBanner).hidden = false;
        document.getElementById(alertMessage).innerText = "Name cannot be empty!";
        return false
    } else {
        document.getElementById(alertBanner).hidden = true;
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
    if (validateModalName('longGroupName', 'groupLongNameAlertBanner', 'groupLongNameAlertMessage')
    ) {
        document.getElementById('groupSettingForm').onsubmit = () => { return false };

        if (document.getElementById('groupSettingForm').action.includes('add')) {
            addGroup()
        } else {
            console.log("here")
            editGroupSetting()
        }
        document.getElementById('groupSettingForm').onsubmit = () => {validateGroupSetting(); return false}
    }
}