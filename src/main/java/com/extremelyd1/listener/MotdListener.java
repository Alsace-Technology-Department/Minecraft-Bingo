package com.extremelyd1.listener;

import com.extremelyd1.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class MotdListener implements Listener {

    /**
     * The game instance
     */
    private final Game game;

    public MotdListener(Game game) {
        this.game = game;
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent e) {
        if (game.isMaintenance()) {
            e.setMotd(
                    Game.PREFIX + ChatColor.WHITE + "维护模式"
                            + ChatColor.DARK_RED + "\n暂时无法进入"
            );
            e.setMaxPlayers(0);
        } else if (game.getConfig().isPreGenerateWorlds()) {
            e.setMotd(
                    Game.PREFIX + ChatColor.WHITE + "地图正在准备中"
                            + ChatColor.DARK_RED + "\n暂时无法进入"
            );
            e.setMaxPlayers(0);
        } else {
            e.setMotd(
                    Game.PREFIX + ChatColor.WHITE + game.getState().getName()
                            + ChatColor.RESET + "\n玩家: "
                            + ChatColor.AQUA + Bukkit.getOnlinePlayers().size()
            );
        }
    }

}
