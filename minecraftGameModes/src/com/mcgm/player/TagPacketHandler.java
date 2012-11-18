/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.player;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import com.mcgm.MCPartyCore;
import com.mcgm.game.event.ReceiveNameTagEvent;
import java.util.logging.Logger;
import org.bukkit.craftbukkit.libs.jline.internal.Log.Level;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author Thomas
 */
public class TagPacketHandler extends PacketAdapter {

    private PluginManager pluginManager;

    /**
     * Construct a packet handler with the given parent plugin.
     *
     * @param plugin - parent plugin.
     */
    public TagPacketHandler(MCPartyCore plugin, PluginManager pluginManager) {
        super(plugin, ConnectionSide.SERVER_SIDE, Packets.Server.NAMED_ENTITY_SPAWN);
        this.pluginManager = pluginManager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {

        if (!event.isCancelled() && event.getPacketID() == Packets.Server.NAMED_ENTITY_SPAWN) {

            PacketContainer packet = event.getPacket();
            StructureModifier<String> text = packet.getSpecificModifier(String.class);

            try {
                String tag = text.read(0);
                Player observer = event.getPlayer();
                Entity watched = packet.getEntityModifier(observer.getWorld()).read(0);

                if (watched instanceof Player) {
                    ReceiveNameTagEvent nameTagEvent = new ReceiveNameTagEvent(event.getPlayer(), (Player) watched, tag);
                    pluginManager.callEvent(nameTagEvent);

                    if (nameTagEvent.isModified()) {
                        // Trim excess
                        tag = nameTagEvent.getTrimmedTag();

                        // Uh, ok.
                        if (tag == null) {
                            tag = "";
                        }
                        text.write(0, tag);
                    }

                }
            } catch (FieldAccessException e) {
            }
        }
    }
}
