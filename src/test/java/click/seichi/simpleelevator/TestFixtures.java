package click.seichi.simpleelevator;

import be.seeseemelk.mockbukkit.Coordinate;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

final class TestFixtures {
  static final Material PLATE = Material.HEAVY_WEIGHTED_PRESSURE_PLATE;
  static final Material BASE = Material.IRON_BLOCK;

  private TestFixtures() {}

  static void placePlatform(World world, int y) {
    placePlatform(world, 0, y, 0);
  }

  static void placePlatform(World world, int x, int y, int z) {
    world.getBlockAt(x, y, z).setType(PLATE);
    world.getBlockAt(x, y - 1, z).setType(BASE);
  }

  static Location location(World world, double y) {
    return new Location(world, 0.25D, y, 0.75D, 37.0F, -12.0F);
  }

  static void setPermission(Player player, JavaPlugin plugin, String permission, boolean value) {
    player.addAttachment(plugin, permission, value);
  }

  static Block blockAbove(World world, int y) {
    return world.getBlockAt(0, y + 1, 0);
  }

  static PassableWorldMock createWorld() {
    return new PassableWorldMock();
  }

  /**
   * MockBukkit の標準 WorldMock から、isPassable() を実装したブロックを返すワールドです。 本番コードは World#getBlockAt()
   * 経由でブロックを取得するため、ワールド側で差し替えます。
   */
  static final class PassableWorldMock extends WorldMock {
    private final Map<Coordinate, BlockMock> blocks = new HashMap<>();

    PassableWorldMock() {
      super(Material.AIR, -64, 320, 0);
    }

    @Override
    public BlockMock createBlock(Coordinate coordinate) {
      return blocks.computeIfAbsent(
          coordinate, key -> new PassableBlockMock(new Location(this, key.x, key.y, key.z)));
    }
  }

  /** MockBukkit の BlockMock#isPassable() は未実装で例外を投げるため、 テストでは空気だけを通過可能として扱います。 */
  private static final class PassableBlockMock extends BlockMock {
    private PassableBlockMock(Location location) {
      super(location);
    }

    @Override
    public boolean isPassable() {
      return getType() == Material.AIR;
    }
  }
}
