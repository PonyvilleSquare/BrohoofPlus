package com.brohoof.brohoofplus.bukkit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

class ItemNope implements Listener {
    private final List<String> projectile_names;
    private final List<String> tracer;
    private BrohoofPlusPlugin p;

    ItemNope(final BrohoofPlusPlugin p) {
        this.p = p;
        projectile_names = new ArrayList<String>();
        tracer = new ArrayList<String>();
        for (final String name : p.getConfig().getStringList("modules.itemnope.projectile.names"))
            projectile_names.add(name.toLowerCase());
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    void projectileLaunchEvent(final ProjectileLaunchEvent event) {
        if (event.getEntityType() == null)
            return;
        final String itemName = event.getEntityType().toString().toLowerCase();
        // If entity is in the restriction list
        if (projectile_names.contains(itemName)) {
            LivingEntity shooter = null;
            if (event.getEntity().getShooter() instanceof LivingEntity)
                shooter = (LivingEntity) event.getEntity().getShooter();
            // Worldspawn (Dispenser or something that is not living)
            if (shooter == null) {
                if (!p.getConfig().getBoolean("modules.itemnope.projectile.worldspawn_bypasses_restriction"))
                    event.setCancelled(true);
            }
            // NPC
            else if (!(shooter instanceof Player)) {
                if (!p.getConfig().getBoolean("modules.itemnope.projectile.npc_bypasses_restriction"))
                    event.setCancelled(true);
            }
            // Player
            else if (shooter instanceof Player) {
                final Player ply = (Player) shooter;
                if (!ply.hasPermission("itemnope.itemrestrict.bypass"))
                    event.setCancelled(true);
            }
        }
        if (!tracer.contains(itemName)) {
            tracer.add(itemName);
            LivingEntity shooter = null;
            if (event.getEntity().getShooter() instanceof LivingEntity)
                shooter = (LivingEntity) event.getEntity().getShooter();
            String responsible = "UNKNOWN";
            if (shooter == null)
                responsible = "WORLDSPAWN";
            else if (!(shooter instanceof Player))
                responsible = "(" + shooter.getClass().getName() + ") " + shooter.getType().getName();
            else if (shooter instanceof Player)
                responsible = "(Player) " + ((Player) shooter).getName();
            else
                responsible = "(" + shooter.getClass().getName() + ")";
            String extra = "";
            if (event.isCancelled())
                extra = "(CANCELLED) ";
            p.getLogger().info(extra + itemName + " has been thrown by " + responsible);
        }
    }
}