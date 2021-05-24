/*
 * jaoLicense
 *
 * Copyright (c) 2021 jao Minecraft Server
 *
 * The following license applies to this project: jaoLicense
 *
 * Japanese: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE.md
 * English: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE-en.md
 */

package com.jaoafa.mymaid4.event;

import com.jaoafa.mymaid4.Main;
import com.jaoafa.mymaid4.lib.EventPremise;
import com.jaoafa.mymaid4.lib.MyMaidData;
import com.jaoafa.mymaid4.lib.MyMaidLibrary;
import com.jaoafa.mymaid4.lib.NMSManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Event_AntiToolbar extends MyMaidLibrary implements Listener, EventPremise {
    @Override
    public String description() {
        return "ツールバーの利用を制限します。";
    }

    Pattern damagePattern = Pattern.compile("\\{Damage:[0-9]+}");

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (isAMR(player)) {
            return;
        }

        if (MyMaidData.getCreativeInventoryWithNBTs().isEmpty()) {
            return;
        }

        boolean isDeny = false;
        ItemStack is = null;
        if (event.getCurrentItem() != null) {
            isDeny = isDenyItemStack(event.getCurrentItem());
            if (isDeny) is = event.getCurrentItem();
        }
        if (!isDeny) {
            isDeny = isDenyItemStack(event.getCursor());
            if (isDeny) is = event.getCursor();
        }

        if (isExistsInventory(player.getInventory(), is)) {
            return;
        }

        if (!isDeny) {
            return;
        }

        event.setCurrentItem(null);
        player.sendMessage(Component.text().append(
            Component.text("[AntiToolbar] "),
            Component.text("ツールバーからの取得と思われるアイテムが見つかったため、規制しました。この事象は報告されます。", NamedTextColor.RED)
        ));
        try {
            saveToolbarItem(player, is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.setCancelled(true);
    }

    boolean isDenyItemStack(ItemStack is) {
        Material material = is.getType();
        String nbt = NMSManager.getNBT(is);
        if (nbt == null) {
            return false;
        }

        Map<Material, List<String>> creativeInventoryWithNBTs = MyMaidData.getCreativeInventoryWithNBTs();
        if (!creativeInventoryWithNBTs.containsKey(material)) {
            if (damagePattern.matcher(nbt).matches()) {
                return false; // ダメージ値のみの場合
            }
            return !nbt.equals("{}");
        }
        List<String> registeredNBT = creativeInventoryWithNBTs.get(material);
        if (damagePattern.matcher(nbt).matches()) return false;
        return !registeredNBT.contains(nbt);
    }

    void saveToolbarItem(Player player, ItemStack is) throws IOException {
        String nbt = NMSManager.getNBT(is);
        Path path = Paths.get(Main.getJavaPlugin().getDataFolder().getAbsolutePath(), "toolbar-items.tsv");
        List<String> lines = Files.exists(path) ? Files.readAllLines(path) : new ArrayList<>();
        String messageNonTime = player.getName() + "\t" + is.getType().name() + "\t" + nbt;
        if (lines.stream().anyMatch(s -> s.startsWith(messageNonTime))) {
            return;
        }
        lines.add(messageNonTime + "\t" + sdfFormat(new Date()));
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    boolean isExistsInventory(Inventory inv, ItemStack is) {
        return Arrays
            .stream(inv.getContents())
            .filter(Objects::nonNull)
            .anyMatch(item ->
                item.getType() == is.getType() && item.getItemMeta().equals(is.getItemMeta())
            );
    }
}