package jhelp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;

/**
 * Class defines a process for all events what happens in client form.
 * @author <strong >Y.D.Zakovryashin</strong>, 2009
 * @version 1.0
 */
public class ClientListener extends WindowAdapter
        implements ActionListener, KeyListener, TextListener {

    private Client client;

    /**
     * Single constructor of the class.
     * @param client references to client form
     */
    public ClientListener(Client client) {
        this.client = client;
    }

    /**
     * Method for processing of {@link java.awt.event.ActionEvent} events.
     * @param e reference to {@link java.awt.event.ActionEvent} event what happens
     * @see java.awt.event.ActionEvent
     * @see java.awt.event.ActionListener
     */
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    /**
     * Method for processing of {@link java.awt.event.KeyEvent} event. The
     * method invokes in case a user pushes any keyboard button with typed symbol.
     * @param e reference to {@link java.awt.event.KeyEvent} event what happens
     * @see java.awt.event.KeyEvent
     * @see java.awt.event.KeyListener
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Method for processing of {@link java.awt.event.KeyEvent} event.
     * The method invokes in case a user pushes but not releases any keyboard
     * button.
     * @param e reference to {@link java.awt.event.KeyEvent} event what happens
     * @see java.awt.event.KeyEvent
     * @see java.awt.event.KeyListener
     */
    @Override
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Method for processing of {@link java.awt.event.KeyEvent} event. The
     * method invokes in case a user releases any keyboard button.
     * @param e reference to {@link java.awt.event.KeyEvent} event what happens
     * @see java.awt.event.KeyEvent
     * @see java.awt.event.KeyListener
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * This method are invoked when an object's text changed. This high-level
     * event is generated by an object (such as a TextComponent) when its text
     * changes.
     * @param e reference to {@link java.awt.event.TextEvent} event what happens
     * @see java.awt.event.TextEvent
     * @see java.awt.event.TextListener
     */
    @Override
    public void textValueChanged(TextEvent e) {
    }
}
