
/**
 * Search bioportal for a term with an optional restriction to an individual ontology
 *
 * @param {string} term Term to lookup on bioportal
 * @param {string} ontology Ontology to search for term in
 *
 */

function searchBioportal(term, ontology) {

  if (term.length >2) {
    var search = "http://data.bioontology.org/search?q=";
    var ontologies = "&ontologies=";
    var parameters = "&include=prefLabel,definition"
        + "&exact_match=false"
        + "&no_links=false"
        + "&no_context=true"
        + "&apikey=73c9dd31-4bc1-42cc-9b78-cf30741a9723"
        + "&pagesize=20";

    var query = search+term+parameters;
    if (ontology != null || ontology != '') {
      query += ontologies+ontology;
    }
    return getObjectFromUrl(query);
  }
}

/**
 * Construct a query string for bioportal
 *
 * @return {string} A bioportal REST API query string for an ontology, class and type
 * @param ontology the ontology to query
 * @param cls the uri of the term
 * @param type the type {subclasses, descendants}
 **/
function _getBPQuery(ontology, cls, type) {

  var encoded = encodeURIComponent(cls);
  var ontologies = "http://data.bioontology.org/ontologies/";
  var clses = "/classes/";
  var parameters = "?apikey=73c9dd31-4bc1-42cc-9b78-cf30741a9723&no_links=true&no_context=true&include=prefLabel,synonym&pagesize=500";

  return ontologies + ontology + clses + encoded + "/" + type + parameters;
}

/**
 * Query bioportal for terms. Bioportal returns 50 results per page, so this function
 * will page through the results until the end
 * @param ontology ontology where the terms came from
 * @param cls  uri of the term
 * @param label label for the term
 * @param type type of terms {children or descendants}
 *
 * @return {string [][]} 2D array of terms {label, uri}
 */
function getOntologyTermsFromBP(ontology, cls, label, type){

  var query = _getBPQuery(ontology, cls, type);
  var terms = [];
  try {
    var doc = getObjectFromUrl(query);
    
    if (doc.pageCount != null && doc.collection != null) {
      var pageLength = doc.collection.length
      var pageCount = doc.pageCount;
      var total = (pageLength * pageCount);
      
      if (total > 5000) {
        var ui = SpreadsheetApp.getUi();    
        var alert = ui.alert("You are trying to create a data restriction with " + total + " terms. This is above the recommended limit of 5000 terms so it may take a while to create the validation and the sheet performance may suffer. Do you want to continue?", ui.ButtonSet.YES_NO);
        if(alert == ui.Button.NO){
          return terms;
        }  
      }
    }
    
    terms = _processResult(doc);
    terms.push([label, cls]);
    
    if(parseInt(doc.pageCount) > 1){
      var url = doc.links.nextPage;
      var done = false;
      while(!done){
        var data = getObjectFromUrl(url);
        var current = _processResult(data);

        terms = terms.concat(current);

        url = data.links.nextPage;

        if(url == null){
          done = true;
        }
      }
    }

  } catch (e) {
    throw new Error("Can't query BioPortal")
  }

  return terms;

}
/**
 * Get sorted list of ontologies from bioportal
 **/

function getBioPortalOntologies () {


  // if ontologies already restricted from template use them

  if (SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData") != null) {
    var ontologies = getRestrictedOntologySet();

    if (ontologies.length > 0) {
      return ontologies;
    }
  }
  // otherwise get all ontologies from bioportal 

  var search = "http://data.bioontology.org/ontologies?apikey=73c9dd31-4bc1-42cc-9b78-cf30741a9723";

  var json = getObjectFromUrl(search);
  return json.sort(function(a,b) {
    return (a.name > b.name) ? 1 : (a.name < b.name) ? -1 : 0;
  });

}

/**
 * COnvert bioprtaol JSON result into array of simple data object [label, uri]
 * @param result
 * @return {Array}
 * @private
 */
function _processResult(result){
  var values = [];

  if (result.collection != null) {
    for(var i=0; i<result.collection.length; i++){
      var current = result.collection[i];
      var value = new Array(2);
      
      value[0] = current.prefLabel;
      value[1] = current["@id"];
      
      values.push(value);
    }
  }
  
  return values;
}