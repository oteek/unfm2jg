import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * Developer console for Need For Madness
 *
 * @author oteek
 */
public class DevTool {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField inputField;

    public boolean godmode = false;

    public DevTool(CheckPoints checkpoints, Madness madness[], xtGraphics xt) {

        frame = new JFrame("UNFM2 Developer Console");
        textArea = new JTextArea();
        inputField = new JTextField();

        textArea.setEditable(false);
        textArea.setLineWrap(true);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = inputField.getText();
                executeCommand(command, checkpoints, madness, xt);
                inputField.setText("");
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(inputField, BorderLayout.SOUTH);

        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    public void showConsole() {
        frame.setVisible(true);
    }

    public void print(String s) {
        textArea.append(s + "\n");
    }

    private void executeCommand(String command, CheckPoints checkpoints, Madness madness[], xtGraphics xt) {
        textArea.append("> " + command + "\n");

        String[] parts = command.split(" ");
        String commandName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        switch (commandName) {
            case "nplayers":
                if (args.length == 1) {
                    try {
                        int nplayers = Integer.parseInt(args[0]);
                        if (GameSparker.gameStateID > 1) {
                            if ((nplayers >= 1 && nplayers <= 51)) {
                                GameFacts.numberOfPlayers = nplayers;
                                print("Number of players set to " + nplayers + ".\n");
                            } else {
                                print("Player number cannot be less than 1 or greater than 51.\n");
                            }
                        } else {
                            print("This command only works in menus and before stage select.\n");
                        }
                    } catch (NumberFormatException e) {
                        print("Invalid number of players.\n");
                    }
                } else {
                    print("nplayers is " + GameFacts.numberOfPlayers + "\nUsage: nplayers <1-51>\n");
                }
                break;
            case "fix":
                if (args.length == 1) {
                    try {
                        int n = Integer.parseInt(args[0]);
                        if (GameSparker.gameStateID == 0) {
                            madness[n].devFixCar();
                            print("Car " + n + " fixed");
                        } else {
                            print("This command only works in game.\n");
                        }
                    } catch (NumberFormatException e) {
                        print("Invalid argument.\n");
                    }
                } else {
                    print("Usage: fix <n>\n");
                }
                break;
            case "god":
                if (GameSparker.gameStateID == 0) {
                    int oldclrad = madness[0].stat.clrad;
                    if (!godmode) {
                        madness[0].stat.clrad = 0;
                        print("Godmode ON");
                        godmode = true;
                    } else {
                        madness[0].stat.clrad = oldclrad;
                        print("Godmode OFF");
                        godmode = false;
                    }
                } else {
                    print("This command only works in game.\n");
                }
                break;
            case "unlocked":
                if (args.length == 1) {
                    try {
                        int n = Integer.parseInt(args[0]);
                        xt.unlocked = n;
                        print("xtGraphics.unlocked set to " + n);
                    } catch (NumberFormatException e) {
                        print("Invalid argument.\n");
                    }
                } else {
                    print("xtGraphics.unlocked is " + xt.unlocked + "\nUsage: unlocked <n>\n");
                }
                break;
            case "status":
                print("Game State: " + GameSparker.gameState + "\n");
                break;
            case "clear":
                textArea.setText("");
                break;
            default:
                print("Unknown command: " + commandName + "\n");
                break;
        }
    }
    // public static void main(String[] args) {
    //     DevTool console = new DevTool();
    //     console.showConsole();
    // }
}
