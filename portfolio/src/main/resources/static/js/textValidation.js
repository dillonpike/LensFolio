/**
 * Makes it so users can't type or paste invalid characters into the given input
 * @param elementId element id of the input
 */
function disallowInvalidCharacters(elementId) {
    const input = document.getElementById(elementId);
    input.addEventListener( "input", event => {
        input.value = input.value.replace( /[^a-zA-Z0-9~!@#$%^&*()_+|}{:"?><,./;' ]/gm, '');
    }, false);

    input.addEventListener( "paste", event => {
        input.value = input.value.replace( /[^a-zA-Z0-9~!@#$%^&*()_+|}{:"?><,./;' ]/gm, '');
    }, false);
}

/**
 * Removes invalid characters from the given input
 * @param elementId element id of the input
 */
function removeInvalidCharacters(elementId) {
    const input = document.getElementById(elementId);
    input.value = input.value.trim().replace( /[^a-zA-Z0-9~!@#$%^&*()_+|}{:"?><,./;' ]/gm, '');
}