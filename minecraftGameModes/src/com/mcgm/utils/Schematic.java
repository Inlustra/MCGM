package com.mcgm.utils;

import com.mcgm.utils.Paths;
import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Max
 */
public class Schematic {

    private short[] blocks;
    private byte[] data;
    private short width;
    private short length;
    private short height;

    public Schematic(File f) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(f);
            NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(stream));
            CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
            if (!schematicTag.getName().equals("Schematic")) {
                throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
            }
            Map<String, Tag> schematic = schematicTag.getValue();
            if (!schematic.containsKey("Blocks")) {
                throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
            }
            width = getChildTag(schematic, "Width", ShortTag.class).getValue();
            length = getChildTag(schematic, "Length", ShortTag.class).getValue();
            height = getChildTag(schematic, "Height", ShortTag.class).getValue();
            String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
            if (!materials.equals("Alpha")) {
                throw new IllegalArgumentException("Schematic file is not an Alpha schematic");
            }
            byte[] rawBlocks = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
            data = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
            blocks = new short[rawBlocks.length];

            if (schematic.containsKey("AddBlocks")) {
                byte[] addBlockIds = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
                for (int i = 0, index = 0; i < addBlockIds.length && index < blocks.length; ++i) {
                    blocks[index] = (short) (((addBlockIds[i] >> 4) << 8) + (rawBlocks[index++] & 0xFF));
                    if (index < blocks.length) {
                        blocks[index] = (short) (((addBlockIds[i] & 0xF) << 8) + (rawBlocks[index++] & 0xFF));
                    }
                }
            } else {
                for (int i = 0; i < rawBlocks.length; ++i) {
                    blocks[i] = (short) (rawBlocks[i] & 0xFF);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Schematic.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                Logger.getLogger(Schematic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Schematic(String s) {
        this(new File(Paths.schematicDir + "/" + s + ".schematic"));
    }

    public Schematic(short[] blocks, byte[] data, short width, short lenght, short height) {
        this.blocks = blocks;
        this.data = data;
        this.width = width;
        this.length = lenght;
        this.height = height;
    }

    /**
     * @return the blocks
     */
    public short[] getBlocks() {
        return blocks;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @return the width
     */
    public short getWidth() {
        return width;
    }

    /**
     * @return the lenght
     */
    public short getLenght() {
        return length;
    }

    /**
     * @return the height
     */
    public short getHeight() {
        return height;
    }

    public void pasteSchematic(Location loc) {

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    Block block = new Location(loc.getWorld(), x + loc.getX(), y + loc.getY(), z + loc.getZ()).getBlock();
                    if (blocks[index] > -1) {
                        block.setTypeIdAndData(blocks[index], data[index], true);
                    }
                }
            }
        }
    }

    public HashMap<Material, ArrayList<Location>> pasteSchematic(Location loc, boolean setAir, Material... typeList) {
        HashMap<Material, ArrayList<Location>> map = new HashMap<>();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    Block block = new Location(loc.getWorld(), x + loc.getX(), y + loc.getY(), z + loc.getZ()).getBlock();
                    boolean addBlock = true;
                    for (Material m : typeList) {
                        int mid = m.getId();
                        if (mid == blocks[index]) {
                            addBlock = setAir ? false : true;
                            ArrayList<Location> locs = map.get(m);
                            if (locs == null) {
                                locs = new ArrayList<>();
                                map.put(m, locs);
                            }
                            locs.add(block.getLocation());
                        }
                    }
                    if (addBlock) {
                        WorldUtils.setTypeAndIdWithoutUpdating(block.getWorld(), (int) (x + loc.getX()), (int) (y + loc.getY()), (int) (z + loc.getZ()), blocks[index], data[index]);
                    } else {
                        WorldUtils.setTypeAndIdWithoutUpdating(block.getWorld(), (int) (x + loc.getX()), (int) (y + loc.getY()), (int) (z + loc.getZ()), Material.AIR.getId(), data[index]);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Get child tag of a NBT structure.
     *
     * @param items The parent tag map
     * @param key The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws DataException if the tag does not exist or the tag is not of the
     * expected type
     */
    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws IllegalArgumentException {
        if (!items.containsKey(key)) {
            throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
}
