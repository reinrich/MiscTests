package testlib.tools;

import java.util.ArrayList;

public class NestedArrayList<T> {

    // idx = | level 0       | level 1 | level 2 |
    //                                  Little End
    private static final int LEVEL1_SIZE_LOG2 = 7;
    private static final int LEVEL2_SIZE_LOG2 = 7;
    private static final int LEVEL1_MASK = (1 << LEVEL1_SIZE_LOG2) - 1;
    private static final int LEVEL2_MASK = (1 << LEVEL2_SIZE_LOG2) - 1;
    
    private int size;
    private ArrayList<ArrayList<ArrayList<T>>> l0list; // level 0 of the nesting
    private ArrayList<T> lastElementList;

    public NestedArrayList() {
        l0list = new ArrayList<ArrayList<ArrayList<T>>>();
    }

    private static int getL0idx(long idx) {
        return (int)(idx >> (LEVEL1_SIZE_LOG2 + LEVEL2_SIZE_LOG2));
    }

    private static int getL1idx(long idx) {
        return (int)(idx >> LEVEL2_SIZE_LOG2) & LEVEL1_MASK;
    }

    private static int getL2idx(long idx) {
        return (int)(idx & LEVEL2_MASK);
    }

    public int size() {
        return size;
    }

    public T get(long i) {
        int l0idx = getL0idx(i);
        int l1idx = getL1idx(i);
        int l2idx = getL2idx(i);
        return l0list.get(l0idx).get(l1idx).get(l2idx);
    }

    public void set(long i, T elem) {
        if (i == size) {
            add(elem);
            return;
        }
        int l0idx = getL0idx(i);
        int l1idx = getL1idx(i);
        int l2idx = getL2idx(i);
        ArrayList<ArrayList<T>> l1list = l0list.get(l0idx);
        ArrayList<T> l2list = l1list.get(l1idx);
        l2list.set(l2idx, elem);
    }

    public boolean add(T elem) {
        int i = size++;
        int l0idx = getL0idx(i);
        int l1idx = getL1idx(i);
        if (l0idx >= l0list.size()) {
            // add new level 1 list to level 0 list
            l0list.add(new ArrayList<ArrayList<T>>(1<<LEVEL1_SIZE_LOG2));
        }
        ArrayList<ArrayList<T>> l1list = l0list.get(l0idx);
        if (l1idx >= l1list.size()) {
            // add new level 2 list to level 1 list
            l1list.add(new ArrayList<T>(1<<LEVEL2_SIZE_LOG2));
        }
        ArrayList<T> l2list = lastElementList = l1list.get(l1idx);
        return l2list.add(elem);
    }

    public T removeLast() {
        size--;
        T result = lastElementList.remove(lastElementList.size() - 1);
        if (lastElementList.size() == 0) {
            lastElementList = findLastElementList();
        }
        return result;
    }

    private ArrayList<T> findLastElementList() {
        int i = size-1;
        int l0idx = getL0idx(i);
        int l1idx = getL1idx(i);
        ArrayList<ArrayList<T>> l1list = l0list.get(l0idx);
        return l1list.get(l1idx);
    }

    @Override
    public String toString() {
        int s0 = l0list.size();
        int s1 = 0;
        int s2 = 0;

        if (s0 > 0) {
            s1 = l0list.get(0).size();
            if (s1 > 0) {
                s2 = l0list.get(0).get(0).size();
            }
        }

        
//        StringBuilder sb = new StringBuilder("NestedArrayList:");
//        int size0 = l0list.size();
//        for (int i = 0; i < size0; i++) {
//            ArrayList<ArrayList<T>> l1list = l0list.get(i);
//            int size1 = l1list.size();
//            for (int j = 0; j < size1; j++) {
//                ArrayList<T> l2list = l1list.get(j);
//                int size2 = l2list.size();
//                sb.append(System.lineSeparator());
//                sb.append("["+size0+"]["+size1+"]["+size2+"]" + "i" + i + "j" + j);
//            }
//            
//        }
//        
//        return sb.toString();
        return "NestedArrayList("+size+")["+s0+"]["+s1+"]["+s2+"]";
    }
}
