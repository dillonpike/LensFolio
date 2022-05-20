/**
 * Returns true if the key pressed is a valid key for dates, otherwise false.
 * Valid keys are numbers, forward slashes, backspace, enter, and delete.
 * @param event key press event
 * @returns {boolean} true if the key pressed is a valid key for dates, otherwise false
 */
function checkDateKeys(event) {
    return '1234567890/'.includes(event.key) || [8, 13, 46].includes(event.keyCode);
}