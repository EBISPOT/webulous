//This is the App master file

//TO DO - bug fixes


function onOpen() {
    SpreadsheetApp.getUi()
        .createMenu('Webulous')
        .addItem('Set source ontology', 'showSetSource')
        .addItem('Open ontology term selection', 'showSidebar')
        .addItem('Select OPPL patterns', 'showPatterns')
        .addItem('Assign variables', 'selectVariables')
        .addItem('Submit data to server', 'showSubmitData')
        .addItem('About', 'showAbout')
        .addItem('Help', 'showHelp')
        .addToUi();

}

function showSidebar() {
    if(SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData") == null){
        noSource;
    }

    else{
        var html = (HtmlService.createTemplateFromFile('Sidebar').evaluate())
            .setSandboxMode(HtmlService.SandboxMode.NATIVE)
            .setTitle('Data Restrictions')
            .setWidth(400);
        SpreadsheetApp.getUi()
            .showSidebar(html);
    }
}


function showSetSource(){
    var html = HtmlService.createHtmlOutputFromFile('SourceSelect')
        .setWidth(400)
        .setHeight(200);
    SpreadsheetApp.getUi()
        .showModalDialog(html, 'Source ontology selection');
}

function showPatterns(){
    if(SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData") == null){
        noSource;
    }
    else{
        var html = (HtmlService.createTemplateFromFile('OPPLPatterns').evaluate())
            .setWidth(800)
            .setHeight(500);
        SpreadsheetApp.getUi()
            .showModalDialog(html, 'OPPL pattern selection');
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

function selectVariables(){
    if(SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData") == null){
        noSource;
    }
    else{
        var html =(HtmlService.createTemplateFromFile('VariableSelector').evaluate())
            .setWidth(400)
            .setHeight(500);
        SpreadsheetApp.getUi()
            .showModalDialog(html, 'Variable Selection');
    }
}

function noSource(){
    var ui = SpreadsheetApp.getUi();
    var alert = ui.alert("You need to select a source ontology first", ui.ButtonSet.OK_CANCEL);
    if(alert == ui.Button.OK){
        showSetSource();
    }
}

function showSubmitData(){
    if(SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData") == null){
        noSource;
    }

    else if(SpreadsheetApp.getActiveSheet().getLastColumn() == 0){
        var ui = SpreadsheetApp.getUi();
        var alert = ui.alert("This spreadsheet contains no data.");
    }

    else{
        var ui = SpreadsheetApp.getUi();
        var alert = ui.alert("Are you sure you are ready to submit? Have you selected all the required patterns and assigned all the necessary variables?", ui.ButtonSet.YES_NO);
        if(alert == ui.Button.YES){
            submitData();
        }
    }
}