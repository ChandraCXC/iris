/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.builder;

import cfa.vo.sed.quantities.IQuantity;
import cfa.vo.sed.quantities.IUnit;
import cfa.vo.sed.quantities.SPVYQuantity;
import cfa.vo.sed.quantities.SPVYUnit;
import cfa.vo.sed.quantities.XQuantity;
import cfa.vo.sed.quantities.XUnit;
import cfa.vo.sed.setup.ErrorType;
import cfa.vo.sed.setup.SetupBean;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbudynk
 */
public class AsciiConf {
    
    /**
    * Create a configuration SetupBean for reading ASCIITABLE formatted files
    * 
    * @param url URL of ASCII file to read
    * @return the SetupBean object describing the SED data in the ASCII file
    */
	
    public SetupBean makeConf(URL url) throws SedImporterException, AsciiConfException {
        try {

            // read-in Iris ASCII header data

            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            String colnames = "";
	    HashMap metadata = new HashMap();

            while ((line = bufferedReader.readLine()).startsWith("#")) {
                
                    if(line.contains("=")) {
			String[] data = line.substring(2).split("= ", 2);
			metadata.put(data[0].replaceAll("\\s+", ""), data[1]);
		    }
                    if(line.startsWith("# x")) {
                            //colnames = line.substring(2).split("[ ]+");
			    colnames = line.substring(2);
                    }
            }

	    bufferedReader.close();
	    
            //If we want to throw an exception saying the Iris ASCII format is 
            //   incorrect
            
            if(metadata.size() < 5){
                throw new AsciiConfException("Iris ASCII file is formatted incorrectly. Check the file.");
            }

            SetupBean conf = new SetupBean();

	    // Get the quantities from the flux and spectral units.
	    
	    IUnit unitx = XUnit.getFromUnitString(metadata.get("XUNIT").toString());
	    IUnit unity = SPVYUnit.getFromUnitString(metadata.get("YUNIT").toString());
	    IQuantity yquantity = AsciiConf.getXQuantityFromUnit(unity);
	    IQuantity xquantity = AsciiConf.getXQuantityFromUnit(unitx);
	    
	    // Setup the configuration bean
	    
            conf.setFileLocation(url.toString());
            conf.setFormatName("ASCIITABLE");
            conf.setPublisher(" ");
            conf.setTargetName(metadata.get("TARGET").toString());
            conf.setTargetRa(metadata.get("RA").toString());
            conf.setTargetDec(metadata.get("DEC").toString());
            conf.setXAxisColumnNumber(0);
            conf.setYAxisColumnNumber(1);
            conf.setXAxisQuantity(xquantity.getName().toUpperCase());
	    System.out.println("");
            conf.setXAxisUnit(metadata.get("XUNIT").toString());
            conf.setYAxisQuantity(yquantity.getName().toUpperCase());
            conf.setYAxisUnit(metadata.get("YUNIT").toString());    
            if(colnames.contains("y_err")) {
                    conf.setErrorType(ErrorType.SymmetricColumn.name());
                    conf.setSymmetricErrorColumnNumber(2);
            }
            else {
                    conf.setErrorType(ErrorType.Unknown.name());
            }

            return conf;

        } catch (IOException ex) {
            Logger.getLogger(AsciiConf.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static IQuantity getXQuantityFromUnit(IUnit unit) {
	
	List<IQuantity> quantities = new ArrayList();
	
	for (XQuantity quantity : EnumSet.allOf(XQuantity.class)) {
	    quantities.add(quantity);
	}
	for (SPVYQuantity quantity : EnumSet.allOf(SPVYQuantity.class)) {
	    quantities.add(quantity);
	}
	
	for (IQuantity quantity : quantities) {
	    for (IUnit unitCheck : quantity.getPossibleUnits()) {
		if (unitCheck == unit) {
		    return quantity;
		}
	    }
	} 
	return null;
    }
    
}
