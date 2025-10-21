import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class IUArrayList<T> implements IndexedUnsortedList<T> {
    private T[] array;
    private int rear;
    public static final int DEFAULT_SIZE = 10;
    private int modCount;

    /**
     * Initialize list with default size
     */
    public IUArrayList() {
        this(DEFAULT_SIZE);
    }

    /**
     * initialize list with user chosen size.
     * 
     * @param size size of the array.
     */
    @SuppressWarnings("unchecked")
    public IUArrayList(int size) {
        array = (T[]) (new Object[size]);
        rear = 0;
        modCount = 0;
    }

    /**
     * Checks to see if the array can carry the added element and if not
     * double the list size.
     */
    private void expandCheck() {
        if (rear == array.length) {
            array = Arrays.copyOf(array, array.length * 2);
        }
    }

    /**
     * Moves the array forward from the specified index
     * 
     * @param index index the array is shifting forward from
     */
    private void shiftElementForwardFrom(int index) {
        for (int i = rear; i > index; i--) {
            array[i] = array[i - 1];
        }
    }

    /**
     * Moves the element backward from the specified index
     * 
     * @param index index the array is shifting backward from
     */
    private void shiftElementBackwardFrom(int index) {
        for (int i = index; i < rear - 1; i++) {
            array[i] = array[i + 1];
        }
    }

    @Override
    public void addToFront(T element) {
        expandCheck();
        shiftElementForwardFrom(0);
        array[0] = element;
        rear++;
        modCount++;
    }

    @Override
    public void addToRear(T element) {
        expandCheck();
        array[rear] = element;
        rear++;
        modCount++;
    }

    @Override
    public void add(T element) {
        addToRear(element);
    }

    @Override
    public void addAfter(T element, T target) {
        expandCheck();
        int targetIndex = indexOf(target);
        if (targetIndex == -1) {
            throw new NoSuchElementException();
        }
        shiftElementForwardFrom(targetIndex + 1);
        array[targetIndex + 1] = element;
        rear++;
        modCount++;
    }

    @Override
    public void add(int index, T element) {
        expandCheck();
        if (index < 0 || index > rear) {
            throw new IndexOutOfBoundsException();
        }
        shiftElementForwardFrom(index);
        array[index] = element;
        rear++;
        modCount++;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return remove(0);
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return remove(rear - 1);
    }

    @Override
    public T remove(T element) {
        if (indexOf(element) == -1) {
            throw new NoSuchElementException();
        }
        return remove(indexOf(element));
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        T retVal = array[index];
        shiftElementBackwardFrom(index);
        rear--;
        array[rear] = null;
        modCount++;
        return retVal;
    }

    @Override
    public void set(int index, T element) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        array[index] = element;
        modCount++;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }

    @Override
    public int indexOf(T element) {
        int returnValue = -1;
        for (int i = 0; returnValue < 0 && i < rear; i++) {
            if (Objects.equals(array[i], (element))) {
                returnValue = i;
            }
        }
        return returnValue;
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return array[0];
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return array[rear - 1];
    }

    @Override
    public boolean contains(T target) {
        return indexOf(target) > -1;
    }

    @Override
    public boolean isEmpty() {
        return rear == 0;
    }

    @Override
    public int size() {
        return this.rear;
    }

    @Override
    public String toString() {
        StringBuilder arrayStringBuilder = new StringBuilder();
        arrayStringBuilder.append("[");
        // for (int i = 0; i < rear; i++) {
        // arrayStringBuilder.append(array[i].toString());
        // arrayStringBuilder.append(", ");
        // }
        for (T element : this) {
            arrayStringBuilder.append(element.toString());
            arrayStringBuilder.append(", ");
        }
        if (!isEmpty()) {
            arrayStringBuilder.delete(arrayStringBuilder.length() - 2, arrayStringBuilder.length());
        }
        arrayStringBuilder.append("]");
        return arrayStringBuilder.toString();
    }

    private class IUArrayListIterator implements Iterator<T> {

        private int currLocation;
        private int lastReturnedIndex;
        private int callsToRemove;
        private int expectedModCount;

        private IUArrayListIterator() {
            /*
             * details the current 'location' of the iterator if at -1, it is between -1 and
             * 0,
             * if at 0 it is between 0 and 1, and so on
             */
            currLocation = -1;
            callsToRemove = 1;
            expectedModCount = modCount;
        }

        /**
         * Checks to see if the array has changed using outside methods
         * to help enforce fail fast behavior
         */
        private void hasChanged() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public boolean hasNext() {
            hasChanged();
            return currLocation + 1 < rear;
        }

        @Override
        public T next() {
            hasChanged();
            if (hasNext()) {
                currLocation++;
                lastReturnedIndex = currLocation;
                callsToRemove = 0;
                return array[currLocation];
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            hasChanged();
            if (callsToRemove >= 1) {
                throw new IllegalStateException();
            } else {
                IUArrayList.this.remove(lastReturnedIndex);
                expectedModCount++;
                currLocation--;
                callsToRemove++;
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new IUArrayListIterator();
    }

    @Override
    public ListIterator<T> listIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
    }

    @Override
    public ListIterator<T> listIterator(int startingIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
    }

}
