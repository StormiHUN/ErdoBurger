module com.example.erdoburger {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.erdoburger to javafx.fxml;
    exports com.example.erdoburger;
}