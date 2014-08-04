/**
 * Created by dwelter on 09/07/14.
 */


/**
 * Creates a menu entry in the Google Docs UI when the document is opened.
 */


function onOpen() {
    SpreadsheetApp.getUi() // Or DocumentApp or FormApp.
        .createMenu('Custom Menu')
        .addItem('Show sidebar', 'showSidebar')
        .addToUi();

}

function showSidebar() {
    var html = HtmlService.createHtmlOutputFromFile('sidebar')
        .setTitle('My custom sidebar')
        .setWidth(300);
    SpreadsheetApp.getUi() // Or DocumentApp or FormApp.
        .showSidebar(html);
}

//function onOpen() {
//
//    var spreadsheet = SpreadsheetApp.getActiveSpreadsheet();
//
//    SpreadsheetApp.getUi().createAddonMenu()
//        .addItem('Perform Ontology Search', 'showOntologySearchSidebar')
//        .addItem('Run Annotator', 'showAnnotatorSidebar')
//        .addItem('About', 'showAbout')
//        .addItem('Settings', 'showSettings')
//        .addToUi();
//}
//
///**
// * Runs when the add-on is installed.
// */
//function onInstall() {
//    onOpen();
//}
//
//
//function showOntologySearchSidebar() {
//    var ui = HtmlService.createHtmlOutputFromFile('SearchSidebar')
//        .setTitle('Webulous');
//    SpreadsheetApp.getUi().showSidebar(ui);
//}
//
//function showAnnotatorSidebar() {
//    var ui = HtmlService.createHtmlOutputFromFile('AnnotatorSidebar')
//        .setTitle('Webulous');
//    SpreadsheetApp.getUi().showSidebar(ui);
//}
//
//
//function runSearch(service, term) {
//    return performSearch(service,term);
//}
//
//function runAnnotator() {
//    return performAnnotation();
//}
//
//function performSearch(service, term) {
//    if (service == "bioportal") {
//        return searchBioPortal(term);
//    } else {
//        return searchLOV(term);
//    }
//}