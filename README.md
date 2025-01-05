# Redstone Disabler

Paper plugin to automatically turn off levers (and the contraptions connected to them) before server restarts.

### Usage
- /registerLever: look at a lever and run this command to register a lever
- /unregisterLever: look at a lever and run this command to unregister a lever
- /turnOffAllLevers: turn all your levers off or specify a player-name to turn off all off this player's levers (only admins can turn off other player's levers)
- Players can only unregister their own levers. Admins can bypass this.
- The countdown time between turning off all levers and server stop can be configured