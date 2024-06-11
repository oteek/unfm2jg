import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Developer console for Need For Madness
 *
 * @author oteek
 */
public class DevTool {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField inputField;
    private List<String> commandHistory;
    private int historyIndex;

    public boolean godmode = false;
    public int oldclrad;

    public DevTool(CheckPoints checkpoints, Madness madness[], xtGraphics xt) {
        commandHistory = new ArrayList<>();
        historyIndex = -1;

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
                if (!command.trim().isEmpty()) {
                    commandHistory.add(command);
                    historyIndex = commandHistory.size();
                }
                inputField.setText("");
            }
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (historyIndex > 0) {
                        historyIndex--;
                        inputField.setText(commandHistory.get(historyIndex));
                    } else if (historyIndex == 0) {
                        inputField.setText(commandHistory.get(historyIndex));
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (historyIndex < commandHistory.size() - 1) {
                        historyIndex++;
                        inputField.setText(commandHistory.get(historyIndex));
                    } else if (historyIndex == commandHistory.size() - 1) {
                        inputField.setText("");
                    }
                }
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
                    if (!godmode) {
                        oldclrad = madness[0].stat.clrad;
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
            case "fase":
                if (args.length == 1) {
                    try {
                        int n = Integer.parseInt(args[0]);
                        xt.fase = n;
                        print("Set xtGraphics.fase to " + n);
                    } catch (NumberFormatException e) {
                        print("Invalid argument.\n");
                    }
                } else {
                    print("Usage: setfase <n>\n");
                }
                break;
            case "spawn_ai":
                if (args.length == 1) {
                    try {
                        int n = Integer.parseInt(args[0]);
                        if (GameSparker.gameStateID == 0) {
                            if ((n >= 0 && n <= GameFacts.numberOfCars - 1)) {
                                ContO aconto[] = new ContO[GameSparker.ContosCount];
                                ContO aconto1[] = new ContO[3000];
                                int i1 = 0;
                                for (int i = 0; i < GameFacts.numberOfPlayers; i++) {
                                    i1 = i;
                                }
                                i1 += 1;
                                //if (madness[i1].newcar) {
                                    int j5 = aconto1[i1].xz;
                                    int j6 = aconto1[i1].xy;
                                    int l8 = aconto1[i1].zy;
                                    aconto1[i1] = new ContO(aconto[n], aconto1[i1].x, aconto1[i1].y, aconto1[i1].z,
                                            0);
                                    aconto1[i1].xz = j5;
                                    aconto1[i1].xy = j6;
                                    aconto1[i1].zy = l8;
                                    //madness[i1].newcar = false;
                                //}
                                print("Spawned " + xt.names[xt.sc[n]]);
                            } else {
                                print("Invalid car number.\n");
                            }
                        } else {
                            print("This command only works in game.\n");
                        }
                    } catch (NumberFormatException e) {
                        print("Invalid argument.\n");
                    }
                }
                break;
            case "status":
                print("Game State: " + GameSparker.gameState);
                break;
            case "clear":
                textArea.setText("");
                break;
            default:
                print("Unknown command: " + commandName);
                break;
        }
    }
    // public static void main(String[] args) {
    //     DevTool console = new DevTool();
    //     console.showConsole();
    // }
}
