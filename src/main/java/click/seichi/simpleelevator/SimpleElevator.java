package click.seichi.simpleelevator;

import java.io.File;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

public final class SimpleElevator extends JavaPlugin {
  public SimpleElevator() {
    super();
  }

  private SimpleElevator(
      JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }

  @Override
  public void onEnable() {
    getServer()
        .getPluginManager()
        .registerEvents(new ElevatorListener(new ElevatorMovementService()), this);
  }
}
