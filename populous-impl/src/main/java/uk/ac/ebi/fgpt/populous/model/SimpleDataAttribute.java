package uk.ac.ebi.fgpt.populous.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dwelter on 04/07/14.
 */
public class SimpleDataAttribute implements DataAttribute{

    private int index;
    private Collection<Term> permissibleTerms;
    private String type, typeURI;

    public SimpleDataAttribute(int index){
        this.index = index;
        permissibleTerms = new ArrayList<Term>();
    }


    @Override
    public Collection<Term> getPermissibleTerms() {
        return permissibleTerms;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getTypeURI() {
        return typeURI;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void addTerm(Term term){
        permissibleTerms.add(term);
    }

    public void setTypeURI(String typeURI) {
        this.typeURI = typeURI;
    }

    public void setType(String type) {
        this.type = type;
    }
}
