/**
 * Returns true if the key pressed is a valid key for dates, otherwise false.
 * Valid keys are numbers, forward slashes, backspace, enter, and delete.
 * @param event key press event
 * @returns {boolean} true if the key pressed is a valid key for dates, otherwise false
 */
function checkDateKeys(event) {
    return '1234567890/'.includes(event.key) || [8, 13, 46].includes(event.keyCode);
}

/**
 * Customises the delete modal attributes with depending on what type of thing (e.g. sprint, event, milestone) is
 * being deleted, and which thing it is.
 */
function deleteModalSetup() {
    const deleteModal = document.getElementById('deleteModal')
    deleteModal.addEventListener('show.bs.modal', function (event) {
        // Button that triggered the modal
        const button = event.relatedTarget

        // Extract info from data-bs-* attributes
        const id = button.getAttribute('data-bs-id')
        const name = button.getAttribute('data-bs-name')
        const type = button.getAttribute('data-bs-type')

        // Update the modal's content.
        const modalTitle = deleteModal.querySelector('.modal-title')
        const modalBodyLabel = deleteModal.querySelector('.modal-body label')
        const modalButton = deleteModal.querySelector('.modal-footer button')
        const modalLink = deleteModal.querySelector('.modal-footer a')

        modalTitle.innerText = `Delete ${type.charAt(0).toUpperCase() + type.slice(1)}`
        modalBodyLabel.textContent = `Are you sure you want to delete ${name}?`
        modalButton.innerHTML = `Delete ${type.charAt(0).toUpperCase() + type.slice(1)}`
        modalLink.href = `delete-${type}/${id}`
    })
}