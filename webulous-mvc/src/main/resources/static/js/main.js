

$(document).ready(function() {

    $('#addOntologyImportInput')
        // Add button click handler
        .on('click', function() {
            $('<input type="text"/>')
                .attr('name','ontologyImports')
                .attr('class', 'form-control ontologyImport')
                .appendTo($('#import'));
        });

    $('#removeOntologyImportInput')
        // Add button click handler
        .on('click', function() {
            // Add new field
            if ($('.ontologyImport').length >0) {
                $('.ontologyImport:last').remove();
            }
        });


    //$('#removeRestriction')
    //        // Add button click handler
    //        .on('click', function() {
    //            // Add new field
    //            if ($('.restriction-div').length >1) {
    //                $('.restriction-div:last').remove();
    //            }
    //        });
    //
    //$('#removePattern')
    //        // Add button click handler
    //        .on('click', function() {
    //            // Add new field
    //            if ($('.pattern-div').length >0) {
    //                $('.pattern-div:last').remove();
    //            }
    //        });
    //
    //
    //
    //$('#addRestriction')
    //    // Add button click handler
    //    .on('click', function() {
    //        var index =  $('.restriction-div').length;
    //
    //        var restrictionOptionName = 'dataRestrictions[0].restrictionType';
    //        var restrictionTypeOptions = $('select[name=\'' + restrictionOptionName+ '\'] > option' ).clone();
    //
    //        $('<div class="restriction-div"></div>')
    //            .append($('<h4></h4>').text('Restriction ' + (index+1) ))
    //            .append(
    //                $('<div class="form-group"></div>')
    //                    .append(
    //                        $('<label  class="col-lg-2 control-label"></label>').attr('for', 'dataRestrictions-name-' + index).text('Name')
    //                    )
    //                    .append(
    //                        $('<div class="col-lg-10"></div>').attr('id', 'dataRestrictions-name-' + index)
    //                            .append($('<input type="text" class="form-control" />').attr('name', 'dataRestrictions[' + index + '].restrictionName'))
    //                    )
    //                    .append(
    //                        $('<label  class="col-lg-2 control-label"></label>').attr('for', 'dataRestrictions-variableName-' + index).text('Variable name')
    //                    )
    //                    .append(
    //                        $('<div class="col-lg-10"></div>').attr('id', 'dataRestrictions-variableName-' + index)
    //                            .append($('<input type="text" class="form-control" />').attr('name', 'dataRestrictions[' + index + '].variableName'))
    //                    )
    //                    .append(
    //                        $('<label  class="col-lg-2 control-label"></label>').attr('for', 'dataRestrictions-columnIndex-' + index).text('Column Index')
    //                    )
    //                    .append(
    //                        $('<div class="col-lg-10"></div>').attr('id', 'dataRestrictions-columnIndex-' + index)
    //                            .append($('<input type="text" class="form-control" />').attr('name', 'dataRestrictions[' + index + '].columnIndex'))
    //                    )
    //                    .append(
    //                        $('<label  class="col-lg-2 control-label"></label>').attr('for', 'dataRestrictions-classExpression-' + index).text('Class Expression')
    //                    )
    //                    .append(
    //                        $('<div class="col-lg-10"></div>').attr('id', 'dataRestrictions-classExpression-' + index)
    //                            .append($('<input type="text" class="form-control" />').attr('name', 'dataRestrictions[' + index + '].classExpression'))
    //                    )
    //                    .append(
    //                        $('<label  class="col-lg-2 control-label"></label>').attr('for', 'dataRestrictions-restrictionType-' + index).text('Restriction Type')
    //                    )
    //                    .append(
    //                        $('<div class="col-lg-10"></div>').attr('id', 'dataRestrictions-restrictionType-' + index)
    //                            .append(
    //                                $('<select  class="form-control"></select>').attr('name', 'dataRestrictions[' + index + '].restrictionType').append(restrictionTypeOptions)
    //                            )
    //                    )
    //                    .append(
    //                        $('<label  class="col-lg-2 control-label"></label>').attr('for', 'dataRestrictions-defaultValue-' + index).text('Default value')
    //                    )
    //                    .append(
    //                        $('<div class="col-lg-10"></div>').attr('id', 'dataRestrictions-defaultValue-' + index)
    //                            .append($('<input type="text" class="form-control" />').attr('name', 'dataRestrictions[' + index + '].defaultValue'))
    //                    )
    //                    .append(
    //                        $('<label  class="col-lg-2 control-label"></label>').attr('for', 'dataRestrictions-multivalueField-' + index).text('Multivalue field')
    //                    )
    //                    .append(
    //                        $('<div class="col-lg-10"></div>').attr('id', 'dataRestrictions-multivalueField-' + index)
    //                            .append(
    //                                $('<select  class="form-control"></select>').attr('name', 'dataRestrictions[' + index + '].restrictionType')
    //                                    .append($('<option value="true"></option>').text('true'))
    //                                    .append($('<option value="false" selected="selected"></option>').text('false'))
    //                            )
    //                    )
    //                    .append(
    //                        $('<label  class="col-lg-2 control-label"></label>').attr('for', 'dataRestrictions-required-' + index).text('Required')
    //                    )
    //                    .append(
    //                        $('<div class="col-lg-10"></div>').attr('id', 'dataRestrictions-required-' + index)
    //                            .append(
    //                                $('<select  class="form-control"></select>').attr('name', 'dataRestrictions[' + index + '].required')
    //                                    .append($('<option value="true"></option>').text('true'))
    //                                    .append($('<option value="false" selected="selected"></option>').text('false'))
    //                            )
    //                    )
    //        ).appendTo($('#restrictions'));
    //    });
    //
    //$('#addPattern')
    //        // Add button click handler
    //        .on('click', function() {
    //            var index =  $('.pattern-div').length;
    //
    //
    //            $('<div class="pattern-div"></div>')
    //                .append($('<h4></h4>').text('Pattern ' + (index+1) ))
    //                .append(
    //                    $('<div class="form-group"></div>')
    //                        .append(
    //                            $('<label  class="col-lg-2 control-label"></label>').attr('for', 'patternName-' + index).text('Pattern Name')
    //                        )
    //                        .append(
    //                            $('<div class="col-lg-10"></div>').attr('id', 'patternName-' + index)
    //                                .append($('<input type="text" class="form-control" />').attr('name', 'patterns[' + index + '].patternName'))
    //                        )
    //                        .append(
    //                            $('<label  class="col-lg-2 control-label"></label>').attr('for', 'patternValue-' + index).text('Pattern Value')
    //                        )
    //                        .append(
    //                            $('<div class="col-lg-10"></div>').attr('id', 'patternValue-' + index)
    //                                .append($('<textarea class="form-control" />').attr('name', 'patterns[' + index + '].patternValue'))
    //                        )
    //            ).appendTo($('#patterns'));
    //        });
    // Instance the tour


});


