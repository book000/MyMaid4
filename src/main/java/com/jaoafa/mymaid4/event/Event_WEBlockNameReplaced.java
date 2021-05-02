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

import com.jaoafa.mymaid4.lib.EventPremise;
import com.jaoafa.mymaid4.lib.MyMaidLibrary;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Event_WEBlockNameReplaced extends MyMaidLibrary implements Listener, EventPremise {
    Map<String, String> blocks = new HashMap<>();

    {
        blocks.put("43", "smooth_stone_slab[type=double]");
    }

    @Override
    public String description() {
        return "WorldEditコマンドの各種ブロックを従来の動作に戻します。";
    }

    @EventHandler
    public void onTeleportCommandFromPlayer(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        String[] args = command.split(" ");

        if (!args[0].startsWith("//")) {
            return;
        }

        for (Map.Entry<String, String> entry : blocks.entrySet()) {
            args = Arrays.stream(args)
                .map(a -> a.equalsIgnoreCase(entry.getKey()) ? entry.getValue() : a)
                .toArray(String[]::new);
        }
        if (command.equals(String.join(" ", args))) {
            return;
        }
        event.setMessage(String.join(" ", args));
    }
}
