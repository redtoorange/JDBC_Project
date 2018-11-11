package view;

import db.Employee;
import db.Factory;
import db.LengthException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class MainViewController {
    private AppState currentState = AppState.CLEAR;
    private Employee currentEmployeeData;

    private ApplicationController controller;
    @FXML
    private TextField fNameField;
    @FXML
    private TextField lNameField;
    @FXML
    private TextField salaryField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField stateField;
    @FXML
    private TextField zipField;
    @FXML
    private TextField revNumField;
    @FXML
    private TextField empIDField;
    @FXML
    private Button saveButton;
    @FXML
    private Button loadButton;
    @FXML
    private Button clearButton;

    public void setController(ApplicationController controller) {
        this.controller = controller;
    }

    @FXML
    public void initialize() {
        installValidators();

        saveButton.setOnAction(this::onSaveClicked);
        loadButton.setOnAction(this::onLoadClicked);
        clearButton.setOnAction(this::onClearClicked);

        saveButton.setDisable(true);
    }

    /**
     * Attempt to write the current textfield data to the DB.
     *
     * @param event unused.
     */
    private void onSaveClicked(ActionEvent event) {
        Employee newEmployeeData = getEmployeeFromFields();

        if (newEmployeeData != null) {
            System.out.println( newEmployeeData );
            System.out.println(currentEmployeeData);
            if(currentEmployeeData.equals(newEmployeeData)){
                displayMessage("No Changes", "Not Data Changes", "The data in the form has not been altered, ignoring save request.");
                return;
            }
            else{
                String result = Factory.saveEmployee(newEmployeeData);

                switch (result) {
                    case "SUCCESS":
                        displayMessage("Success", "Data Saved Successfully", "Your data was successfully written to the database.  Revision number and fields have been updated.");
                        onLoadClicked(null);
                        break;
                    case "REVNUM_ERROR":
                        displayError("Revision Error", "Revision number mismatch from current DB revision number, updating local state.");
                        onLoadClicked(null);
                        break;
                    default:
                        displayError("Error Writing to DB", "Unhandled Error: " + result);
                        onClearClicked(null);
                        break;
                }
            }
        }
    }

    private Employee getEmployeeFromFields() {
        Employee employeeData = null;

        try {
            employeeData = new Employee(
                    empIDField.getText(),
                    lNameField.getText(),
                    fNameField.getText(),
                    Integer.parseInt(salaryField.getText()),
                    streetField.getText(),
                    cityField.getText(),
                    stateField.getText(),
                    zipField.getText(),
                    Integer.parseInt(revNumField.getText())
            );

        } catch (NumberFormatException nfe) {
            displayError("Not a Number", "Salary is not a valid number.");
        } catch (LengthException le) {
            displayError("Field Length", "One or more fields are of invalid length.");
        } catch (Exception e) {
            displayError("Unknown Error", "There has been an unknown error.  No changes will be written.");
        }

        return employeeData;
    }

    /**
     * Load/Reload an employee's data from the DB and display it in the textfields.
     *
     * @param event unused.
     */
    private void onLoadClicked(ActionEvent event) {
        String text = empIDField.getText();
        if (!text.isEmpty()) {
            currentEmployeeData = Factory.getEmployee(text);
            if (currentEmployeeData != null) {
                displayEmployee(currentEmployeeData);
                changeButtonState(AppState.LOADED);
            } else {
                displayError("Employee Not Found", "There was an error locating the specified employee.");
            }
        } else {
            displayError("Employee ID Empty", "Please specify the employee's ID.");
        }
    }

    /**
     * Reset the textfields and the application state.
     *
     * @param event unused.
     */
    private void onClearClicked(ActionEvent event) {
        changeButtonState(AppState.CLEAR);

        currentEmployeeData = null;
        fNameField.setText("");
        lNameField.setText("");
        salaryField.setText("");
        streetField.setText("");
        cityField.setText("");
        stateField.setText("");
        zipField.setText("");
        revNumField.setText("");
    }

    /**
     * Populate the textfields of the window with the Employee's data.
     *
     * @param employee Source to read from.
     */
    public void displayEmployee(Employee employee) {
        empIDField.setText(employee.getEmpID());
        lNameField.setText(employee.getlName());
        fNameField.setText(employee.getfName());
        salaryField.setText(Integer.toString(employee.getSalary()));
        streetField.setText(employee.getStreet());
        cityField.setText(employee.getCity());
        stateField.setText(employee.getState());
        zipField.setText(employee.getZip());
        revNumField.setText(Integer.toString(employee.getRevnum()));
    }

    /**
     * Helper for displaying an error popup to the user.
     *
     * @param header  Header of the content.
     * @param message Body of the content.
     */
    private void displayError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }

    /**
     * Helper for displaying an information popup to the user.
     *
     * @param title   Title of the popup window.
     * @param header  Header of the content.
     * @param message Body of the content.
     */
    private void displayMessage(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Change the state of the application's DB interface buttons.
     *
     * @param newState State to change to.
     */
    private void changeButtonState(AppState newState) {
        if (newState == currentState)
            return;

        switch (newState) {
            case CLEAR:
                empIDField.setDisable(false);
                saveButton.setDisable(true);
                loadButton.setText("Load");
                break;
            case LOADED:
                empIDField.setDisable(true);
                saveButton.setDisable(false);
                loadButton.setText("Reload");
                break;
        }

        currentState = newState;
    }

    private void installValidators() {
        empIDField.setOnKeyTyped(event -> {
            if (empIDField.getText().length() >= Employee.EMPID_LENGTH)
                event.consume();
        });

        lNameField.setOnKeyTyped(event -> {
            if (lNameField.getText().length() >= Employee.LNAME_LENGTH)
                event.consume();
        });

        fNameField.setOnKeyTyped(event -> {
            if (fNameField.getText().length() >= Employee.FNAME_LENGTH)
                event.consume();
        });

        salaryField.setOnKeyTyped(event -> {
            if (!event.getCharacter().matches("[0-9]") || salaryField.getText().length() >= Employee.SALARY_LENGTH)
                event.consume();
        });

        streetField.setOnKeyTyped(event -> {
            if (streetField.getText().length() >= Employee.STREET_LENGTH)
                event.consume();
        });

        cityField.setOnKeyTyped(event -> {
            if (cityField.getText().length() >= Employee.CITY_LENGTH)
                event.consume();
        });

        stateField.setOnKeyTyped(event -> {
            if (stateField.getText().length() >= Employee.STATE_LENGTH)
                event.consume();
        });

        zipField.setOnKeyTyped(event -> {
            if (!(event.getCharacter().matches("[0-9]") && zipField.getText().length() < Employee.ZIP_LENGTH))
                event.consume();
        });
    }

    private enum AppState {
        CLEAR, LOADED
    }
}
