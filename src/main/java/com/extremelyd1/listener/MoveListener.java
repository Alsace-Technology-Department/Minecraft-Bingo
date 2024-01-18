package com.extremelyd1.listener;

import com.extremelyd1.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    /**
     * The game instance
     */
    private final Game game;

    public MoveListener(Game game) {
        this.game = game;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (game.getState().equals(Game.State.PRE_GAME) && !game.isMaintenance()) {
            // Sanity check
            if (e.getTo() == null) {
                return;
            }

            Location spawnLocation = game.getWorldManager().getSpawnLocation();
            if (e.getTo().distance(spawnLocation) > game.getConfig().getPreGameBorderRadius()) {
                e.getPlayer().sendMessage(
                        ChatColor.DARK_RED + "Error: "
                                + ChatColor.WHITE + "你不能在等待时离开出生点"
                );
                e.getPlayer().teleport(spawnLocation);
            }
        }
    }

}
