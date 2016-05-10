package de.hochschuledarmstadt.transport.tcp;

import java.io.InputStream;
import java.io.OutputStream;

public class TcpMessageReaderFactory {

    public TcpMessageReaderFactory(){

    }

    public TcpMessageReader create(InputStream inputStream){
        return new TcpMessageReader(inputStream);
    }

}
