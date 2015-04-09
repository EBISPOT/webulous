
/**
 * Search bioportal for a term with an optional restriction to an individual ontology
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
    Logger.log(query);
    var text = UrlFetchApp.fetch(query).getContentText();
    return JSON.parse(text);
  } 
}

function getSubclassesFromBioPortal (uri, descendants) {
  
     //   Logger.log(doc);
    var uri;
    for(var i=0; i<doc.collection.length; i++){ 
      var record = doc.collection[i]; 
      var label = record.prefLabel;
      
      Logger.log(label);
      
      if(label.toLowerCase() === cls.toLowerCase()){
        uri = record["@id"];
        Logger.log(uri);
        break;
      }
      else{
        var synonyms = record.synonym;
        
        if(synonyms != null && synonyms.indexOf(cls) == -1){
          for(var k=0; k<synonyms.length; k++){
            if(synonyms[k].toLowerCase() === cls.toLowerCase()){
              uri = record["@id"];
              Logger.log(uri);
              break;
            }      
          }
          Logger.log(label + " does not match " + cls);
        }  
        else{
          uri = record["@id"];
          Logger.log(uri);
          break;
        }
      }
    }
  
  return uri;   
  
}

/**
 * Get list of ontologies from bioportal 
 **/

function getBioPortalOntologies () {
           

  // if ontologies already restricted from template use them
  
  if (SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData") != null) {
    var ontologies = getRestrictedOntologySet();
    Logger.log("found " + ontologies.length + " ontologies in source");

    if (ontologies.length > 0) {
      return ontologies;
    }
  }
  // otherwise get all ontologies from bioportal 
  
  var search = "http://data.bioontology.org/ontologies?apikey=73c9dd31-4bc1-42cc-9b78-cf30741a9723";

  var text = UrlFetchApp.fetch(search).getContentText();
  var json = JSON.parse(text);
  return json.sort(function(a,b) {
    return (a.name > b.name) ? 1 : (a.name < b.name) ? -1 : 0;    
  });

}
