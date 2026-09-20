package click.seichi.simpleelevator;

import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

final class ElevatorMovementService {
  private static final Material PLATE_MATERIAL = Material.HEAVY_WEIGHTED_PRESSURE_PLATE;
  private static final Material BLOCK_MATERIAL = Material.IRON_BLOCK;

  void movePlayer(
      @NotNull Player player, @NotNull Location currentLocation, @NotNull BlockFace direction) {
    Optional<Block> destinationPlatform =
        getBlockAt(currentLocation)
            .filter(this::isElevatorPlatform)
            .flatMap(platform -> findDestination(platform, direction));
    if (destinationPlatform.isEmpty()) {
      return;
    }

    Location destination = currentLocation.clone();
    destination.setY(destinationPlatform.orElseThrow().getY());

    if (!player.teleport(destination)) {
      return;
    }

    player.playSound(destination, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0F, 1.0F);
  }

  private Optional<Block> findDestination(
      @NotNull Block currentPlatform, @NotNull BlockFace direction) {
    int step = direction == BlockFace.DOWN ? -1 : 1;
    int boundary =
        step > 0
            ? currentPlatform.getWorld().getMaxHeight()
            : currentPlatform.getWorld().getMinHeight();

    for (int y = currentPlatform.getY() + step; step > 0 ? y < boundary : y > boundary; y += step) {
      Block candidate =
          currentPlatform.getWorld().getBlockAt(currentPlatform.getX(), y, currentPlatform.getZ());
      if (isElevatorPlatform(candidate) && isAllowedSpace(candidate.getRelative(BlockFace.UP))) {
        return Optional.of(candidate);
      }
    }
    return Optional.empty();
  }

  private boolean isElevatorPlatform(@NotNull Block plate) {
    return plate.getType() == PLATE_MATERIAL
        && plate.getRelative(BlockFace.DOWN).getType() == BLOCK_MATERIAL;
  }

  /** 移動先の台の上にプレイヤーが入れるかを判定します。 isPassable() は衝突の有無だけを判定するため、安全でないブロックは明示的に除外します。 */
  private boolean isAllowedSpace(@NotNull Block block) {
    Material material = block.getType();
    boolean isSafeMaterial =
        material != Material.COBWEB // クモの巣
            && material != Material.SWEET_BERRY_BUSH // スイートベリーの茂み
            && material != Material.WATER // 水
            && material != Material.LAVA // 溶岩
            && !Tag.FIRE.isTagged(material); // 炎・魂の炎

    return block.isPassable() && isSafeMaterial;
  }

  private Optional<Block> getBlockAt(@NotNull Location location) {
    return Optional.ofNullable(location.getWorld())
        .map(
            world ->
                world.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
  }
}
