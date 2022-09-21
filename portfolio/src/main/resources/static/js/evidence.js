/**
 * Checks that the evidence modal inputs have text entered in them, then submits the evidence adding form and adds
 * the new evidence to the page if the action was successful, otherwise updates the modal with error messages.
 * @returns {Promise<void>} null
 */
async function validateEvidence() {
    const titleValid = validateEvidenceTextInput('evidenceTitle', 'evidenceTitleAlertBanner', 'evidenceTitleAlertMessage', 'Title');
    const descriptionValid = validateEvidenceTextInput('evidenceDescription', 'evidenceDescriptionAlertBanner', 'evidenceDescriptionAlertMessage', 'Description');
    const dateValid = validateModalDate('evidenceDate', 'milestoneModalButton', 'evidenceDateAlertBanner', 'evidenceDateAlertMessage')
    if (titleValid && descriptionValid && dateValid) {
        document.getElementById('evidenceForm').onsubmit = () => { return false };
        removeInvalidCharacters('evidenceWeblink');
        removeInvalidCharacters('evidenceSkillTag');
        addEvidence()
        document.getElementById('evidenceForm').onsubmit = () => {validateEvidence(); return false}
        /* Refresh the container after adding a piece of evidence*/
        var url = "account?userId=" + document.getElementById('userId').value
           setTimeout(function() {
            $("#evidence").load(url+" #evidence>*","");
           }, 10);
    }
}

/**
 * Submits the evidence piece adding form and adds the new evidence to the page if the action was successful, otherwise updates
 * the modal with error messages.
 */
function addEvidence() {

    let dateValue = new Date(document.getElementById('evidenceDate').value);

    if (dateValue.toString() === "Invalid Date") {
        dateValue = new Date("01/01/1970");
    }

    const data = {
        title: document.getElementById('evidenceTitle').value,
        description: document.getElementById('evidenceDescription').value,
        date: dateValue,
        projectId: 0,
        userId: document.getElementById('userId').value,
        webLinks: webLinksList
    }


    $.post(document.getElementById('evidenceForm').action + "?" + new URLSearchParams(data)).done((result) => {
        replaceEvidenceModalBody(result);
        let messageAlert = $("evidenceTitleAlertBanner");
        messageAlert.toggleClass("alert-danger alert-success");
        $('#evidenceModal').modal('toggle')
        showAlertToast("Evidence added successfully!");
        clearEvidenceModalFields();
        $("#webLinkList").html(""); // clear web links
        $("#skillTagList").html(""); // clear skill tags
    }).fail((response) => {
        replaceEvidenceModalBody(response.responseText);
    })
}

/**
 * Replaces the body of the evidence modal with the given modalBody response from the backend.
 * @param modalBodyResponse response with new modalBody to display (evidenceModalBody fragment)
 */
function replaceEvidenceModalBody(modalBodyResponse) {
    const webLinks = $("#webLinkList").children();
    $("#evidenceModalBody").replaceWith(modalBodyResponse);
    // Restore weblinks that were deleted when the modal was replaced
    // Uses two duplicate jquery selectors since the element is replaced between each use
    $("#webLinkList").html(webLinks);
    const skillTags = $("#skillTagList").children();
        $("#evidenceModalBody").replaceWith(modalBodyResponse);
        // Restore skilltags that were deleted when the modal was replaced
        // Uses two duplicate jquery selectors since the element is replaced between each use
        $("#skillTagList").html(skillTags);
    updateCharsLeft('evidenceTitle', 'evidenceTitleLength', 30);
    updateCharsLeft('evidenceDescription', 'evidenceDescriptionLength', 250);
    configureEvidenceDatePicker();
    setEvidenceDatePickerValues();
    configureWebLinkInput();
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
    const regex = /^[\p{N}\p{P}\p{S}\p{Zs}]+$/u; // this regex use Unicode awareness regex, it matches with any sequence of characters that are number, punctuation, or whitespace. Supposedly it works with all languages
    if (regex.test(input) || input.length < 2) {
        document.getElementById(alertBanner).removeAttribute("hidden");
        document.getElementById(alertMessage).innerText = typeTextInput+" cannot only contains numbers, punctuation, and/or symbols. and must be at least 2 characters long.";
        return false
    } else if (input.trim().length === 1){
        document.getElementById(alertBanner).removeAttribute("hidden");
        document.getElementById(alertMessage).innerText = typeTextInput+" should not have only 1 character non-space";
        return false
    } else {
        document.getElementById(alertBanner).setAttribute("hidden", "hidden");
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
    document.getElementById('evidenceWeblink').value = "";
    document.getElementById('evidenceSkillTag').value = "";
    evidenceDatePicker.dates.setValue(tempusDominus.DateTime.convert(new Date()));
    webLinksList = [];
    skillTagsList = [];
    $("#evidenceTitleAlertBanner").attr("hidden", "hidden");
    $("#evidenceDescriptionAlertBanner").attr("hidden", "hidden");
    $("#evidenceDateAlertBanner").attr("hidden", "hidden");
    $("#evidenceWebLinksAlertBanner").attr("hidden", "hidden");
}

/**
 * Validate the input so that the user cannot input emoji
 * @param elementId the ID of the input element
 */
function disallowEmojiCharacters(elementId){
    const input = document.getElementById(elementId);
    const emojiRegex = /\p{Emoji_Presentation}/gu
    input.addEventListener('input', function() {
        if (emojiRegex.test(input.value)) {
            input.value = input.value.replace(emojiRegex, '');
        }
    }, false);
}