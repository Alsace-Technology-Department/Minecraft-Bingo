package com.extremelyd1.title;

import com.extremelyd1.game.team.PlayerTeam;
import com.extremelyd1.game.winCondition.WinReason;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Manager class to send titles to players
 */
public class TitleManager {

    public TitleManager() {
    }

    /**
     * Send the start title to all players
     */
    public void sendStartTitle() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(
                    ChatColor.BOLD.toString() + ChatColor.BLUE + "BINGO",
                    "游戏开始!",
                    10,
                    30,
                    10
            );
        }
    }

    /**
     * Send the end title to all players based on the win reason
     * @param winReason The win reason
     */
    public void sendEndTitle(WinReason winReason) {
        String title = "";
        String subtitle = "";

        switch (winReason.getReason()) {
            case COMPLETE -> {
                PlayerTeam team = winReason.getTeam();
                title = ChatColor.BOLD.toString() + ChatColor.BLUE + "BINGO";
                subtitle = team.getColor() + team.getName()
                        + ChatColor.WHITE + " 队 "
                        + "赢得了胜利!";
            }
            case RANDOM_TIE -> {
                title = "游戏结束!";
                subtitle = ChatColor.BLUE + "平局";
            }
            default -> title = "游戏结束!";
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(
                    title,
                    subtitle,
                    10,
                    60,
                    10
            );
        }
    }
}
