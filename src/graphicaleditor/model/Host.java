/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicaleditor.model;

import javafx.scene.image.Image;

/**
 *
 * @author KHANH
 */
public class Host {
    private Image image;
    private int id;
    private int power;
    private boolean state;

    public Host(Image image, int id, int power, boolean state) {
        this.image = image;
        this.id = id;
        this.power = power;
        this.state = state;
    }

    public Host(Image image) {
        this.image = image;
    }
    
    

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
    
    
}
