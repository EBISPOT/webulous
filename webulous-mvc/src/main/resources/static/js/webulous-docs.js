$(document).ready(function() {

    // read the window location to set the breadcrumb
    var path = window.location.pathname;
    var pagename = path.substr(path.lastIndexOf('/') + 1);
    var url_header = "docs/content/".concat(pagename).concat("-content.html #header");
    var url_footer = "docs/content/".concat(pagename).concat("-content.html #footer");
    var url = "docs/content/".concat(pagename).concat("-content.html #content");
    console.log("Documentation should be loaded from " + url + "...");

    $("#docs-header").load (url_header);
    $("#docs-content").load (url);
    $("#docs-footer").load (url_footer);

    // load the page content
//    $.get(url, loadDocumentation(pagename, content)).fail(console.log("Failed to get content from " + url));
});

//var loadDocumentation = function(pagename, content) {
//    console.log("Attempting to load documentation...");
//    return function(data, textStatus, jqXHR) {
//
//        // load the data content
//        console.log("Updating " + content + "...");
//        console.log(data);
//        content.html(data);
//        console.log("Done!");
//
//    }
//};