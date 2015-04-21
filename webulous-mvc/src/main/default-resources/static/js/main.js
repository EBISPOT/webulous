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


});

