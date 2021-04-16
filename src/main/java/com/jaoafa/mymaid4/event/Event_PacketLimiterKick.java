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
import com.jaoafa.mymaid4.lib.MyMaidData;
import com.jaoafa.mymaid4.lib.MyMaidLibrary;
import net.dv8tion.jda.api.EmbedBuilder;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import java.awt.*;
import java.util.Random;

public class Event_PacketLimiterKick extends MyMaidLibrary implements Listener, EventPremise {
    @Override
    public String description() {
        return "PacketLimiterによるキック時に通知を行います。";
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        String reason = PlainComponentSerializer.plain().serialize(event.reason());
        if (!reason.equalsIgnoreCase("You are sending too many packets!") &&
            !reason.equalsIgnoreCase("You are sending too many packets, :(")) {
            return;
        }

        Location loc = event.getPlayer().getLocation();
        String location = loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " "
            + loc.getBlockZ();

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("警告！！")
            .appendDescription("プレイヤーがパケットを送信しすぎてKickされました。ハッククライアントの可能性があります。")
            .setAuthor(event.getPlayer().getName(),
                "https://users.jaoafa.com/" + event.getPlayer().getUniqueId(),
                "https://crafatar.com/avatars/" + event.getPlayer().getUniqueId())
            .setColor(Color.ORANGE)
            .addField("プレイヤー", "`" + event.getPlayer().getName() + "`", true)
            .addField("理由", "`" + reason + "`", false)
            .addField("座標", location, false);

        Random rand = new Random();
        boolean x_isMinus = rand.nextBoolean();
        int x = rand.nextInt(310) + 152; // 152 - 462
        x = x_isMinus ? -x : x;

        boolean z_isMinus = rand.nextBoolean();
        int z = rand.nextInt(310) + 152; // 152 - 462
        z = x_isMinus ? -z : z;

        Location teleportLoc = new Location(Bukkit.getWorld("Jao_Afa"), x, 70, z);
        event.getPlayer().teleport(teleportLoc);
        System.out.println("[PacketLimiter_AutoTP] teleport to Jao_Afa " + x + " 70 " + z);

        if (MyMaidData.getJaotanChannel() == null) {
            return;
        }
        MyMaidData.getJaotanChannel().sendMessage(embed.build()).queue();
    }
}