

import java.util.Iterator;
import java.util.ArrayList;
import DS.SimpleArray;
import javafx.util.Pair;

/**
 * Class to implement a hashmap using a simple array
 * and quadratic probing to
 * @param <K>
 * @param <V>
 */
public class HashMapDouble<K, V> implements Map<K, V> {

    private static final double loadFactor = 0.5;
    private static final int start = 11;

    private SimpleArray<Unit<K, V>> data;
    private int size;
    private int capacity;



    private static class Unit<K, V> {
        K key;
        V value;

        Unit(K k, V v) {
        this.key = k;
        this.value = v;
        }


        public boolean equals(Object unit) {
            return (unit instanceof Unit) && (this.key.equals(((Unit)unit).key));
        }

        public int hashCode() {
            return this.key.hashCode();
        }


    }


    public HashMapDouble() {
        this.data = new SimpleArray<>(start, null);
        this.capacity = start;
    }

    private int nextPrime() {
        int cur = this.capacity;
        int n = 1;
        while (true) {
            for (int i = 1; i <= cur; i++) {
                n *= i;
            }
            if ((n % (cur + 1)) / cur == 1) {
                return cur + 1;
            } else {
                cur++;
            }
            n = 1;
        }
    }

    private void resize() {
        int newSize = nextPrime();
        SimpleArray<Unit<K, V>> temp = this.data;
        this.data = new SimpleArray<Unit<K, V>>(newSize, null);
        for (Unit<K, V> unit : temp) {
            if (unit != null) {
                this.insert(unit);
            }
        }
        this.capacity = newSize;
    }


    private Unit<K, V> find(K k) {
        int check = hashVal(k);
        int unitsCmp = 1;
        int j = 0;
        int begin = check;

        while (j < this.size) {
            Unit<K, V> comp = this.data.get(check);

            if (comp == null) {
                return null;
            } else if (comp.key.equals(k) && comp.value != null) {
                return comp;
            } else {
                check = (begin + unitsCmp * unitsCmp) % this.capacity;
                unitsCmp++;
            }
            j++;
        }
        return null;
    }


    private int hashVal(K k) {
        return Math.abs(k.hashCode()) % this.capacity;
    }


    public void insert(K k, V v) {
        if (k == null) {
            throw new IllegalArgumentException();
        } else if (this.find(k) != null) {
            throw new IllegalArgumentException();
        } else {
            Unit<K, V> unit = new Unit<K, V>(k, v);
            insert(unit);
        }

    }

    public boolean has(K k) {
        if (k == null) {
            throw new IllegalArgumentException();
        } else {
            return find(k) != null;
        }
    }



    private void insert(Unit<K, V> unit) {

        if ((double) size / capacity > loadFactor) {
            resize();
        }

        if (unit.key == null) {
            throw new IllegalArgumentException();
        }

        int check = this.hashVal(unit.key);
        int j = 0;
        int unitsCmp = 1;
        int begin = check;

        while (j < this.capacity) {
            Unit<K, V> exists = this.data.get(check);

            if (exists == null || exists.value == null) {
                this.data.put(check, unit);
                this.size++;
                return;
            } else {
                check = (begin + unitsCmp * unitsCmp) % this.capacity;
                unitsCmp++;
            }

            j++;
        }
    }


    public V remove(K k) {
        if (k == null) {
            throw new IllegalArgumentException();
        }
        if (!this.has(k)) {
            throw new IllegalArgumentException();
        }

        int check = this.hashVal(k);
        Unit<K, V> unit = this.data.get(check);
        V temp = unit.value;
        unit.value = null;
        this.data.put(check, unit);
        this.size--;
        return temp;

    }


    public void put(K k, V v) {
        if (k == null) {
            throw new IllegalArgumentException();
        }
        if (!this.has(k)) {
            throw new IllegalArgumentException();
        }

        this.find(k).value = v;
    }

    public V get(K k) {
        if (k == null) {
            throw new IllegalArgumentException();
        }
        if (!this.has(k)) {
            throw new IllegalArgumentException();
        }
        Unit<K, V> unit = this.find(k);
        return unit.value;

    }

    public int size() {
        return this.size;
    }


    public Iterator<K> iterator() {
        ArrayList<K> keys = new ArrayList<>();
        for (Unit<K, V> unit : this.data) {
            if (unit != null && unit.value != null) {
                keys.add(unit.key);
            }
        }
        return keys.iterator();
    }


    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("{");

        for (K k : this) {
            s.append(k);
            s.append(" : ");
            s.append(this.get(k));
            s.append(", ");
        }

        int length = s.length();
        if (length > 1) {
            s.setLength(length - 2);
        }

        s.append("}");
        return s.toString();


    }

}