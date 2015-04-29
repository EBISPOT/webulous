function onOpen() {
  var ui = SpreadsheetApp.getUi();
    ui.createAddonMenu()
        .addItem('Ontology search', 'showSidebar')
        .addItem('Remove selected validation', 'removeRestriction')        
        .addItem('Remove all validations', 'removeAllValidations')        
        .addItem('Remove template', 'removeTemplate')        
        .addSeparator()
        .addSubMenu(ui.createMenu('Webulous server')
              .addItem('Load a template...', 'showSelectTemplate')
              .addItem('Submit populated template...', 'showSubmitData'))      
        .addSeparator()
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
    .setWidth(430)
    .setHeight(240);
  SpreadsheetApp.getUi() 
      .showModalDialog(html, 'Load template from server');
}



function noSource(){
  var ui = SpreadsheetApp.getUi();    
  var alert = ui.alert("You need to choose a Webulous server to submit data", ui.ButtonSet.OK_CANCEL);
  if(alert == ui.Button.OK){  
     showSelectTemplate();
  }    
}

function showSubmitData(){
  if(SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData") == null){
    noSource();   
  }
  else if(SpreadsheetApp.getActiveSheet().getLastColumn() == 0){
    var ui = SpreadsheetApp.getUi();    
    var alert = ui.alert("This spreadsheet contains no data.");    
  }
  else{
    var html = (HtmlService.createTemplateFromFile('SubmitData').evaluate())
    .setSandboxMode(HtmlService.SandboxMode.IFRAME)
    .setWidth(410)
    .setHeight(310);
    SpreadsheetApp.getUi() 
    .showModalDialog(html, 'Submit populated template to server');   
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
        ss.setActiveSheet(templateSheet);
        _removeAllValidationsFromSheet();

        if (templateSheet != null) {
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
      Logger.log("Found sheet with name %s", sheetName);
      ss.deleteSheet(ss.getSheetByName(sheetName));
    } catch (e) {
      
    }
  }
  
  // remove any restrictions from the active sheet
  var sheet = ss.getActiveSheet();
  var range = sheet.getRange(1, 1, sheet.getMaxRows(), sheet.getMaxColumns());
  range.clearDataValidations();
}


function showAbout(){  
  var html = HtmlService.createHtmlOutputFromFile('About')
  .setWidth(280)
      .setHeight(180);
  SpreadsheetApp.getUi() 
      .showModalDialog(html, 'About Webulous');
}
