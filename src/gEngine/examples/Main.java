package gEngine.examples;

import gEngine.SetupManager;

public class Main {
    public static void main(String[] args) {
        SetupManager.startVirtualClient("localhost", 8080);
//        SetupManager.startGame(AngryFlappy.class);
    }
}
