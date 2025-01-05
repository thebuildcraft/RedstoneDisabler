package de.the_build_craft.redstoneDisabler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Switch;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static de.the_build_craft.redstoneDisabler.RedstoneDisabler.*;

public class TurnOffCommand extends Command {
    public TurnOffCommand()
    {
        super("turnOffAllLevers");
        setDescription("Turns off all of your levers or from another player (admin only)");
        setUsage("Turns off all of your levers or from another player (admin only)");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                turnOffAll(player.getName(), sender, false);
                return true;
            } else {
                sender.sendMessage("You have to specify a player-name when executing the command on the console!");
                return false;
            }
        } else {
            if (sender instanceof Player player) {
                if (player.getName().equals(args[0])) {
                    turnOffAll(args[0], sender, false);
                    return true;
                } else {
                    if (player.isOp()) {
                        turnOffAll(args[0], sender, true);
                        return true;
                    } else {
                        sender.sendMessage("You must be OP to disable levers from other players!");
                        return false;
                    }
                }
            } else {
                turnOffAll(args[0], sender, true);
                return true;
            }
        }
    }

    private void turnOffAll(String owner, CommandSender sender, boolean notifyPlayer) {
        int all = 0;
        int wasOn = 0;
        for (String locationStr : RedstoneDisabler.getLeverList()) {
            if (owner.equals(Bukkit.getOfflinePlayer(UUID.fromString(locationStr.split(",")[4].trim())).getName())) {
                Location location = stringToLocation(locationStr);
                if (location.getBlock().getType() != Material.LEVER) continue;
                BlockState state = location.getBlock().getState();
                Switch lever = (Switch) state.getBlockData();
                if (lever.isPowered()) wasOn++;
                turnOffLever(location);
                all++;
            }
        }
        sender.sendMessage("Turned off " + wasOn + " / " + all + " levers from " + owner + "!");
        if (notifyPlayer) {
            Player player = Bukkit.getPlayerExact(owner);
            if (player != null) {
                player.sendMessage("An admin turned off " + wasOn + " / " + all + " levers from you because your Redstone made to much lag!");
            }
        }
    }
}
