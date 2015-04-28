

/**
 * Submits data in the actove sheet to the Webulous server
 *
 * @param {string} comment optional comment on the submission
 * @param {string} apikey optional API key for URIGen users
 *
 */

function submitData(comment, apikey) {
  var ui = SpreadsheetApp.getUi();    

  var request = {};
  
  var url = getTemplateServerURL()
  var templateId = getTemplateId();
  var email = Session.getEffectiveUser().getEmail();
  var data = getData();
  
  if (url == null || templateId == null) {
    throw new Error ("No template server configured, you must load an existing template from an existing Webulous server") ;
  }
  else if (email == "") {
      throw new Error ("You must be logged in with a valid google account and email to use this service");
  }
  else if (data.length == 0) {
      throw new Error ("You must supply at least one row of data");
  }
  else {
    
    request.templateId = templateId;
    request.reference = comment;
    request.urigenApiKey = apikey;
    request.email = email;
    request.data = data;
    var payload = JSON.stringify(request);
    
    var submissionURL = url + "/submissions";
    var options = { "method":"POST",
                   "contentType" : "application/json",
                   "payload" : payload
                  };
    
    var response = UrlFetchApp.fetch(submissionURL, options);
    var responseJson = JSON.parse(response)
    var message = responseJson.message;

    SpreadsheetApp.getActiveSpreadsheet().toast(message, 'Status');      
    
  }
}

/**
 * Get the data form the active sheet ignoring first row
 *
 * @return {data[][]} 2D array of values form the active spreadsheet
 *
 */

function getData(){
 //process the spreadsheet row by row 
  
  var dataSheet = SpreadsheetApp.getActiveSheet();  
  var colNum = dataSheet.getLastColumn();
  var rowNum = dataSheet.getLastRow();
  
  if (rowNum == 1) {
   throw new Error("You must supply at least one row of data"); 
  }
  
  return dataSheet.getRange(2, 1, rowNum-1, colNum).getValues();
  
}


