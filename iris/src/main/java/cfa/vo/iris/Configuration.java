package cfa.vo.iris;

import cfa.vo.iris.cli.CommandLine;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {
    private CommandLine commandLine;

    public Configuration(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public void apply() {
        setLogLevel();
        createConfigurationDir();
        platformDependentInit();
    }

    private void setLogLevel() {
        Logger.getLogger("").setLevel(commandLine.getLogLevel());
    }

    private void createConfigurationDir() {
        if (!commandLine.getConfigurationDir().exists()) {
            commandLine.getConfigurationDir().mkdirs();
        }
    }

    private void platformDependentInit() {
        if (commandLine.isMacOsX()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        if (commandLine.isCrossLookAndFeel()) {
            try {
                System.out.println("Setting cross platform Look and Feel...");
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.out.println("Failed to set the Look and Feel");
                Logger.getLogger(Iris.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
