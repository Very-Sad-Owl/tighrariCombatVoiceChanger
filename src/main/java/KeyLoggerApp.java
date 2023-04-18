import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

public class KeyLoggerApp extends JFrame implements NativeKeyListener, NativeMouseListener {

    private static final long serialVersionUID = 1L;

    private KeyEventDispatcher dispatcher;
    private JComboBox<String> keyDropdown;
    private java.util.List<String> slotKeys;
    private JButton startButton;
    private JLabel statusLabel;
    private String selectedKey;
    private boolean listening = true;
    private boolean stopQ;
    private boolean stopE;
    boolean qCooldown = false;
    boolean eCooldown = false;
    boolean keyPressCooldown = false;
    boolean charIsActive = false;
    boolean isHold = false;
    SoundPlayer player;

    public KeyLoggerApp() {
        super("Key Logger App");

        // Create the dropdown menu
        player = new SoundPlayer();
        keyDropdown = new JComboBox<String>();
        keyDropdown.addItem("1");
        keyDropdown.addItem("2");
        keyDropdown.addItem("3");
        keyDropdown.addItem("4");
        slotKeys = new ArrayList<>();
        slotKeys.addAll(Arrays.asList("1", "2", "3", "4"));

        // Create the button to start logging
        startButton = new JButton("Start Logging");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (listening) {
                    stopListening();
                } else {
                    startListening();
                }
            }
        });

        // Create the status label
        statusLabel = new JLabel("Select a key to listen for");

        // Add the components to the frame
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(keyDropdown, BorderLayout.WEST);
        contentPane.add(startButton, BorderLayout.CENTER);
        contentPane.add(statusLabel, BorderLayout.SOUTH);
        setContentPane(contentPane);

        // Set the frame properties
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.setEventDispatcher(new SwingDispatchService());
        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseListener(this);
    }

    private void startListening() {
        listening = true;
    }

    public void nativeMouseClicked(NativeMouseEvent e) {
        System.out.println("Mouse Clicked: " + e.getClickCount());
        System.out.println("Mouse: " + e.getButton());
        int mouse = e.getButton();
        if (mouse == 1 && !isHold && charIsActive) {
            int click = e.getClickCount();
            if (click > 5) {
                click = 1;
            }
            System.out.println("attack" + click);
            player.playRandomAttackLine(click);
        } else if (mouse == 1 && isHold && charIsActive) {
            System.out.println("hold");
            player.playRandomHoldLine();
        } else if (mouse == 2 && charIsActive) {
            player.playRandomSprintLine();
        }
    }

    public void nativeKeyPressed(NativeKeyEvent event) {
        System.out.println("native key pressed: " + event.getKeyCode() + " " + NativeKeyEvent.getKeyText(event.getKeyCode()));

        // Get the selected key from the dropdown menu
        selectedKey = (String) keyDropdown.getSelectedItem();
        if (listening) {
            // Update the status label
            statusLabel.setText("Listening for key " + selectedKey);
        }

        if (event.getID() == 2401) {
            setPressCooldown();
            if (NativeKeyEvent.getKeyText(event.getKeyCode()).equals(selectedKey)) {
                // Log the selected key press
                System.out.println("Slot " + selectedKey + " selected");
                charIsActive = true;

                // Remove the key binding for the selected key
                listening = false;
                // Start listening for Q, E, and LMB
                statusLabel.setText("Listening for Q, E, and LMB");
            //switched to another slot
            } else if (!NativeKeyEvent.getKeyText(event.getKeyCode()).equals(selectedKey)
                    && slotKeys.contains(NativeKeyEvent.getKeyText(event.getKeyCode())) && charIsActive) {
                charIsActive = false;
                listening = true;
                System.out.println("Switched to another character. Listen to " + selectedKey + " again.");
                // Update the status label
                statusLabel.setText("Listening for key " + selectedKey);
            } else if (event.getKeyCode() == 16 && charIsActive) {
                if (qCooldown) {
                    // Cooldown is active, return without logging
                    System.out.println("Q is on cooldown");
                } else {
                    // Log the Q key press
                    System.out.println("Q key pressed");
                    player.playRandomBurstLine();

                    // Set the qCooldown flag to true and start the cooldown timer
                    qCooldown = true;
                    new Timer(12000, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            qCooldown = false;
                        }
                    }).start();
                }
            } else if (event.getKeyCode() == 18 && charIsActive) {
                if (eCooldown) {
                    // Cooldown is active, return without logging
                    System.out.println("E is on cooldown");
                } else {
                    // Log the Q key press
                    System.out.println("E key pressed");
                    player.playRandomSkillLine();

                    // Set the qCooldown flag to true and start the cooldown timer
                    eCooldown = true;
                    new Timer(12000, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            eCooldown = false;
                        }
                    }).start();
                }
            } else if (NativeKeyEvent.getKeyText(event.getKeyCode()).equals("R") && charIsActive) {
                isHold = !isHold;
                System.out.println("Hold active: " + isHold);
            } else if (event.getKeyCode() == 57 && charIsActive) { //space
                player.playRandomJumpLine();
            }
        }
    }

    private void stopListening() {
        // Update the status label
        statusLabel.setText("Select a key to listen for");

        // Set the listening flag to false
        listening = false;

        // Reset the stop flags for Q and E
        stopQ = false;
        stopE = false;

        // Remove all key bindings
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this.dispatcher);

    }

    private void setPressCooldown() {
        if (!keyPressCooldown) {
            keyPressCooldown = true;
            new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    keyPressCooldown = false;
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        new KeyLoggerApp();
    }
}
