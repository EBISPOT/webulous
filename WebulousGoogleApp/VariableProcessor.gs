//TO DO right now, this code assumes a very basic pattern of variables and a single ADD statement - make this more dynamic!

function displayPattern(col){
    var source = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");
    var name = source.getRange(2, col).getValue();
    var pattern = source.getRange(3, col).getValue();
    var variables = source.getRange(4, col, source.getLastRow()).getValues();

    var html = "<b>" + name + "</b> <br> <br>";

    for(var i=0; i<variables.length; i++){
        var thisvar = variables[i][0];

        if(thisvar != ""){
            html += "<div style=\"margin-left: 10px;\">" + thisvar + ", <br> </div>"
        }
    }

    html = html.substring(0, html.length-13);
    html += " <br> </div> <div style=\"margin-left: 10px;\">BEGIN <br></div> <div style=\"margin-left: 10px;\">ADD <br></div>";
    html += "<div style=\"margin-left: 15px;\">" + pattern + "<br></div> "
    html += "<div style=\"margin-left: 10px;\">END;<br></div>";


    return html;
}

function noPatternSelected(){
    var ui = SpreadsheetApp.getUi();
    var result = ui.alert("You have to select at least one pattern", ui.ButtonSet.OK);

    if(result == ui.Button.OK){
        showPatterns();
    }
}

var selectedPatterns =[];

function getVariableSelector(checked){
    setPatternSelection(checked);
    selectVariables();
}


function setSelected(checked){
    if(selectedPatterns.length == 0){
        selectedPatterns = checked;
    }
    else{
        for(var i=0; i<checked.length;i++){
            if(selectedPatterns.indexOf(checked[i])== -1){
                selectedPatterns.push(checked[i]);
            }
        }
        for(var j=0; j<selectedPatterns.length; j++){
            if(checked.indexOf(selectedPatterns[j])== -1){
                selectedPatterns.splice(j,1);
            }
        }
    }

}

//a method to get the selected columns from the HTML file
function getSelected(){
    return selectedPatterns;
}

function setPatternSelection(checked){
    setSelected(checked);
    Logger.log("I know which columns were ticked");

    var source = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("SourceData");
    var max = source.getLastColumn();

    for(var i=3; i<max+1; i++){
        if(checked.indexOf(i.toString()) == -1){
            source.getRange(1, i).setValue("");
        }
        else{
            source.getRange(1, i).setValue("selected");
        }
    }
}

function assignVariables(key,map){

    var variableSheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("Variables");
    var variables = variableSheet.getRange(1, 1, variableSheet.getLastRow()).getValues();

    var names = [];

    for(var k=0; k<variables.length; k++){
        names.push(variables[k][0]);
        Logger.log(variables[k][0]);
    }

    for(var i=0; i<map.length; i++){
        var varName = key[i].trim();
        Logger.log(varName);

        var rowNum = names.indexOf(varName)+1;
        Logger.log(rowNum);
        variableSheet.getRange(rowNum, 2).setValue(map[i]);


    }


}
