
/**
 * Search bioportal for a term with an optional restriction to an individual ontology
 *
 */

function searchBioportal(term, ontology) {

  term = "liver";
  ontology = "EFO";

  if (term.length >2) {
    var search = "http://data.bioontology.org/search?q=";
    var ontologies = "&ontologies=";
    var parameters = "&include=prefLabel,synonym"
    + "&exact_match=false"
    + "&no_links=true"
    + "&require_definition=true"
    + "&no_context=true"
    + "&apikey=73c9dd31-4bc1-42cc-9b78-cf30741a9723"
    + "&pagesize=10";

    var query = search+term+parameters;
    if (ontology != null || ontology != '') {
      query += ontologies+ontology;
    }
    Logger.log(query);
    var text = UrlFetchApp.fetch(query).getContentText();
    var doc = JSON.parse(text);

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

}
