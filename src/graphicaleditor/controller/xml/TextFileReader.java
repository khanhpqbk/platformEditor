/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextFileReader {

    @SuppressWarnings("NestedAssignment")
    public List<String> read(File file) {
        List<String> lines = new ArrayList<String>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(TextFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lines;
    }
}
