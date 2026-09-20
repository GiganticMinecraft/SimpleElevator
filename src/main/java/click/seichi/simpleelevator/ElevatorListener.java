package click.seichi.simpleelevator;

import java.util.Optional;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

public final class ElevatorListener implements Listener {
  private static final String UP_PERMISSION = "elevator.up";
  private static final String DOWN_PERMISSION = "elevator.down";

  private final ElevatorMovementService elevatorMovementService;

  public ElevatorListener(@NotNull ElevatorMovementService elevatorMovementService) {
    this.elevatorMovementService = elevatorMovementService;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerMove(@NotNull PlayerMoveEvent event) {
    Player player = event.getPlayer();
    Optional.of(event.getTo())
        .filter(to -> !player.isFlying())
        .filter(to -> to.getY() > event.getFrom().getY())
        .filter(to -> player.hasPermission(UP_PERMISSION))
        .ifPresent(to -> elevatorMovementService.movePlayer(player, to, BlockFace.UP));
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerToggleSneak(@NotNull PlayerToggleSneakEvent event) {
    if (!event.isSneaking()) {
      return;
    }

    Player player = event.getPlayer();
    if (!player.hasPermission(DOWN_PERMISSION)) {
      return;
    }

    elevatorMovementService.movePlayer(player, player.getLocation(), BlockFace.DOWN);
  }
}
