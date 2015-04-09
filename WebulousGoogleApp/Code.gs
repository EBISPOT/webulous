function onOpen() {
  var ui = SpreadsheetApp.getUi();
    ui.createMenu('Webulous')
        .addItem('Ontology search', 'showSidebar')
        .addItem('Remove selected restriction', 'removeRestriction')        
        .addItem('Reset this spreadsheet', 'resetSpreadsheet')        
        .addSeparator()
        .addSubMenu(ui.createMenu('Webulous server')
              .addItem('Load template...', 'showSelectTemplate')
              .addItem('Submit...', 'showSubmitData'))      
        .addSeparator()
        .addItem('About', 'showAbout')        
        .addItem('Help', 'showHelp')        
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
    .setWidth(400)
    .setHeight(300);
  SpreadsheetApp.getUi() 
      .showModalDialog(html, 'Select template from server');
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
    .setWidth(400)
    .setHeight(400);
    SpreadsheetApp.getUi() 
    .showModalDialog(html, 'Submit data to Webulous server');   
  }        
}

function resetSpreadsheet() {
  var ui = SpreadsheetApp.getUi();    
  var alert = ui.alert("Are you sure you want to reset spreadsheet, removing all patterns and restrictions?", ui.ButtonSet.OK_CANCEL);
  if(alert == ui.Button.OK){  
    
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
    
    if (ss.getSheetByName('SourceData') != null) {
      ss.deleteSheet(ss.getSheetByName('SourceData'));
    }
  }  
}

function showAbout(){  
  var html = HtmlService.createHtmlOutputFromFile('About')
  .setWidth(400)
      .setHeight(300);
  SpreadsheetApp.getUi() 
      .showModalDialog(html, 'About Webulous');
}


function showHelp(){  
  var html = HtmlService.createHtmlOutputFromFile('Help')
      .setWidth(400)
      .setHeight(300);
  SpreadsheetApp.getUi() 
      .showModalDialog(html, 'Help');
}