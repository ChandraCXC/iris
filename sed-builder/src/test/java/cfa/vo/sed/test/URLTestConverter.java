/**
 * Copyright (C) Smithsonian Astrophysical Observatory
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

package cfa.vo.sed.test;

import java.net.URL;

/**
 *
 * @author olaurino
 */
public class URLTestConverter {

    public static URL getURL(String testUrl) throws Exception {
        if(testUrl.contains("test://"))
            testUrl = testUrl.replace("test://", "file://");

        URL url = new URL(testUrl);
        
        if(url.getProtocol().equals("file")){
            String fileName = url.getFile();
            try {
                URL fileURL = URLTestConverter.class.getResource(fileName);
                return fileURL;
            } catch (Exception ex) {
                throw ex;
            }
        } else {

            return url;
            
        }


    }
}
