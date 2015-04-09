
/**
 * Create a data validation in the given range for the given ontology terms
 *
 * This function allows you to add new terms into an existing validation if the
 * given range is already within an existing range that has a vailidation.
 * The terms in the validation are stored in a hidden sheet with a a name made
 * from the range in A1 notation (e.g. a list of terms for a validation in C2 would
 * be stored in a hidden sheet called C2)
 *
 * @param {string} range The given range from the active sheet
 * @param {string} ontology The ontology where the terms came from
 * @param {string} cls The query class URI
 * @param {string} label The query class label
 * @param {string} type The type of query, one term, sublcasses of a term or descendants of a term {individual, subclass, descendants}
 * @param {string[][]} terms List of [label, uri] objects to store for the validation
 * @return {string} response with status and message in JSON
 */

function _createRestriction(range, ontology, cls, label, type, terms) {

  Logger.log("Creating restriction with: %s, %s, %s", ontology , cls , type  );

  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var selectedRangeAsString = range.getA1Notation();
  var existingRange  = getExistingRange(range);
  var restrictionSheet;
  var lastCol = 1;

  if (existingRange.length > 1) {
    SpreadsheetApp.getUi().alert("Can't create a restriction in this range as it covers two existing ranges");
  }
  else if (existingRange.length == 1) {

    restrictionSheet = ss.getSheetByName(existingRange[0]);
    range = ss.getRange(existingRange[0]);

    // get existing terms in validation range
    var existingTerms = restrictionSheet.getRange(4, 1, restrictionSheet.getLastRow() - 3, 2).getValues();
    terms =  arrayUnique(existingTerms.concat(terms));
    lastCol = getLastNonEmptyCellInRow(restrictionSheet, 2);
  }
  else {
    restrictionSheet = ss.insertSheet(selectedRangeAsString);
    restrictionSheet.hideSheet();
  }

  terms.sort(function(a,b) {
    return (a[0] > b[0]) ? 1 : (a[0] < b[0]) ? -1 : 0;
  });

  // store the details of the restriction in the hidden sheet
  restrictionSheet.getRange(1, lastCol).setValue(ontology);
  restrictionSheet.getRange(2, lastCol).setValue(cls);
  restrictionSheet.getRange(3, lastCol).setValue(type);
  restrictionSheet.getRange(4, 1, terms.length, 2).setValues(terms);

  var termRange = restrictionSheet.getRange(4, 1, terms.length);

  var rule = SpreadsheetApp.newDataValidation().requireValueInList(termRange.getValues()).build();
  range.setDataValidation(rule);

  return {"status": "1", "message": "Restriction created for " + type + " of " +  label};

}

/**
 * Check if the range supplied is in an existing range from hidden sheets
 * @returns {string} the existing ranges in A1 notation
 */

function getExistingRange(range) {

  var sheets = SpreadsheetApp.getActive().getSheets();
  var existingRanges = new Array(0);

  for (var x = 0 ; x < sheets.length ; x++) {
    var sheetName = sheets[x].getName();
    // get a range from sheet name
    Logger.log("looking for sheet with name %s", sheetName);

    try {
      var existingRange = sheets[x].getRange(sheetName);
      Logger.log("Found sheet with name %s", sheetName);

      if (intersects(range, existingRange)) {
        existingRanges.push(existingRange.getA1Notation());
      }
    } catch (e) {
       // we allow this to fail and continue looking for sheets with this name
    }
  }
  return existingRanges;
}


/**
 * Check if two ranges intersect by checking if any corners in range 1 and within range 2;
 * @param {string} range1 a range of cells
 * @param {string} range2 a range of cells
 * @returns {boolean} true if range 1 intersects with range 2
 */

function intersects(range1, range2) {

  var innerTopLeftX = range1.getColumn();
  var innerTopLeftY = range1.getRow();

  var innerTopRightX = range1.getLastColumn();
  var innerTopRightY = range1.getRow();

  var innerBottomLeftX = range1.getColumn();
  var innerBottomLeftY = range1.getLastRow();

  var innerBottomRightX = range1.getLastColumn();
  var innerBottomRightY = range1.getLastRow();

  // find the top left and bottom right corners of the second range
  var outerTopLeftX = range2.getColumn();
  var outerTopLeftY = range2.getRow();

  var outerBottomRightX = range2.getLastColumn();
  var outerBottomRightY = range2.getLastRow();

  // if top left x,y in range 1 is greater that top left range 2, but less that bottom right range 2 then return true
  if (betweenCorners(innerTopLeftX, innerTopLeftY, outerTopLeftX, outerTopLeftY, outerBottomRightX, outerBottomRightY) ) {
    return true;
  }
  else if (betweenCorners(innerTopRightX, innerTopRightY, outerTopLeftX, outerTopLeftY, outerBottomRightX, outerBottomRightY)) {
    return true;
  }
  else if (betweenCorners(innerBottomLeftX, innerBottomLeftY, outerTopLeftX, outerTopLeftY, outerBottomRightX, outerBottomRightY)) {
    return true;
  }
  else if (betweenCorners(innerBottomRightX, innerBottomRightY, outerTopLeftX, outerTopLeftY, outerBottomRightX, outerBottomRightY)) {
    return true;
  }
  return false;
}

