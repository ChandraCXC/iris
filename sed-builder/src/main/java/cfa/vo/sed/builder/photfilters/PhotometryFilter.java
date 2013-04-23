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
package cfa.vo.sed.builder.photfilters;

import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sedlib.common.SedException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.util.DataSource;
import uk.ac.starlink.util.FileDataSource;
import uk.ac.starlink.votable.VOTableBuilder;

/**
 *
 * @author olaurino
 */
public class PhotometryFilter implements Cloneable, PassBand {

    private String id;
    private String unit;
    private String band;
    private String instrument;
    private String facility;
    private String description;
    private Float wlmean;
    private Float wleff;
    private Float wlmin;
    private Float wlmax;
    private URL curveURL;
    private String localFile;

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public URL getCurveURL() {
        return curveURL;
    }

    public void setCurveURL(URL curveURL) {
        this.curveURL = curveURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Float getWleff() {
        return wleff;
    }

    public void setWleff(Float wleff) {
        this.wleff = wleff;
    }

    public Float getWlmax() {
        return wlmax;
    }

    public void setWlmax(Float wlmax) {
        this.wlmax = wlmax;
    }

    public Float getWlmean() {
        return wlmean;
    }

    public void setWlmean(Float wlmean) {
        this.wlmean = wlmean;
    }

    public Float getWlmin() {
        return wlmin;
    }

    public void setWlmin(Float wlmin) {
        this.wlmin = wlmin;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }

    public boolean getCurve() throws SedException, IOException {


        //check the existence of the filters dir
        File confDir = SedBuilder.getApplication().getConfigurationDir();

        if (confDir.exists() && confDir.isDirectory()) {

            File filtersDir = new File(confDir.getAbsolutePath() + "/" + "filters");;

            //get the filters dir or create it if it doesn't exist
            List<String> ls = Arrays.asList(confDir.list());
            if (ls.contains("filters")) {
                if (!filtersDir.isDirectory()) {
                    NarrowOptionPane.showMessageDialog(null,
                            "Cannot read the filters directory\n" + filtersDir.getAbsolutePath(),
                            "Error reading configuration",
                            NarrowOptionPane.ERROR_MESSAGE);
                    return false;
                }

            } else {
                filtersDir.mkdir();
            }

            //check file existence
            String filterFileName = filtersDir.getAbsolutePath() + "/" + id.replaceAll("/", "_");

            File filterFile = new File(filterFileName);

            if (!filterFile.exists()) {
                try {
                    ReadableByteChannel rbc = Channels.newChannel(curveURL.openStream());
                    FileOutputStream fos = new FileOutputStream(filterFile);
                    fos.getChannel().transferFrom(rbc, 0, 1 << 24);
                    fos.close();
                } catch (Exception ex) {
                    Logger.getLogger(PhotometryFilter.class.getName()).log(Level.SEVERE, null, ex);
                    NarrowOptionPane.showMessageDialog(null,
                            "Cannot download file. Please check connection.\n" + curveURL,
                            "Error downloading file",
                            NarrowOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            localFile = filterFileName + ".res";
            File filterTextFile = new File(localFile);

            if (!filterTextFile.exists()) {
                VOTableBuilder b = new VOTableBuilder();
                DataSource ds = new FileDataSource(filterFileName);
                StarTable table = b.makeStarTable(ds, false, StoragePolicy.ADAPTIVE);

                FileWriter fw = new FileWriter(localFile);
                BufferedWriter out = new BufferedWriter(fw);

                RowSequence rows = table.getRowSequence();
                
                while(rows.next())
                    out.write(rows.getRow()[0] + " " + rows.getRow()[1] + "\n");
                

                out.close();
            }
        } else {
            NarrowOptionPane.showMessageDialog(null,
                    "Cannot read the configuration directory\n" + confDir.getAbsolutePath(),
                    "Error reading configuration",
                    NarrowOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return getId().split("/")[1];
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(PhotometryFilter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
