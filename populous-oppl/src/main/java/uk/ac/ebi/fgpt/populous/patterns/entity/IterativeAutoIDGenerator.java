package uk.ac.ebi.fgpt.populous.patterns.entity;

import org.semanticweb.owlapi.model.OWLEntity;
import uk.ac.ebi.fgpt.populous.exception.AutoIDException;
import uk.ac.ebi.fgpt.populous.model.Revertable;

import java.util.Stack;

/**
 * Author: Simon Jupp<br>
 * Date: Jan 4, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class IterativeAutoIDGenerator extends AbstractIDGenerator implements Revertable {

    private long id;

    private long end;

    private Stack<Long> checkpoints = new Stack<Long>();




    protected long getRawID(Class<? extends OWLEntity> type) throws AutoIDException {

        if (end != -1 && id > end){
            throw new AutoIDException("You have run out of IDs for creating new entities - max = " + end);
        }
        return id++;
    }


    public void checkpoint() {
        checkpoints.push(id);
    }


    public void revert() {
        id = checkpoints.pop();
    }

    public void initialise() {
        id = getEntitiesPrefs().getAutoIDStart();
        end = getEntitiesPrefs().getAutoIDEnd();
    }
}
