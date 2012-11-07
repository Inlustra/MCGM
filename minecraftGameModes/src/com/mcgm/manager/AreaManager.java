/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.manager;

import com.mcgm.Plugin;
import com.mcgm.game.area.Area;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tom
 */
public class AreaManager {
    
    private List<Area> areaList;
    private static AreaManager instance;
    private Plugin p;

    public AreaManager(Plugin p) {
        areaList = new ArrayList<>();
        this.p = p;
    }
    
    public static AreaManager getInstance(Plugin p) {
        synchronized (AreaManager.class) {
            if (instance == null) {
                instance = new AreaManager(p);
            }
        }
        return instance;
    }
}
