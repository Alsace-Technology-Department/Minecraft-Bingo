package com.extremelyd1.command;

import com.extremelyd1.game.Game;
import com.extremelyd1.game.team.PlayerTeam;
import com.extremelyd1.game.team.Team;
import com.extremelyd1.util.CommandUtil;
import com.extremelyd1.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CardCommand implements TabExecutor {

    /**
     * The game instance
     */
    private final Game game;

    public CardCommand(Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!CommandUtil.checkCommandSender(sender, false, false)) {
            return true;
        }

        Player player = (Player) sender;

        if (!game.getState().equals(Game.State.IN_GAME)) {
            player.sendMessage(
                    ChatColor.DARK_RED + "Error: " + ChatColor.WHITE + "现在无法执行此命令"
            );

            return true;
        }

        Team team = game.getTeamManager().getTeamByPlayer(player);
        if (team == null || team.isSpectatorTeam()) {
            player.sendMessage(
                    ChatColor.DARK_RED + "Error: " + ChatColor.WHITE + "无法以旁观者身份执行此命令"
            );

            return true;
        }

        if (ItemUtil.hasBingoCard(player)) {
            player.sendMessage(Game.PREFIX + "你的库存中已经有一张Bingo card了");

            return true;
        }

        player.getInventory().addItem(
                game.getBingoCardItemFactory().create(game.getBingoCard(), (PlayerTeam) team)
        );
        player.sendMessage(
                Game.PREFIX + "您已获得一张新Bingo card"
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
