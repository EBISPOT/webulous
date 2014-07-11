package uk.ac.ebi.fgpt.populous.entity;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
import uk.ac.ebi.fgpt.populous.model.NewEntities;

/**
 * Author: Simon Jupp<br>
 * Date: Oct 11, 2010<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class SimpleNewEntities implements NewEntities{

    private IRI baseIRI;
    private boolean random = false;
    private String prefix;
    private OWLManager manager;

    public IRI getBaseIRI() {
        return baseIRI;
    }

    public void setBaseIRI(IRI baseIRI) {
        this.baseIRI = baseIRI;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public OWLManager getManager() {
        return manager;
    }

    public void setManager(OWLManager manager) {
        this.manager = manager;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }

    private OWLOntology ontology;


    public SimpleNewEntities(IRI baseIRI, boolean random, String prefix) {
        this.baseIRI = baseIRI;
        this.random = random;
        this.prefix = prefix;

//        this.ontologyManager = ontologyManager;
//        this.ontology = ontology;
        
    }



}
