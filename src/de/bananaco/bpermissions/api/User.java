package de.bananaco.bpermissions.api;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.MapCalculable;
import de.bananaco.bpermissions.api.util.Permission;

/**
 * User extends MapCalculable to make permission checks in the HasPermission class considerably faster.
 * A slight increase in memory usage for a dramatic increase in speed is definately a worthwhile trade-off.
 */
public class User extends MapCalculable {
	
	private World w;
	private WorldManager wm = WorldManager.getInstance();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public User(String name, List<String> groups, Set<Permission> permissions,
			String world, World w) {
		super(name, groups == null ? new HashSet() : new HashSet(groups),
				permissions, world);
		this.w = w;
	}

	@Override
	public CalculableType getType() {
		return CalculableType.USER;
	}
	
	public boolean hasPermission(String node) {
		node = node.toLowerCase();
		boolean allowed = internalHasPermission(node);
		return allowed;
	}
	
	private boolean internalHasPermission(String node) {
		Map<String, Boolean> perms = getMappedPermissions();
		
		if(perms.containsKey(node))
			return perms.get(node);
		
		String permission = node;
		int index = permission.lastIndexOf('.');
		while (index >= 0) {
			permission = permission.substring(0, index);
			String wildcard = permission + ".*";
			if(perms.containsKey(wildcard))
				return perms.get(wildcard);
			index = permission.lastIndexOf('.');
		}
		if(perms.containsKey("*"))
			return perms.get("*");
		return false;
	}
	
	/*
	 * These methods are added
	 * to allow auto-saving of
	 * the World on any changes
	 */
	
	@Override
	public void addGroup(String group) {
		super.addGroup(group);
		setDirty(true);
		if(wm.getAutoSave())
			w.save();
	}

	@Override
	public void removeGroup(String group) {
		super.removeGroup(group);
		setDirty(true);
		if(wm.getAutoSave())
			w.save();
	}

	@Override
	public void addPermission(String permission, boolean isTrue) {
		super.addPermission(permission, isTrue);
		setDirty(true);
		if(wm.getAutoSave())
			w.save();
	}

	@Override
	public void removePermission(String permission) {
		super.removePermission(permission);
		setDirty(true);
		if(wm.getAutoSave())
			w.save();
	}

	@Override
	public void setValue(String key, String value) {
		super.setValue(key, value);
		setDirty(true);
		if(wm.getAutoSave())
			w.save();
	}

	@Override
	protected World getWorldObject() {
		return w;
	}

}
