

function submitData(comment, apikey) {
  var ui = SpreadsheetApp.getUi();    

  var request = {};
  
  var url = getTemplateServerURL()
  var templateId = getTemplateId();
  var email = Session.getActiveUser().getEmail();
  var data = getData();
  
  if (url == null || templateId == null) {
   ui.alert ("No template server configured, you must load an existing template from an existing Webulous server") ;
  }
  else if (email == null) {
    ui.alert("You must be logged in with a valid google account and email to use this service");
  }
  else if (data.length == 0) {
   ui.alert("You must supply at least one row of data"); 
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
    
    Logger.log(response);
    
    SpreadsheetApp.getActiveSpreadsheet().toast(message, 'Status');      
    
  }
}



function getData(){
 //process the spreadsheet row by row 
  
  var dataSheet = SpreadsheetApp.getActiveSheet();  
  var colNum = dataSheet.getLastColumn();
  var rowNum = dataSheet.getLastRow();
    
  return dataSheet.getRange(2, 1, rowNum-1, colNum).getValues();
  
}


