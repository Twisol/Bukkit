package org.bukkit.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.plugin.Plugin;

public class PluginCommandYamlParser {
    
    public static List<Command> parse(Plugin plugin) {
        List<Command> pluginCmds = new ArrayList<Command>();
        Object object = plugin.getDescription().getCommands();
        if (object == null)
            return pluginCmds;            
        
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>)object;

        if (map != null) {
            for(Entry<String, Map<String, Object>> entry : map.entrySet()) {
                Object description = entry.getValue().get("description");
                Object usage = entry.getValue().get("usage");
                Object aliases = entry.getValue().get("aliases");
                Object classname = entry.getValue().get("class");
                
                Command newCmd = null;
                
                if (classname != null) {
                    try {
                        Class<?> klass = Class.forName(classname.toString(), true, plugin.getClass().getClassLoader());
                        Class<? extends Command> commandClass = klass.asSubclass(Command.class);
                        
                        Constructor<? extends Command> ctor = null;
                        try {
                            ctor = commandClass.getConstructor(String.class, Plugin.class);
                        } catch (NoSuchMethodException e) {
                            System.out.printf("Command class '%s' has no constructor that accepts (String, Plugin).%n", commandClass);
                        }
                        
                        try {
                            newCmd = ctor.newInstance(entry.getKey(), plugin);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } catch (ClassNotFoundException e) {
                        System.out.printf("Command class for '/%s' not found.%n", entry.getKey());
                    }
                } else {
                    newCmd = new PluginCommand(entry.getKey(), plugin);
                }
                
                if (description != null)
                    newCmd.setTooltip(description.toString());

                if (usage != null) {
                    newCmd.setUsage(usage.toString());
                }
                
                if (aliases != null) {
                    List<String> aliasList = new ArrayList<String>();
                    
                    for(String a : aliases.toString().split(",")) {
                            aliasList.add(a);
                    }
                        
                    newCmd.setAliases(aliasList);
                }
                
                pluginCmds.add(newCmd);
            }
        }
        return pluginCmds;
    }

}
