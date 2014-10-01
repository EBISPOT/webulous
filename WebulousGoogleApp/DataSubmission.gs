//need to change this as it currently submits data for *all* patterns, not just selected ones!

function submitData() {

    var output = {};

    var sourceSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");
    var source = sourceSheet.getRange(1, 1).getValue();

    output.configuration = {};
    output.configuration.sourceOntology = source;

    output.configuration.opplPatterns = processPatterns();

    //this should only return null if not all the variables have been assigned
    if(output.configuration.opplPatterns != null){
        Logger.log(output.configuration.opplPatterns.length);

        output.configuration.dataRestrictions = processRestrictions();
        Logger.log(output.configuration.dataRestrictions.length);

        output.data = getData();
//  Logger.log(JSON.stringify(output));

        var payload = JSON.stringify(output);

        var url = "http://wwwdev.ebi.ac.uk/fgpt/webulous/api/data/all";
        var options = { "method":"POST",
            "contentType" : "application/json",
            "payload" : payload
        };

        var response = UrlFetchApp.fetch(url, options);
        Logger.log(response);


        var ui = SpreadsheetApp.getUi();
        var alert = ui.alert(response.getContentText(), ui.ButtonSet.OK);
    }


}

function processPatterns(){
    var patternList = [];
    var varSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("Variables");
    var varRange = varSheet.getRange(1, 1, varSheet.getLastRow(), 2).getValues();

    var sourceSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");
    var patterns = sourceSheet.getRange(1, 3, sourceSheet.getLastRow(), sourceSheet.getLastColumn()).getValues();
    var selectedCount = 0;

    for(var i=0; i<patterns[0].length; i++){
        if(patterns[0][i] == "selected"){

            var pattern = {};
            pattern.name = patterns[1][i];
            Logger.log(pattern.name + " was selected by the user");
            pattern.variables = [];
            for(var j=3; j<patterns.length; j++){
                if(patterns[j][i] == ""){
                    break;
                }
                else{
                    var varMap = {};
                    var variable = (patterns[j][i].split(":"))[0];
                    Logger.log(variable);
                    var index;

                    for(var k=0; k<varRange.length; k++){
                        if(varRange[k][0] == variable){
                            index = varRange[k][1];
                            if(index == ""){
                                var ui = SpreadsheetApp.getUi()
                                var alert = ui.alert("You need to assign a column for variable " + variable, ui.ButtonSet.OK_CANCEL);
                                if(alert == ui.Button.OK){
                                    selectVariables();
                                    return null;
                                }
                            }
                            else{
                                varMap.variable = variable;
                                varMap.columnIndex = index.charCodeAt(0)-65;
                            }
                        }
                    }
                    pattern.variables.push(varMap);
                }
            }
            patternList.push(pattern);
        }
    }
    return patternList;
}

function processRestrictions(){
    var restrictions = [];
//  var varSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("Variables");
//  var varRange = varSheet.getRange(1, 1, varSheet.getLastRow(), 2).getValues();

    var restrictSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("Restrictions");

    if(restrictSheet == null){
        restrictions.push("No restrictions available");
    }
    else{
        var restrictRange = restrictSheet.getRange(1, 1, restrictSheet.getLastRow(), restrictSheet.getLastColumn()).getValues();

        //we need to know the number of data columns in order to generate the column index
        //   var restrictionNumber = SpreadsheetApp.getActiveSheet().getLastColumn();
        var restrictionNumber = restrictSheet.getLastColumn();

        for(var k=0; k< restrictionNumber; k+=2){
//      var colID = String.fromCharCode(65+k);
            var restriction = {};

            restriction.restrictionIndex = k+1;

//      for(var j=0; j<varRange.length; j++){
//        if(varRange[j][1] == colID){
//         hasVar = true;
//         restriction.opplVariable = varRange[j][0];
//         break;
//        }
//      }
//
//      if(!hasVar){
//         restriction.opplVariable = "null";
//      }

//      for(var i=0; i<restrictRange[0].length; i+=2){
            //       var loc = restrictRange[0][k];
            //       var col = loc.charAt(0);

            //       if(col = colID){
            //       hasRestrict = true;
            restriction.restrictionOntology = restrictRange[0][k];
            restriction.restrictionType = restrictRange[0][k+1];
            restriction.restrictionName = restrictRange[1][k];
            restriction.restrictionURI = restrictRange[1][k+1];
//          break;
//        }
            restrictions.push(restriction);

        }

//      if(!hasRestrict){
//         restriction.restrictionOntology = "null";
//         restriction.restrictionType = "null";
//        restriction.restrictionName = "null";
//        restriction.restrictionURI = "null";
//      }

    }

    return restrictions;
}



function getData(){
    var data = [];
    //process the spreadsheet row by row

    var dataSheet = SpreadsheetApp.getActiveSheet();
    var colNum = dataSheet.getLastColumn();
    var rowNum = dataSheet.getLastRow();

    var dataRange = dataSheet.getRange(2, 1, rowNum-1, colNum).getValues();
    var restrictionSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("RestrictedFields");
    var restrictions;
    var hasRestrictions = false;

    if(restrictionSheet != null){
        restrictions = restrictionSheet.getRange(2, 1, rowNum-1, colNum).getValues();
        hasRestrictions = true;
    }

    for(var i=0; i<dataRange.length; i++){
        var row = [];
        for(var j=0; j < dataRange[0].length; j++){

            var map = {};
            if(dataRange[i][j] == ""){
                map.value = null;
            }
            else{
                map.value = dataRange[i][j];
            }
            if(!hasRestrictions || restrictions[i][j] == ""){
                map.restrictionIndex = null;
            }
            else{
                map.restrictionIndex = restrictions[i][j];
            }

            row.push(map);
        }
        data.push(row);
    }
    return data;
}


