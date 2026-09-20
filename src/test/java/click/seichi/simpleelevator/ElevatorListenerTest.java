package click.seichi.simpleelevator;

import static click.seichi.simpleelevator.TestFixtures.location;
import static click.seichi.simpleelevator.TestFixtures.placePlatform;
import static click.seichi.simpleelevator.TestFixtures.setPermission;
import static org.junit.jupiter.api.Assertions.assertEquals;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ElevatorListenerTest {
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
  class WhenMovingUp {
    @Test
    void canMoveWithPermission() {
      placePlatform(world, 10);
      placePlatform(world, 12);
      setPermission(player, plugin, "elevator.up", true);
      Location current = location(world, 10.25D);
      player.setLocation(current);

      server
          .getPluginManager()
          .callEvent(new PlayerMoveEvent(player, location(world, 10.0D), current));

      assertEquals(12.0D, player.getLocation().getY());
    }

    @Test
    void doesNotMoveWithoutPermission() {
      placePlatform(world, 10);
      placePlatform(world, 12);
      setPermission(player, plugin, "elevator.up", false);
      Location current = location(world, 10.25D);
      player.setLocation(current);

      server
          .getPluginManager()
          .callEvent(new PlayerMoveEvent(player, location(world, 10.0D), current));

      assertEquals(10.25D, player.getLocation().getY());
    }

    @Test
    void doesNotMoveWhileFlying() {
      placePlatform(world, 10);
      placePlatform(world, 12);
      setPermission(player, plugin, "elevator.up", true);
      player.setAllowFlight(true);
      player.setFlying(true);
      Location current = location(world, 10.25D);
      player.setLocation(current);

      server
          .getPluginManager()
          .callEvent(new PlayerMoveEvent(player, location(world, 10.0D), current));

      assertEquals(10.25D, player.getLocation().getY());
    }

    @Test
    void doesNotMoveWhenYDoesNotIncrease() {
      placePlatform(world, 10);
      placePlatform(world, 12);
      setPermission(player, plugin, "elevator.up", true);
      Location current = location(world, 10.25D);
      player.setLocation(current);

      server
          .getPluginManager()
          .callEvent(new PlayerMoveEvent(player, current.clone(), current.clone()));

      assertEquals(10.25D, player.getLocation().getY());
    }

    @Test
    void doesNotMoveWhenEventIsCancelled() {
      placePlatform(world, 10);
      placePlatform(world, 12);
      setPermission(player, plugin, "elevator.up", true);
      Location current = location(world, 10.25D);
      player.setLocation(current);
      PlayerMoveEvent event = new PlayerMoveEvent(player, location(world, 10.0D), current);
      event.setCancelled(true);

      server.getPluginManager().callEvent(event);

      assertEquals(10.25D, player.getLocation().getY());
    }
  }

  @Nested
  class WhenMovingDown {
    @Test
    void canMoveWhenSneakStartsWithPermission() {
      placePlatform(world, 10);
      placePlatform(world, 13);
      setPermission(player, plugin, "elevator.down", true);
      Location upper = location(world, 13.25D);
      player.setLocation(upper);

      server.getPluginManager().callEvent(new PlayerToggleSneakEvent(player, true));

      assertEquals(10.0D, player.getLocation().getY());
    }

    @Test
    void doesNotMoveWhenSneakIsReleased() {
      placePlatform(world, 10);
      placePlatform(world, 13);
      setPermission(player, plugin, "elevator.down", true);
      Location upper = location(world, 13.25D);
      player.setLocation(upper);

      server.getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));

      assertEquals(13.25D, player.getLocation().getY());
    }

    @Test
    void doesNotMoveWithoutPermission() {
      placePlatform(world, 10);
      placePlatform(world, 13);
      setPermission(player, plugin, "elevator.down", false);
      Location upper = location(world, 13.25D);
      player.setLocation(upper);

      server.getPluginManager().callEvent(new PlayerToggleSneakEvent(player, true));

      assertEquals(13.25D, player.getLocation().getY());
    }
  }
}
