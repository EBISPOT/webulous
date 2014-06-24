package uk.ac.ebi.fgpt.webpopulous.patterns.oppl.entity;

import org.semanticweb.owlapi.model.*;

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
public interface OWLEntityFactory {

    /**
     *
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    public OWLEntityCreationSet<OWLClass> createOWLClass(String shortName, URI baseURI) throws OWLEntityCreationException;

    /**
     *
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    public OWLEntityCreationSet<OWLObjectProperty> createOWLObjectProperty(String shortName, URI baseURI) throws OWLEntityCreationException;

    /**
     *
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    public OWLEntityCreationSet<OWLDataProperty> createOWLDataProperty(String shortName, URI baseURI) throws OWLEntityCreationException;

    /**
     *
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    public OWLEntityCreationSet<OWLNamedIndividual> createOWLIndividual(String shortName, URI baseURI) throws OWLEntityCreationException;

    /**
     *
     * @param type OWLClass, OWLObjectProperty, OWLDataProperty or OWLIndividual
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    public <T extends OWLEntity> OWLEntityCreationSet<T> createOWLEntity(Class<T> type, String shortName, URI baseURI) throws OWLEntityCreationException;


    /**
     * Use this to check if the entity can be created without affecting any generated IDs
     * @param type OWLClass, OWLObjectProperty, OWLDataProperty or OWLIndividual
     * @param shortName user supplied name
     * @param baseURI specify a base or leave as null to let the factory decide
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     * @return an entity creation set - this should never be applied to the ontology
     */
    public <T extends OWLEntity> OWLEntityCreationSet<T> preview(Class<T> type, String shortName, URI baseURI) throws OWLEntityCreationException;

}
