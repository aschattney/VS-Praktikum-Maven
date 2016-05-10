package de.hochschuledarmstadt.transport.processor;

import de.hochschuledarmstadt.component.IMessageProcessor;
import de.hochschuledarmstadt.component.IMessageReader;
import de.hochschuledarmstadt.component.OnConnectionResetListener;
import de.hochschuledarmstadt.transport.model.TransportMessage;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class TransportProcessor implements Runnable {

    private static final Object LOCK  = new Object();

    private final IMessageReader messageReader;
    private IMessageProcessor messageProcessor;
    private OnConnectionResetListener connectionResetListener;
    private boolean isRunning = true;

    public TransportProcessor(IMessageReader messageReader, IMessageProcessor messageProcessor, OnConnectionResetListener connectionResetListener){
        this.messageReader = messageReader;
        this.messageProcessor = messageProcessor;
        this.connectionResetListener = connectionResetListener;
    }

    public TransportProcessor(IMessageReader messageReader, IMessageProcessor messageProcessor){
        this(messageReader, messageProcessor, null);
    }

    public void run() {
        while(isRunning) {
            try {
                TransportMessage message = messageReader.read();
                synchronized (LOCK) {
                    messageProcessor.processMessage(message);
                }
            }catch(SocketTimeoutException e){

            } catch (IOException e) {
                System.out.println("Connection lost");
                isRunning = false;
                if (connectionResetListener != null)
                    connectionResetListener.onConnectionReset();
            }
        }
    }
}
