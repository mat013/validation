package dk.emstar.common.validation;

import java.util.Iterator;

public class ValidationRegistrationIterator implements Iterator<ValidationRegistration> {

    private final Iterator<Object> iterator;
    private ValidationRegistration nextItem = null;
    private Iterator<ValidationRegistration> subIterator;

    public ValidationRegistrationIterator(Iterator<Object> iterator) {
        this.iterator = iterator;
        findNext();
    }

    @Override
    public boolean hasNext() {
        return nextItem != null;
    }

    @Override
    public ValidationRegistration next() {
        ValidationRegistration result = nextItem;
        findNext();
        return result;
    }

    @SuppressWarnings("resource")
    private void findNext() {
        if (subIterator != null && subIterator.hasNext()) {
            nextItem = subIterator.next();
            return;
        }

        while (iterator.hasNext()) { // find next
            Object element = iterator.next();
            // use its erasure object instead
            if (element instanceof ValidationRegistration) {
                nextItem = (ValidationRegistration) element;
                return;
            } else {
                ValidationResult listElement = (ValidationResult) element;
                Iterator<ValidationRegistration> flatIteratorForListElement = listElement.iterator();
                if (flatIteratorForListElement.hasNext()) {
                    nextItem = flatIteratorForListElement.next();
                    this.subIterator = flatIteratorForListElement;
                    return;
                }
            }
        }

        nextItem = null;
    }
}
