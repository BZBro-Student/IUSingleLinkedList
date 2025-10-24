import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
/**
 * Implements IndexedUnsortedList methods to create a single link node 
 * based implementation of a indexed unsorted list. 
 * 
 * @author Broden
 */
public class IUSingleLinkedList<T> implements IndexedUnsortedList<T> {
    private Node<T> head, tail;
    private int size;
    private int modCount;

    public IUSingleLinkedList() {
        head = null;
        tail = null;
        size = 0;
        modCount = 0;
    }

    @Override
    public void addToFront(T element) {
        Node<T> newNode = new Node<T>(element);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setNextNode(head);
            head = newNode;
        }
        size++;
        modCount++;
    }

    @Override
    public void addToRear(T element) {
        Node<T> newNode = new Node<T>(element);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNextNode(newNode);
            tail = newNode;
        }
        size++;
        modCount++;
    }

    @Override
    public void add(T element) {
        addToRear(element);
    }

    @Override
    public void addAfter(T element, T target) {
        Node<T> currNode = head;
        Node<T> newNode = new Node<T>(element);
        boolean inserted = false;
        if (head == null) {
            throw new NoSuchElementException();
        }

        while (currNode != null) {
            if (currNode.getElement().equals(target)) {
                Node<T> tempNext = currNode.getNextNode();
                currNode.setNextNode(newNode);
                newNode.setNextNode(tempNext);
                if (currNode == tail) {
                    tail = newNode;
                }
                size++;
                modCount++;
                inserted = true;
                break;
            }
            currNode = currNode.getNextNode();
        }
        if (inserted == false) {
            throw new NoSuchElementException();
        }

    }

    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            addToFront(element);
        } else if (index == size) {
            addToRear(element);
        } else {
            Node<T> newNode = new Node<T>(element);
            int currIndex = 0;
            Node<T> currNode = head;
            while (currNode != null) {
                if (currIndex == index - 1) {
                    Node<T> tempNext = currNode.getNextNode();
                    currNode.setNextNode(newNode);
                    newNode.setNextNode(tempNext);
                    break;
                }
                currNode = currNode.getNextNode();
                currIndex++;
            }
            size++;
            modCount++;
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T returnValue = head.getElement();
        head = head.getNextNode();
        if (head == null) {
            tail = null;
        }
        size--;
        modCount++;
        return returnValue;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T returnValue = null;
        if (size == 1) {
            returnValue = head.getElement();
            head = null;
            tail = null;
        } else {
            returnValue = tail.getElement();
            Node<T> currNode = head;
            for (int i = 0; i < size - 2; i++) {
                currNode = currNode.getNextNode();
            }
            tail = currNode;
            currNode.setNextNode(null);

        }
        size--;
        modCount++;
        return returnValue;
    }

    @Override
    public T remove(T element) {
        int index = indexOf(element);
        if (index < 0 || index >= size) {
            throw new NoSuchElementException();
        }
        T returnValue = remove(index);
        return returnValue;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        int currIndex = 0;
        T returnValue = null;
        Node<T> currNode = head;
        Node<T> nodeBeforeIndex = null;
        Node<T> nodeToBeRemoved = null;
        Node<T> nextNode;
        if (index == 0) {
            returnValue = removeFirst();
        } else {
            while (currNode != null) {
                if (currIndex == index - 1) {
                    nodeBeforeIndex = currNode;
                }
                if (currIndex == index) {
                    nodeToBeRemoved = currNode;
                    nextNode = nodeToBeRemoved.getNextNode();
                    returnValue = nodeToBeRemoved.getElement();
                    if (nodeToBeRemoved == tail) {
                        tail = nodeBeforeIndex;
                    }
                    nodeBeforeIndex.setNextNode(nextNode);
                    size--;
                    modCount++;
                }
                if (returnValue != null) {
                    break;
                }
                currNode = currNode.getNextNode();
                currIndex++;
            }
        }
        return returnValue;
    }

    @Override
    public void set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        int currIndex = 0;
        Node<T> currNode = head;
        while (currIndex < index) {
            currNode = currNode.getNextNode();
            currIndex++;
        }
        currNode.setElement(element);
        modCount++;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        int currIndex = 0;
        Node<T> currNode = head;
        while (currIndex < index) {
            currNode = currNode.getNextNode();
            currIndex++;
        }
        return currNode.getElement();
    }

    @Override
    public int indexOf(T element) {
        Node<T> currNode = head;
        int currIndex = 0;
        //initialize at -1 since index of returns -1 if the element is never found
        int foundIndex = -1;
        while (currNode != null) {
            //checks to see if equal
            if (currNode.getElement().equals(element)) {
                foundIndex = currIndex;
            }
            //if our index is no longer -1, found the element and break out of the loop
            if (foundIndex != -1) {
                break;
            }
            currNode = currNode.getNextNode();
            currIndex++;
        }
        return foundIndex;
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return head.getElement();
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return tail.getElement();
    }

    @Override
    public boolean contains(T target) {
      return indexOf(target) > -1;
    }

    @Override
    public boolean isEmpty() {
        boolean isEmpty = false;
        if (head == null && tail == null) {
            isEmpty = true;
        }
        return isEmpty;
    }

    @Override
    public int size() {
        return size;
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

    private class IUSingleLinkedListIterator implements Iterator<T> {
        private Node<T> currLocation;
        private Node<T> lastReturnedNode;
        private Node<T> nodeBeforeRemove;
        private int callsToRemove;
        private int expectedModCount;

        public IUSingleLinkedListIterator() {
            currLocation = head;
            nodeBeforeRemove = null;
            lastReturnedNode = null;
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
            return currLocation != null;
        }

        @Override
        public T next() {
            hasChanged();
            if (hasNext()) {
                nodeBeforeRemove = lastReturnedNode;
                T returnValue = currLocation.getElement();
                lastReturnedNode = currLocation;
                currLocation = currLocation.getNextNode();
                callsToRemove = 0;
                return returnValue;
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            hasChanged();
            if (callsToRemove >= 1) {
                throw new IllegalStateException();
            }
            Node<T> nodeToBeRemoved = lastReturnedNode;
            if (nodeBeforeRemove == null) {
                head = currLocation;
            } else {
                nodeBeforeRemove.setNextNode(currLocation);
            }
            if (nodeToBeRemoved == tail) {
                tail = nodeBeforeRemove;
            }
            IUSingleLinkedList.this.size--;
            IUSingleLinkedList.this.modCount++;
            expectedModCount++;
            callsToRemove++;
            lastReturnedNode = nodeBeforeRemove;

        }
    }

    @Override
    public Iterator<T> iterator() {
        return new IUSingleLinkedListIterator();
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
