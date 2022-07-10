/**
 * Returns true if the key pressed is a valid key for dates, otherwise false.
 * Valid keys are numbers, letters, forward slashes, backspace, enter, and delete.
 * @param event key press event
 * @returns {boolean} true if the key pressed is a valid key for dates, otherwise false
 */
function checkDateKeys(event) {
    return '1234567890/'.includes(event.key) || [8, 13, 46].includes(event.keyCode) || 65 <= event.keyCode && event.keyCode <= 90;
}

/**
 * Customises the sprint modal attributes with depending on what sprint it should display and whether it's being used
 * for adding or editing a sprint.
 */
function sprintModalSetup() {
    const sprintModal = document.getElementById('sprintModal')
    sprintModal.addEventListener('show.bs.modal', function (event) {
        // Button that triggered the modal
        const button = event.relatedTarget

        // Extract info from data-bs-* attributes
        const sprint = JSON.parse(button.getAttribute('data-bs-sprint'))
        const type = button.getAttribute('data-bs-type')

        // Update the modal's content.
        const modalTitle = sprintModal.querySelector('.modal-title')
        const modalBodyInputs = sprintModal.querySelectorAll('.modal-body input')
        const modalBodyTextArea = sprintModal.querySelector('.modal-body textarea')
        const modalButton = sprintModal.querySelector('.modal-footer button')
        const modalForm = sprintModal.querySelector('form')

        if (type === 'add') {
            modalTitle.innerText = 'Add Sprint'
            modalButton.innerHTML = 'Add Sprint'
            modalForm.action = 'add-sprint'
            modalForm.setAttribute('data-sprint-id', -1)
        } else {
            modalTitle.innerText = 'Edit Sprint'
            modalButton.innerHTML = 'Save Sprint'
            modalForm.action = `edit-sprint/${sprint.id}`
            modalForm.setAttribute('data-sprint-id', sprint.id)
        }
        console.log(sprint)
        modalBodyInputs[0].value = sprint.name
        $('#sprintStart').datepicker('setDate', sprint.startDateString)
        $('#sprintEnd').datepicker('setDate', sprint.endDateString)
        modalBodyTextArea.value = sprint.description

        // Initial run of updateSprintDateError function in case initial values are invalid
        updateSprintDateError();
        updateCharsLeft('addSprintName', 'addSprintNameLength', 50);
        updateCharsLeft('addSprintDescription', 'addSprintDescriptionLength', 500);
    })
}