package graphicaleditor.connection;

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
import graphicaleditor.connection.SessionStatus;
import graphicaleditor.connection.SimulationSystemService;
import graphicaleditor.controller.fileprocessors.CoDecomFileProcessor;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
//import sun.misc.IOUtils;

public class JavaClient {
    TTransport transport;
    SimulationSystemService.Client client;
    String dir;
    
    public JavaClient(String dir) {
        try {
            this.dir = dir;
            transport = new TSocket("127.0.0.1", 26102);
//            transport = new TSocket("58.187.134.226", 7624);
//            transport = new TSocket("192.168.1.141", 26102);
            transport.open();
            
            TProtocol protocol = new TBinaryProtocol(transport);
            client = new SimulationSystemService.Client(protocol);
        } catch (TTransportException ex) {
            Logger.getLogger(JavaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SessionStatus simulate() {
        SessionStatus sessionStatus = null;
        try {       
            new CoDecomFileProcessor().zipDirSimgrid(dir, "/tmp/sessionZip.zip");
            RandomAccessFile aFile = new RandomAccessFile(
                    "/tmp/sessionZip.zip", "r");
            FileChannel inChannel = aFile.getChannel();
            long fileSize = inChannel.size();
            ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
            inChannel.read(buffer);
            //buffer.rewind();
            buffer.flip();
            inChannel.close();
            aFile.close();
            client.ping();
            sessionStatus = client.simulate(buffer);
            System.out.println("" + sessionStatus.status.toString());

        } catch (TException x) {
            x.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(JavaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return sessionStatus;
    }
    
    public Result getResult(String sessionId) {
        if (client != null) {
            try {
                return client.getResultFile(sessionId);
            } catch (TException ex) {
                Logger.getLogger(graphicaleditor.connection.JavaClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    public StatusCode getStatusCode(String sessionId) {
        if (client != null) {
            try {
                SessionStatus stt =
                        client.getSessionStatus(sessionId);
                System.out.println(stt.output);
                return stt.status;
            } catch (TException ex) {
                Logger.getLogger(JavaClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    

}
