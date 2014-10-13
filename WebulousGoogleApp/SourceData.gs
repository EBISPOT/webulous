//wwwdev.ebi.ac.uk/fgpt/webulous/api/source/EFO

function setSource(ontology){
    var sourceData = getSourceData(ontology);

    var sourceSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");

    if(sourceSheet == null){
        sourceSheet = SpreadsheetApp.getActiveSpreadsheet().insertSheet("SourceData");
    }

    sourceSheet.hideSheet();

    var baseURI = sourceSheet.getRange("A1");
    var uri = ontology.replace("source/"+sourceData.sourceOntology.acronym, "");
    baseURI.setValue(uri);

//this sets the source ontology, which be default goes into cells A2 (acronym) and B2 (full name)
    var source = sourceSheet.getRange("A2");
    source.setValue(sourceData.sourceOntology.acronym);

    var name = sourceSheet.getRange("B2");
    name.setValue(sourceData.sourceOntology.name);

    Logger.log(sourceData.sourceOntology.acronym);

    Logger.log(sourceData.sourceOntology.name);

//this sets the other import ontologies
    var imports = sourceData.importOntologies;

    for(var s=0; s<imports.length; s++){
        var acro = sourceSheet.getRange(s+3, 1);
        acro.setValue(imports[s].acronym);

        var name = sourceSheet.getRange(s+3, 2);
        name.setValue(imports[s].name);
    }

    var patterns = sourceData.opplPatterns;

    for(var t=0; t<patterns.length; t++){
        var col = t+3;

        var pat = patterns[t];

        var name = pat.name;
        sourceSheet.getRange(2,col).setValue(name);
        var pattern = pat.pattern;
        sourceSheet.getRange(3,col).setValue(pattern);

        var vars = pat.variables;

        for(var u=0; u<vars.length; u++){
            var row = 4+u;

            sourceSheet.getRange(row, col).setValue(vars[u]);
        }
    }

    setVariables();
}


function getSourceData(ontology){

    //TO DO - API call goes here

    var apiCall = "http://" + ontology
    Logger.log(apiCall);
    var result = UrlFetchApp.fetch(apiCall).getContentText();

// var result = '{"status": "OK", ' +
//                 '"sourceOntology": { "acronym": "EFO", "name": "Experimental Factor Ontology"}, ' +
//                 '"importOntologies": [ { "acronym": "CLO", "name": "Cell Line Ontology"},  { "acronym": "GO", "name": "Gene Ontology"}],' +
//                 '"opplPatterns": [ ' +
//                   '{ "name": "Bearer of some disease", "pattern": "?cellLine subClassOf bearer_of some ?disease", "variables": ["?cellLine:CLASS", "?disease:CLASS"] },' +
//                   '{ "name": "Cell type, species, tissue", "pattern": "?cellLine subClassOf derives_from some (?cellType and part_of some (?tissue and part_of some ?species))", "variables": ["?cellLine:CLASS", "?cellType:CLASS", "?species:CLASS", "?tissue:CLASS"] },' +
//                   '{ "name": "Has quality some sex", "pattern": "?cellLine subClassOf has_quality some ?sex", "variables": ["?cellLine:CLASS", "?sex:CLASS"] }' +
//                  ']}';
//

    var data = JSON.parse(result);
//  Logger.log(data);
//
//  var a = data.importOntologies;
//
//  for(var i =0; i< a.length; i++){
//      Logger.log(a[i]);
//  }
//
//  var b = data.opplPatterns;
//
//  for(var j =0; j< b.length; j++){
//    var pat = b[j];
//
//    Logger.log(pat.name);
//    Logger.log(pat.pattern);
//    Logger.log(pat.variables);
//
//  }

    return data;
}

function setVariables(){

    var sourceSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");

    var variableSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("Variables");

    if(variableSheet == null){
        variableSheet = SpreadsheetApp.getActiveSpreadsheet().insertSheet("Variables");
    }
    variableSheet.hideSheet();
    var existing = [];
    var potential;

    if(variableSheet.getLastRow() != 0){
        potential = variableSheet.getRange(1, 1, variableSheet.getLastRow()).getValues();

        for(var k=0; k<potential.length; k++){
            if(potential[k][0] != ''){
                existing[k] = potential[k][0];
                Logger.log("Already found variable " + existing[k]);
            }
        }
    }

    var dataRange = sourceSheet.getRange(4, 3, sourceSheet.getLastRow(), sourceSheet.getLastColumn()).getValues();
    var variables = [];

    for(var row=0; row<dataRange.length; row++){
        for(var col=0; col<dataRange[row].length; col++){
            var current = dataRange[row][col].split(":")[0];

            if((variables.indexOf(current) == -1) && (current != '') && (existing.indexOf(current) == -1)){
                variables.push(current);
            }
        }
    }

    var start = existing.length;
    Logger.log(start);
    for(var i=0; i < variables.length; i++){
        variableSheet.getRange(start+i+1, 1).setValue(variables[i]);
        Logger.log("Writing variable " + variables[i] + " into cell " + variableSheet.getRange(start+i+1, 1).getA1Notation());
    }
}


