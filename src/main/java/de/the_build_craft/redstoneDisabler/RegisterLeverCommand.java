package de.the_build_craft.redstoneDisabler;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RegisterLeverCommand extends Command {
    public RegisterLeverCommand()
    {
        super("registerLever");
        setDescription("register a lever to be turned off before server restart");
        setUsage("look at a lever and execute the command");
    }

    /**
     * Executes the command, returning its success
     *
     * @param sender       Source object which is executing this command
     * @param commandLabel The alias of the command used
     * @param args         All arguments passed to the command, split via ' '
     * @return true if the command was successful, otherwise false
     */
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            RayTraceResult rayTraceResult = player.rayTraceBlocks(10, FluidCollisionMode.NEVER);
            if (rayTraceResult != null) {
                Block block = rayTraceResult.getHitBlock();
                if (block != null && block.getBlockData().getMaterial() == Material.LEVER) {
                    Location location = block.getLocation();
                    if (RedstoneDisabler.instance.isRegisteredLever(location)) {
                        String owner = RedstoneDisabler.instance.getLeverOwner(location).strip();
                        if (owner.equals(player.getPlayerProfile().getId().toString())) {
                            RedstoneDisabler.spawnParticles(player, Color.ORANGE, location);
                            sender.sendMessage("You have already registered this lever!");
                        } else {
                            RedstoneDisabler.spawnParticles(player, Color.ORANGE, location);
                            sender.sendMessage("This lever was already registered by " + Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName() + "!");
                        }
                    } else {
                        RedstoneDisabler.instance.addLever(location, player);
                        RedstoneDisabler.spawnParticles(player, Color.GREEN, location);
                        sender.sendMessage("Successfully registered lever!");
                    }
                    return true;
                } else {
                    sender.sendMessage("You have to look at a lever (max distance = 10 blocks)!");
                    return false;
                }
            } else {
                sender.sendMessage("You have to look at a lever (max distance = 10 blocks)!");
                return false;
            }
        } else {
            sender.sendMessage("Can only be used by a player!");
            return false;
        }
    }
}
