function onOpen() {
  var ui = SpreadsheetApp.getUi();
    ui.createAddonMenu()
        .addItem('Ontology search...', 'showSidebar')
        .addItem('Manage validations...', 'viewValidations')
        .addItem('Webulous template...', 'webulousServer')        
        .addItem('About', 'showAbout')
        .addToUi();

}

function showSidebar() {
  var html = (HtmlService.createTemplateFromFile('OntologySearch').evaluate())
       .setSandboxMode(HtmlService.SandboxMode.IFRAME)
       .setTitle('Ontology Search')
  SpreadsheetApp.getUi() 
        .showSidebar(html);
}

function showSelectTemplate () {
    var html = (HtmlService.createTemplateFromFile('SelectTemplate').evaluate())
     .setSandboxMode(HtmlService.SandboxMode.IFRAME)
    .setWidth(350)
    .setHeight(290);
  SpreadsheetApp.getUi() 
      .showModalDialog(html, 'Load template from server');
}

function webulousServer () {
 if(SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData") == null){
     showSelectTemplate();
 }
 else {
    showSubmitData();
 }
  
}

function noSource(){
  var ui = SpreadsheetApp.getUi();    
  var alert = ui.alert("You can only upload data for a template loaded from a Webulous server.\n Would you like to load a Webulous template now?", ui.ButtonSet.YES_NO);
  if(alert == ui.Button.YES){  
     showSelectTemplate();
  }    
}

function showSubmitData(){
  if(SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData") == null){
    noSource();   
  }
  else{
    
    var templateName = getTemplateName();
    var templateSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName(templateName);
    SpreadsheetApp.getActiveSpreadsheet().setActiveSheet(templateSheet);
    
    
    var html = (HtmlService.createTemplateFromFile('SubmitData').evaluate())
    .setSandboxMode(HtmlService.SandboxMode.IFRAME)
    .setWidth(440)
    .setHeight(340);
    SpreadsheetApp.getUi() 
    .showModalDialog(html, 'Submit data for template to the Webulous server');   
  }        
}

function viewValidations() {
  
  // get hidden sheets that are a range
  var sheets = SpreadsheetApp.getActive().getSheets();
  var validationsExist = false;
  
  for (var x = 0 ; x < sheets.length ; x++) {
    var sheetName = sheets[x].getName();
    // get a range from sheet name
    Logger.log("looking for sheet with name %s", sheetName);

    try {
      var existingRange = sheets[x].getRange(sheetName);
      var a1 = existingRange.getA1Notation();
      if (a1 != null) {
       validationsExist = true; 
      }
    } catch (e) {
       // we allow this to fail and continue looking for sheets with this name
    }
  }

  if (validationsExist) {
   var html = (HtmlService.createTemplateFromFile('Validations').evaluate())
    .setSandboxMode(HtmlService.SandboxMode.IFRAME)
    .setWidth(300)
    .setHeight(260);
    SpreadsheetApp.getUi() 
    .showModalDialog(html, 'Manage Validations');   
  }
  else {
    SpreadsheetApp.getUi().alert("No ontology validations defined, use the \"Ontology Search\" option to create new validations")
  }
  
}

function removeAllValidations () {
  var ui = SpreadsheetApp.getUi();    
  var alert = ui.alert("Are you sure you want to remove all data validations from this sheet?", ui.ButtonSet.OK_CANCEL);
  if(alert == ui.Button.OK){  
    _removeAllValidationsFromSheet();
  }  
  
}

function removeTemplate() {
  var ui = SpreadsheetApp.getUi();    
  var ss = SpreadsheetApp.getActive();

  var sourceSheet = ss.getSheetByName('SourceData');

  if (sourceSheet != null) {
      
    var templateName = sourceSheet.getRange("C1").getValue();
   
    if (templateName != null) {
      
      var alert = ui.alert("Are you sure you want to remove \"" + templateName + "\"?", ui.ButtonSet.YES_NO);
      if(alert == ui.Button.YES){  
        var templateSheet = ss.getSheetByName(templateName);
        if (templateSheet != null) {
          ss.setActiveSheet(templateSheet);
          _removeAllValidationsFromSheet();
          ss.deleteSheet(templateSheet);
        }
        ss.deleteSheet(sourceSheet);
      }
    }
  } else {
     ui.alert("No template to remove");
  }
    
}

function _removeAllValidationsFromSheet () {
  var ss = SpreadsheetApp.getActive();
  var activeSheet = SpreadsheetApp.getActiveSheet();
  var sheets = SpreadsheetApp.getActive().getSheets();
  
  for (var x = 0 ; x < sheets.length ; x++) {
    var sheetName = sheets[x].getName();
    // get a range from sheet name
    Logger.log("looking for sheet with name %s", sheetName);
    
    try {
      var existingRange = sheets[x].getRange(sheetName);
      Logger.log("Founds sheet with name %s", sheetName);
      ss.deleteSheet(ss.getSheetByName(sheetName));
      var range = activeSheet.getRange(sheetName);
      range.clearDataValidations();
    } catch (e) {
      
    }
  }
}


function showAbout(){  
  var html = HtmlService.createHtmlOutputFromFile('About')
  .setWidth(560)
      .setHeight(460);
  SpreadsheetApp.getUi() 
      .showModalDialog(html, 'About Webulous');
}
