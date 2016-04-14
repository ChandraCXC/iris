/**
 * Copyright (C) 2012, 2015 Smithsonian Astrophysical Observatory
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

package cfa.vo.sherpa.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class ModelFactory {
    private Map<String, Model> modelsMap = new TreeMap<>();

    public ModelFactory() {
        try {
            Properties props = new Properties();
            InputStream in = getClass().getResource("models.properties").openStream();
            props.load(in);
            in.close();
            URL modelsFile = getClass().getResource(props.getProperty("datafile"));
            build(modelsFile);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot find file models.properties", e);
        }
    }

    private void build(URL modelsFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(modelsFile.openStream());
            Element root = doc.getDocumentElement();
            NodeList models = root.getElementsByTagName("model");
            for (int i=0; i<models.getLength(); i++) {
                Element modelElem = (Element) models.item(i);
                Model model = new ModelImpl(modelElem);
                modelsMap.put(model.getName(), model);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Cannot parse sherpa xml models file", e);
        }
    }

    public int getSize() {
        return modelsMap.size();
    }

    public Model getModel(String name, String id) throws NoSuchElementException {
        if (!modelsMap.containsKey(name)) {
            throw new NoSuchElementException("No such model: "+ name);
        }
        return new ModelImpl(modelsMap.get(name), id);
    }

    public Collection<Model> getModels() {
        return modelsMap.values();
    }
}
