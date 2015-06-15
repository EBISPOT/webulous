
/**
 *
 * Get a list of template on a webulous server
 *
 * @param {string} url The base URI of the webulous template server
 */
function getTemplates(url) { 
  if (url == "") {
    throw new Error ("URL can't be empty");
  }
  return getObjectFromUrl(url + "/templates.json");
}

/**
 *
 * Creates a new sheet based on data form a webulous server 
 *
 * @param {string} url The base URI of the webulous template server
 * @param {string} id The id of the template on the webulous template server
 */
function setSource(url, id, rows){
  
  if (url == null || id == null) {
    throw new Error ("You must supply a valid template server url");
  }
  
  var template = url + "/api/templates/" + id;

  var sourceData = getObjectFromUrl(template);
  var sourceSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");
  
  if(sourceSheet == null && sourceData.description != null){
    sourceSheet = SpreadsheetApp.getActiveSpreadsheet().insertSheet("SourceData");
    sourceSheet.hideSheet();
    // set the webulous server and template id
    var baseURI = sourceSheet.getRange("A1");
    baseURI.setValue(url);
    var templateId = sourceSheet.getRange("B1");
    templateId.setValue(id);
    var templateName = sourceSheet.getRange("C1");
    templateName.setValue(sourceData.description);
  } else {
   
    var templateName = sourceSheet.getRange("C1").getValue();
    if (templateName != sourceData.description) {
      var ui = SpreadsheetApp.getUi();    
      var alert = ui.alert("You have already loaded the template \"" + templateName + "\", you must remove the template using \"Remove template\" from the Webulous menu", ui.ButtonSet.OK_CANCEL); 
      return;
    }
    
  }
  
  /** currently not supporting this feature
  //this sets the source ontology, which be default goes into cells A2 (acronym) and B2 (full name)
   
  saveToRestrictedOntologySet(sourceData.activeOntology.acronym, sourceData.activeOntology.name, sourceData.activeOntology.iri);
  
  //this sets the other import ontologies  
    var imports = sourceData.imports;
  
  for(var s=0; s<imports.length; s++){    
  saveToRestrictedOntologySet(imports[s].acronym, imports[s].name, imports[s].iri);
  }
  **/

  _initilaiseFromTemplate(sourceData, rows);

}



/**
 *
 * This method does the actual work of creating the new sheet based on source data from 
 * a webulous server
 *
 * @param {string} sourceData data form the webulous server
 */
function _initilaiseFromTemplate (sourceData, numberOfRows) {

  var activeSheet;
  if (SpreadsheetApp.getActiveSpreadsheet().getSheetByName(sourceData.description) == null) {
    activeSheet = SpreadsheetApp.getActiveSpreadsheet().insertSheet(sourceData.description);
  }
  else {
   activeSheet  = SpreadsheetApp.getActiveSpreadsheet().getSheetByName(sourceData.description)
  }
  
  if (numberOfRows == null) {
    numberOfRows = sourceData.defaultNumberOfRows;
  }
  
  
  if (sourceData.dataRestrictions != null) {
    
    for (var i=0; i<sourceData.dataRestrictions.length; i++) {
      // add the header in the active sheet
      activeSheet.getRange(1, sourceData.dataRestrictions[i].columnIndex).setValue(sourceData.dataRestrictions[i].restrictionName)
      // create a data validation
      var datarange = activeSheet.getRange(2, sourceData.dataRestrictions[i].columnIndex, numberOfRows)
      if (sourceData.dataRestrictions[i].values.length>1) {
          _createRestriction(datarange, sourceData.description, sourceData.dataRestrictions[i].restrictionName, sourceData.dataRestrictions[i].defaultValue, "SUPPLIED", sourceData.dataRestrictions[i].values);
      }
    }
  }
}

function getTemplateServerURL () {
  var sourceSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");
  if (sourceSheet != null) {
    return sourceSheet.getRange("A1").getValue();
  }
}

function getTemplateId () {
  var sourceSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");
  if (sourceSheet != null) {
    return sourceSheet.getRange("B1").getValue();
  }
}

function getTemplateName () {
  var sourceSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");
  if (sourceSheet != null) {
    return sourceSheet.getRange("C1").getValue();
  }
}


/**
 * Get the name and URI for all ontologies that are restricted by this template
 *
 * @return {ontology[]} Returns a list of ontology objects like {"acronym":shortname, "name":fullname, "@id":iri};
 */
function getRestrictedOntologySet() {
 
  var source = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");
  if(source == null){
    return [];
  }

  var max = source.getLastRow();
  var data = source.getRange(2, 1, max, 3)
  var ontologies = new Array(max-1);
  for (var i = 0; i < max; i++) {   
    var shortname = data.getCell(i+1, 1).getValue();
    var fullname = data.getCell(i+1, 2).getValue();
    var iri = data.getCell(i+1, 3).getValue();
    if (shortname != '' && fullname != '' && iri != '') {
      ontologies[i] = {"acronym":shortname, "name":fullname, "@id":iri};
    }
  }
  return ontologies;
}

/**
 * Set the name and URI for all ontologies that are restricted by this template
 */
function saveToRestrictedOntologySet(shortname, fullname, iri) {
 
  
  var source = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");
  
  if(source == null){
    source = SpreadsheetApp.getActiveSpreadsheet().insertSheet("SourceData");
    source.hideSheet();
  }

  // get any existing ontologies and delete them
  var max = source.getLastRow();
  var data = source.getRange(2, 1, max, 3);
  var exists = false;
  for (var i = 0; i < max; i++) {   
    var c_name = data.getCell(i+1, 1).getValue();
    var c_full = data.getCell(i+1, 2).getValue();
    var c_iri = data.getCell(i+1, 3).getValue();
    if (iri == c_iri) {
      exists = true; 
      return;
    }
  }

  // if we get here add it as a new ontology to the sheet
  var range = source.getRange(max+1,1,1,3);
  range.getCell(1, 1).setValue(shortname);
  range.getCell(1, 2).setValue(fullname);
  range.getCell(1, 3).setValue(iri);
  
}
