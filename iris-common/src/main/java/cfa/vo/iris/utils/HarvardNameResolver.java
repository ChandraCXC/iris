/**
 * This class was adapted from the CSCView name resolver.
 */
package cfa.vo.iris.utils;

import cfa.vo.iris.utils.NameResolver.Position;
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/********************
This class represents the actual name resolver service used to
convert a target name into a coordinate.

We support simbad and ned and they each have a URL interface.
 *********************/
public class HarvardNameResolver {

    private boolean isIdentifierAmbiguous = false; // true if request was ambiguous
    private BufferedReader stdInput = null;
    private Services services;
    private static HarvardNameResolver resolver;

    private class Services extends HashMap<String, String> {

        public Services() {
            put("NED", "ned");
            put("SIMBAD", "simbad");
        }
    }

    private HarvardNameResolver() {
        services = new Services();
    }

    public static HarvardNameResolver getInstance() {
        if (resolver == null) {
            resolver = new HarvardNameResolver();
        }

        return resolver;
    }

    /**
    Convert identifier into coordinates from name resolver.
    
    @param identifier client-requested identifier
    
    @return coordinates corresponding to identifier in this resolver. Returns
    null if no coordinates could be found (because resolver is unavailable, the
    identifier was not known to the resulver, or the identifier was ambiguous).
    
    @throws ServiceNotAvailableException  if the resolver produced unexpected
    output or wasn't free to connnect to the service
     */
    public Position resolve(String service, String identifier)
            throws IOException {

        String resolverName = services.get(service);

        Position coord = null;
        isIdentifierAmbiguous = false;

        String urlName1, urlName2;
        ArrayList<String> resolverURLs = new ArrayList<String>();
        urlName1 = urlName2 = "";

        if (resolverName.compareTo("simbad") != 0
                && resolverName.compareTo("ned") != 0) {
            throw new IOException("resolverName " + resolverName + " invalid");
        }

        try {
            String script;

            if (resolverName.equals("simbad")) {

                script = "/simbad/sim-script?script=format%20object%20%22%25IDLIST(1)%7C%25COO(d;A%20D;FK5)%22%0aoutput%20console=off%20script=off%0aquery%20id%20";
                urlName1 = "http://simbad.harvard.edu" + script
                        + URLEncoder.encode(identifier, "UTF-8") + "%0a";
                urlName2 = "http://simbad.u-strasbg.fr" + script
                        + URLEncoder.encode(identifier, "UTF-8") + "%0a";
                resolverURLs.add(urlName2);
                resolverURLs.add(urlName1);

            } else if (resolverName.equals("ned")) {

                script = "/cgi-bin/nph-objsearch?extend=no&out_csys=Equatorial&out_equinox=J2000.0&of=ascii_bar&list_limit=5&img_stamp=NO&";
                urlName1 = "http://nedwww.ipac.caltech.edu" + script
                        + "objname=" + URLEncoder.encode(identifier, "UTF-8");
                resolverURLs.add(urlName1);
            }

        } catch (UnsupportedEncodingException uee) {
        }

        for (String resolverURL : resolverURLs) {

            // simbad will try Garden St. and Strasburg, ned only has one server


            // print URL to log for admin purposes
            Logger.getLogger(getClass().getName()).log(Level.INFO, "resolverURL = " + resolverURL);

            URL url1 = new URL(resolverURL);
            URLConnection conn = url1.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;

            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            httpConn.setRequestProperty("User-agent", "nameresolver/CXCDS8.0");

            // NB ned cgi is not returning well-formed HTTP status code
            if (resolverName.equals("simbad") && httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {

                Logger.getLogger(getClass().getName()).log(Level.INFO, "simbad (" + identifier + ") returned " + httpConn.getResponseCode() + ": " + httpConn.getResponseMessage());;

            } else {

                stdInput =
                        new BufferedReader(new InputStreamReader(httpConn.getInputStream()));


                // simbad resolver returns either data or error response
                // the error response will be returned verbatim unless it is
                // 'identifier not found', which is not an error per se

                if (resolverName.equals("simbad")) {

                    coord = resolveBySimbad(identifier);

                } else if (resolverName.equals("ned")) {

                    coord = resolveByNed(identifier);

                }

                if (stdInput != null) {
                    stdInput.close();
                    stdInput = null;
                }
                if (isIdentifierAmbiguous) {
                    coord = null;
                }
            }

            if (coord != null) {
                break;
            }

        }

        return coord;
    }

    public Position resolveBySimbad(String identifier)
            throws IOException {

        Position coord = null;
        String inputLine;

        while ((inputLine = stdInput.readLine()) != null) {

            if (inputLine.indexOf("::error::") >= 0) {

                while ((inputLine = stdInput.readLine()) != null) {
                    int pos = inputLine.indexOf(":");
                    if (inputLine.indexOf(":") >= 0) {

                        // If error is not 'not found', print it to the log
                        if (inputLine.indexOf("Identifier not found") == -1) {
                            Logger.getLogger(getClass().getName()).log(Level.INFO, "simbad error: " + inputLine);
                        }

                        // finish reading output
                        while ((inputLine = stdInput.readLine()) != null);
                    }
                }

            } else if (inputLine.indexOf("::data::") >= 0) {

                while ((inputLine = stdInput.readLine()) != null
                        && isIdentifierAmbiguous == false) {

                    int pos = inputLine.indexOf("|");
                    if (pos > -1) {
                        if (coord != null) {
                            Logger.getLogger(getClass().getName()).log(Level.INFO, "simbad finds " + identifier + " ambiguous");
                            isIdentifierAmbiguous = true;
                        } else {
                            StringTokenizer st =
                                    new StringTokenizer(inputLine.substring(pos + 1), " ");
                            String firstToken = st.nextToken();
                            String secondToken = st.nextToken();

                            try {
                                double first = Double.parseDouble(firstToken);
                                double second = Double.parseDouble(secondToken);
                                coord = new Position(
                                        Double.valueOf(firstToken).doubleValue(),
                                        Double.valueOf(secondToken).doubleValue());
                            } catch (NumberFormatException nfe) {
                                // no processing
                            }
                        }
                    }
                }
            }

        }
        
        if(coord==null)
            throw new IOException("Could not resolve name "+identifier);

        return coord;

    }

    public Position resolveByNed(String identifier)
            throws IOException {

        // ned prints a report header, then columns separated by |
        // but the error comes as in VOTable in a param element with
        // with the message either in the value attribute or in a
        // cdata element.  A mix of XML and pseudo-rdb format.

        // The 'not found' message can be either in a one-line value attribute 
        // or in an 'identifier not currently recognized' message in cdata.

        Position coord = null;
        String inputLine;
        StringBuffer errorMessage = new StringBuffer();

        while ((inputLine = stdInput.readLine()) != null) {

            int pos, endPos;

            if ((pos = inputLine.indexOf("value=")) >= 0) {

                // one-line error: print to log if it is not 'not found'
                endPos = inputLine.indexOf("\"/>");
                if (endPos >= 0) {
                    errorMessage = new StringBuffer(inputLine.substring(pos + 7, endPos));
                } else {
                    errorMessage = new StringBuffer(inputLine.substring(pos + 7));
                }

                if (inputLine.indexOf("is no object with this name") < 0) {
                    Logger.getLogger(getClass().getName()).log(Level.INFO, errorMessage.toString());
                }

                while ((inputLine = stdInput.readLine()) != null);

            } else if (inputLine.indexOf("<VALUE>") >= 0) {

                if (inputLine.indexOf("multiple choices") >= 0) {
                    isIdentifierAmbiguous = true;
                }

                // a cdata element: print to log if it is not 'not found'
                while ((inputLine = stdInput.readLine()) != null) {
                    if (inputLine.indexOf("</VALUE>") >= 0) {
                        Logger.getLogger(getClass().getName()).log(Level.INFO, errorMessage.toString());
                        // read through the rest
                        while ((inputLine = stdInput.readLine()) != null);
                    } else if (inputLine.indexOf("is not currently recognized") >= 0) {
                        // read through the rest
                        while ((inputLine = stdInput.readLine()) != null);
                    } else {
                        errorMessage.append(inputLine + "\n");
                    }
                }

            } else if (inputLine.indexOf("RA(deg)") >= 0) {

                // RA(deg) is in the column header and the coordinates are in
                // the next line separated by |

                while ((inputLine = stdInput.readLine()) != null
                        && isIdentifierAmbiguous == false) {

                    if (coord != null) {
                        Logger.getLogger(getClass().getName()).log(Level.INFO, "ned finds " + identifier + " ambiguous");
                        isIdentifierAmbiguous = true;
                    } else {
                        StringTokenizer st = new StringTokenizer(inputLine, "|");

                        if (st.countTokens() > 3) {
                            // bypass first two column to get to ra, dec
                            String tmpToken = st.nextToken();
                            tmpToken = st.nextToken();
                            String firstToken = st.nextToken().trim();
                            String secondToken = st.nextToken().trim();

                            try {
                                double first = Double.parseDouble(firstToken);
                                double second = Double.parseDouble(secondToken);
                                coord = new Position(
                                        Double.valueOf(firstToken).doubleValue(),
                                        Double.valueOf(secondToken).doubleValue());

                            } catch (NumberFormatException nfe) {
                                // no processing
                            }
                        }
                    }
                }
            }
        }
        
        if(coord==null)
            throw new IOException("Could not resolve name "+identifier);
        
        return coord;

    }

    /**
    If resolver returned null, this tells caller whether
    the most recent call to resolve() returned an ambiguous answer.
    @return true if there was more than one coordinate for the name
     */
    public boolean isIdentifierAmbiguous() {
        return isIdentifierAmbiguous;
    }
}
