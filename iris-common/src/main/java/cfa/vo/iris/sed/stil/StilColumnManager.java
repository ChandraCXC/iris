package cfa.vo.iris.sed.stil;

import uk.ac.starlink.table.*;

import java.io.IOException;
import java.security.KeyException;
import java.util.*;

public class StilColumnManager {
    private ColumnInfoIndex index = new ColumnInfoIndex();

    /**
     * Generate ColumnInfos for a series of StarTables. The resulting array can be used to create a new table
     * that concatenates and joins different tables.
     *
     * ColumnInfos are guaranteed to have different IDs.
     *
     * ColumnInfos without ID are not included.
     *
     * @param tables The tables that are to be joined
     * @return An Array of ColumnInfo objects representing the Columns of a table concatenation of the input tables.
     */
    public ColumnInfo[] extractColumns(StarTable... tables) {
        for (StarTable t : tables) {
            ColumnInfo[] infos = Tables.getColumnInfos(t);
            for (ColumnInfo i : infos) {
                index.put(i);
            }
        }

        return index.getValues().toArray(new ColumnInfo[]{});

    }

    public ColumnStarTable flatten(StarTable... tables) throws IOException {
        return new FlattenedStarTable(tables);
    }

    public static int getColumnIndex(StarTable table, String utype) {
        for (int i=0; i<table.getColumnCount(); i++) {
            if (utype.equals(table.getColumnInfo(i).getUtype())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Class for managing indexes of ColumnInfos templates, with keys that depend on the metadata available for the info.
     *
     * The ID of a ColumnInfo is its utype if available, otherwise it is its name.
     */
    public static class ColumnInfoIndex {
        private Map<String, ColumnInfo> infosMap = new TreeMap<>();

        /**
         * get the ColumnInfo template for a specific ID
         * @param id the ID
         * @return A ColumnInfo instance if available
         * @throws KeyException when no ColumnInfos in the index have that ID.
         */
        public ColumnInfo get(String id) throws KeyException {
            if(infosMap.containsKey(id)) {
                return infosMap.get(id);
            }
            throw new KeyException("Key "+ id + " not in index");
        }

        /**
         * return whether the index contains an info with the same ID as the argument.
         * Note that not necessarily the ColumnInfo provided as argument and the one in the index are equals.
         * This call simply checks if a ColumnInfo (template) with the same ID as the argument is in the index.
         *
         * @param info The ColumnInfo object to check
         * @return true if a ColumnInfo with the same ID is in the index, false otherwise.
         */
        public Boolean hasInfo(ColumnInfo info) {
            return containsId(info);
        }

        /**
         * put info in index. The operation will be actually performed only if there is no ColumnInfo template
         * with the same ID in the index. The return value informs the client whether the operation happened or not.
         *
         * If the instance does not have a valid ID (e.g. an empty string as name and no utype) then the operation
         * is not performed either.
         *
         * @param info the ColumnInfo instance to add to the index
         * @return true if the operation was performed, i.e. if no ColumnInfo with the same ID was found in the index,
         * false, if a ColumnInfo template with the same ID was found, so the argument was not inserted.
         */
        public Boolean put(ColumnInfo info) {
            if (!getId(info).isEmpty() && !containsId(info)) {
                doPut(info);
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

        /**
         * return an unmodifiable Collection of the ColumnInfo instances in this index.
         * @return an unmodifiable Collections of ColumnInfo instances.
         */
        public Collection<ColumnInfo> getValues() {
            return Collections.unmodifiableCollection(infosMap.values());
        }

        private Boolean containsId(ColumnInfo info) {
            String id = getId(info);
            if (!id.isEmpty() && infosMap.containsKey(id)) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

        private void doPut(ColumnInfo info) {
            String id = getId(info);
            infosMap.put(id, info);
        }

        private static String getId(ColumnInfo info) {
            String utype = info.getUtype();

            if (utype != null && !utype.isEmpty()) {
                return utype;
            }

            String name = info.getName();

            if (name != null && !name.isEmpty()) {
                return name;
            }

            return "";
        }

        /**
         * check that two ColumnInfo instances have the same ID.
         * @param info1
         * @param info2
         * @return
         */
        public static boolean sameId(ColumnInfo info1, ColumnInfo info2) {
            return getId(info1).equals(getId(info2));
        }
    }

    private class FlattenedStarTable extends uk.ac.starlink.table.ColumnStarTable {
        private long nRows;

        public FlattenedStarTable(StarTable... tables) throws IOException {
            ColumnInfo[] infos = extractColumns(tables);
            int nCols = infos.length;

            long nRows = 0;
            for (StarTable t : tables) {
                nRows += t.getRowCount();
            }
            this.nRows = nRows;

            List<Object[]> dataMatrix = readData(tables);

            for (int i=0; i<nCols; i++) {
                ColumnInfo info = infos[i];
                ColumnData column;
                if (info.getContentClass().isPrimitive()) {
                    // It does not look like this is even possible, see:
                    // http://www.star.bristol.ac.uk/~mbt/stil/javadocs/uk/ac/starlink/table/ValueInfo.html#getContentClass%28%29
                    column = PrimitiveArrayColumn.makePrimitiveColumn(info, dataMatrix.get(i));
                } else {
                    column = new ObjectArrayColumn(info, dataMatrix.get(i));
                }
                this.addColumn(column);
            }
        }

        @Override
        public long getRowCount() {
            return nRows;
        }

        private List<Object[]> readData(StarTable... tables) throws IOException {
            List<Object[]> retValue = new ArrayList<>();

            Map<ColumnInfo, List<Object>> map = new HashMap<>();
            for (ColumnInfo info : index.getValues()) {
                List<Object> arrayList = new ArrayList<>();
                map.put(info, arrayList);
                tableLoop:
                for (StarTable t : tables) {
                    for (int i=0; i<t.getColumnCount(); i++) {
                        if (ColumnInfoIndex.sameId(info, t.getColumnInfo(i))) {
                            for (long j = 0; j < t.getRowCount(); j++) {
                                arrayList.add(t.getCell(j, i));
                            }
                            continue tableLoop;
                        }
                    }
                    int tableRows = Tables.checkedLongToInt(t.getRowCount());
                    Object[] tableArray = new Object[tableRows];
                    Arrays.fill(tableArray, null); // Not sure this is necessary, in any case this is not the final implementation.
                    arrayList.addAll(Arrays.asList(tableArray));
                }
                retValue.add(arrayList.toArray());
            }
            return retValue;
        }
    }
}
