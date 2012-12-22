/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.provider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Tom
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GameInfo {

    String name();

    String[] aliases() default {};

    String description();

    String[] authors();

    double version() default 0.1;

    int maxPlayers() default 100;

    int teamAmount() default -1;

    boolean pvp() default false;

    int gameTime() default 300;

    int credits() default 50;

    String seed() default "";

    boolean customMapGeneration() default false;

    boolean playable() default true;

    boolean blocksBreakable() default true;

    boolean blocksPlaceable() default true;
    
    boolean infiniteFood() default false;
}
