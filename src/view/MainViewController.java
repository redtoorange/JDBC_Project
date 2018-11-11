package view;

import db.Employee;
import db.Factory;
import db.LengthException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller class for the MainView.  This class handles communication with the DB through the Factory class.  The
 * only data model manipulated is an Employee.  Basic flow is the user specifies an EmployeeID, which is used to query
 * the DB.  The data is brought back as an Employee object then parsed into the View's textfields.  The user modifies
 * the texrfields and clicks save, which will send the data back to the DB as an update.
 */
public class MainViewController {
    private AppState currentState = AppState.CLEAR;
    private Employee currentEmployeeData;
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

    /**
     * Setup the GUI for first use.
     */
    @FXML
    public void initialize() {
        installValidators();

        saveButton.setOnAction( this::onSaveClicked );
        loadButton.setOnAction( this::onLoadClicked );
        clearButton.setOnAction( this::onClearClicked );

        saveButton.setDisable( true );
    }

    /**
     * Attempt to write the current textfield data to the DB.
     * @param event unused.
     */
    private void onSaveClicked( ActionEvent event ) {
        Employee newEmployeeData = getEmployeeFromFields();

        // If we have a valid employee try to save it
        if( newEmployeeData != null ) {
            // There were no changes, should ignore save request
            if( currentEmployeeData.equals( newEmployeeData ) ) {
                displayMessage( "No Changes", "Not Data Changes", "The data in the form has not been altered, ignoring save request." );
                return;
            }

            // Have the factory attempt to write the new data
            else {
                String result = Factory.saveEmployee( newEmployeeData );

                switch( result ) {
                    case "SUCCESS":
                        displayMessage( "Success", "Data Saved Successfully", "Your data was successfully written to the database.  Revision number and fields have been updated." );
                        onLoadClicked( null );
                        break;
                    case "REVNUM_ERROR":
                        displayError( "Revision Error", "Revision number mismatch from current DB revision number, updating local state." );
                        onLoadClicked( null );
                        break;
                    default: // Handle all other SQL errors
                        displayError( "Error Writing to DB", "Unhandled Error: " + result );
                        onClearClicked( null );
                        break;
                }
            }
        }
    }

    /**
     * Parse the textfields of the form into a new Employee object.
     * @return an Employee if all is well, null otherwise.
     */
    private Employee getEmployeeFromFields() {
        Employee employeeData = null;

        try {
            employeeData = new Employee(
                    empIDField.getText(),
                    lNameField.getText(),
                    fNameField.getText(),
                    Integer.parseInt( salaryField.getText() ),
                    streetField.getText(),
                    cityField.getText(),
                    stateField.getText(),
                    zipField.getText(),
                    Integer.parseInt( revNumField.getText() )
            );

        }
        catch( NumberFormatException nfe ) {
            displayError( "Not a Number", "Salary is not a valid number." );
        }
        catch( LengthException le ) {
            displayError( "Field Length", "One or more fields are of invalid length." );
        }
        catch( Exception e ) {
            displayError( "Unknown Error", "There has been an unknown error.  No changes will be written." );
        }

        return employeeData;
    }

    /**
     * Load/Reload an employee's data from the DB and display it in the textfields.
     * @param event unused.
     */
    private void onLoadClicked( ActionEvent event ) {
        String text = empIDField.getText();
        if( !text.isEmpty() ) {
            currentEmployeeData = Factory.getEmployee( text );
            if( currentEmployeeData != null ) {
                displayEmployee( currentEmployeeData );
                changeButtonState( AppState.LOADED );
            }
            else {
                displayError( "Employee Not Found", "There was an error locating the specified employee." );
            }
        }
        else {
            displayError( "Employee ID Empty", "Please specify the employee's ID." );
        }
    }

