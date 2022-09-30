/**
 * Redirects to the evidence page of the user with userId.
 * @param userId id of user whose evidence page to redirect to
 */
function redirectToEvidence(userId) {
    localStorage.setItem('tab', 'evidence-tab');
    window.location.href = "account?userId=" + userId;
}

/**
 * Redirects to the profile page of the user with userId.
 * @param userId id of user whose evidence page to redirect to
 */
function redirectToProfile(userId) {
    localStorage.setItem('tab', 'profile-tab');
    window.location.href = "account?userId=" + userId;
}

/**
 * Redirect to tag page
 * @param userId current user ID (note: it is not necessarily the user that logged in)
 * @param tagId the id of a tag
 * @param tagType the type of tag (skill or category) being searched for.
 */
function redirectToTagPage(userId, tagId, tagType) {
    window.location.href = "evidence-tags?userId="+userId+"&tagId="+tagId+"&tagType="+tagType;
}
