package de.bananaco.permissions.fornoobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;

public class ForNoobs {
	private final WorldManager wm = WorldManager.getInstance();
	private final JavaPlugin plugin;

	public ForNoobs(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public void addAll() {
		System.out.println("Adding to example files...");
		try {
			addDefaults(wm.getWorld(null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addDefaults(World world) throws Exception {
        List<String> regPerms = getNormalPermissions();
        List<String> opPerms = getOPPermissions();
		// Do the groups first
		String admin = "admin";
		String mod = "moderator";
		String def = world.getDefaultGroup();
		// Let's sort the permissions into shizzledizzle
		for(String perm : regPerms) {
			// Users now get all the nodes, they'll auto be true or false, based on what the plugin specified.
			world.getGroup(def).addPermission(perm, true);
            // Try to add some common ones to mods. This doesn't work well atm, because perms can be given op or not
            // which kinda exclusdes the poor moderators :/ But this is just a default!
			if(perm.contains(".ban") || perm.contains(".kick") || perm.contains(".mod") || perm.contains(".fly"))
				world.getGroup(mod).addPermission(perm, true);
		}
        // Thes perms were desginated by plugin developers for OPs.
        // We'll give them to the admins.
        for (String perm : opPerms) {
            world.getGroup(admin).addPermission(perm, true);
        }
		// admin
		world.getGroup(admin).addGroup(mod);
		world.getGroup(admin).addPermission("group."+mod, false);
		world.getGroup(admin).addPermission("group."+admin, true);
		world.getGroup(admin).setValue("prefix", "&5admin");
		// moderator
		world.getGroup(mod).addGroup(def);
		world.getGroup(mod).addPermission("group."+def, false);
		world.getGroup(mod).addPermission("group."+mod, true);
		world.getGroup(mod).setValue("prefix", "&7moderator");
		// default
		world.getGroup(def).addPermission("group."+def, true);
		world.getGroup(def).setValue("prefix", "&9user");
		// Now do some example users
		String user1 = "codename_B";
		String user2 = "Notch";
		String user3 = "pyraetos";
		// And set their groups
		// user1
		world.getUser(user1).getGroupsAsString().clear();
		world.getUser(user1).addGroup(admin);
		world.getUser(user1).setValue("prefix", "&8developer");
		// user2
		world.getUser(user2).getGroupsAsString().clear();
		world.getUser(user2).addGroup(mod);
		world.getUser(user2).setValue("prefix", "&8mojang");
		// user3
		world.getUser(user3).setValue("prefix", "&3helper");
		// Finally, save the changes
		world.save();
	}

    private List<String> getOPPermissions() {
   		List<String> regPerms = new ArrayList<String>();
   		for (Permission p : plugin.getServer().getPluginManager().getPermissions()) {
   			if (!p.getName().equals("*") && !p.getName().equals("*.*")) {
                // If the default was intended as false, this perm could harm people.
                if (p.getDefault() == PermissionDefault.FALSE ||  p.getDefault() == PermissionDefault.NOT_OP) {
                    regPerms.add("^" + p.getName());
                } else {
                    // PermissionDefault.FALSE or PermissionDefault.OP
                    regPerms.add(p.getName());
                }
            }
   		}
   		Collections.sort(regPerms, new Comparator<String>() {
   			public int compare(String a, String b) {
   				return a.compareTo(b);
   			};
   		});
   		return regPerms;
   	}

    private List<String> getNormalPermissions() {
    		List<String> opPerms = new ArrayList<String>();
    		for (Permission p : plugin.getServer().getPluginManager().getPermissions()) {
    			if (!p.getName().equals("*") && !p.getName().equals("*.*")) {
                    // If the default was intended as false, this perm could harm people.
                    if (p.getDefault() == PermissionDefault.FALSE || p.getDefault() == PermissionDefault.OP) {
                        opPerms.add("^" + p.getName());
                    } else {
                        // PermissionDefault.FALSE or PermissionDefault.NOT_OP
                        opPerms.add(p.getName());
                    }
                }
    		}
    		Collections.sort(opPerms, new Comparator<String>() {
    			public int compare(String a, String b) {
    				return a.compareTo(b);
    			};
    		});
    		return opPerms;
    	}

}
