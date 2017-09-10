package org.osori.sectionadapter;

/**
 * IndexPath
 *
 * Concept of IndexPath is came from Swift TableView.
 * It have big Section in table and
 * Table contains Item.
 *
 * section is index of section and
 * item is index of item.
 */

public class IndexPath {
    public int section;
    public int item;

    // Header's item value
    public static int HEADER = -1;
    // Footer's item value
    public static int FOOTER = -2;

    public IndexPath(int section, int item) {
        this.section = section;
        this.item = item;
    }
}
