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

package com.jaoafa.mymaid4;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.jaoafa.mymaid4.lib.ClassFinder;
import com.jaoafa.mymaid4.lib.CommandPremise;
import com.jaoafa.mymaid4.lib.MyMaidConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public final class Main extends JavaPlugin {
    private static Main Main = null;
    private MyMaidConfig config = null;

    public static JavaPlugin getJavaPlugin() {
        return Main;
    }

    @Override
    public void onEnable() {
        Main = this;

        config = new MyMaidConfig();
        if (!isEnabled())
            return;

        registerCommand();
        if (!isEnabled())
            return;

        registerEvent();
    }

    private void registerCommand() {
        getLogger().info("----- registerCommand -----");
        final PaperCommandManager<CommandSender> manager;
        try {
            manager = new PaperCommandManager<>(this, CommandExecutionCoordinator.SimpleCoordinator.simpleCoordinator(),
                Function.identity(), Function.identity());
        } catch (Exception e) {
            getLogger().warning("コマンドの登録に失敗しました。PaperCommandManagerを取得できません。");
            e.printStackTrace();
            return;
        }

        try {
            ClassFinder classFinder = new ClassFinder(this.getClassLoader());
            for (Class<?> clazz : classFinder.findClasses("com.jaoafa.mymaid4.command")) {
                if (!clazz.getName().startsWith("com.jaoafa.mymaid4.command.Cmd_")) {
                    continue;
                }
                if (clazz.getEnclosingClass() != null) {
                    continue;
                }
                if (clazz.getName().contains("$")) {
                    continue;
                }
                String commandName = clazz.getName().substring("com.jaoafa.mymaid4.command.Cmd_".length())
                    .toLowerCase();
                try {
                    Constructor<?> construct = clazz.getConstructor();
                    Object instance = construct.newInstance();
                    CommandPremise cmdPremise = (CommandPremise) instance;

                    Command.Builder<CommandSender> builder = manager.commandBuilder(cmdPremise.getDetails().getName(),
                        ArgumentDescription.of(cmdPremise.getDetails().getDescription()),
                        cmdPremise.getDetails().getAliases().toArray(new String[0]));

                    cmdPremise.register(builder).getCommands().forEach(manager::command);

                    getLogger().info(String.format("%s registered", commandName));
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    getLogger().warning(String.format("%s register failed", commandName));
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            getLogger().warning("registerCommand failed");
            e.printStackTrace();
        }
    }

    private void registerEvent() {
        getLogger().info("----- registerEvent -----");
        try {
            ClassFinder classFinder = new ClassFinder(this.getClassLoader());
            for (Class<?> clazz : classFinder.findClasses("com.jaoafa.mymaid4.event")) {
                if (!clazz.getName().startsWith("com.jaoafa.mymaid4.event.Event_")) {
                    continue;
                }
                if (clazz.getEnclosingClass() != null) {
                    continue;
                }
                if (clazz.getName().contains("$")) {
                    continue;
                }
                String commandName = clazz.getName().substring("com.jaoafa.mymaid4.event.Event_".length())
                    .toLowerCase();
                try {
                    Constructor<?> construct = clazz.getConstructor();
                    Object instance = construct.newInstance();

                    if (!(instance instanceof Listener)) {
                        getLogger().warning(clazz.getSimpleName() + ": Listener not implemented [0]");
                        return;
                    }

                    try {
                        Listener listener = (Listener) instance;
                        getServer().getPluginManager().registerEvents(listener, this);
                        getLogger().info(String.format("%s registered", clazz.getSimpleName()));
                    } catch (ClassCastException e) {
                        getLogger().warning(String.format("%s: Listener not implemented [1]", clazz.getSimpleName()));
                    }
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    getLogger().warning(String.format("%s register failed", commandName));
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            getLogger().warning("registerCommand failed");
            e.printStackTrace();
        }
    }

    @NotNull
    public MyMaidConfig getMyMaidConfig() {
        return config;
    }
}
