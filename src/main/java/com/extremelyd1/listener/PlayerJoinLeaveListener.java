package com.extremelyd1.listener;

import com.extremelyd1.game.Game;
import com.extremelyd1.game.team.PlayerTeam;
import com.extremelyd1.game.team.Team;
import com.extremelyd1.sound.SoundManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.UUID;

public class PlayerJoinLeaveListener implements Listener {

    /**
     * The game instance
     */
    private final Game game;
    private JavaPlugin plugin;
    private boolean countdownRunning = false;

    public PlayerJoinLeaveListener(Game game) {
        this.game = game;
        this.plugin = game.getPlugin();
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (game.isMaintenance()) {
            for (OfflinePlayer offlinePlayer : Bukkit.getOperators()) {
                if (e.getUniqueId().equals(offlinePlayer.getUniqueId())) {
                    return;
                }
            }

            e.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    "Game is currently in maintenance mode"
            );
            return;
        }

        if (game.getConfig().isPreGenerateWorlds()) {
            e.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    "Game is currently pre-generating worlds"
            );
            return;
        }

        if (!game.getState().equals(Game.State.PRE_GAME)) {
            for (PlayerTeam team : game.getTeamManager().getActiveTeams()) {
                for (UUID uuid : team.getUUIDs()) {
                    if (uuid.equals(e.getUniqueId())) {
                        return;
                    }
                }
            }

            for (UUID uuid : game.getTeamManager().getSpectatorTeam().getUUIDs()) {
                if (uuid.equals(e.getUniqueId())) {
                    return;
                }
            }

            e.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    "Game is currently in progress"
            );
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        this.game.onPregameUpdate(Bukkit.getOnlinePlayers().size());

        Player player = e.getPlayer();

        if (game.getState().equals(Game.State.PRE_GAME)) {
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20D);
            player.setFoodLevel(20);
            player.setSaturation(5);

            Location spawnLocation = game.getWorldManager().getSpawnLocation();
            player.teleport(spawnLocation);
            player.setBedSpawnLocation(spawnLocation);
        }

        Team team = game.getTeamManager().getTeamByPlayer(player);
        if (team == null) {
            game.getTeamManager().addPlayerToTeam(player, game.getTeamManager().getSpectatorTeam(), false);

            team = game.getTeamManager().getSpectatorTeam();
        }

        e.setJoinMessage(
                ChatColor.GREEN + "+ " + ChatColor.RESET
                        + team.getColor() + player.getName()
                        + ChatColor.WHITE + " joined"
        );

        if (game.getState().equals(Game.State.PRE_GAME)) {
            Game.giveBackLobbyItem(player);
        }
        checkStartCountdown();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // Online players - 1, because the player object is not yet removed from
        // this list when the event is called
        this.game.onPregameUpdate(Bukkit.getOnlinePlayers().size() - 1);

        Player player = e.getPlayer();

        Team team = game.getTeamManager().getTeamByPlayer(player);
        if (team == null) {
            e.setQuitMessage(
                    ChatColor.RED + "- "
                            + player.getName()
                            + ChatColor.WHITE + " left"
            );

            return;
        }

        e.setQuitMessage(
                ChatColor.RED + "- "
                        + team.getColor() + player.getName()
                        + ChatColor.WHITE + " left"
        );
        checkStartCountdown();
    }

    private void checkStartCountdown() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        if (game.getState().equals(Game.State.PRE_GAME) && onlinePlayers >= game.getConfig().getMinPlayers() && !countdownRunning) {
            startCountdown();
        }
    }

    private void startCountdown() {
        SoundManager soundManager = new SoundManager();
        countdownRunning = true;

        new BukkitRunnable() {
            int seconds = 60;

            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() < game.getConfig().getMinPlayers() || !game.getState().equals(Game.State.PRE_GAME)) {
                    countdownRunning = false;
                    cancel();
                    return;
                }

                if (seconds > 0) {
                    if (seconds >= 20 && seconds % 10 == 0) {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "游戏将在 " + seconds + " 后开始");
                    } else if (seconds >= 10 && seconds < 20 && seconds % 5 == 0) {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "游戏将在 " + seconds + " 后开始");
                    } else if (seconds < 10) {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "游戏将在 " + seconds + " 后开始");
                        soundManager.startWait();
                    }
                    seconds--;
                } else {
                    Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                    //设置每队最多人数
                    int totalPlayers = players.size();
                    int numTeams = 0;
                    if (totalPlayers >= 17) {
                        numTeams = 5;
                    } else if (totalPlayers >= 13) {
                        numTeams = 4;
                    } else if (totalPlayers >= 9) {
                        numTeams = 3;
                    } else if (totalPlayers >= 4) {
                        numTeams = 2;
                    }
                    game.getTeamManager().createRandomizedTeams(
                            players,
                            numTeams,
                            true
                    );
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "游戏正在初始化，请稍后...");
                    game.start();
                    countdownRunning = false;
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
