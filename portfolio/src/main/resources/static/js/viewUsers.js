/**
 * call 'delete_role' post request and bringing the user id and the role that want to be deleted at the same time through the url * @param id
 * @param role an Integer indicating the user id of a user that a role will be deleted from
 * @param firstName user's first name
 * @param lastName user's last name
 * @param username user's username
 */
function  deleteUserRole(id, role, firstName, lastName, username) {
    const url = "delete_role?userId="+id+"&deletedRole="+role;
    $.post(url).done((result) => {
        if (role === 'STUDENT') {
            sendRemoveStudentRoleNotification(firstName, lastName, username);
        }
        window.location.reload();
    });

}