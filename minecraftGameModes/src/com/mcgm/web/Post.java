/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.web;

import com.mcgm.utils.Misc;
import com.mcgm.utils.WebUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.misc.utils.ClientHttpRequest;
import org.json.simple.JSONObject;

/**
 *
 * @author Thomas
 */
public abstract class Post {

    public URL url;
    public JSONObject item;

    public abstract void serverResponse(String response);

    public Post(String url, Object key, Object value) {
        try {
            this.url = new URL(url);
            this.item = new JSONObject();
            this.item.put(key, value);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Post.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void request() {
        try {
            serverResponse(WebUtils.convertStreamToString(new ClientHttpRequest(url).post("data", item)));
        } catch (IOException ex) {
            Logger.getLogger(Post.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