/**
 * Given a set of coordinates {x,y}, check if they are within a rectangular range with given top left and bottom right coordinates
 * @param x x coordinates for the query
 * @param y y coordinates for the query
 * @param tlx top left x coordinates for the query
 * @param tly top left y coordinates for the query
 * @param brx bottom right x coordinates for the query
 * @param bry bottom right y coordinates for the query
 * @returns {boolean} true if {x,y} between top left and bottom right corner
 */

function betweenCorners (x, y, tlx, tly, brx, bry) {
  return (x >= tlx && y >=tly && x <= brx && y <= bry);
}

/**
 * Given a 2D of labels and URI, create a new array of unique values
 * @param {string[][]} array 2D of values to unique
 * @return {string[][]} new array of unique values
 */
function arrayUnique(array) {
  var a = array.concat();
  for(var i=0; i<a.length; ++i) {
    for(var j=i+1; j<a.length; ++j) {
      if(a[i][0] === a[j][0])
        a.splice(j--, 1);
    }
  }
  return a;
}

/**
 * Create restriction for a single term (type individual)
 * @param ontology ontology where the terms came from
 * @param cls  uri of the term
 * @param label label for the term
 */
function getRestrictionValues(ontology, cls, label){
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getActiveSheet();
  var range = sheet.getActiveRange();

  var terms = [[label, cls]];
  return _createRestriction(range, ontology, cls, label, 'individual', terms);
}

/**
 * Create restriction for a collection of terms from bioportal
 * @param ontology ontology where the terms came from
 * @param cls  uri of the term
 * @param label label for the term
 * @param type type of terms {subclass or descendants}
 */
function getRestrictionValuesFromBP(ontology, cls, label, type){
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getActiveSheet();
  var range = sheet.getActiveRange();

  try {
    var terms = getOntologyTermsFromBP(ontology, cls, label, type);

    if (terms.length == 0) {
      return {"status":1, "message": "No " + type + " found for " + cls};
    }

    return _createRestriction(range, ontology, cls, label, type, terms);
  }
  catch (e) {
    showToast(e, "Error", 10);
  }
}


/**
 * Removes any data restrictions form the active range
 */
function removeRestriction(){

  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getActiveSheet();
  var range = sheet.getActiveRange();

  var existingRanges  = getExistingRange(range);

  for (var j = 0; j < existingRanges.length; j++) {

    var existingRangeA1 = existingRanges[j];

    Logger.log("Removing range: " + existingRangeA1);

    // delete the validation in this range from active sheet
    sheet.getRange(existingRangeA1).clearDataValidations();

    // removed the hidden sheet with this name
    ss.deleteSheet(ss.getSheetByName(existingRangeA1));
  }
}

/**
 * Find the last non empty cell in row of a given sheet
 * @param sheet
 * @param rowToCheck
 * @return {*} the number of the last row
 */
function getLastNonEmptyCellInRow(sheet, rowToCheck) {

  var maxColumns = sheet.getLastColumn();

  var rowData = sheet.getRange(rowToCheck, 1, 1, maxColumns).getValues();
  rowData = rowData[0]; //Get inner array of two dimensional array

  var rowLength = rowData.length;

  for (var i = 0; i < rowLength; i++) {
    var thisCellContents = rowData[i];

    Logger.log('thisCellContents: ' + thisCellContents);

    if (thisCellContents === "") {
      Logger.log(i);
      return i + 1;  //Pass the count plus one, which is the last column with data
    }
  }
  return rowLength+1;
}


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// tests
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function _testIntersects() {
  var innerRange = SpreadsheetApp.getActiveSheet().getRange("B2:C3");
  var outerRange = SpreadsheetApp.getActiveSheet().getRange("B4:C5");

  Logger.log(intersects(innerRange, outerRange));
}

