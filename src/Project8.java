
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javafx.application.Platform;

/**
 * CSCI 3510
 * Theory of Computation Project 8
 * 
 *  @author Daniel Swain Jr
 */
public class Project8 extends Application {

  private static final String[][] template = {
    {"a", "b", "c", "d"},
    {"+", "-", "*", "/"},
    {"(", ")", "CLR", "EXT"}
  };

  private final Map<String, Button> accelerators = new HashMap<>();

  private final SimpleStringProperty inputText = new SimpleStringProperty("");
  private final SimpleStringProperty stackText = new SimpleStringProperty("");
  private final SimpleStringProperty outputText = new SimpleStringProperty("");

  private final Stack<String> operand_stack = new Stack<>();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    final TextField input = new TextField();
    input.setStyle("-fx-background-color: aquamarine;");
    input.setAlignment(Pos.CENTER_RIGHT);
    input.setEditable(false);
    input.textProperty().bind(Bindings.format("%-2s", inputText));

    final TextField stack = new TextField();
    stack.setStyle("-fx-background-color: aquamarine;");
    stack.setAlignment(Pos.CENTER_RIGHT);
    stack.setEditable(false);
    stack.textProperty().bind(Bindings.format("%-2s", stackText));

    final TextField output = new TextField();
    output.setStyle("-fx-background-color: aquamarine;");
    output.setAlignment(Pos.CENTER_RIGHT);
    output.setEditable(false);
    output.textProperty().bind(Bindings.format("%-2s", outputText));

    final TilePane buttons = createButtons();

    stage.setTitle("Theory Calculator");
    stage.initStyle(StageStyle.UTILITY);
    stage.setResizable(false);
    stage.setScene(new Scene(createLayout(new TextField[]{input, stack, output}, buttons)));
    stage.show();
  }

  private VBox createLayout(TextField[] screens, TilePane buttons) {
    final VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);
    layout.setStyle("-fx-background-color: chocolate; -fx-padding: 20; -fx-font-size: 20;");
    layout.getChildren().setAll(screens[0], screens[1], screens[2], buttons);
    handleAccelerators(layout);
    screens[0].prefWidthProperty().bind(buttons.widthProperty());
    screens[1].prefWidthProperty().bind(buttons.widthProperty());
    screens[2].prefWidthProperty().bind(buttons.widthProperty());

    return layout;
  }

  private void handleAccelerators(VBox layout) {
    layout.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent keyEvent) {
        Button activated = accelerators.get(keyEvent.getText());
        if (activated != null) {
          activated.fire();
        }
      }
    });
  }

  private TilePane createButtons() {
    TilePane buttons = new TilePane();
    buttons.setVgap(7);
    buttons.setHgap(7);
    buttons.setPrefColumns(template[0].length);
    for (String[] r : template) {
      for (String s : r) {
        buttons.getChildren().add(createButton(s));
      }
    }
    return buttons;
  }

  private Button createButton(final String s) {
    Button button = makeStandardButton(s);

    if (s.matches("[a-d]")) {
      makeAlphaButton(s, button);
    } else {
      if (s.matches("[()]")) {
        makeParenthesesButton(s, button);
      } else if (s.matches("[-+*/]")) {
        makeOperandButton(s, button);
      } else if ("CLR".equals(s)) {
        makeClearButton(button);
      } else if ("EXT".equals(s)) {
        makeExitButton(button);
      }
    }

    return button;
  }

  private Button makeStandardButton(String s) {
    Button button = new Button(s);
    button.setStyle("-fx-base: beige;");
    accelerators.put(s, button);
    button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    return button;
  }

  private void makeParenthesesButton(final String s, Button button) {
    button.setStyle("-fx-base: lightgray;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        inputText.set(inputText.get() + s);

        switch (s) {
          case "(":
            operand_stack.push(s);
            stackText.set(getOperandStackString());
            break;
          case ")":
            while (!operand_stack.isEmpty()) {
              String op = operand_stack.pop();
              if (op.equals("(")) {
                break;
              }
              outputText.set(outputText.get() + op);
            }
            stackText.set(getOperandStackString());
            break;
        }
      }
    });
  }

  private void makeOperandButton(final String s, Button button) {
    button.setStyle("-fx-base: lightgray;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        inputText.set(inputText.get() + s);
        operand_stack.push(s);
        stackText.set(getOperandStackString());
      }
    });
  }

  private void makeAlphaButton(final String s, Button button) {
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        inputText.set(inputText.get() + s);
        outputText.set(outputText.get() + s);
      }
    });
  }

  private void makeClearButton(Button button) {
    button.setStyle("-fx-base: mistyrose;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        inputText.set("");
        stackText.set("");
        outputText.set("");
        operand_stack.clear();
      }
    });
  }

  private void makeExitButton(Button button) {
    button.setStyle("-fx-base: mistyrose;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        Platform.exit();
      }
    });
  }

  private String getOperandStackString() {
    String s = "";
    for (String a : operand_stack) {
      s += a;
    }
    return s;
  }
}
