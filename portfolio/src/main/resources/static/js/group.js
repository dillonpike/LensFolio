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
        const group = JSON.parse(button.getAttribute('data-bs-group'))

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
        } else {
            modalTitle.innerText = 'Edit Group'
            modalButton.innerHTML = 'Save Group'
            modalForm.action = `edit-group/${group.id}`
            modalForm.setAttribute('group', group)
        }
    })
 }