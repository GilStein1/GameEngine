package gEngine.examples;

import gEngine.SetupManager;

public class Main {
    public static void main(String[] args) {
//        SetupManager.startGame(Jello.class);
        SetupManager.startVirtualClient("localhost", 8080);
    }
}
