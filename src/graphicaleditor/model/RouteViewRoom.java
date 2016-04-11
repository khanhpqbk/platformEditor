/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicaleditor.model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Polyline;

/**
 *
 * @author KHANH
 */
public class RouteViewRoom extends Polyline {
    private String src;
    private String des;
    private List<LinkView> links = new ArrayList<>();


    public RouteViewRoom(String src, String des, List<LinkView> links) {
        this.src = src;
        this.des = des;
        this.links = links;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public List<LinkView> getLinks() {
        return links;
    }

    public void setLinks(List<LinkView> links) {
        this.links = links;
    }

    
    

    @Override
    public String toString() {
        return "RouteViewRoom{" + "src=" + src + ", des=" + des + ", links=" + links + '}';
    }
}
