package dev.sunglasses.sunnyutils.modules.base;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<>();

    public static void register(Module module) {
        for (Module m : modules) {
            if (m.getClass() == module.getClass()) {
                // Module of this class already registered
                return;
            }
        }
        modules.add(module);
        System.out.println("Registering module: " + module.getName() + " -> " + module + " | list size: " + modules.size());
    }

    public static void registerAll(List<Module> moduleList) {
        for (Module m : moduleList) {
            register(m);
        }
    }


    public static List<Module> getModules() {
        return modules;
    }

    public static <T extends Module> T getModule(Class<T> c) {
        for (Module m : modules) {
            if (c.isInstance(m)) return c.cast(m);
        }
        return null;
    }
}
