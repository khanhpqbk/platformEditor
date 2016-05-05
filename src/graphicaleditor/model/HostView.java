/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicaleditor.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author KHANH
 */
public class HostView extends ImageView {
    private Image image;
    private String id;
    private int power;
    private String state;
    private double pstate;
    private double availability;
    private int core;
    private int coordinates;
    
    private BooleanProperty selected = new SimpleBooleanProperty();
    

    public HostView(Image image, String id, int power, String state) {
        super(image);
        this.image = image;
        this.id = id;
        this.power = power;
        this.state = state;
        
        setOnChanged();
    }
    
    private void setOnChanged() {
        selected.addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                
            }
        });
    }

    public HostView(Image image, String id, int power, String state, double pstate, double availability, int core, int coordinates) {
        super(image);
//        this.image = image;
        this.id = id;
        this.power = power;
        this.state = state;
        this.pstate = pstate;
        this.availability = availability;
        this.core = core;
        this.coordinates = coordinates;
        setOnChanged();
    }
    
    

    public HostView(Image image) {
        super(image);
        this.image = image;
    }
    
    

    public Image getmImage() {
        return image;
    }

    public void setmImage(Image image) {
        this.image = image;
    }

    public String getmId() {
        return id;
    }

    public void setmId(String id) {
        this.id = id;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getPstate() {
        return pstate;
    }

    public void setPstate(double pstate) {
        this.pstate = pstate;
    }

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }

    public int getCore() {
        return core;
    }

    public void setCore(int core) {
        this.core = core;
    }

    public int getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "HostView{" + "image=" + image + ", id=" + id + ", power=" + power + ", state=" + state + ", pstate=" + pstate + ", availability=" + availability + ", core=" + core + ", coordinates=" + coordinates + '}';
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean checked) {
        this.selected.set(checked);
    }
    
    public BooleanProperty booleanProperty() {
        return selected;
    }
    
}
