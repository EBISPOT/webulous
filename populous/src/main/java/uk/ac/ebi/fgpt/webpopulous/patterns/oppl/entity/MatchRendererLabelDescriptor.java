package uk.ac.ebi.fgpt.webpopulous.patterns.oppl.entity;

import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.net.URI;/*
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

/**
 * Author: Simon Jupp<br>
 * Date: Jan 4, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class MatchRendererLabelDescriptor {

    OWLOntologyManager man;
    public MatchRendererLabelDescriptor (OWLOntologyManager man) {
        this.man = man;
    }


    public String getLanguage() {

//        final List<URI> uris = new ArrayList<URI>();
//        for (OWLOntology onto : man.getOntologies()) {
//            for (OWLAnnotation anno : onto.getAnnotations()) {
//                uris.add(anno.getProperty().getIRI().toURI());
//            }
//        }
//        if (!uris.isEmpty()){
//            List<String> langs = OWLRendererPreferences.getInstance().getAnnotationLangs(uris.get(0));
//            if (!langs.isEmpty()){
//                return langs.get(0);
//            }
//        }
        return "en";
    }


    public URI getURI() {
        return OWLRDFVocabulary.RDFS_LABEL.getURI();
    }
}
