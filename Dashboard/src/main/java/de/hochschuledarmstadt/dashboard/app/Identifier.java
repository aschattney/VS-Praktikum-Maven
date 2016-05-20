package de.hochschuledarmstadt.dashboard.app;

public class Identifier {

    private final int fabricId;
    private final int printerId;

    public Identifier(int fabricId, int printerId){
        this.fabricId = fabricId;
        this.printerId = printerId;
    }

    public int getFabricId() {
        return fabricId;
    }

    public int getPrinterId() {
        return printerId;
    }
}
