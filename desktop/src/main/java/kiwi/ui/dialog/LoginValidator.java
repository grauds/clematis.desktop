package kiwi.ui.dialog;

public interface LoginValidator {

    boolean validate(String text, String s);

    void validationCancelled();
}
