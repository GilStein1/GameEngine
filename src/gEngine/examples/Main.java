package gEngine.examples;

import gEngine.SetupManager;

public class Main {
    public static void main(String[] args) {
//        SetupManager.startGame(Jello.class);
//        SetupManager.startVirtualClient("192.168.0.218", 8080);
        SetupManager.startVirtualClient("localhost", 8080);
    }
}
