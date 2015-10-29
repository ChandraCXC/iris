/*
 * This software is distributed under a BSD license,
 * as described in the LICENSE file at the top source directory.
 */

package cfa.vo.iris.units.spv;

/*
 *  Revision history:
 *  ----------------
 *
 *
 *  15 Apr 99  -  Implemented (IB)
 *  04 Aug 99  -  Moved to spv.util (IB)
 *  29 Jan 01  -  Replace Vector by ArrayList (IB)
 *  30 Apr 01  -  Fix replacement in put() method (IB)
 *  17 Aug 01  -  Clone method via serialization (IB)
 *  26 Jun 03  -  Renamed things (IB)
 *  10 Jum 04  -  remove(index) method (IB)
 *  09 Dec 04  -  values() method (IB)
 *  21 Dec 05  -  array with keys (IB)
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Collection;

import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 *  This class defines an <code>Vector</code>-type object whose elements
 *  can be accessed by keys in a similar way as an <code>Hashtable</code>.
 *  Hashtables return their elements in an unpredictable ordering,
 *  this class keeps elements ordered in the same sequence they were
 *  stored.
 *  <p>
 *  Objects to be stored can be anything, but key objects must implement
 *  the <code>equals()</code> method. See also the entry for the
 *  <code>clone</code> method below.
 *  <p>
 *  7/1/99 - Added support for the <code>DQBits</code> class.
 *  <p>
 *  29/1/01 - Replaced <code>Vector</code> by <code>ArrayList</code>.
 *
 *
 *  @version  1.0 - 15Apr99
 *  @version  1.1 - 01Jul99
 *  @author   Ivo Busko (Space Telescope Science Institute)
 */

public class KeyedVector implements Serializable, Cloneable {

    static final long serialVersionUID = 11L;

    private ArrayList object_storage = new ArrayList();
    private ArrayList key_storage    = new ArrayList();

    /**
     *  Stores object with key. If an object with the same key
     *  is found already stored, it is replaced by the new one.
     *
     *  @param  key      the object's key
     *  @param  object   the object to be stored
     */
    public void put (Object key, Object object) {
        if (containsKey (key)) {
            remove (key);
        }
        synchronized (this) {
            object_storage.add (object);
            key_storage.add (key);
        }
    }

    /**
     *  Removes object with given key.
     *
     *  @param  key   key of object to be removed
     */
    public void remove (Object key) {

        // We cannot use remove() directly on both vectors since only
        // the key objects are required to implement equals().

        int k = key_storage.indexOf (key);
        if (k == -1) {
            return;
        }
        synchronized (this) {
            object_storage.remove (k);
            key_storage.remove (k);
        }
    }

    /**
     *  Removes object with given index.
     *
     *  @param   index   index of object to be removed
     */
    public void remove (int index) {
        synchronized (this) {
            object_storage.remove (index);
            key_storage.remove (index);
        }
    }

    /**
     *  Returns a <code>Collection</code> with the objects stored in this.
     *
     *  @return     a <code>Collection</code> with the objects stored in this
     */
    public Collection values() {
        return object_storage;
    }

    /**
     *  Returns an <code>Enumeration</code> with the keys stored in this.
     *
     *  @return  an <code>Enumeration</code> with the keys stored in this
     */
    public Enumeration keys() {
        return new Enumeration() {
            private Object[] array = key_storage.toArray();
            private int current = 0;
            public boolean hasMoreElements() {
                return (current >= 0 && current < array.length);
            }
            public Object nextElement() {
                return array[current++];
            }
        };
    }

    /**
     *  Returns an array with the keys.
     *
     *  @return     teh array with the keys
     */
    public Object[] getKeysArray() {
        return key_storage.toArray();
    }

    /**
     *  Tests if the given key is stored in this.
     *
     *  @return  <code>true</code> if the give key is stored in this,
     *           <code>false</code> otherwise.
     */
    public boolean containsKey (Object key) {
        return key_storage.contains (key);
    }

    /**
     *  Returns the object identified by the given key.
     *
     *  @param   key   the object's key
     *  @return        the object identified by key, or <code>null</code>
     *                 if key is not stored in this.
     */
    public Object get (Object key) {
        int k = key_storage.indexOf (key);
        if (k == -1) {
            return null;
        }
        return object_storage.get (k);
    }

    /**
     *  Returns the number of elements stored in the keyed vector.
     *
     *  @return  the number of elements stored in the keyed vector
     */
    public int getSize() {
        return object_storage.size();
    }

    /**
     *  Returns the key specified by the given index.
     *
     *  @param   index  the key's index
     *  @return         the key at the index position, or <code>null</code>
     *                  if index is not valid.
     */
    public String getKey (int index) {
        if (index < 0 || index > key_storage.size()) {
            return null;
        }
        return (String)(key_storage.get (index));
    }

    /**
     *  Returns the object specified by the given index.
     *
     *  @param   index  the object's index
     *  @return         the object at the index position, or <code>null</code>
     *                  if index is not valid.
     */
    public Object get (int index) {
        if (index < 0 || index > object_storage.size()) {
            return null;
        }
        return object_storage.get (index);
    }

    /**
     *  Replaces the object specified by the given index, keeping
     *  the original object's key.
     *
     *  @param   index  the object's index
     *  @param   obj    the replacing object
     */
    public void set (int index, Object obj) {
        object_storage.set (index, obj);
    }


    /////////////////////////////////////////////////////////////////
    //
    //                 Cloneable interface.
    //
    /////////////////////////////////////////////////////////////////


    /**
     *  Returns a clone copy of this object. This method resorts to
     *  serialization to perform a deep copy, thus it only works
     *  correctly if the objects stored in the <code>KeyedVector</code>
     *  are serializable. Note in particular that
     *  <code>Observer</code><-><code>Observable</code> links are
     *  broken by the serialization process, since <code>Observable</code>
     *  does not implement <code>Serializable</code>.
     *
     *  @return  the clone
     */
    public Object clone() throws CloneNotSupportedException {

        // ArrayList.clone performs a shallow copy, not good
        // enough for us. We resort to serialization to perform
        // a deep copy that is at the same time very general.
        // Note that explicit cloning of stored objects via the
        // clone() method is not possible since clone() in the
        // Object class is protected.

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        KeyedVector kv = null;
        try {
            // Serialize this object into a byte array.
            ObjectOutputStream oos = new ObjectOutputStream (baos);
            oos.writeObject (this);
            byte buf[] = baos.toByteArray();
            oos.close();

            // Deserialize byte array into a new KeyedVector.

            ByteArrayInputStream bais = new ByteArrayInputStream (buf);
            ObjectInputStream ois = new ObjectInputStream (bais);
            kv = (KeyedVector)ois.readObject();
            ois.close();
        } catch (IOException e) {
            throw new CloneNotSupportedException();
        } catch (ClassNotFoundException e) {
            throw new CloneNotSupportedException();
        }

        return kv;
    }
}
