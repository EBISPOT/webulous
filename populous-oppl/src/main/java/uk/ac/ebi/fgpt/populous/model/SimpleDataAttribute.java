package uk.ac.ebi.fgpt.populous.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dwelter on 04/07/14.
 */
public class SimpleDataAttribute implements DataAttribute{

    private int index;
    private Collection<Term> permissibleTerms;
    private String typeURI;
    private String typeLabel;
    private String typeRestriction;

    public SimpleDataAttribute(int index){
        this.index = index;
        permissibleTerms = new ArrayList<Term>();
    }


    @Override
    public Collection<Term> getPermissibleTerms() {
        return permissibleTerms;
    }

    @Override
    public String getTypeLabel() {
        return typeLabel;
    }

    @Override
    public String getTypeURI() {
        return typeURI;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getTypeRestriction(){
        return typeRestriction;
    }

    public void addTerm(Term term){
        permissibleTerms.add(term);
    }

    public void setTypeURI(String typeURI) {
        this.typeURI = typeURI;
    }

    public void setType(String type) {
        this.typeLabel = type;
    }

    public void setTypeRestriction(String restriction){
        this.typeRestriction = restriction;
    }
}
