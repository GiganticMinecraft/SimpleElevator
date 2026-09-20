package click.seichi.simpleelevator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

class PluginDescriptionTest {
  @Test
  void pluginYmlIsValid() throws Exception {
    try (InputStream input = getClass().getResourceAsStream("/plugin.yml")) {
      assertNotNull(input);

      PluginDescriptionFile description = new PluginDescriptionFile(input);

      assertEquals("SimpleElevator", description.getName());
      assertFalse(description.getVersion().isBlank());
      assertEquals(SimpleElevator.class.getName(), description.getMain());
      assertEquals("1.18", description.getAPIVersion());

      Class<?> mainClass = Class.forName(description.getMain(), false, getClass().getClassLoader());
      assertTrue(JavaPlugin.class.isAssignableFrom(mainClass));

      assertEquals(
          Set.of("elevator.up", "elevator.down"),
          description.getPermissions().stream()
              .map(Permission::getName)
              .collect(Collectors.toSet()));
      assertTrue(
          description.getPermissions().stream()
              .allMatch(permission -> permission.getDefault() == PermissionDefault.TRUE));
    }
  }
}