    /**
     * Reset the textfields and the application state.
     * @param event unused.
     */
    private void onClearClicked( ActionEvent event ) {
        changeButtonState( AppState.CLEAR );

        currentEmployeeData = null;
        fNameField.setText( "" );
        lNameField.setText( "" );
        salaryField.setText( "" );
        streetField.setText( "" );
        cityField.setText( "" );
        stateField.setText( "" );
        zipField.setText( "" );
        revNumField.setText( "" );
    }

    /**
     * Populate the textfields of the window with the Employee's data.
     * @param employee Source to read from.
     */
    public void displayEmployee( Employee employee ) {
        empIDField.setText( employee.getEmpID() );
        lNameField.setText( employee.getlName() );
        fNameField.setText( employee.getfName() );
        salaryField.setText( Integer.toString( employee.getSalary() ) );
        streetField.setText( employee.getStreet() );
        cityField.setText( employee.getCity() );
        stateField.setText( employee.getState() );
        zipField.setText( employee.getZip() );
        revNumField.setText( Integer.toString( employee.getRevnum() ) );
    }

    /**
     * Helper for displaying an error popup to the user.
     * @param header  Header of the content.
     * @param message Body of the content.
     */
    private void displayError( String header, String message ) {
        Alert alert = new Alert( Alert.AlertType.ERROR );

        alert.setTitle( "Error" );
        alert.setHeaderText( header );
        alert.setContentText( message );

        alert.showAndWait();
    }

    /**
     * Helper for displaying an information popup to the user.
     * @param title   Title of the popup window.
     * @param header  Header of the content.
     * @param message Body of the content.
     */
    private void displayMessage( String title, String header, String message ) {
        Alert alert = new Alert( Alert.AlertType.INFORMATION );
        alert.setTitle( title );
        alert.setHeaderText( header );
        alert.setContentText( message );
        alert.showAndWait();
    }

    /**
     * Change the state of the application's DB interface buttons.
     * @param newState State to change to.
     */
    private void changeButtonState( AppState newState ) {
        if( newState == currentState ) {
            return;
        }

        switch( newState ) {
            case CLEAR:
                empIDField.setDisable( false );
                saveButton.setDisable( true );
                loadButton.setText( "Load" );
                break;
            case LOADED:
                empIDField.setDisable( true );
                saveButton.setDisable( false );
                loadButton.setText( "Reload" );
                break;
        }

        currentState = newState;
    }

    /**
     * Install some very simple length restrictions to the text fields.  For fields that should only take a number,
     * disallow letter input.
     */
    private void installValidators() {
        empIDField.setOnKeyTyped( event -> {
            if( empIDField.getText().length() >= Employee.EMPID_LENGTH ) {
                event.consume();
            }
        } );

        lNameField.setOnKeyTyped( event -> {
            if( lNameField.getText().length() >= Employee.LNAME_LENGTH ) {
                event.consume();
            }
        } );

        fNameField.setOnKeyTyped( event -> {
            if( fNameField.getText().length() >= Employee.FNAME_LENGTH ) {
                event.consume();
            }
        } );

        salaryField.setOnKeyTyped( event -> {
            if( !event.getCharacter().matches( "[0-9]" ) || salaryField.getText().length() >= Employee.SALARY_LENGTH ) {
                event.consume();
            }
        } );

        streetField.setOnKeyTyped( event -> {
            if( streetField.getText().length() >= Employee.STREET_LENGTH ) {
                event.consume();
            }
        } );

        cityField.setOnKeyTyped( event -> {
            if( cityField.getText().length() >= Employee.CITY_LENGTH ) {
                event.consume();
            }
        } );

        stateField.setOnKeyTyped( event -> {
            if( stateField.getText().length() >= Employee.STATE_LENGTH ) {
                event.consume();
            }
        } );

        zipField.setOnKeyTyped( event -> {
            if( !(event.getCharacter().matches( "[0-9]" ) && zipField.getText().length() < Employee.ZIP_LENGTH) ) {
                event.consume();
            }
        } );
    }

    /**
     * Enum to represent the state if the application.  Right now only two states, either an Employee is loaded or one
     * is not.
     */
    private enum AppState {
        CLEAR, LOADED
    }


}
