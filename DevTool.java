import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public boolean debugstat = false;
    public int oldclrad;

    private Map<String, String> commandDescriptions; // for help command

    public DevTool(CheckPoints checkpoints, Madness madness[], ContO conto[], ContO conto1[], xtGraphics xt) {
        commandHistory = new ArrayList<>();
        historyIndex = -1;

        commandDescriptions = new HashMap<>();
        populateCommandDescriptions();

        frame = new JFrame("UNFM2 Developer Console");
        textArea = new JTextArea();
        inputField = new JTextField();

        textArea.setEditable(false);
        textArea.setLineWrap(true);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = inputField.getText();
                executeCommand(command, checkpoints, madness, conto, conto1, xt);
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

    private void executeCommand(String command, CheckPoints checkpoints, Madness madness[], ContO conto[], ContO conto1[], xtGraphics xt) {
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
                                //GameFacts.numberOfPlayers = nplayers;
                                xt.nplayers_debug = true;
                                xt.nplayers_override = nplayers;
                                print("Numbers of players set to " + nplayers + ", overriden for all stages.");
                            } else if (nplayers == 0) {
                                xt.nplayers_debug = false;
                                print("Number of players are now determined by stage\n(xtGraphics, public void carspergame)");
                            } else {
                                print("Invalid player number.");
                            }
                        } else {
                            print("This command only works in menus and before stage select.");
                        }
                    } catch (NumberFormatException e) {
                        print("Invalid number of players.\n");
                    }
                } else {
                    print("nplayers is " + GameFacts.numberOfPlayers + "\nUsage: nplayers <0-51>");
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
                            print("This command only works in game.");
                        }
                    } catch (NumberFormatException e) {
                        print("Invalid argument.");
                    }
                } else {
                    print("Usage: fix <n>");
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
                    print("This command only works in game.");
                }
                break;
            case "spectate":
                if (args.length == 1) {
                    try {
                        int n = Integer.parseInt(args[0]);
                        if (GameSparker.gameStateID == 0) {
                            if (n > 0 && n < GameFacts.numberOfPlayers) {
                                xt.spectate = n;
                                print("Spectating [AI]" + xt.names[xt.sc[n]]);
                            } else if (n == 0) {
                                xt.spectate = n;
                                print("Spectating [Player]" + xt.names[xt.sc[n]]);
                            } else {
                                print("Invalid player ID.");
                            }
                        } else {
                            print("This command only works in game.");
                        }
                    } catch (NumberFormatException e) {
                        print("Invalid argument.");
                    }
                } else {
                    print("Usage: spectate <n>");
                }
                break;
            case "debug":
                if (!debugstat) {
                    xt.debugmode = true;
                    print("Debug mode enabled.");
                    debugstat = true;
                } else {
                    xt.debugmode = false;
                    print("Debug mode disabled");
                    godmode = false;
                }
                break;
            case "unlocked":
                if (args.length == 1) {
                    try {
                        int n = Integer.parseInt(args[0]);
                        xt.unlocked = n;
                        print("xtGraphics.unlocked set to " + n);
                    } catch (NumberFormatException e) {
                        print("Invalid argument.");
                    }
                } else {
                    print("xtGraphics.unlocked is " + xt.unlocked + "\nUsage: unlocked <n>");
                }
                break;
            case "fase":
                if (args.length == 1) {
                    try {
                        int n = Integer.parseInt(args[0]);
                        xt.fase = n;
                        print("Set xtGraphics.fase to " + n);
                    } catch (NumberFormatException e) {
                        print("Invalid argument.");
                    }
                } else {
                    print("Usage: fase <n>");
                }
                break;
            case "spawn_ai":
                if (GameSparker.gameStateID == 0) {

                    int n = GameFacts.numberOfPlayers + 1;

                    conto[n] = new ContO(conto1[xt.sc[n]], 0, 250 - conto1[xt.sc[n]].grat, -760 + ((n / 3) * 760), 0);
                    madness[n].reseto(xt.sc[n], conto[n], checkpoints);
                    print("Spawned " + xt.names[xt.sc[n]]);
                } else {
                    print("This command only works in game.");
                }
                break;
            case "nfm":
                if (args.length == 1) {
                    try {
                        int n = Integer.parseInt(args[0]);
                        if (GameSparker.gameStateID == 10) {
                            if ((n >= 1 && n <= 3)) {
                                xt.nfmmode = n;
                                if (n < 2) {
                                    print("Need For Madness " + n);
                                } else {
                                    print("Freeplay mode");
                                }
                            } else {
                                print("Prevented from creating a paradox.");
                            }
                        } else {
                            print("This command only works in main menu.");
                        }
                    } catch (NumberFormatException e) {
                        print("Invalid argument.");
                    }
                } else {
                    print("Usage: nfm <n>");
                }
                break;
            case "loadstage":
                if (args.length == 1) {
                    try {
                        String path = args[0];
                        if (GameSparker.gameStateID == 1) {
                            checkpoints.stage = -1;
                            xt.fase = 9001;
                            xt.nfmmode = 3;
                            GameSparker.loadStageCus = path;    //idk
                            print("Loading stage from: " + path + ".txt");
                        }
                    } catch (NumberFormatException e) {
                        print("Invalid argument.");
                    }
                } else {
                    print("Usage: loadstage <directory>");
                }
                break;
            case "stagesubdir":
                if (args.length == 1) {
                    try {
                        String sub = args[0];
                            GameSparker.stageSubDir = sub + "/";    //idk
                            xt.nfmmode = 3;
                            print("Set stage subdir to " + sub);
                    } catch (NumberFormatException e) {
                        print("Invalid argument.");
                    }
                } else {
                    print("Usage: stagesubdir <subdir>");
                }
                break;
            case "status":
                print("Game State: " + GameSparker.gameState);
                break;
            case "connect":
                if (args.length == 1) {
                    String sub = args[0];
                    try {
                        // Validate and parse IP and port
                        String regex = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})$";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(sub);

                        if (matcher.matches()) {
                            String ip = matcher.group(1);
                            int port = Integer.parseInt(matcher.group(2));

                            // Validate IP and port range
                            if (isValidIP(ip) && port >= 0 && port <= 65535) {
                                print("Connecting to " + ip + " on port " + port + "...");

                                try {
                                    
                                    xt.socket = new Socket(ip, port);
                                    print("Connected to the server");

                                    // Get the input stream to receive data from the server
                                    xt.serverresponse = new BufferedReader(new InputStreamReader(xt.socket.getInputStream()));

                                    // Read the message from the server
                                    String message = xt.serverresponse.readLine();
                                    print("Server says: " + message);

                                } catch (java.net.ConnectException e) {
                                    print(e.getMessage());
                                } catch (IOException e) {
                                    print("An error occurred:\n" + e.toString());
                                } finally {
                                    // Close the streams and sockets
                                    try {
                                        if (xt.serverresponse != null) xt.serverresponse.close();
                                        if (xt.socket != null) xt.socket.close();
                                    } catch (IOException e) {
                                        print("An error occurred:\n" + e.toString());
                                    }
                                }

                            } else {
                                print("Invalid IP address or port range.");
                            }
                        } else {
                            print("Invalid IP:Port format.");
                        }
                    } catch (NumberFormatException e) {
                        print("Invalid port number.");
                    }
                } else {
                    print("Usage: connect <ip:port>");
                }
                break;
            case "clear":
                textArea.setText("");
                break;
            case "help":
                if (args.length == 0) {
                    print("Available commands:");
                    for (String cmd : commandDescriptions.keySet()) {
                        print(cmd);
                    }
                } else if (args.length == 1) {
                    String helpCommand = args[0];
                    if (commandDescriptions.containsKey(helpCommand)) {
                        print(helpCommand + ": " + commandDescriptions.get(helpCommand));
                    } else {
                        print("No help available for unknown command: " + helpCommand);
                    }
                } else {
                    print("Usage: help <command>");
                }
                break;
            default:
                print("Unknown command: " + commandName);
                break;
        }
    }

    private boolean isValidIP(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            int num = Integer.parseInt(part);
            if (num < 0 || num > 255) {
                return false;
            }
        }
        return true;
    }
    
    private void populateCommandDescriptions() {
        commandDescriptions.put("nplayers", "Sets the number of players. Usage: nplayers <1-51>");
        commandDescriptions.put("fix", "Fixes the specified car. Usage: fix <n>");
        commandDescriptions.put("god", "Toggles god mode.");
        commandDescriptions.put("unlocked", "Sets the unlocked value in xtGraphics. Usage: unlocked <n>");
        commandDescriptions.put("fase", "Sets the phase in xtGraphics. Usage: fase <n>");
        commandDescriptions.put("spawn_ai", "Spawns an AI car (unimplemented).");
        commandDescriptions.put("nfm", "Sets the NFM mode. Usage: nfm <n>");
        commandDescriptions.put("loadstage", "Loads a stage from the specified path. Usage: loadstage <directory>");
        commandDescriptions.put("stagesubdir", "Sets the stage subdirectory. Usage: stagesubdir <subdir>");
        commandDescriptions.put("status", "Displays the current game state.");
        commandDescriptions.put("clear", "Clears the console.");
        commandDescriptions.put("connect", "Connects to a server. Usage: connect <ip:port>");
        commandDescriptions.put("help", "Displays help information. Usage: help <command>");
    }
}
