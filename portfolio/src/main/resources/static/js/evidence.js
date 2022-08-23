


/**
 * Checks that the evidence modal inputs have text entered in them, then submits the evidence adding form and adds
 * the new evidence to the page if the action was successful, otherwise updates the modal with error messages.
 * @returns {Promise<void>} null
 */
async function validateEvidence() {
    if (validateModalName('evidenceTitle', 'evidenceTitleAlertBanner', 'evidenceTitleAlertMessage') &&
        validateModalName('evidenceDescription', 'evidenceDescriptionAlertBanner', 'evidenceDescriptionAlertMessage') &&
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
        shortName: document.getElementById('evidenceTitle').value,
        longName: document.getElementById('evidenceDescription').value
    }
    $.post(document.getElementById('evidenceForm').action + "?" + new URLSearchParams(data)).done((result) => {
        // TODO update the page with the new evidence using some sort of replacement technique.
        // Below is what the group page does, for help with how to implement such a step.
        $('#evidenceModal').modal('toggle')
        // document.getElementById("groupList").innerHTML += result
    }).fail(replaceEvidenceModalBody)
}

/**
 * Replaces the body of the evidence modal with the given modalBody response from the backend.
 * @param modalBodyResponse response with new modalBody to display (evidenceModalBody fragment)
 */
function replaceEvidenceModalBody(modalBodyResponse) {
    $("#evidenceModalBody").replaceWith(modalBodyResponse.responseText)
    updateCharsLeft('evidenceTitle', 'evidenceTitleLength', 30)
    updateCharsLeft('evidenceDescription', 'evidenceDescriptionLength', 250)
}