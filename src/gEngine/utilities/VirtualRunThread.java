package gEngine.utilities;

import gEngine.SetupManager;
import gEngine.VirtualGSetup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class VirtualRunThread extends Thread{

    public VirtualRunThread(Class<? extends VirtualGSetup> classOfSetup, int port) {
        super(() -> {
            try {
                Constructor<?> con = classOfSetup.getConstructor(int.class);
                con.newInstance(port);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                String[] nameParts = classOfSetup.getName().split("\\.");
//                throw new RuntimeException("error initializing VirtualGSetup - " + nameParts[nameParts.length-1]);
                throw new RuntimeException(e);
            }
        });
        start();
    }

}
