
/**
 *
 * Get json from a URL and return as a generic object

 * @param {string} uri The URI of the document server
 * @return {object} new object parsed from JSON
 */
function getObjectFromUrl(uri){
  try {
    var result = UrlFetchApp.fetch(uri).getContentText();
    if (result != null) {
      return JSON.parse(result);
    } else {
      throw new Error("No results from " + uri);
    }
  } catch (e) {
    throw new Error("Can't query " + uri + ":" + e);
  }
}

function showToast(msg, title, timeout) {
 SpreadsheetApp.getActiveSpreadsheet().toast(msg,title, timeout);
}
