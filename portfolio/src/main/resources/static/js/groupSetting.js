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