function templateTour() {

    var tour = new Tour({
        template: "<div class='popover tour'>" +
        "<div class='arrow'></div>" +
        "<h3 class='popover-title'></h3>" +
        "<div class='popover-content'></div>" +
        "<div class='popover-navigation'>" +
        "<button class='btn btn-sm btn-info' data-role='prev'>« Prev</button>" +
        "<span data-role='separator'>|</span>" +
        "<button class='btn btn-sm btn-info' data-role='next'>Next »</button>" +
        "<button class='btn btn-sm btn-info' data-role='end'>End tour</button>" +
        "</div>" +
        "</nav>" +
        "</div>",
        steps: [
            {
                element: "#description",
                title: "Description",
                content: "Enter a short description for your template"
            },
            {
                element: "#templateGroupName",
                title: "Template group",
                content: "You can group templates together with a common name."
            },
            {
                element: "#adminEmailAddresses",
                title: "Admin e-mail",
                content: "Set the admin e-mail to receive notifications about data submissions for this template."
            },
            {
                element: "#defaultNumberOfRows",
                title: "Default number of rows",
                content: "Default number of input rows that a UI should create for this pattern."
            },
            {
                element: "#urigenserver",
                title: "UriGen server",
                content: "Webulous can use a UriGen server to manage URI creation. Specify the URL to the UriGen server here."
            },
            {
                element: "#activeOntology",
                title: "Active ontology",
                content: "This is the Ontology URI where newly generated axioms will be stored."
            },
            {
                element: "#ontologyImports",
                title: "Ontology imports",
                content: "List any additional ontologies required by Webulous and OPPL to generate the new axioms."
            },
            {
                element: "#datarestrictions",
                title: "Restrictions",
                placement:"top",
                content: "Restrictions represent columns in the template. Columns can be free text, or you can place ontology based restrictions (or data validations) on " +
                "individual columns in the template."
            },
            {
                element: "#dataRestrictions-name-0",
                title: "Data restriction name",
                content: "The name of the data restriction."
            },
            {
                element: "#dataRestrictions-variableName-0",
                title: "Column variable",
                content: "Choose a variable name to refer to data this column in your OPPL patterns."
            },
            {
                element: "#dataRestrictions-columnIndex-0",
                title: "Column index",
                content: "The column index for the restrictions. Uses base 0 notation, so column 1 would be 0."
            },
            {
                element: "#dataRestrictions-classExpression-0",
                title: "Class expression",
                content: "You can specify a DL query here to retrieve classes from the imported ontologies to restrict the values in this column. This field " +
                "support manchester syntax and the default label rendering. Use single quotes for labels with spaces. e.g. 'cell line' and 'part of' some liver"
            },
            {
                element: "#dataRestrictions-restrictionType-0",
                title: "Restriction type",
                content: "Set the restriction type. This can be free text for this column, or sublcasses/descendants of the DL query above."
            },
            {
                element: "#dataRestrictions-defaultValue-0",
                title: "Default value",
                content: "Default value for this field. This is not required."
            },
            {
                element: "#dataRestrictions-multivalueField-0",
                title: "Multivalue",
                content: "Field can have multiple values, separated by a ||"
            },
            {
                element: "#dataRestrictions-required-0",
                title: "Required",
                content: "This is a required field so the processor will fail if empty."
            },
            {
                element: "#opplpatterns",
                title: "Patterns",
                placement:"top",
                content: "Specify OPPL patterns to be executed for each row of input data."
            },
            {
                element: "#patternName-0",
                title: "Pattern name",
                content: "Name the OPPL pattern"
            },
            {
                element: "#patternValue-0",
                title: "Pattern value",
                content: "The OPPL pattern to be executed for each row of input data. Please refer to the OPPL documentation for syntax spec."
            }
        ]});

// Initialize the tour
    tour.init();

// Start the tour
    tour.start(true);

    tour.restart();
}