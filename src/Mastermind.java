import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class Mastermind {
    private final JFrame frame;
    private final String[] colors = {"Red", "Blue", "Green", "Yellow", "Orange", "Purple"};
    private final ArrayList<String> secretCode;
    private final ArrayList<JComboBox<String>> colorSelectors;
    private final JTextArea feedbackArea;

    private int attempts = 10;
    private final JLabel attemptsLabel;

    public Mastermind() {
        frame = new JFrame("Mastermind Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        secretCode = generateSecretCode();
        System.out.println(secretCode);

        JPanel colorSelectionPanel = new JPanel();
        colorSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        colorSelectors = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            JComboBox<String> comboBox = new JComboBox<>(colors);
            colorSelectors.add(comboBox);
            colorSelectionPanel.add(comboBox);
        }

        // Label for displaying attempts left
        attemptsLabel = new JLabel("Attempts left: " + attempts);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(colorSelectionPanel, BorderLayout.WEST);
        topPanel.add(attemptsLabel, BorderLayout.EAST);

        JButton submitButton = new JButton("Submit Guess");
        submitButton.setPreferredSize(new Dimension(150, 40));
        submitButton.addActionListener(new SubmitGuessListener());

        feedbackArea = new JTextArea();
        feedbackArea.setEditable(false);
        JScrollPane feedbackScrollPane = new JScrollPane(feedbackArea);
        feedbackScrollPane.setPreferredSize(new Dimension(600, 200));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(submitButton, BorderLayout.CENTER);
        frame.add(feedbackScrollPane, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private ArrayList<String> generateSecretCode() {
        Random random = new Random();
        ArrayList<String> code = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            code.add(colors[random.nextInt(colors.length)]);
        }
        return code;
    }

    private class SubmitGuessListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (attempts <= 0) {
                JOptionPane.showMessageDialog(frame, "No more attempts left! You lost. The correct code was: " + secretCode);
                frame.dispose();
                return;
            }

            ArrayList<String> guess = new ArrayList<>();
            for (JComboBox<String> selector : colorSelectors) {
                guess.add((String) selector.getSelectedItem());
            }

            Feedback feedback = checkGuess(guess);
            feedbackArea.append("Guess: " + guess + "\n");
            feedbackArea.append("Correct Positions: " + feedback.correctPosition + ", Correct Colors: " + feedback.correctColor + "\n");
            attempts--;

            attemptsLabel.setText("Attempts left: " + attempts);

            if (feedback.correctPosition == 4) {
                JOptionPane.showMessageDialog(frame, "Congratulations! You guessed the code!");
                frame.dispose();
            } else if (attempts == 0) {
                JOptionPane.showMessageDialog(frame, "No more attempts left! You lost. The correct code was: " + secretCode);
                frame.dispose();
            } else {
                feedbackArea.append("Attempts left: " + attempts + "\n\n");
            }
        }
    }

    private Feedback checkGuess(ArrayList<String> guess) {
        int correctPosition = 0;
        int correctColor = 0;

        ArrayList<String> secretCopy = new ArrayList<>(secretCode);
        ArrayList<String> guessCopy = new ArrayList<>(guess);

        for (int i = 0; i < guess.size(); i++) {
            if (guess.get(i).equals(secretCopy.get(i))) {
                correctPosition++;
                secretCopy.set(i, null);
                guessCopy.set(i, null);
            }
        }

        for (String color : guessCopy) {
            if (color != null && secretCopy.contains(color)) {
                correctColor++;
                secretCopy.remove(color);
            }
        }

        return new Feedback(correctPosition, correctColor);
    }

    private record Feedback(int correctPosition, int correctColor) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Mastermind::new);
    }
}