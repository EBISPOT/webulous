function createRestriction2(range, ontology, cls, label, type, terms) {

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
    Logger.log("Existing range found, getting sheet called %s",  existingRange[0] );
    
    restrictionSheet = ss.getSheetByName(existingRange[0]);
    range = ss.getRange(existingRange[0]);
    
    // get existing terms in validation range
    var existingTerms = restrictionSheet.getRange(4, 1, restrictionSheet.getLastRow() - 3, 2).getValues();   
    terms =  arrayUnique(existingTerms.concat(terms));
    lastCol = getLastNonEmptyCellInRow(restrictionSheet, 2);
    Logger.log("Last column in existing sheet is %s", lastCol );
  }
  else {
   restrictionSheet = ss.insertSheet(selectedRangeAsString);
   restrictionSheet.hideSheet();
  }
    
  terms.sort(function(a,b) {
    return (a[0] > b[0]) ? 1 : (a[0] < b[0]) ? -1 : 0;    
  });
  
  restrictionSheet.getRange(1, lastCol).setValue(ontology);
  restrictionSheet.getRange(2, lastCol).setValue(cls);
  restrictionSheet.getRange(3, lastCol).setValue(type);
  restrictionSheet.getRange(4, 1, terms.length, 2).setValues(terms);
 
  var termRange = restrictionSheet.getRange(4, 1, terms.length);
  Logger.log("New term range: %s", termRange.getA1Notation()  );

  var rule = SpreadsheetApp.newDataValidation().requireValueInList(termRange.getValues()).build();  
  Logger.log("validation rule: " + rule);  
  range.setDataValidation(rule);
  
  return {"status": "1", "message": "Restriction created for " + type + " of " +  label};

}

// check if the range supplied is in an existing range from hidden sheets
// retruns the existing ranges in A1 notation
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
      
    }
  }
  return existingRanges;
}



function intersects(range1, range2) {
       
  // find the corners of the first range
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
  
  // check if any corners in range 1 and within range 2;
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

// check is a point is between two corners
function betweenCorners (x, y, tlx, tly, brx, bry) {
  return (x >= tlx && y >=tly && x <= brx && y <= bry);
}

function arrayUnique(array) {
    var a = array.concat();
    for(var i=0; i<a.length; ++i) {
        for(var j=i+1; j<a.length; ++j) {
            if(a[i][0] === a[j][0])
                a.splice(j--, 1);
        }
    }
    return a;
};

// get restricted values from the ontology and create a data validation in the selected range
function getRestrictionValues(ontology, cls, label, type){
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getActiveSheet();
  var range = sheet.getActiveRange();

  var terms = [[label, cls]];  
  return createRestriction2(range, ontology, cls, label, type, terms);
}

function getRestrictionValuesFromBP(ontology, cls, label, type){
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getActiveSheet();
  var range = sheet.getActiveRange();
  
  var terms = getOntologyTermsFromBP(ontology, cls, label, type);

  
  if (terms.length == 0) {
    return {"status":1, "message": "No " + type + " found for " + cls}; 
  }
  
  return createRestriction2(range, ontology, cls, label, type, terms);
}


function getBPQuery(ontology, cls, type) {

  var encoded = encodeURIComponent(cls);
  var ontologies = "http://data.bioontology.org/ontologies/";
  var clses = "/classes/";
  var parameters = "?apikey=73c9dd31-4bc1-42cc-9b78-cf30741a9723&no_links=true&no_context=true&include=prefLabel,synonym&pagesize=500";  
  
  return ontologies + ontology + clses + encoded + "/" + type + parameters;
}

function getOntologyTermsFromBP(ontology, cls, label, type){
  
  var query = getBPQuery(ontology, cls, type);
  var terms = [];
  try {
    Logger.log(query);
    var text = UrlFetchApp.fetch(query).getContentText();
    var doc = JSON.parse(text);
    
    terms = processResult(doc);
    terms.push([label, cls]);
    
    if(parseInt(doc.pageCount) > 1){
      var url = doc.links.nextPage;
      Logger.log("There's more than one page! There are " + doc.pageCount);
      var done = false;
      while(!done){
        var page = UrlFetchApp.fetch(url).getContentText();
        var data = JSON.parse(page);
        var current = processResult(data);

        terms = terms.concat(current);

        url = data.links.nextPage;

        if(url == null){
          done = true;
        }        
      }
    }

  } catch (e) {
   showToast("Couldn't query BioPortal", "Error", 5) 
  }
  
  return terms; 

}

function processResult(result){
  var values = [];
  
  for(var i=0; i<result.collection.length; i++){
    var current = result.collection[i];
    var value = new Array(2);
    
    value[0] = current.prefLabel;
    value[1] = current["@id"];
    
    values.push(value);
  }
  return values;
}


function removeRestriction(){
  Logger.log("Removal request received");
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

