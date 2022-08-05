/**
 * Customises the group modal attributes with whether it's being
 * used for adding or editing a group.
 */
function groupModalSetup() {
    const groupModal = document.getElementById('groupModal')
    groupModal.addEventListener('show.bs.modal', function (event) {
        // Button that triggered the modal
        const button = event.relatedTarget

        // Extract info from data-bs-* attributes
        const id = button.getAttribute('data-bs-id')
        const shortName = button.getAttribute('data-bs-shortname')
        const longName = button.getAttribute('data-bs-longname')

        const type = button.getAttribute('data-bs-type')
        // Update the modal's content.
        const modalTitle = groupModal.querySelector('.modal-title')
        const modalBodyInputs = groupModal.querySelectorAll('.modal-body input')
        const modalButton = groupModal.querySelector('.modal-footer button')
        const modalForm = groupModal.querySelector('form')

        if (type === 'add') {
            modalTitle.innerText = 'Add Group'
            modalButton.innerHTML = 'Add Group'
            modalForm.action = 'add-group'
            const modalAlerts = groupModal.querySelectorAll('.modal-body div.alert')
            modalAlerts.forEach(element => element.hidden = true)
        } else {
            modalTitle.innerText = 'Edit Group'
            modalButton.innerHTML = 'Save Group'
            modalForm.action = `edit-group/${id}`
        }

        modalBodyInputs[0].value = shortName
        modalBodyInputs[1].value = longName

        updateCharsLeft('shortGroupName', 'shortGroupNameLength', 10)
        updateCharsLeft('longGroupName', 'longGroupNameLength', 30)
    })
 }

/**
 * Submits the group adding form and adds the new group to the page if the action was successful, otherwise updates the
 * modal with error messages.
 * @returns {Promise<void>} null
 */
async function validateGroup() {
    if (validateModalName('shortGroupName', 'groupShortNameAlertBanner', 'groupShortNameAlertMessage') &&
        validateModalName('longGroupName', 'groupLongNameAlertBanner', 'groupLongNameAlertMessage')
    ) {
        document.getElementById('groupForm').onsubmit = () => { return false };

        const data = {
            shortName: document.getElementById('shortGroupName').value,
            longName: document.getElementById('longGroupName').value
        }
        $.post(document.getElementById('groupForm').action + "?" + new URLSearchParams(data)).done((result) => {
            $('#groupModal').modal('toggle')
            document.getElementById("groupList").innerHTML += result
        }).fail((result) => {
            $("#groupModalBody").replaceWith(result.responseText)
            updateCharsLeft('shortGroupName', 'shortGroupNameLength', 10)
            updateCharsLeft('longGroupName', 'longGroupNameLength', 30)
        })

        document.getElementById('groupForm').onsubmit = () => {validateGroup(); return false}
    }
}