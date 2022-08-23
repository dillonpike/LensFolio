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