function updateCommitsList() {
    $.post('repository-commits').done((result) => {
        console.log(result)
        // $(`#commits_list_refresh`).replaceWith(result)
    })
}