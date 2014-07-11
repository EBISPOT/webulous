package uk.ac.ebi.fgpt.populous.entity;

import org.semanticweb.owlapi.model.OWLEntity;
import uk.ac.ebi.fgpt.populous.exception.AutoIDException;
import uk.ac.ebi.fgpt.populous.model.AutoIDGenerator;
import uk.ac.ebi.fgpt.populous.model.EntityCreation;

import java.text.FieldPosition;
import java.text.NumberFormat;

/**
 * Author: Simon Jupp<br>
 * Date: Jan 4, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public abstract class AbstractIDGenerator implements AutoIDGenerator {

    private static final String START_MACRO = "\\[";
    private static final String END_MACRO = "\\]";

    private EntityCreation entitiesPrefs;

    public void setEntityPrefs(EntityCreation entitiesPrefs) {
        this.entitiesPrefs = (SimpleEntityCreation) entitiesPrefs;
    }

    public EntityCreation getEntitiesPrefs () {
        return entitiesPrefs;
    }

    public String getNextID(Class<? extends OWLEntity> type) throws AutoIDException {
        return getPrefix(type) + pad(getRawID(type), getDigitLength()) + getSuffix(type);
    }


    protected abstract long getRawID(Class<? extends OWLEntity> type) throws AutoIDException;


    protected String getPrefix(Class<? extends OWLEntity> type){
        String prefix = entitiesPrefs.getPrefix();
        return preprocess(prefix, type);
    }


    protected String getSuffix(Class<? extends OWLEntity> type){
        String suffix = entitiesPrefs.getSuffix();
        return preprocess(suffix, type);
    }


    protected String preprocess(String s, Class<? extends OWLEntity> type) {
        s = s.replaceAll(START_MACRO + "user" + END_MACRO, System.getProperty("user.name", "no_one"));
        s = s.replaceAll(START_MACRO + "type" + END_MACRO, type.getSimpleName());
        return s;
    }


    protected int getDigitLength(){
        return entitiesPrefs.getAutoIDDigitCount();
    }


    private String pad(long l, int padding) {
        StringBuffer sb = new StringBuffer();
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(0);
        format.setMinimumIntegerDigits(padding);
        format.setMaximumIntegerDigits(padding);
        format.setGroupingUsed(false);
        format.format(l, sb, new FieldPosition(0));
        return sb.toString();
    }
}
