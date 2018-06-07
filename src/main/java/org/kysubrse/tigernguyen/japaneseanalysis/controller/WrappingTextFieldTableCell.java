package org.kysubrse.tigernguyen.japaneseanalysis.controller;

import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.converter.DefaultStringConverter;

public class WrappingTextFieldTableCell<S> extends TextFieldTableCell<S, String> {

    private final Text cellText;

    public WrappingTextFieldTableCell() {
        super(new DefaultStringConverter());
        this.cellText = createText();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setGraphic(cellText);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (!isEmpty() && !isEditing()) {
            setGraphic(cellText);
        }
    }

    private Text createText() {
        Text text = new Text();
        text.wrappingWidthProperty().bind(widthProperty());
        text.textProperty().bind(itemProperty());
        return text;
    }
}