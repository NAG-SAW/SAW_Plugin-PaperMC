package de.hallo5000.main;

import de.hallo5000.commands.authenticateCommand;
import de.hallo5000.commands.friendCommand;
import de.hallo5000.commands.pingCommand;
import de.hallo5000.commands.unfriendCommand;
import de.hallo5000.listener.handleGuests;
import de.hallo5000.listener.updateName;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.node.NodeMutateEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends JavaPlugin {

    public static LuckPerms lp;
    private static File commandLog;

    @Override
    public void onEnable() {
        super.onEnable();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) lp = provider.getProvider();

        getCommand("friend").setExecutor(new friendCommand());
        getCommand("unfriend").setExecutor(new unfriendCommand());
        getCommand("authenticate").setExecutor(new authenticateCommand());
        getCommand("ping").setExecutor(new pingCommand());

        getCommand("friend").setTabCompleter(new friendCommand());
        getCommand("unfriend").setTabCompleter(new unfriendCommand());
        getCommand("authenticate").setTabCompleter(new authenticateCommand());
        getCommand("ping").setTabCompleter(new pingCommand());

        getServer().getPluginManager().registerEvents(new updateName(), this);
        getServer().getPluginManager().registerEvents(new handleGuests(), this);
        lp.getEventBus().subscribe(this, NodeMutateEvent.class, updateName::onNodeMutate);
        //lp.getEventBus().subscribe(this, NodeMutateEvent.class, handleGuests::onNodeMutate);

        //create /friend-logfile
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        commandLog = new File(getDataFolder(), "commandUsage.log");
        if(!commandLog.exists()){
            try {
                commandLog.createNewFile();
            } catch (IOException e) {
                getServer().getConsoleSender().sendMessage("§c[SAW-Plugin] commandUsage.log could'nt be created!");
            }
        }else{
            if(commandLog.length() > 10000){
                File oldFile = new File(commandLog.getParent(), commandLog.getName()+"-"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));
                commandLog.renameTo(oldFile);
                commandLog=new File(getDataFolder(), "commandUsage.log");
            }
        }

        getServer().getConsoleSender().sendMessage("[SAW-Plugin] Successfully loaded!");

    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static void logCommand(Command cmd, CommandSender p, String uuid, String target){
        try {
            FileWriter fw = new FileWriter(commandLog, true);
            fw.write("["+LocalDateTime.now()+"] "+p.getName()+" with UUID: "+uuid+" used "+cmd.toString()+" on "+target+".\n");
            fw.close();
        } catch (IOException ex) {
            Main.getPlugin(Main.class).getServer().getConsoleSender().sendMessage("§c[SAW-Plugin] Error while trying to write a command-log!");
            Main.getPlugin(Main.class).getServer().getConsoleSender().sendMessage("§c[SAW-Plugin] Time: " + LocalDateTime.now());
            Main.getPlugin(Main.class).getServer().getConsoleSender().sendMessage("§c[SAW-Plugin] UUID: " + uuid);
            Main.getPlugin(Main.class).getServer().getConsoleSender().sendMessage("§c[SAW-Plugin] Player: " + p.getName());
            Main.getPlugin(Main.class).getServer().getConsoleSender().sendMessage("§c[SAW-Plugin] trying to use : " + cmd.toString());
            Main.getPlugin(Main.class).getServer().getConsoleSender().sendMessage("§c[SAW-Plugin] on : " + target);
        }
    }

    public static String removeColorCodes(String withColorCodes){
        return withColorCodes.replace("§a", "")
                .replace("§b", "")
                .replace("§c", "")
                .replace("§d", "")
                .replace("§e", "")
                .replace("§f", "")
                .replace("§0", "")
                .replace("§1", "")
                .replace("§2", "")
                .replace("§3", "")
                .replace("§4", "")
                .replace("§5", "")
                .replace("§6", "")
                .replace("§7", "")
                .replace("§8", "")
                .replace("§9", "")
                .replace("§k", "")
                .replace("§l", "")
                .replace("§m", "")
                .replace("§n", "")
                .replace("§o", "")
                .replace("§r", "");
    }

    public static boolean isIPinRange(InetAddress target, InetAddress start, InetAddress end){
        byte[] targetOctets = target.getAddress();
        long targetLong = 0;
        for (byte octet : targetOctets) {
            targetLong <<= 8;
            targetLong |= octet & 0xff;
        }

        byte[] startOctets = start.getAddress();
        long startLong = 0;
        for (byte octet : startOctets) {
            startLong <<= 8;
            startLong |= octet & 0xff;
        }

        byte[] endOctets = end.getAddress();
        long endLong = 0;
        for (byte octet : endOctets) {
            endLong <<= 8;
            endLong |= octet & 0xff;
        }

        return startLong <= targetLong && targetLong <= endLong;

    }

}