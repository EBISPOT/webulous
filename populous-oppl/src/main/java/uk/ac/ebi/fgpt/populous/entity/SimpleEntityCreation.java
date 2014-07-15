package uk.ac.ebi.fgpt.populous.entity;

import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import uk.ac.ebi.fgpt.populous.model.AutoIDGenerator;
import uk.ac.ebi.fgpt.populous.model.EntityCreation;
import uk.ac.ebi.fgpt.populous.model.LabelDescriptor;

import java.net.URI;

/*
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
 * Date: Dec 20, 2010<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class    SimpleEntityCreation implements EntityCreation {


    private String DEFAULT_BASE_URI = "http://www.co-ode.org/ontologies/ont.owl#";
    private boolean USE_DEFAULT_BASE_URI = true;

    private String DEFAULT_URI_SEPARATOR = "#";

    private boolean USE_AUTO_ID_FOR_FRAGMENT = false;

    private boolean NAME_LABEL_GENERATE = false;
    private String NAME_LABEL_URI = OWLRDFVocabulary.RDFS_LABEL.toString();
    private String NAME_LABEL_LANG = "en";

    private boolean ID_LABEL_GENERATE = false;
    private String ID_LABEL_URI;
    private String ID_LABEL_LANG;

    private String AUTO_ID_GENERATOR;
    private String DEFAULT_AUTO_ID_GENERATOR_CLASS_NAME = "package uk.ac.ebi.fgpt.populous.entity.PseudoRandomAutoIDGenerator";

    private String AUTO_ID_PREFIX = "[type]";
    private String AUTO_ID_SUFFIX = "";
    private int AUTO_ID_SIZE = 20;
    private int AUTO_ID_START = 1;
    private int AUTO_ID_END = -1;

    private String DEFAULT_LABEL_DESCRIPTOR_CLASS = "package uk.ac.ebi.fgpt.populous.entity.CustomLabelDescriptor";


    public URI getDefaultBaseURI() {
        return URI.create(DEFAULT_BASE_URI);
    }


    public boolean useDefaultBaseURI() {
        return USE_DEFAULT_BASE_URI;
    }


    public void setUseDefaultBaseURI(boolean use) {
        USE_DEFAULT_BASE_URI = use;
    }


    public void setDefaultBaseURI(String defaultBase) {
        DEFAULT_BASE_URI = defaultBase;
    }


    public String getDefaultSeparator() {
        return DEFAULT_URI_SEPARATOR;
    }


    public void setDefaultSeparator(String sep) {
        DEFAULT_URI_SEPARATOR = sep;
    }


    public Class<? extends AutoIDGenerator> getAutoIDGeneratorClass(){

        if (AUTO_ID_GENERATOR != null) {
            try {
                return (Class<AutoIDGenerator>)Class.forName(getAutoIDGeneratorName());
            }
            catch (ClassNotFoundException e) {
//            logger.error("Cannot find an Auto ID generator.", e);
            }
        }
        try {
            return (Class<AutoIDGenerator>)Class.forName(DEFAULT_AUTO_ID_GENERATOR_CLASS_NAME);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void setAutoIDGeneratorClass(Class<? extends AutoIDGenerator> cls){
        AUTO_ID_GENERATOR = cls.getName();
    }

    public String getAutoIDGeneratorName(){
        return AUTO_ID_GENERATOR;
    }

    public  String getPrefix() {
        return AUTO_ID_PREFIX;
    }


    public void setPrefix(String prefix) {
        AUTO_ID_PREFIX = prefix;
    }


    public String getSuffix() {
        return AUTO_ID_SUFFIX;
    }


    public void setSuffix(String suffix) {
        AUTO_ID_SUFFIX = suffix;
    }


    public int getAutoIDDigitCount() {
        return AUTO_ID_SIZE; // just about big enough for System.currentTimeMillis()
    }


    public void setAutoIDDigitCount(int size) {
        AUTO_ID_SIZE = size;
    }


    public int getAutoIDStart() {
        return AUTO_ID_START;
    }


    public void setAutoIDStart(int start) {
        AUTO_ID_START = start;
    }

    public int getAutoIDEnd() {
        return AUTO_ID_END;
    }


    public void setAutoIDEnd(int end) {
        AUTO_ID_END = end;
    }


    public boolean isFragmentAutoGenerated() {
        return USE_AUTO_ID_FOR_FRAGMENT;
    }


    public void setFragmentAutoGenerated(boolean autoGenerateFragment) {
        USE_AUTO_ID_FOR_FRAGMENT = autoGenerateFragment;
    }


    public boolean isGenerateNameLabel() {
        return NAME_LABEL_GENERATE;
    }


    public void setGenerateNameLabel(boolean gen) {
        NAME_LABEL_GENERATE = gen;
    }


    public URI getNameLabelURI() {
        if (NAME_LABEL_URI != null){
            return URI.create(NAME_LABEL_URI);
        }
        return null;
    }


    public void setNameLabelURI(URI labelURI) {
        NAME_LABEL_URI = (labelURI == null) ? null : labelURI.toString();
    }

    public String getNameLabelLang() {
        return NAME_LABEL_LANG;
    }


    public void setNameLabelLang(String lang) {
        NAME_LABEL_LANG = lang;
    }


    public boolean isGenerateIDLabel() {
        return ID_LABEL_GENERATE;
    }


    public void setGenerateIDLabel(boolean gen) {
        ID_LABEL_GENERATE = gen;
    }

// for now just use the same as the name
//    public static URI getIDLabelURI() {
//        Preferences prefs = getPrefs();
//        String uriStr = prefs.getString(ID_LABEL_URI, null);
//        if (uriStr != null){
//            return URI.create(uriStr);
//        }
//        return null;
//    }
//
//
//    public static void setIDLabelURI(URI labelURI) {
//        Preferences prefs = getPrefs();
//        prefs.putString(ID_LABEL_URI, (labelURI == null) ? null : labelURI.toString());
//    }
//
//
//    public static String getIDLabelLang() {
//        Preferences prefs = getPrefs();
//        return prefs.getString(ID_LABEL_LANG, null);
//    }
//
//
//    public static void setIDLabelLang(String utils) {
//        Preferences prefs = getPrefs();
//        prefs.putString(ID_LABEL_LANG, utils);
//    }


    public Class<? extends LabelDescriptor> getLabelDescriptorClass() {
//        String className = LABEL_DESCRIPTOR;
//        try {
//            return (Class<LabelDescriptor>)Class.forName(className);
//        }
//        catch (ClassNotFoundException e) {
////            logger.error("Cannot find a label descriptor.", e);
//        }
        try {
            return (Class<LabelDescriptor>)Class.forName(DEFAULT_LABEL_DESCRIPTOR_CLASS);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

//    public void setLabelDescriptorClass(Class<? extends LabelDescriptor> cls) {
//        LABEL_DESCRIPTOR = cls.getName();
//    }

}
