/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicaleditor.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author KHANH
 */
public class RouterView extends ImageView {
    private String id;
    
    public RouterView(Image img, String id) {
        super(img);
        this.id = id;
    }
    
    public String getmId() {
        return id;
    }
    
    public void setmId(String id) {
        this.id = id;
    }
}
