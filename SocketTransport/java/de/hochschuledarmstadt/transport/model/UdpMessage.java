package de.hochschuledarmstadt.transport.model;

import org.json.JSONObject;

import java.net.DatagramPacket;

public class UdpMessage extends TransportMessage{

    private DatagramPacket datagramPacket;

    public UdpMessage(JSONObject jsonMessage, DatagramPacket datagramPacket) {
        super(jsonMessage);
        this.datagramPacket = datagramPacket;
    }

    public DatagramPacket getDatagramPacket(){
        return datagramPacket;
    }

}
