package com.modcrafting.diablodrops;

import com.modcrafting.diablodrops.items.SocketGem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

public final class MythicSockettingSettings implements SockettingSettings {

    private String socketGemName;
    private List<String> socketGemLore;
    private String sockettedItemString;
    private List<String> sockettedItemLore;
    private boolean useAttackerItemInHand;
    private boolean useAttackerArmorEquipped;
    private boolean useDefenderItemInHand;
    private boolean useDefenderArmorEquipped;
    private List<Material> socketGemMaterialDatas;
    private Map<String, SocketGem> socketGemMap;
    private List<String> socketGemPrefixes;
    private boolean preventMultipleChangesFromSockets;
    private List<String> socketGemSuffixes;
    private boolean canDropSocketGemsOnItems;

    public MythicSockettingSettings() {
        socketGemLore = new ArrayList<>();
        sockettedItemLore = new ArrayList<>();
        socketGemMaterialDatas = new ArrayList<>();
        socketGemMap = new HashMap<>();
        socketGemPrefixes = new ArrayList<>();
        socketGemSuffixes = new ArrayList<>();
    }

    @Override
    @Deprecated
    public boolean isEnabled() {
        return true;
    }

    @Deprecated
    public void setEnabled(boolean enabled) {
        // do nothing
    }

    @Override
    public String getSocketGemName() {
        return socketGemName;
    }

    public void setSocketGemName(String socketGemName) {
        this.socketGemName = socketGemName;
    }

    @Override
    public List<String> getSocketGemLore() {
        return socketGemLore;
    }

    public void setSocketGemLore(List<String> socketGemLore) {
        this.socketGemLore = socketGemLore;
    }

    @Override
    public String getSockettedItemString() {
        return sockettedItemString;
    }

    public void setSockettedItemString(String sockettedItemString) {
        this.sockettedItemString = sockettedItemString;
    }

    @Override
    public List<String> getSockettedItemLore() {
        return sockettedItemLore;
    }

    public void setSockettedItemLore(List<String> sockettedItemLore) {
        this.sockettedItemLore = sockettedItemLore;
    }

    @Override
    public boolean isUseAttackerItemInHand() {
        return useAttackerItemInHand;
    }

    public void setUseAttackerItemInHand(boolean useAttackerItemInHand) {
        this.useAttackerItemInHand = useAttackerItemInHand;
    }

    @Override
    public boolean isUseAttackerArmorEquipped() {
        return useAttackerArmorEquipped;
    }

    public void setUseAttackerArmorEquipped(boolean useAttackerArmorEquipped) {
        this.useAttackerArmorEquipped = useAttackerArmorEquipped;
    }

    @Override
    public boolean isUseDefenderItemInHand() {
        return useDefenderItemInHand;
    }

    public void setUseDefenderItemInHand(boolean useDefenderItemInHand) {
        this.useDefenderItemInHand = useDefenderItemInHand;
    }

    @Override
    public boolean isUseDefenderArmorEquipped() {
        return useDefenderArmorEquipped;
    }

    public void setUseDefenderArmorEquipped(boolean useDefenderArmorEquipped) {
        this.useDefenderArmorEquipped = useDefenderArmorEquipped;
    }

    @Override
    public boolean isPreventMultipleChangesFromSockets() {
        return preventMultipleChangesFromSockets;
    }

    @Override
    public List<Material> getSocketGemMaterials() {
        return socketGemMaterialDatas;
    }

    public void setSocketGemMaterials(List<String> socketGemMaterialDatas) {
    	List<Material> socketGemMaterials = new ArrayList<Material>();
    	for (String MaterialName : socketGemMaterialDatas) {
    		socketGemMaterials.add(Material.getMaterial(MaterialName));
    	}
        this.socketGemMaterialDatas = socketGemMaterials;
    }

    @Override
    public Map<String, SocketGem> getSocketGemMap() {
        return socketGemMap;
    }

    public void setSocketGemMap(Map<String, SocketGem> socketGemMap) {
        this.socketGemMap = socketGemMap;
    }

    @Override
    public List<String> getSocketGemPrefixes() {
        return socketGemPrefixes;
    }

    @Override
    public List<String> getSocketGemSuffixes() {
        return socketGemSuffixes;
    }

    @Override
    public boolean isCanDropSocketGemsOnItems() {
        return canDropSocketGemsOnItems;
    }

    public void setCanDropSocketGemsOnItems(boolean canDropSocketGemsOnItems) {
        this.canDropSocketGemsOnItems = canDropSocketGemsOnItems;
    }

    public void setPreventMultipleChangesFromSockets(boolean preventMultipleChangesFromSockets) {
        this.preventMultipleChangesFromSockets = preventMultipleChangesFromSockets;
    }
}
