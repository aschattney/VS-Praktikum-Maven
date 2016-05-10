package de.hochschuledarmstadt.transport.tcp;

import java.io.OutputStream;

public class TcpMessageSenderFactory {

    public TcpMessageSenderFactory(){ }

    public TcpMessageSender create(OutputStream outputStream){
        return new TcpMessageSender(outputStream);
    }

}
