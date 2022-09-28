/**
 * Returns an array of arrays, where the i-th array contains the i-th element from each of the arrays provided.
 * @param arrays arrays to zip
 * @returns {*} an array of arrays, where the i-th array contains the i-th element from each of the arrays provided
 */
function zip(arrays) {
    return arrays[0].map(function(_, i){
        return arrays.map(function(array){return array[i]})
    });
}