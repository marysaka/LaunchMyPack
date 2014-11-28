package eu.thog92.launcher.updater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import eu.thog92.launcher.util.OperatingSystem;

public class SelfUpdate {
        public static void runUpdate(String currentPath, String temporaryUpdatePath) {
                List<String> arguments = new ArrayList<String>();
                
                String separator = System.getProperty("file.separator");
                String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
                arguments.add(path);
                arguments.add("-cp");
                arguments.add(temporaryUpdatePath);
                arguments.add(SelfUpdate.class.getCanonicalName());
                arguments.add(currentPath);
                arguments.add(temporaryUpdatePath);

                System.out.println("Would update with: " + arguments);
                System.out.println("c: " + currentPath);
                System.out.println("n: " + temporaryUpdatePath);
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command(arguments);
                
                try {
                    System.out.println("Run Updater");
                        processBuilder.start();
                } catch (IOException e) { e.printStackTrace(); }
                System.out.println("Exiting for update ...");
                System.exit(0);
        }

        public static void main(String[] args) {
                try {
                        if (OperatingSystem.getCurrentPlatform() != OperatingSystem.LINUX || OperatingSystem.getCurrentPlatform() != OperatingSystem.OSX) {
                                Thread.sleep(4000);
                        }
                } catch (InterruptedException ignored) {
                        JOptionPane.showMessageDialog(null, "Auto Updating Failed");
                        ignored.printStackTrace();
                }
                String launcherPath = args[0];
                String temporaryUpdatePath = args[1];
                File launcher2 = new File(launcherPath);
                File temporaryUpdate = new File(temporaryUpdatePath);
                try {
                        FileUtils.deleteQuietly(launcher2);
                        FileUtils.copyFile(temporaryUpdate, launcher2);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Auto Updating Failed");
                        e.printStackTrace();
                }

                List<String> arguments = new ArrayList<String>();

                String separator = System.getProperty("file.separator");
                String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
                arguments.add(path);
                arguments.add("-jar");
                arguments.add(launcherPath);
                JOptionPane.showMessageDialog(null, "ReRun with args : " + arguments);
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command(arguments);
                try {
                        processBuilder.start();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Failed to start launcher process after updating. Relaunch your launcher.");
                        e.printStackTrace();
                }
        }
}