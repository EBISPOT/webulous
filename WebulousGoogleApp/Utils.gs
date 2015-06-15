
/**
 *
 * Get json from a URL and return as a generic object

 * @param {string} uri The URI of the document server
 * @return {object} new object parsed from JSON
 */
function getObjectFromUrl(uri){
  try {
    var result = UrlFetchApp.fetch(uri).getContentText();
    if (result != null) {
      return JSON.parse(result);
    } else {
      throw new Error("No results from " + uri);
    }
  } catch (e) {
    throw new Error("Can't query " + uri);
  }
}

function showToast(msg, title, timeout) {
 SpreadsheetApp.getActiveSpreadsheet().toast(msg,title, timeout);
}

// set active sheet to supplied name
function showSheet(name) {
  var sheet = SpreadsheetApp.getActive().getSheetByName(name);
  sheet.showSheet();
  SpreadsheetApp.setActiveSheet(sheet);
}

function getWebulousServers() {
 
  var servers = new Array(0);
  var scriptProperties = PropertiesService.getScriptProperties();
  
  var defaultServers = scriptProperties.getProperty("webulous_servers");
  var defaultJson = JSON.parse(defaultServers);
  for (var x = 0 ; x < defaultJson.length; x++) {
    servers.push(defaultJson[x]); 
  }


  var userProperties = PropertiesService.getUserProperties();
  var userServers = userProperties.getProperty("webulous_servers");
  if (userServers != undefined) {
    var userJson = JSON.parse(userServers);
    for (var x = 0 ; x < userJson.length; x++) {
      var userValue = userJson[x];
      if (servers.indexOf(userValue) == -1) {
        servers.push (userValue);      
      }
    }
  }
  
  return servers;  
}

function addUsersWebulousServer(newServer) {

  var servers = new Array(0);  
  var userProperties = PropertiesService.getUserProperties();
  var userServers = userProperties.getProperty("webulous_servers");
  
  if (userServers != undefined) {
    var userJson = JSON.parse(userServers);
    for (var x = 0 ; x < userJson.length; x++) {
      servers.push (userJson[x]);      
    }
  }
  
  if (servers.indexOf(newServer) == -1) {
    servers.push (newServer);      
  }
  
  var newUserServersJson = JSON.stringify(servers);
  userProperties.setProperty("webulous_servers", newUserServersJson);
}