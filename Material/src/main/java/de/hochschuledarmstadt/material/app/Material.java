package de.hochschuledarmstadt.material.app;

import de.hochschuledarmstadt.model.RequiredMaterial;

public class Material {

    public static final String COLOR_RED = "red";
    public static final String COLOR_BLUE = "blue";
    public static final String COLOR_YELLOW = "yellow";

    private int amountRed;
    private int amountBlue;
    private int amountYellow;

    private int maximumRed;
    private int maximumBlue;
    private int maximumYellow;

    private static final Object LOCK_OBJ = new Object();

    public Material(int fillLevelForEachColor){

        this.maximumRed = fillLevelForEachColor;
        this.maximumBlue = fillLevelForEachColor;
        this.maximumYellow = fillLevelForEachColor;

        this.amountRed = fillLevelForEachColor;
        this.amountBlue = fillLevelForEachColor;
        this.amountYellow = fillLevelForEachColor;

    }

    public void use(String color){
        synchronized (LOCK_OBJ) {
            boolean found = false;
            if (color.equals(COLOR_RED)) {
                amountRed--;
                found = true;
            } else if (color.equals(COLOR_BLUE)) {
                amountBlue--;
                found = true;
            } else if (color.equals(COLOR_YELLOW)) {
                amountYellow--;
                found = true;
            }
            if (!found)
                throw new IllegalArgumentException(String.format("unknown color: %s"));
        }
    }

    public boolean isEnoughColorAvailable(RequiredMaterial requiredMaterial){
        synchronized (LOCK_OBJ) {
            if (amountRed - requiredMaterial.getRed() < 0)
                return false;
            if (amountBlue - requiredMaterial.getBlue() < 0)
                return false;
            if (amountYellow - requiredMaterial.getYellow() < 0)
                return false;
            return true;
        }
    }

    public void refill() {
        synchronized (LOCK_OBJ) {
            amountRed = maximumRed;
            amountYellow = maximumYellow;
            amountBlue = maximumBlue;
        }
    }

    public void empty(){
        synchronized (LOCK_OBJ) {
            amountRed = 0;
            amountYellow = 0;
            amountBlue = 0;
        }
    }

    public RequiredMaterial getColorFillLevel() {
        synchronized (LOCK_OBJ) {
            return new RequiredMaterial(amountRed, amountBlue, amountYellow);
        }
    }
}
