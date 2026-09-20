package click.seichi.simpleelevator;

import static click.seichi.simpleelevator.TestFixtures.blockAbove;
import static click.seichi.simpleelevator.TestFixtures.location;
import static click.seichi.simpleelevator.TestFixtures.placePlatform;
import static org.junit.jupiter.api.Assertions.assertEquals;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ElevatorServiceTest {
  private ServerMock server;
  private SimpleElevator plugin;
  private TestFixtures.PassableWorldMock world;
  private PlayerMock player;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    plugin = MockBukkit.load(SimpleElevator.class);
    world = TestFixtures.createWorld();
    server.addWorld(world);
    player = server.addPlayer();
  }

  @AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  @Nested
  class WhenFindingDestination {
    @Test
    void movesToNearestValidPlatformAndPreservesView() {
      placePlatform(world, 10);
      placePlatform(world, 12);
      blockAbove(world, 12).setType(Material.STONE);
      placePlatform(world, 14);

      Location current = location(world, 10.25D);
      player.setLocation(current);
      ElevatorMovementService service = new ElevatorMovementService();

      service.movePlayer(player, current, BlockFace.UP);

      assertEquals(14.0D, player.getLocation().getY());
      assertEquals(current.getX(), player.getLocation().getX());
      assertEquals(current.getZ(), player.getLocation().getZ());
      assertEquals(current.getYaw(), player.getLocation().getYaw());
      assertEquals(current.getPitch(), player.getLocation().getPitch());
      player.assertSoundHeard(Sound.ENTITY_ENDER_DRAGON_FLAP);
    }

    @Test
    void doesNotMoveWhenCurrentBlockIsNotAnElevatorPlatform() {
      Location current = location(world, 10.25D);
      player.setLocation(current);
      placePlatform(world, 12);
      ElevatorMovementService service = new ElevatorMovementService();

      service.movePlayer(player, current, BlockFace.UP);

      assertEquals(current, player.getLocation());
      player.assertNotTeleported();
    }

    @Test
    void movesToDestinationBeyondFormerConfiguredDistance() {
      placePlatform(world, 10);
      placePlatform(world, 15);
      Location current = location(world, 10.25D);
      player.setLocation(current);
      ElevatorMovementService service = new ElevatorMovementService();

      service.movePlayer(player, current, BlockFace.UP);

      assertEquals(15.0D, player.getLocation().getY());
    }

    @ParameterizedTest
    @MethodSource("unsafeDestinationMaterials")
    void skipsDestinationWithUnsafeOrSolidSpace(Material material) {
      placePlatform(world, 10);
      placePlatform(world, 12);
      blockAbove(world, 12).setType(material);
      Location current = location(world, 10.25D);
      player.setLocation(current);
      ElevatorMovementService service = new ElevatorMovementService();

      service.movePlayer(player, current, BlockFace.UP);

      assertEquals(current, player.getLocation());
      player.assertNotTeleported();
    }

    @Test
    void skipsUnsafeNearestPlatformAndUsesNextValidPlatform() {
      placePlatform(world, 10);
      placePlatform(world, 12);
      blockAbove(world, 12).setType(Material.WATER);
      placePlatform(world, 14);
      Location current = location(world, 10.25D);
      player.setLocation(current);
      ElevatorMovementService service = new ElevatorMovementService();

      service.movePlayer(player, current, BlockFace.UP);

      assertEquals(14.0D, player.getLocation().getY());
    }

    private static Stream<Material> unsafeDestinationMaterials() {
      return Stream.of(
          Material.STONE,
          Material.WATER,
          Material.LAVA,
          Material.FIRE,
          Material.SOUL_FIRE,
          Material.COBWEB,
          Material.SWEET_BERRY_BUSH);
    }
  }

  @Nested
  class WhenMovingRepeatedly {
    @Test
    void canMoveImmediatelyInBothDirections() {
      placePlatform(world, 10);
      placePlatform(world, 13);
      Location current = location(world, 10.25D);
      player.setLocation(current);
      ElevatorMovementService service = new ElevatorMovementService();

      service.movePlayer(player, current, BlockFace.UP);
      Location upper = player.getLocation().clone();
      service.movePlayer(player, upper, BlockFace.DOWN);
      assertEquals(10.0D, player.getLocation().getY());
    }

    @Test
    void doesNotMoveWhenDestinationIsMissing() {
      placePlatform(world, 10);
      Location current = location(world, 10.25D);
      player.setLocation(current);
      ElevatorMovementService service = new ElevatorMovementService();

      service.movePlayer(player, current, BlockFace.UP);
      placePlatform(world, 12);
      service.movePlayer(player, current, BlockFace.UP);

      assertEquals(12.0D, player.getLocation().getY());
    }
  }
}
