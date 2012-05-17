/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.setup;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sed.builder.ISegmentColumn;
import cfa.vo.sed.setup.validation.AbstractValidable;
import cfa.vo.sed.setup.validation.AbstractValidableParent;
import cfa.vo.sed.builder.dm.ExtendedTarget;
import cfa.vo.sed.builder.dm.PhotometryCatalog;
import cfa.vo.sed.builder.dm.PhotometryCatalogEntry;
import cfa.vo.sed.builder.dm.PhotometryPoint;
import cfa.vo.sed.builder.dm.PhotometryPointSegment;
import cfa.vo.sed.setup.validation.Validable;
import cfa.vo.sed.setup.validation.Validation;
import cfa.vo.sed.filters.AbstractSingleStarTableFilter;
import cfa.vo.sed.filters.IFilter;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import uk.ac.starlink.table.StarTable;

/**
 *
 * @author olaurino
 */
public class PhotometryCatalogBuilder extends AbstractValidableParent implements Builder<List<PhotometryCatalogEntry>> {

    private List<PhotometryPointBuilder> pointBuilders = ObservableCollections.observableList(new ArrayList());
    private StarTable templateTable;
    private IFilter filter;

    public PhotometryCatalogBuilder(AbstractSingleStarTableFilter filter, ExtSed sed, int positionInFile) throws Exception {
        this.templateTable = filter.getStarTable();
        this.filter = filter;
        this.sed = sed;
        this.columns = ObservableCollections.observableList(filter.getMetadata().get(positionInFile).getColumns());
        this.children = ObservableCollections.observableList(new ArrayList());
        this.children.addObservableListListener(getValidator());
        this.children.add(targetBuilder);
    }
    private int[] sources = new int[]{};

    
    public static final String PROP_SOURCES = "sources";

    /**
     * Get the value of sources
     *
     * @return the value of sources
     */
    public int[] getSources() {
        return sources;
    }

    /**
     * Set the value of sources
     *
     * @param sources new value of sources
     */
    public void setSources(int[] sources) {
        int[] oldSources = this.sources;
        this.sources = sources;
        propertyChangeSupport.firePropertyChange(PROP_SOURCES, oldSources, sources);
    }
    PhotometryCatalog catalog;

    public PhotometryCatalog build() throws Exception {
        if (catalog != null) {
            catalog.clear();
        } else {
            catalog = new PhotometryCatalog();
        }

        int rows = filter.getColumnData(0, 0).length;

        if (sources!=null && !(sources.length == 0)) {
            rows = sources.length;
        }

        for (int i = 0; i < rows; i++) {

            int row = sources.length == 0 ? i : sources[i];

            for (int k = 0; k < pointBuilders.size(); k++) {
                PhotometryPointBuilder conf = pointBuilders.get(k);
                PhotometryCatalogEntry entry = new PhotometryCatalogEntry();
                ExtendedTarget t = targetBuilder.build(filter, row);
                entry.getTarget().setName(t.getName());
                entry.getTarget().setRa(t.getRa());
                entry.getTarget().setDec(t.getDec());
                entry.setPublisher(t.getPublisher());
                catalog.add(entry);
                PhotometryPoint point = conf.build(filter, row);
                PhotometryPointSegment s = new PhotometryPointSegment(point);
                entry.add(s);
            }

        }

        return catalog;
    }

    public boolean addPointBuilder(PhotometryPointBuilder conf) {
        children.add(conf);
        updateValidables();
        return pointBuilders.add(conf);
    }

    public boolean removePointBuilder(PhotometryPointBuilder conf) {
        children.remove(conf);
        return pointBuilders.remove(conf);
    }

    public boolean removeAll(List<PhotometryPointBuilder> conf) {
        return pointBuilders.removeAll(conf);
    }

    public List<PhotometryPointBuilder> getPointBuilders() {
        return pointBuilders;
    }

    public StarTable getTemplateTable() {
        return templateTable;
    }

