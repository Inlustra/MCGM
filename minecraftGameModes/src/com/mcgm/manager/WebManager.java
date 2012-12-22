/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.manager;

import com.mcgm.MCPartyCore;
import com.mcgm.config.MCPartyConfig;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Thomas
 */
public class WebManager implements Listener {

    MCPartyCore plugin;
    final SocketChannel socketChannel;
    Charset charset = Charset.defaultCharset();
    CharsetDecoder decoder = charset.newDecoder();
    JSONParser parser = new JSONParser();
    ByteBuffer buff = ByteBuffer.allocateDirect(1024);

    public WebManager(MCPartyCore mcPartyCore) {
        this.plugin = mcPartyCore;
        SocketChannel tempSock = null;
        try {
            tempSock = SocketChannel.open();
        } catch (IOException ex) {
        }
        socketChannel = tempSock;

        serverReconnect.start();
        mcPartyCore.getServer().getPluginManager().registerEvents(this, mcPartyCore);
        Command shutdownServer = new Command("shutdownweb", "Shut down the web server", "WEBSTOP", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                plugin.getWebManager().sendData("shutdown", "pt is a twat");
                return true;
            }
        };
        plugin.getCommandManager().addCommand(shutdownServer);
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        JSONObject playerMessage = new JSONObject();
        playerMessage.put("username", event.getPlayer().getName());
        playerMessage.put("message", event.getMessage());
        sendData("chat", playerMessage);
    }

    public void sendData(String type, Object data) {
        if (socketChannel.isConnected()) {
            JSONObject p = new JSONObject();
            p.put("type", type);
            p.put("data", data);
            try {
                if (MCPartyConfig.getBoolean("Development.OutputWebSocket")) {
                    System.out.println(p.toJSONString());
                }
                socketChannel.write(ByteBuffer.wrap((p.toJSONString() + "\n").getBytes()));
            } catch (IOException ex) {
                Logger.getLogger(WebManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Cannot send data: (" + data + ") due to not connected");
        }
    }

    public void handleRecieve(JSONObject jdata) {
        switch ((String) jdata.get("type")) {
            case "player":
                plugin.getPlayerManager().login((JSONObject) jdata.get("data"));
                break;
            case "playerfirst":
                plugin.getPlayerManager().loginFirst((JSONObject) jdata.get("data"));
                break;
            case "badlogin":
                Player p = plugin.getServer().
                        getPlayer((String) jdata.get("data"));
                p.sendMessage(MCPartyConfig.parse("badlogin", p.getName()));
                break;
            case "playerupdate":
                plugin.getPlayerManager().updatePlayer((JSONObject) jdata.get("data"));
                break;
        }
    }
    InetSocketAddress isa = new InetSocketAddress("www.mcparty.co", 19283);
    Thread serverReconnect = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (!socketChannel.connect(isa)) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WebManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        System.out.println("Attempting reconnect");
                        socketChannel.configureBlocking(false);
                    } catch (IOException ex) {
                        System.out.println("Failed to connect to: (" + isa + ")");
                    }
                }
                System.out.println("Connected to: " + isa);
                serverRecieve.start();
            } catch (IOException ex) {
                Logger.getLogger(WebManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    });
    Thread serverRecieve = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (socketChannel.isConnected()) {
                    while (socketChannel.read(buff) != 0) {
                        buff.flip();
                        String line = decoder.decode(buff).toString();
                        try {
                            line = parser.parse(line).toString();
                            handleRecieve((JSONObject) parser.parse(line));
                        } catch (Exception e) {
                        }
                        if (MCPartyConfig.getBoolean("Development.OutputWebSocket")) {
                            System.out.println(line);
                        }
                        buff.clear();
                    }
                    Thread.sleep(500);
                }
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(WebManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            serverReconnect.start();
        }
    });
}
