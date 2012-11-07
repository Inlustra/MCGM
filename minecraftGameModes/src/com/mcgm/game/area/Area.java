/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.area;

import com.mcgm.Plugin;
import com.mcgm.utils.Misc;
import com.sk89q.worldedit.Vector;
import org.bukkit.Location;

/**
 *
 * @author Tom
 */
public class Area {

    private Vector start;
    private Vector end;
    private Location center;
    private Location centerAboveGround;

    public Area(Plugin p, Vector start, Vector end) {
        this.start = start;
        this.end = end;
        double centerX = (start.getX() + ((start.getX() - end.getX()) / 2));
        double centerY = (start.getY() + ((start.getY() - end.getY()) / 2));
        double centerZ = (start.getZ() + ((start.getZ() - end.getZ()) / 2));

        center = new Location(Misc.mainWorld(), centerX, centerY, centerZ);
        for(;;) {
            break;
        }
        
    }

    public Location getCenterAboveGround() {
        return centerAboveGround;
    }

    public Location getCenter() {
        return center;
    }

}
