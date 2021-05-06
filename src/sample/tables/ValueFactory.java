package sample.tables;

import javafx.event.ActionEvent;
import javafx.scene.control.Spinner;

public class ValueFactory {
    private final int max;

    public ValueFactory(int max) {
        this.max = max;
    }

    public int increment(int curr, int steps) {
        if (curr != max) {
            curr += steps;
        } else {
            curr = 0;
        }
        return curr;
    }

    public int decrement(int curr, int steps) {
        if (curr != 0) {
            curr -= steps;
        } else {
            curr = max;
        }
        return curr;
    }

    public void onAction(ActionEvent event, Spinner<Integer> spinner) {
        Integer oldValue = spinner.getValue();
        String text = spinner.getEditor().getText();
        Integer newValue;
        try {
            newValue = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            newValue = oldValue;
        }
        if (newValue > max || newValue < 0) {
            newValue = oldValue;
        }
        spinner.getEditor().setText(newValue.toString());
        spinner.getValueFactory().setValue(newValue);
    }
}
