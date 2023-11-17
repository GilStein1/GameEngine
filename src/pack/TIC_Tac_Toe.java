package pack;

import java.awt.*;

public class TIC_Tac_Toe extends GSetup{

    GButton[][] buttons;
    boolean[][] is;
    boolean turn = true;
    String winner = "";
    double time = -1.0;
    @Override
    public void initialize() {
        setTitle("X  O");
        setFrameIcon("איקס עיגול.png");
        setFrameSize(610,630);
        setResizable(false);
        buttons = new GButton[3][3];
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                buttons[i][j] = new GButton(600*(i + 1)/3 - 180, 600*(j + 1)/3 - 180,160,160);
                buttons[i][j].setFont(new Font(" ",Font.BOLD,40));
                addButton(buttons[i][j]);
            }
        }
        is = new boolean[3][3];
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                is[i][j] = true;
            }
        }
    }

    @Override
    public void execute() {

        if(!winner.equals("")) {
            setTitle(winner + " Is The Winner!");
        }

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(buttons[i][j].isPressed() && is[i][j] && winner.equals("")) {
                    buttons[i][j].setText(turn? "X" : "O");
                    is[i][j] = false;
                    turn = !turn;

                    if(!buttons[0][0].getText().equals("") && buttons[0][0].getText().equals(buttons[0][1].getText()) && buttons[0][0].getText().equals(buttons[0][2].getText())) {
                        winner = buttons[0][0].getText();
                        time = System.currentTimeMillis();
                    }
                    if(!buttons[1][0].getText().equals("") && buttons[1][0].getText().equals(buttons[1][1].getText()) && buttons[1][0].getText().equals(buttons[1][2].getText())) {
                        winner = buttons[1][0].getText();
                        time = System.currentTimeMillis();
                    }
                    if(!buttons[2][0].getText().equals("") && buttons[2][0].getText().equals(buttons[2][1].getText()) && buttons[2][0].getText().equals(buttons[2][2].getText())) {
                        winner = buttons[2][0].getText();
                        time = System.currentTimeMillis();
                    }
                    if(!buttons[0][0].getText().equals("") && buttons[0][0].getText().equals(buttons[1][0].getText()) && buttons[0][0].getText().equals(buttons[2][0].getText())) {
                        winner = buttons[0][0].getText();
                        time = System.currentTimeMillis();
                    }
                    if(!buttons[0][1].getText().equals("") && buttons[0][1].getText().equals(buttons[1][1].getText()) && buttons[0][1].getText().equals(buttons[2][1].getText())) {
                        winner = buttons[0][1].getText();
                        time = System.currentTimeMillis();
                    }
                    if(!buttons[0][2].getText().equals("") && buttons[0][2].getText().equals(buttons[1][2].getText()) && buttons[0][2].getText().equals(buttons[2][2].getText())) {
                        winner = buttons[0][2].getText();
                        time = System.currentTimeMillis();
                    }
                    if(!buttons[0][0].getText().equals("") && buttons[0][0].getText().equals(buttons[1][1].getText()) && buttons[0][0].getText().equals(buttons[2][2].getText())) {
                        winner = buttons[0][0].getText();
                        time = System.currentTimeMillis();
                    }
                    if(!buttons[0][2].getText().equals("") && buttons[0][2].getText().equals(buttons[1][1].getText()) && buttons[0][2].getText().equals(buttons[2][0].getText())) {
                        winner = buttons[0][2].getText();
                        time = System.currentTimeMillis();
                    }
                    boolean tie = false;
                    for(int o = 0; o < 3; o++) {
                        for(int p = 0; p < 3; p++) {
                            tie = !(winner.equals("") && !(tie || is[o][p]));
                        }
                    }
                    if(!tie) {
                        winner = "No One";
                        time = System.currentTimeMillis();
                    }

                }
            }
        }

    }

    @Override
    public void lastFunction() {

    }

    @Override
    public boolean end() {

        return (time > -1 && System.currentTimeMillis() - time > 5000);


    }
}
