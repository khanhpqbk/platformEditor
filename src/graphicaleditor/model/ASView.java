/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicaleditor.model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author KHANH
 */
public class ASView extends ImageView {
    List<HostView> hostList = new ArrayList<>();
    List<LinkView> linkList = new ArrayList<>();
    List<RouteView> routeList = new ArrayList<>();
    List<RouterView> routerList = new ArrayList<>();
    List<RouteViewRoom> routeRoomList = new ArrayList<>();
    List<ClusterView> clusterList = new ArrayList<>();
    String id;
    Image image;

    public ASView(Image image, String id) {
        super(image);
        this.image = image;
        this.id = id;
    }
    
    

    public String getmId() {
        return id;
    }

    public void setmId(String id) {
        this.id = id;
    }
    
    

    public List<HostView> getHostList() {
        return hostList;
    }

    public void setHostList(List<HostView> hostList) {
        this.hostList = hostList;
    }

    public List<LinkView> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<LinkView> linkList) {
        this.linkList = linkList;
    }

    public List<RouteView> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<RouteView> routeList) {
        this.routeList = routeList;
    }

    public List<RouteViewRoom> getRouteRoomList() {
        return routeRoomList;
    }

    public void setRouteRoomList(List<RouteViewRoom> routeRoomList) {
        this.routeRoomList = routeRoomList;
    }

    public List<RouterView> getRouterList() {
        return routerList;
    }

    public void setRouterList(List<RouterView> routerList) {
        this.routerList = routerList;
    }

    public List<ClusterView> getClusterList() {
        return clusterList;
    }

    public void setClusterList(List<ClusterView> clusterList) {
        this.clusterList = clusterList;
    }
    
    

}
