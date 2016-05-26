/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller.fileprocessors;

import graphicaleditor.model.benchmark.Benchmark;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author khanh
 */
public class BMResultProcessor {
    public static void main(String[] args) {
       
            List<Benchmark> bms = new BMResultProcessor().parseResult("/home/khanh/unzipfolder");
            for(Benchmark bm: bms) {
                System.out.println("type = " + bm.getType());
                System.out.println("info = " + bm.getMoreInfo());
                System.out.println("result" + bm.getResult());
            }
    }
    
    public List<Benchmark> parseResult(String dir) {
        boolean enterBM = false;
        boolean enterBMImportant = false;
        File f = new File(dir, "result.log");
        List<Benchmark> bms = new ArrayList<>();
        Benchmark e = null;
        String result = null;
        int count = 0;
        List<String> lines = new TextFileProcessor().read(f);
//        for(int i = 0; i < lines.size(); i++) {
//            System.out.println(lines.get(i));
//        }
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
//            System.out.println(line);
            if (line.contains("BENCHMARK")) {
                count = 0;
                enterBM = true;
                enterBMImportant = false;
                e = new Benchmark();
                result = "";
                StringTokenizer st = new StringTokenizer(line, ":");
                String type = st.nextToken();
                String moreInfo = st.nextToken();
//                st.nextToken();
                if(!st.hasMoreElements()) {
                    moreInfo = "";
                }
                e.setType(type);
                e.setMoreInfo(moreInfo);
                
                if(line.contains("FAILED")) {
                    result = "Benchmark failed";
                }
            } 
            
            if (enterBM) {
                
                if (line.contains("=====")) {
                    count++;
                } 
                
                if (!enterBMImportant && line.contains("****")) {
                    enterBMImportant = true;
                    
                } else if (enterBMImportant && line.contains("****")) {
                    enterBMImportant = false;
//                    e.setResult(result);
                }
                
                if (enterBMImportant) {
                    if(!line.contains("****"))
                        result += '\n' + line;
                }
                
                
                if(count >=2 || (line.contains("====") && i == lines.size() - 1)) {
                    count = 0;
                    enterBM = false;
                    e.setResult(result);
                    bms.add(e);
                    e = null;
                    
                }
            }

        }
        
        return bms;
    }
}