    @Override
    public List<PhotometryCatalogEntry> build(IFilter filter, int row) throws Exception {
        if (filter == null) {
            return build();
        }

        return new ArrayList();
    }

    public IFilter getFilter() {
        return filter;
    }
    private ExtendedTargetBuilder targetBuilder = new ExtendedTargetBuilder();
    public static final String PROP_TARGETBUILDER = "targetBuilder";

    /**
     * Get the value of targetBuilder
     *
     * @return the value of targetBuilder
     */
    public ExtendedTargetBuilder getTargetBuilder() {
        return targetBuilder;
    }

    /**
     * Set the value of targetBuilder
     *
     * @param targetBuilder new value of targetBuilder
     */
    public void setTargetBuilder(ExtendedTargetBuilder targetBuilder) {
        ExtendedTargetBuilder oldTargetBuilder = this.targetBuilder;
        this.targetBuilder = targetBuilder;
        propertyChangeSupport.firePropertyChange(PROP_TARGETBUILDER, oldTargetBuilder, targetBuilder);
    }
    private ExtSed sed;
    public static final String PROP_SED = "sed";

    /**
     * Get the value of sed
     *
     * @return the value of sed
     */
    public ExtSed getSed() {
        return sed;
    }

    /**
     * Set the value of sed
     *
     * @param sed new value of sed
     */
    public void setSed(ExtSed sed) {
        ExtSed oldSed = this.sed;
        this.sed = sed;
        propertyChangeSupport.firePropertyChange(PROP_SED, oldSed, sed);
    }
    private ObservableList<ISegmentColumn> columns;
    public static final String PROP_COLUMNS = "columns";

    /**
     * Get the value of columns
     *
     * @return the value of columns
     */
    public ObservableList<ISegmentColumn> getColumns() {
        return columns;
    }

    /**
     * Set the value of columns
     *
     * @param columns new value of columns
     */
    public void setColumns(ObservableList<ISegmentColumn> columns) {
        ObservableList<ISegmentColumn> oldColumns = this.columns;
        this.columns = columns;
        propertyChangeSupport.firePropertyChange(PROP_COLUMNS, oldColumns, columns);
    }
    private ObservableList<AbstractValidable> children;

    @Override
    protected List<AbstractValidable> getValidableChildren() {
        return children;
    }
    private Validation validation = new Validation();

    @Override
    public Validation validate() {

        validation.reset();

        if (!pointBuilders.isEmpty()) {

            List<Validable> invalid = new ArrayList();

            for (Validable child : children) {
                if(child instanceof PhotometryPointBuilder) {
                    Validation v = child.validate();
                    if (!v.isValid()) {
                        invalid.add(child);
                    }
                }
            }

            StringBuilder builder = new StringBuilder();

            if (!invalid.isEmpty()) {
                builder.append("This configuration is not valid. \n");

                for (Validable invalidBuilder : invalid) {
                    if (invalidBuilder instanceof PhotometryPointBuilder) {
                        PhotometryPointBuilder b = (PhotometryPointBuilder) invalidBuilder;
                        builder.append(b.getId()).append(": \n");
                        for (String error : b.getValidator().getValidation().getErrors()) {
                            builder.append("\t").append(error).append("\n");
                        }
                    } else {
                        for (String error : invalidBuilder.getValidator().getValidation().getErrors()) {
                            builder.append(error).append("\n");
                        }
                    }
                }

                validation.addError(builder.toString());
            }

        } else {
            validation.addError("No Points in the Catalog. Please start adding Points.");
        }

        for (Validable child : children) {
            if (!(child instanceof PhotometryPointBuilder)) {
                for (String error : child.getValidator().getValidation().getErrors()) {
                    validation.addError(error);
                }
            }
        }

        for (Validable child : children) {
            for (String warning : child.getValidator().getValidation().getWarnings()) {
                validation.addWarning(warning);
            }
        }

        return validation;
    }
}
