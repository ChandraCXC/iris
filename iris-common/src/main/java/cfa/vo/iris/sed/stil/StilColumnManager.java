package cfa.vo.iris.sed.stil;

import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.iris.units.XUnit;
import cfa.vo.iris.units.YUnit;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.utils.Default;
import uk.ac.starlink.table.*;

import java.io.IOException;
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

    public ColumnStarTable flatten(String xUnit, String yUnit, StarTable... tables) throws IOException, UnitsException {
        return new FlattenedStarTable(xUnit, yUnit, tables);
    }

    public static int getColumnIndex(StarTable table, ColumnInfo info) {
        for (int i=0; i<table.getColumnCount(); i++) {
            if (ColumnInfoIndex.sameId(info, table.getColumnInfo(i))) {
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
         * @throws NoSuchElementException when no ColumnInfos in the index have that ID.
         */
        public ColumnInfo get(String id) throws NoSuchElementException {
            // this works with utypes and names, might not work with other stuff we may add in the future
            id = buildId(id);

            if(infosMap.containsKey(id)) {
                return infosMap.get(id);
            }
            throw new NoSuchElementException("Key "+ id + " not in index");
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

        private static String buildId(String id) {
            return new UTYPE(id).getMain();
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
                return buildId(utype);
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

        public FlattenedStarTable(String xUnit, String yUnit, StarTable... tables) throws IOException, UnitsException {
            ColumnInfo[] infos = extractColumns(tables);
            int nCols = infos.length;

            long nRows = 0;
            for (StarTable t : tables) {
                nRows += t.getRowCount();
            }
            this.nRows = nRows;

            List<Object[]> dataMatrix = readData(xUnit, yUnit, tables);

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

        private List<Object[]> readData(String xUnitString, String yUnitString, StarTable... tables) throws IOException, UnitsException {
            UnitsManager unitsManager = Default.getInstance().getUnitsManager();
            XUnit toXUnit = null;
            YUnit toYUnit = null;
            if (xUnitString != null && !xUnitString.isEmpty()) {
                toXUnit = unitsManager.newXUnits(xUnitString);
            }

            if (yUnitString != null && !yUnitString.isEmpty()) {
                toYUnit = unitsManager.newYUnits(yUnitString);
            }

            List<Object[]> retValue = new ArrayList<>();

            for (ColumnInfo info : index.getValues()) {
                List<Object> arrayList = new ArrayList<>();
                Object fillValue = info.getContentClass() == Double.class? Double.NaN : null;
                tableLoop:
                for (StarTable t : tables) {

                    ColumnInfo spectralInfo = new ColumnInfo("");
                    spectralInfo.setUtype(UTYPE.SPECTRAL_VALUES);
                    int spectralIndex = StilColumnManager.getColumnIndex(t, spectralInfo);
                    String fromXUnits = t.getColumnInfo(spectralIndex).getUnitString();
                    XUnit fromXUnit = unitsManager.newXUnits(fromXUnits != null ? fromXUnits : "");

                    ColumnInfo fluxInfo = new ColumnInfo("");
                    fluxInfo.setUtype(UTYPE.FLUX_VALUES);
                    int fluxIndex = StilColumnManager.getColumnIndex(t, fluxInfo);
                    String fromYUnits = t.getColumnInfo(fluxIndex).getUnitString();
                    YUnit fromYUnit = unitsManager.newYUnits(fromYUnits != null ? fromYUnits: "");

                    ColumnInfo statErrorInfo = new ColumnInfo("");
                    statErrorInfo.setUtype(UTYPE.FLUX_STAT_ERROR);
                    int statErrorIndex = StilColumnManager.getColumnIndex(t, statErrorInfo);
                    YUnit fromErrUnit = fromYUnit;

                    for (int i=0; i<t.getColumnCount(); i++) {
                        ColumnInfo columnInfo = t.getColumnInfo(i);
                        if (ColumnInfoIndex.sameId(info, columnInfo)) {
                            boolean convertX = false;
                            boolean convertY = false;
                            boolean convertErr = false;
                            if (i == spectralIndex) {
                                convertX = toXUnit != null && toXUnit.isValid() && !toXUnit.equals(toYUnit);
                            }

                            if (i == fluxIndex) {
                                convertY = toYUnit != null && toXUnit != null && toYUnit.isValid() && toXUnit.isValid() && !fromXUnit.equals(toXUnit) && !fromYUnit.equals(toYUnit);
                            }

                            if (i == statErrorIndex) {
                                convertErr = toYUnit != null && toXUnit != null && toYUnit.isValid() && toXUnit.isValid() && !fromXUnit.equals(toXUnit) && !fromErrUnit.equals(toYUnit);
                            }

                            for (long j = 0; j < t.getRowCount(); j++) {
                                Object value = t.getCell(j, i);
                                if (convertX) {
                                    value = unitsManager.convertX(new double[]{(double) value}, fromXUnit, toXUnit)[0];
                                }
                                if (convertY) {
                                    double spectralValue = (double) t.getCell(j, spectralIndex);
                                    value = unitsManager.convertY(new double[]{(double) value}, new double[]{spectralValue}, fromYUnit, fromXUnit, toYUnit)[0];
                                }

                                if (convertErr) {
                                    double spectralValue = (double) t.getCell(j, spectralIndex);
                                    double fluxValue = (double) t.getCell(j, fluxIndex);
                                    value = unitsManager.convertErrors(new double[]{(double) value}, new double[]{fluxValue}, new double[]{spectralValue}, fromYUnit, fromXUnit, toYUnit)[0];
                                }
                                arrayList.add(value);
                            }
                            continue tableLoop;
                        }
                    }
                    int tableRows = Tables.checkedLongToInt(t.getRowCount());
                    Object[] tableArray = new Object[tableRows];
                    Arrays.fill(tableArray, fillValue); // Not sure this is necessary, in any case this is not the final implementation.
                    arrayList.addAll(Arrays.asList(tableArray));

                }
                retValue.add(arrayList.toArray());
            }
            return retValue;
        }
    }
}
