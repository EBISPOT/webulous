package uk.ac.ebi.fgpt.populous.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwelter on 04/07/14.
 */
public class SimpleDataAttribute implements DataAttribute{

    private int index;
    private List<Term> permissibleTerms;
    private List<PopulousDataRestriction> dataRestrictions;
    private List<String> opplVariables;
//    private String typeURI;
//    private String typeLabel;
//    private String typeRestriction;

    public SimpleDataAttribute(int index){
        this.index = index;
        permissibleTerms = new ArrayList<Term>();
        dataRestrictions = new ArrayList<PopulousDataRestriction>();
        opplVariables = new ArrayList<String>();
    }


    @Override
    public List<Term> getPermissibleTerms() {
        return permissibleTerms;
    }

    public List<String> getOpplVariables(){
        return opplVariables;
    }

    public void addVariable(String opplVariable){
        opplVariables.add(opplVariable);
    }

//    @Override
//    public String getTypeLabel() {
//        return typeLabel;
//    }
//
//    @Override
//    public String getTypeURI() {
//        return typeURI;
//    }

    @Override
    public int getIndex() {
        return index;
    }

//    @Override
//    public String getTypeRestriction(){
//        return typeRestriction;
//    }

    public void addTerm(Term term){
        permissibleTerms.add(term);
    }

    public List<PopulousDataRestriction> getDataRestrictions() {
        return dataRestrictions;
    }

    public void addDataRestriction(PopulousDataRestriction dataRestriction) {
        dataRestrictions.add(dataRestriction);
    }

//    public void setTypeURI(String typeURI) {
//        this.typeURI = typeURI;
//    }
//
//    public void setType(String type) {
//        this.typeLabel = type;
//    }
//
//    public void setTypeRestriction(String restriction){
//        this.typeRestriction = restriction;
//    }
}
