package test.tools;

import test.gc.HeapFiller;

/**
 * List that maps long values to T instances.
 *
 */
public class SparseListMap<T> {

    private ListElement<T> head;
    private ListElement<T> iterator;

    private static class ListElement<T> {
        private final long key;
        private T value;
        private ListElement<T> next;
        
        public ListElement(long k, T value, ListElement<T> next) {
            this.key = k;
            this.value = value;
            this.next = next;
        }
        
        public T getValue() {
            return value;
        }
        
        public void setValue(T val) {
            value = val;
        }
        
        public long getKey() {
            return key;
        }

        public ListElement<T> next() {
            return next;
        }

        public void setNext(ListElement<T> ee) {
            next = ee;
        }
    }
    
    public void put(long key, T val) {
        ListElement<T> cur = getHead();
        ListElement<T> prev = null;
        while (cur != null && cur.getKey() < key) {
            prev = cur;
            cur = cur.next();
        }
        if (cur != null && cur.getKey() == key) {
            cur.setValue(val);
        } else {
            ListElement<T> ee = new ListElement<T>(key, val, cur);
            if (prev != null) {
                prev.setNext(ee);
            } else {
                setHead(ee);
            }
        }
    }

    public T get(long key) {
        ListElement<T> cur = getHead();
        T result = null;
        while (cur != null && cur.getKey() <= key) {
            if (cur.getKey() == key) {
                result =  cur.getValue();
                break;
            }
            cur = cur.next();
        }
        return result;
    }

    public void clear() {
        setHead(null);
    }

    private ListElement<T> getHead() {
        return head;
    }

    private void setHead(ListElement<T> elem) {
        head = elem;
    }

    public void resetIterator() {
        iterator = head;
    }

    public boolean hasNext() {
        return iterator != null;
    }

    public T next() {
        T res = iterator.getValue();
        iterator = iterator.next();
        return res;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SparseListMap:");;
        for (ListElement<T> cur = head; cur != null; cur = cur.next()) {
            sb.append(" " + cur.getKey() + ":" + cur.getValue());
        }
        return sb.toString();
    }
}
