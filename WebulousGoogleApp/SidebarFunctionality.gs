function getRestrictionValues(ontology, cls, type){
    Logger.log(cls);
    Logger.log(type);

    var ss = SpreadsheetApp.getActiveSpreadsheet();
    var sheet = ss.getActiveSheet();
    var range = sheet.getActiveRange();

    var restrictions = ss.getSheetByName("Restrictions");
    var col;
    var rows;
    var restrictionLocations;

    if(restrictions == null){
        restrictions = ss.insertSheet("Restrictions");
        restrictionLocations = ss.insertSheet("RestrictedFields");
        col = 1;
        Logger.log(col);
    }
    else{
        restrictionLocations = ss.getSheetByName("RestrictedFields");
        col = restrictions.getLastColumn()+1;
        Logger.log(col);
    }
    restrictions.hideSheet();
    restrictionLocations.hideSheet();

    var previous = false;
    if(col > 1){
        var existing = restrictions.getRange(1, 1, 2, col-1).getValues();

        for(var n=0; n < existing[0].length; n+=2){
            var ont = existing[0][n];
            var ty = existing[0][n+1];
            var cls = existing[1][n];

            if((ont == ontology) && (ty == type) && (cls == cls)){
                Logger.log("Already have values for restriction " + type + " of " + cls + " in " + ontology);
                previous = true;
                col = n+1;
                rows = restrictions.getLastRow();
                break;
            }
        }
    }

    if(!previous){
        Logger.log("No previous restriction matching these parameters exists");
        var terms = getOntologyTerms(ontology, cls, type);
        rows = terms.length;
        Logger.log(rows);
        restrictions.getRange(1, col).setValue(ontology);
        restrictions.getRange(1, col+1).setValue(type);

        for(var i=0; i<terms.length; i++){
            var row = i+2;

            restrictions.getRange(row, col).setValue(terms[i].name);
            restrictions.getRange(row, col+1).setValue(terms[i].uri);
            Logger.log("Setting " + terms[i].name + " and " + terms[i].uri);
        }
    }
    Logger.log(col + ' ' + rows);
    Logger.log(restrictions.getRange(2, col, rows).getA1Notation());
    var rule = SpreadsheetApp.newDataValidation().requireValueInRange(restrictions.getRange(2,col,rows)).build();
    range.setDataValidation(rule);

    var restrictRange = restrictionLocations.getRange(range.getA1Notation());
    Logger.log(restrictRange.getA1Notation());

    var colNum = restrictRange.getNumColumns();
    var rowNum = restrictRange.getNumRows();

    for(var c=1; c<colNum+1; c++){
        for(var r=1; r<rowNum+1; r++){
            restrictRange.getCell(r, c).setValue(col);
        }
    }
}

function getOntologyTerms(ontology, cls, type){
    var parentID = getParentID(ontology, cls);

    if(parentID == null){
        SpreadsheetApp.getUi().alert(cls + " was not found in ontology " + ontology + ". Please make sure you entered a valid term.");
    }
    else{
        var encoded = encodeURIComponent(parentID);

        var parent =[];
        parent.name = cls;
        parent.uri = parentID;

        var ontologies = "http://data.bioontology.org/ontologies/";
        var clses = "/clses/";
        var parameters = "?apikey=73c9dd31-4bc1-42cc-9b78-cf30741a9723&no_links=true&no_context=true&include=prefLabel,synonym&pagesize=500";

        var query = ontologies + ontology + clses + encoded + "/" + type + parameters;
        Logger.log(query);
        var text = UrlFetchApp.fetch(query).getContentText();
        var doc = JSON.parse(text);

        if(parseInt(doc.pageCount) > 50){
            SpreadsheetApp.getUi().alert(cls + " has too many " + type + " in " + ontology + " to process. You can apply different restrictions to different cells in the same column, eg \"cardiovascular disease\" instead of \"disease\".");
        }

        else{
            var terms = processResult(doc);
            terms.unshift(parent);

            if(parseInt(doc.pageCount) > 1){

                var url = doc.links.nextPage;
                Logger.log("There's more than one page! There are " + doc.pageCount);
                var done = false;
                while(!done){
                    var page = UrlFetchApp.fetch(url).getContentText();
                    var data = JSON.parse(page);
                    var current = processResult(data);

                    for(var k=0; k<current.length; k++){
                        terms.push(current[k]);
                    }
                    url = data.links.nextPage;

                    if(data.nextPage == null){
                        done = true;
                    }
                }
            }
            return terms;
        }
    }
}

function processResult(result){
    var values = [];

    for(var i=0; i<result.collection.length; i++){
        var current = result.collection[i];
        var value = {};

        value.name = current.prefLabel;
        value.uri = current["@id"];

        values.push(value);
        Logger.log(value.name + ' ' + value.uri);
    }
    return values;
}


function getParentID(ontology, cls){
    var search = "http://data.bioontology.org/search?q=";
    var ontologies = "&ontologies=";
    var parameters = "&include=prefLabel,synonym"
        + "&exact_match=true"
        + "&no_links=true"
        + "&no_context=true"
        + "&apikey=73c9dd31-4bc1-42cc-9b78-cf30741a9723"
        + "&pagesize=5000";

    var query = search+cls+ontologies+ontology+parameters;
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

function removeRestriction(){
    Logger.log("Removal request received");
    var ss = SpreadsheetApp.getActiveSpreadsheet();
    var sheet = ss.getActiveSheet();
    var range = sheet.getActiveRange();
    var location = range.getA1Notation();
    Logger.log("For location " + location);

    var restrictions = ss.getSheetByName("RestrictedFields");

    if(restrictions == null || restrictions.getLastColumn() == 0){
        SpreadsheetApp.getUi().alert("No restrictions to remove");
    }

    var cells = restrictions.getRange(location);

    var colNum = cells.getNumColumns();
    var rowNum = cells.getNumRows();

    for(var c=1; c<colNum+1; c++){
        for(var r=1; r<rowNum+1; r++){
            cells.getCell(r, c).setValue('');
        }
    }
    range.clearDataValidations();
}