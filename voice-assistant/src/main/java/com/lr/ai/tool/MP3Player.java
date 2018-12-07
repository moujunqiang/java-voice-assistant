package com.lr.ai.tool;

import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * Created by ran on 2018/11/15.
 */
public class MP3Player {
    private String filename;
    private Player player;

    public MP3Player(String filename) {
        this.filename = filename;
    }

    public void play() {
        try {
            BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(filename));
            player = new Player(buffer);
            player.play();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}


