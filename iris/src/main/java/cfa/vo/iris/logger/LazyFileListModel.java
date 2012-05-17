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

package cfa.vo.iris.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;

public class LazyFileListModel extends AbstractListModel {

    private static final Logger logger =
            Logger.getLogger(LazyFileListModel.class.getName());
    private RandomAccessFile lineCounterFile;
    private List<Long> linePositions =
            Collections.synchronizedList(new ArrayList<Long>());
    private RandomAccessFile readerFile;
    private int last = -1;

    /**
     * Create a <tt>LazyFileListModel</tt> that read list item from the specified
     * file.
     * Each element of the list is a line of the file.
     * @param file
     * @throws java.io.FileNotFoundException
     */
    public LazyFileListModel(File file) throws FileNotFoundException {
        this.lineCounterFile = new RandomAccessFile(file, "r");
        this.readerFile = new RandomAccessFile(file, "r");

        try {
            String line;
            while ((line = lineCounterFile.readLine()) != null) {
                linePositions.add(lineCounterFile.getFilePointer());
            }
            linePositions.remove(linePositions.size() - 1);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "", ex);
        }


        Runnable firer = new Runnable() {

            @Override
            public void run() {
                int current = linePositions.size() - 1;
                if (last < current) {
                    fireIntervalAdded(LazyFileListModel.this, last + 1, current);
                    last = current;
                }
            }
        };

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(firer, 2, 500,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public int getSize() {
        return linePositions.size();
    }

    @Override
    public Object getElementAt(int index) {
        try {
            readerFile.seek(linePositions.get(index));
            return String.format("%s", readerFile.readLine());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "", ex);
            return null;
        }
    }
}
