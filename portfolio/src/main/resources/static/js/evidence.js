


/**
 * Checks that the evidence modal inputs have text entered in them, then submits the evidence adding form and adds
 * the new evidence to the page if the action was successful, otherwise updates the modal with error messages.
 * @returns {Promise<void>} null
 */
async function validateEvidence() {
    if (validateEvidenceTextInput('evidenceTitle', 'evidenceTitleAlertBanner', 'evidenceTitleAlertMessage',"Title") &&
        validateEvidenceTextInput('evidenceDescription', 'evidenceDescriptionAlertBanner', 'evidenceDescriptionAlertMessage', "Description") &&
        validateModalDate('evidenceDate', 'milestoneModalButton', 'evidenceDateAlertBanner', 'evidenceDateAlertMessage')
    ) {
        document.getElementById('evidenceForm').onsubmit = () => { return false };

        addEvidence()

        document.getElementById('evidenceForm').onsubmit = () => {validateEvidence(); return false}
    }
}

/**
 * Submits the evidence piece adding form and adds the new evidence to the page if the action was successful, otherwise updates
 * the modal with error messages.
 */
function addEvidence() {
    const data = {
        evidenceTitle: document.getElementById('evidenceTitle').value,
        evidenceDescription: document.getElementById('evidenceDescription').value,
        evidenceDate: document.getElementById('evidenceDate').value,
    }

    const dataNew = {
        title: document.getElementById('evidenceTitle').value,
        description: document.getElementById('evidenceDescription').value,
        date: new Date(document.getElementById('evidenceDate').value),
        projectId: 0,
        userId: document.getElementById('userId').value
    }
    $.post(document.getElementById('evidenceForm').action + "?" + new URLSearchParams(dataNew), data).done((result) => {
        replaceEvidenceModalBody(result);
        let messageAlert = $("evidenceTitleAlertBanner");
        messageAlert.toggleClass("alert-danger alert-success");
        setTimeout(() => {
            $('#evidenceModal').modal('toggle')
            messageAlert.toggleClass("alert-success alert-danger");
            clearEvidenceModalFields();
        }, 1000);
    }).fail((response) => {
        replaceEvidenceModalBody(response.responseText);
    })
}

/**
 * Replaces the body of the evidence modal with the given modalBody response from the backend.
 * @param modalBodyResponse response with new modalBody to display (evidenceModalBody fragment)
 */
function replaceEvidenceModalBody(modalBodyResponse) {
    $("#evidenceModalBody").replaceWith(modalBodyResponse)
    updateCharsLeft('evidenceTitle', 'evidenceTitleLength', 30)
    updateCharsLeft('evidenceDescription', 'evidenceDescriptionLength', 250)
    configureEvidenceDatePicker();
    setEvidenceDatePickerValues();
}

/**
 * Validate that the text inputted in the given field does not only contain numbers, symbols, or punctuation.
 * Also validates the input so that it cannot be only 1 character non-space
 * @param elementId the id of the element to validate
 * @param alertBanner the id of the alert banner to display if the input is invalid
 * @param alertMessage the id of the alert message to display if the input is invalid
 * @param typeTextInput the type of text input being validated (e.g. "Title")
 * @returns {boolean} returns true if the input is valid, false otherwise
 */
function validateEvidenceTextInput(elementId, alertBanner, alertMessage, typeTextInput) {
    const input = document.getElementById(elementId).value;
    const regex = /^[\p{N}\p{P}\p{S}\p{Zs}]{1,}$/u; // this regex use Unicode awareness regex, it matches with any sequence of characters that are number, punctuation, or whitespace. Supposedly it works with all languages
    if (regex.test(input) || input.length < 2) {
        document.getElementById(alertBanner).hidden = false;
        document.getElementById(alertMessage).innerText = typeTextInput+" cannot only contains numbers, punctuation, and/or symbols. and must be at least 2 characters long.";
        return false
    } else if (input.trim().length === 1){
        document.getElementById(alertBanner).hidden = false;
        document.getElementById(alertMessage).innerText = typeTextInput+" should not have only 1 character non-space";
        return false
    } else {
        document.getElementById(alertBanner).hidden = true;
        return true
    }
}

/**
 * Checks that the Title and Description fields are not empty inside the evidence modal.
 */
function isEvidenceInputFieldFilled() {
    const titleInput = document.getElementById("evidenceTitle").value;
    const descInput = document.getElementById("evidenceDescription").value;
    const addButton = document.getElementById("evidenceModalButton");
    if (titleInput === "" || descInput === "") {
        addButton.classList.add("disabled");
    } else {
        addButton.classList.remove("disabled");
    }
}

function clearEvidenceModalFields() {
    document.getElementById('evidenceTitle').value = "";
    document.getElementById('evidenceDescription').value = "";
    evidenceDatePicker.dates.setValue(tempusDominus.DateTime.convert(new Date()));
    $("#evidenceTitleAlertBanner").hide();
}