package uk.ac.ebi.fgpt.populous.entity;

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
public class PseudoRandomAutoIDGenerator extends AbstractIDGenerator implements Revertable {

    private long nextId = System.nanoTime();

    private Stack<Long> checkpoints = new Stack<Long>();

    protected long getRawID(Class<? extends OWLEntity> type) throws AutoIDException {
        long id = nextId;
        nextId = System.nanoTime();
        return id;
    }


    public void checkpoint() {
        checkpoints.push(nextId);
    }


    public void revert() {
        nextId = checkpoints.pop();
    }

    public void initialise() {
    }
}