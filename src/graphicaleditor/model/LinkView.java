/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicaleditor.model;

import javafx.scene.image.ImageView;

/**
 *
 * @author KHANH
 */
public class LinkView extends ImageView {
    private String id;
    private int bandwidth;
    private double latency;
    private String state;
    private String sharingPolicy;

    public LinkView(String id, int bandwidth, double latency, String state, String sharingPolicy) {
        this.id = id;
        this.bandwidth = bandwidth;
        this.latency = latency;
        this.state = state;
        this.sharingPolicy = sharingPolicy;
    }

    public String getmId() {
        return id;
    }

    public void setmId(String id) {
        this.id = id;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSharingPolicy() {
        return sharingPolicy;
    }

    public void setSharingPolicy(String sharingPolicy) {
        this.sharingPolicy = sharingPolicy;
    }

    @Override
    public String toString() {
        return "LinkView{" + "id=" + id + ", bandwidth=" + bandwidth + ", latency=" + latency + ", state=" + state + ", sharingPolicy=" + sharingPolicy + '}';
    }
    
    
}
