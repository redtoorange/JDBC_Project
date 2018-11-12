package db;

import java.util.Objects;

/** Employee is a purely data class that contains the column data of a row from the EMP table. */
public class Employee {
    // Database specific column length restrictions
    public static final int EMPID_LENGTH = 3;
    public static final int LNAME_LENGTH = 20;
    public static final int FNAME_LENGTH = 20;
    public static final int SALARY_LENGTH = 8;
    public static final int STREET_LENGTH = 30;
    public static final int CITY_LENGTH = 18;
    public static final int STATE_LENGTH = 2;
    public static final int ZIP_LENGTH = 5;
    public static final int REVNUM_LENGTH = 8;

    private String empID;
    private String lName;
    private String fName;
    private int salary;
    private String street;
    private String city;
    private String state;
    private String zip;
    private int revnum;

    public Employee( String empID, String lName, String fName, int salary, String street, String city, String state, String zip, int revnum ) throws LengthException {
        setEmpID( empID );
        setlName( lName );
        setfName( fName );
        setSalary( salary );
        setStreet( street );
        setCity( city );
        setState( state );
        setZip( zip );
        setRevnum( revnum );
    }

    @Override
    public int hashCode() {
        return Objects.hash( empID, lName, fName, salary, street, city, state, zip, revnum );
    }

    @Override
    public boolean equals( Object obj ) {
        boolean equal = false;

        if( obj instanceof Employee ) {
            Employee other = ( Employee ) obj;

            equal = (empID.equals( other.getEmpID() ) &&
                    lName.equals( other.getlName() ) &&
                    fName.equals( other.getfName() ) &&
                    salary == other.getSalary() &&
                    street.equals( other.getStreet() ) &&
                    city.equals( other.getCity() ) &&
                    state.equals( other.getState() ) &&
                    zip.equals( other.getZip() ) &&
                    revnum == other.getRevnum());
        }

        return equal;
    }

    @Override
    public String toString() {
        return "db.Employee{" +
                "empID='" + empID + '\'' +
                ", lName='" + lName + '\'' +
                ", fName='" + fName + '\'' +
                ", salary=" + salary +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", revnum=" + revnum +
                '}';
    }

    //-------------------------------------------------------------------------
    //--------------------------- Getters and Setters -------------------------
    //-------------------------------------------------------------------------
    public String getEmpID() {
        return empID;
    }

    public void setEmpID( String empID ) throws LengthException {
        if( empID.length() > EMPID_LENGTH ) {
            throw new LengthException();
        }

        this.empID = empID;
    }

    public String getlName() {
        return lName;
    }

    public void setlName( String lName ) throws LengthException {
        if( lName.length() > LNAME_LENGTH ) {
            throw new LengthException();
        }

        this.lName = lName;
    }

    public String getfName() {
        return fName;
    }

    public void setfName( String fName ) throws LengthException {
        if( fName.length() > FNAME_LENGTH ) {
            throw new LengthException();
        }

        this.fName = fName;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary( int salary ) throws LengthException {
        if( Integer.toString( salary ).length() > SALARY_LENGTH ) {
            throw new LengthException();
        }

        this.salary = salary;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet( String street ) throws LengthException {
        if( street.length() > STREET_LENGTH ) {
            throw new LengthException();
        }

        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity( String city ) throws LengthException {
        if( city.length() > CITY_LENGTH ) {
            throw new LengthException();
        }

        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState( String state ) throws LengthException {
        if( state.length() > STATE_LENGTH ) {
            throw new LengthException();
        }

        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip( String zip ) throws LengthException {
        if( zip.length() > ZIP_LENGTH ) {
            throw new LengthException();
        }

        this.zip = zip;
    }

    public int getRevnum() {
        return revnum;
    }

    public void setRevnum( int revnum ) throws LengthException {
        if( Integer.toString( revnum ).length() > REVNUM_LENGTH ) {
            throw new LengthException();
        }

        this.revnum = revnum;
    }
}
