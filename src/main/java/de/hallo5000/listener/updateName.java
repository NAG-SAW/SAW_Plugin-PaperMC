package de.hallo5000.listener;

import de.hallo5000.main.Main;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class updateName implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        String formattedName = Main.lp.getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData().getPrefix()
                                                                            .replace("&","§")+" §f"+p.getName();
        p.sendPlayerListHeader(Component.text("Welcome to the official SAW Minecraft Server!\n"));
        p.playerListName(Component.text(formattedName));
        p.setPlayerListOrder(Main.lp.getGroupManager().getGroup(Main.lp.getUserManager().getUser(p.getUniqueId()).getPrimaryGroup()).getWeight().orElse(0));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncChatEvent e){
        e.renderer((p, sourceDisplayName, message, viewer) -> {

            String formattedName = Main.lp.getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData().getPrefix()
                    .replace("&","§")+" §7"+p.getName();
            if(!(viewer instanceof  Player)) {
                return Component.text(Main.removeColorCodes(formattedName)+": ").append(message);
            }
            return Component.text(formattedName + ": §f").append(message);
        });
    }

    public static void onNodeMutate(NodeMutateEvent e){
        if(e.getTarget() instanceof Group) return;
        User u = (User) e.getTarget();
        Player p = Main.getPlugin(Main.class).getServer().getPlayer(u.getUniqueId());
        if(p == null || u.getCachedData().getMetaData().getPrefix() == null) return;
        p.playerListName(Component.text(
                u.getCachedData().getMetaData().getPrefix().replace("&", "§") + " §f" + p.getName()
        ));
        p.setPlayerListOrder(Main.lp.getGroupManager().getGroup(u.getPrimaryGroup()).getWeight().orElse(0));
    }
}
