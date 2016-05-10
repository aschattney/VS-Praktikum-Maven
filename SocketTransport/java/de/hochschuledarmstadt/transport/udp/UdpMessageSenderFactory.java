package de.hochschuledarmstadt.transport.udp;

import de.hochschuledarmstadt.component.IMessageSender;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpMessageSenderFactory {

    private DatagramSocket socket;

    public UdpMessageSenderFactory(DatagramSocket socket){
        this.socket = socket;
    }

    public IMessageSender create(DatagramPacket packet){
        return new UdpMessageSender(socket, packet);
    }

}
