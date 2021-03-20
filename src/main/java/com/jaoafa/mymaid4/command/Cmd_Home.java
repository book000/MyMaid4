package com.jaoafa.mymaid4.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import com.jaoafa.mymaid4.lib.CommandPremise;
import com.jaoafa.mymaid4.lib.Home;
import com.jaoafa.mymaid4.lib.MyMaidCommand;
import com.jaoafa.mymaid4.lib.MyMaidLibrary;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cmd_Home extends MyMaidLibrary implements CommandPremise {
    @Override
    public MyMaidCommand.Detail details() {
        return new MyMaidCommand.Detail(
            "home",
            "ホームにテレポートします。"
        );
    }

    @Override
    public MyMaidCommand.Cmd register(Command.Builder<CommandSender> builder) {
        return new MyMaidCommand.Cmd(
            builder
                .meta(CommandMeta.DESCRIPTION, "デフォルトホームにテレポートします。")
                .senderType(Player.class)
                .handler(this::teleportHome)
                .build(),
            builder
                .meta(CommandMeta.DESCRIPTION, "指定された名前のホームにテレポートします。")
                .senderType(Player.class)
                .argument(StringArgument
                    .<CommandSender>newBuilder("name")
                    .asOptionalWithDefault("default")
                    .withSuggestionsProvider(Home::suggestHomeName))
                .handler(this::teleportHome)
                .build(),
            builder
                .meta(CommandMeta.DESCRIPTION, "ホーム一覧を表示します。")
                .senderType(Player.class)
                .literal("list")
                .handler(this::listHome)
                .build(),
            builder
                .meta(CommandMeta.DESCRIPTION, "指定したホームに関する情報を表示します。")
                .senderType(Player.class)
                .literal("view")
                .argument(StringArgument
                    .<CommandSender>newBuilder("name")
                    .asOptionalWithDefault("default")
                    .withSuggestionsProvider(Home::suggestHomeName))
                .handler(this::viewHome)
                .build()
        );
    }

    void teleportHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String name = context.getOrDefault("name", "default");
        Home home = new Home(player);

        if (!home.exists(name)) {
            SendMessage(player, details(), String.format("指定されたホーム「%s」は見つかりません。", name));
            SendMessage(player, details(), Component.text().append(
                Component.text("ホームの作成は", NamedTextColor.GREEN),
                Component.space(),
                Component.text(String.format("/sethome %s", name), NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                    .hoverEvent(HoverEvent.showText(
                        Component.text("コマンド「" + String.format("/sethome %s", name) + "」をサジェストします。")
                    ))
                    .clickEvent(ClickEvent.suggestCommand(String.format("/sethome %s", name))),
                Component.space(),
                Component.text("を実行してください。", NamedTextColor.GREEN)
            ).build());
            return;
        }

        Home.Detail detail = home.get(name);
        if (Bukkit.getWorld(detail.worldName) == null) {
            SendMessage(player, details(), String.format("ホーム「%s」のワールド「%s」が見つかりませんでした。", name, detail.worldName));
            return;
        }
        player.teleport(detail.getLocation());

        SendMessage(player, details(), String.format("ホーム「%s」にテレポートしました。", name));
    }

    void listHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        // TODO ホームリスト実装
        /*
        想定されるやり方:
        - チャット欄・本などを使ってホームの一覧を表示する
        - チャット欄の場合
          - ページネーション機能を作って10アイテム毎とかで表示する
          - クリックで前に・次に進む → 内部で/home list 2とかたたかせる？
         */
        // Home home = new Home(player);
        // Set<Home.Detail> homes = home.getHomes();

        SendMessage(player, details(), Component.text().append(
            Component.text("この機能は未実装です。(Issue: ", NamedTextColor.GREEN),
            Component.text("#21", NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl("https://github.com/jaoafa/MyMaid4/issues/21")),
            Component.text(")", NamedTextColor.GREEN)
        ).build());
    }


    void viewHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String name = context.getOrDefault("name", "default");
        Home home = new Home(player);
        if (!home.exists(name)) {
            SendMessage(player, details(), "指定されたホームが見つかりませんでした。");
            return;
        }

        Home.Detail detail = home.get(name);

        SendMessage(player, details(), String.format("----- %s -----", detail.name));
        SendMessage(player, details(), String.format(
            "Location: %s %.2f %.2f %.2f %.2f %.2f",
            detail.worldName,
            detail.x,
            detail.y,
            detail.z,
            detail.yaw,
            detail.pitch
        ));
        SendMessage(player, details(), "作成日時: " + detail.getDate());
    }
}
