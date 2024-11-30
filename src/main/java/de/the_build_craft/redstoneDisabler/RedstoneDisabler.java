package de.the_build_craft.redstoneDisabler;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Switch;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Timer;

public final class RedstoneDisabler extends JavaPlugin implements Listener {
    public static final String LEVERS = "levers";
    Command stopCommand;
    boolean allowStop;
    Timer timer = null;
    UpdateTask updateTask;
    public static RedstoneDisabler instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        registerCommand(new RegisterLeverCommand());
        registerCommand(new UnRegisterLeverCommand());
        saveDefaultConfig();

        stopCommand = getServer().getCommandMap().getCommand("stop");
        instance = this;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!(e.getMessage().equals("/stop") || e.getMessage().equals("stop"))) return;

        if (!e.getPlayer().isOp()) {
            e.getPlayer().sendMessage("Nice try!");
            e.setCancelled(true);
            return;
        }
        handleCommand(e);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(ServerCommandEvent e) {
        if (!(e.getCommand().equals("/stop") || e.getCommand().equals("stop"))) return;
        handleCommand(e);
    }

    public void handleCommand(Cancellable c) {
        if (c.isCancelled()) return;
        if (!allowStop) {
            c.setCancelled(true);

            for (var locationStr : getConfig().getStringList(LEVERS)) {
                Location location = stringToLocation(locationStr);
                if (location.getBlock().getType() == Material.LEVER) {
                    BlockState state = location.getBlock().getState();
                    Switch lever = (Switch) state.getBlockData();

                    location.getBlock().breakNaturally();

                    var items = location.getNearbyEntitiesByType(Item.class, 1);
                    for (var item : items) {
                        if (item.getItemStack().getType() == Material.LEVER) item.remove();
                    }

                    lever.setPowered(false);
                    state.setBlockData(lever);
                    state.update(true, true);
                }
            }
            startTimer();
        }
    }

    public void startTimer() {
        if (timer != null) return;

        timer = new Timer(true);
        updateTask = new UpdateTask(getConfig().getInt("time-before-stop"));
        timer.scheduleAtFixedRate(updateTask, 0, 1000);
    }

    public void stopServer() {
        allowStop = true;
        stopCommand.execute(getServer().getConsoleSender(), "stop", new String[0]);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        timer.cancel();
    }

    public static void registerCommand(Command command)
    {
        try {
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(command.getLabel(), command);

        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean isRegisteredLever(Location location) {
        return getConfig().getStringList(LEVERS).stream().anyMatch(o -> o.contains(locationToString(location)));
    }

    public String getLeverOwner(Location location) {
        for (String str : getConfig().getStringList(LEVERS)) {
            if (str.contains(locationToString(location))) return str.split(",")[4];
        }
        return "";
    }

    public void addLever(Location location, Player player) {
        var list = getConfig().getStringList(LEVERS);
        list.add(locationToString(location) + ", " + player.getPlayerProfile().getId());
        getConfig().set(LEVERS, list);
        saveConfig();
    }

    public void removeLever(Location location) {
        var list = getConfig().getStringList(LEVERS);
        list.removeIf(o -> o.contains(locationToString(location)));
        getConfig().set(LEVERS, list);
        saveConfig();
    }

    public static String locationToString(final Location loc) {
        return loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ();
    }

    public static Location stringToLocation(final String string) {
        final String[] split = string.split(",");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
    }

    public static void spawnParticles(Player player, Color color, Location location) {
        player.spawnParticle(Particle.DUST, location.toCenterLocation(), 50, new Particle.DustOptions(color, 1));
    }
}
