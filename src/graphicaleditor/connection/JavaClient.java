/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// Generated code
import graphicaleditor.connection.SimulationSystemService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
//import sun.misc.IOUtils;

public class JavaClient {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        try {
            TTransport transport;

            transport = new TSocket("192.168.1.53", 1234);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            SimulationSystemService.Client client = new SimulationSystemService.Client(protocol);
//            RandomAccessFile aFile = new RandomAccessFile(
//                    "C:/zipfile.zip", "r");
//            FileChannel inChannel = aFile.getChannel();
//            long fileSize = inChannel.size();
//            ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
//            inChannel.read(buffer);
//            //buffer.rewind();
//            buffer.flip();
//            for (int i = 0; i < fileSize; i++) {
//                System.out.print((char) buffer.get());
//            }
//            inChannel.close();
//            aFile.close();
//            client.simulate(buffer);
            
            client.ping();

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }

    /**
     * Zip the contents of the directory, and save it in the zipfile
     */
    private static void zipDirectory(String dir, String zipfile)
            throws IOException, IllegalArgumentException {
        // Check that the directory is a directory, and get its contents
        File d = new File(dir);
        if (!d.isDirectory()) {
            throw new IllegalArgumentException("Not a directory:  "
                    + dir);
        }
        String[] entries = d.list();
        byte[] buffer = new byte[4096]; // Create a buffer for copying
        int bytesRead;

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));

        for (int i = 0; i < entries.length; i++) {
            File f = new File(d, entries[i]);
            if (f.isDirectory()) {
                continue;//Ignore directory
            }
            FileInputStream in = new FileInputStream(f); // Stream to read file
            ZipEntry entry = new ZipEntry(f.getPath()); // Make a ZipEntry
            out.putNextEntry(entry); // Store entry
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            in.close();
        }
        out.close();
    }

//    public static void main(String[] args) throws IOException {
//        zipDirectory("C:/ziptest", "C:/zipfile");
//        
//    }
//  private static void perform(Calculator.Client client) throws TException
//  {
//    client.ping();
//    System.out.println("ping()");
//
//    int sum = client.add(1,1);
//    System.out.println("1+1=" + sum);
//
//    Work work = new Work();
//
//    work.op = Operation.DIVIDE;
//    work.num1 = 1;
//    work.num2 = 0;
//    try {
//      int quotient = client.calculate(1, work);
//      System.out.println("Whoa we can divide by 0");
//    } catch (InvalidOperation io) {
//      System.out.println("Invalid operation: " + io.why);
//    }
//
//    work.op = Operation.SUBTRACT;
//    work.num1 = 15;
//    work.num2 = 10;
//    try {
//      int diff = client.calculate(1, work);
//      System.out.println("15-10=" + diff);
//    } catch (InvalidOperation io) {
//      System.out.println("Invalid operation: " + io.why);
//    }
//
//    SharedStruct log = client.getStruct(1);
//    System.out.println("Check log: " + log.value);
//  }
}
