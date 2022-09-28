/**
 * Conforms the given tag name to match the casing of an identical (except for casing) existing tag of the current user.
 * @param tagName given tag name
 * @param usersCurrentTags tags of the current user
 * @returns {*} conformed tag name
 */
function conformTagCasing(tagName, usersCurrentTags) {
    const lowerCaseCurrentTagNames = usersCurrentTags.map((skill) => skill.name.toLowerCase());
    if (lowerCaseCurrentTagNames.includes(tagName.toLowerCase())) {
        return usersCurrentTags[lowerCaseCurrentTagNames.indexOf(tagName.toLowerCase())].name;
    } else {
        return tagName;
    }
}