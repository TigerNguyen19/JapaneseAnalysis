package org.kysubrse.tigernguyen.japaneseanalysis.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.kysubrse.tigernguyen.japaneseanalysis.data.OutputData;
import org.kysubrse.tigernguyen.japaneseanalysis.function.JapaneseString;
import org.kysubrse.tigernguyen.japaneseanalysis.function.ReadWriteFile;
import org.kysubrse.tigernguyen.japaneseanalysis.function.Util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

public class MyController implements Initializable {

	private List<String> dictionaryKanji = ReadWriteFile.getDictionaryDataList(Util.FILENAMEKANJI);
	private List<String> dictionaryKata = ReadWriteFile.getDictionaryDataList(Util.FILENAMEKATA);
	@FXML
	private Button btnAnalysis;

	@FXML
	private Button btnLoadFile;

	@FXML
	private Button btnExport;

	@FXML
	private Button btnLearn;

	@FXML
	private TextArea taInputData;

	@FXML
	private TableView<OutputData> tbvOutputData;

	private ObservableList<OutputData> data = FXCollections.observableArrayList(new OutputData());

	final Tokenizer tokenizer = Tokenizer.builder().build();

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Create No Column
		TableColumn<OutputData, String> no //
				= new TableColumn<OutputData, String>("No");
		no.prefWidthProperty().bind(tbvOutputData.widthProperty().multiply(0.1));

		// Create JP Column
		TableColumn<OutputData, String> jp//
				= new TableColumn<OutputData, String>("JP");
		jp.prefWidthProperty().bind(tbvOutputData.widthProperty().multiply(0.1));
		
		// Create Katakana Column
		TableColumn<OutputData, String> kana//
				= new TableColumn<OutputData, String>("Kana");
		kana.prefWidthProperty().bind(tbvOutputData.widthProperty().multiply(0.1));

		// Create VN Column
		TableColumn<OutputData, String> vn = new TableColumn<OutputData, String>("VN");

		vn.setCellFactory(tc -> {
			TableCell<OutputData, String> cell = new TableCell<>();
			Text text = new Text();
			cell.setGraphic(text);
			cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
			text.wrappingWidthProperty().bind(vn.widthProperty());
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});

		// Set column size
		vn.prefWidthProperty().bind(tbvOutputData.widthProperty().multiply(0.65));
		no.setCellValueFactory(new PropertyValueFactory<>("No"));
		jp.setCellValueFactory(new PropertyValueFactory<>("strWordJP"));
		kana.setCellValueFactory(new PropertyValueFactory<>("kana"));
		vn.setCellValueFactory(new PropertyValueFactory<>("strWordVN"));
		
		setupJpColumn(jp);
		setupVnColumn(vn);
		setTableEditable();

		tbvOutputData.getColumns().addAll(no, jp, kana, vn);
		tbvOutputData.setItems(data);
	}
	// When user click on btnAnalysis
	// this method will be called.
	public void Analysis(ActionEvent event) {
		System.out.println("Button Clicked!");
		if (data.size() > 0)
			tbvOutputData.getItems().clear();

		/*
		 * TextInputDialog dialog = new TextInputDialog("walter");
		 * dialog.setTitle("Text Input Dialog");
		 * dialog.setHeaderText("Look, a Text Input Dialog");
		 * dialog.setContentText(tbvOutputData.getItems().get(0).strWordVN);
		 * dialog.show();
		 */

		if (taInputData.getText().length() > 0) {
			//List<OutputData> lstOutput = new ArrayList<OutputData>();
			String line = taInputData.getText();
			int i = 1;
			List<Token> result = tokenizer.tokenize(line);
			for (Token token : result) {
				// output = output + (token.getSurfaceForm() + "\n");
				OutputData outputData = new OutputData();
				outputData.setNo(i);
				outputData.setStrWordJP(token.getSurfaceForm().toString());
				outputData.setKana(token.getReading());
				if(token.getSurfaceForm().toString().replaceAll("\n", "").equals("")) {
					outputData.setStrWordJP(token.getReading());
					continue;
				}
				if (Util.isNotExist(data, outputData.getStrWordJP())) {
					if (JapaneseString.containsKanji(outputData.getStrWordJP())) {
						outputData.setStrWordVN(Util.findWord(dictionaryKanji, outputData.getStrWordJP()));
						data.add(outputData);
						i++;
					} else if (JapaneseString.containsKatakana(outputData.getStrWordJP())) {
						outputData.setStrWordVN(Util.findWordKata(dictionaryKata, outputData.getStrWordJP()));
						data.add(outputData);
						i++;
					}
				}
			}
		}
		tbvOutputData.setItems(data);

	}

	public void LoadFile(ActionEvent event) throws FileNotFoundException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("txt File", "txt"));
		fileChooser.setCurrentDirectory(new java.io.File("."));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int option = fileChooser.showDialog(null, "Select File");
		if (option == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			if (!f.isDirectory()) {
				taInputData.setText(ReadWriteFile.getDataTextFile(f.getPath()));
			}
		}
	}

	public void Export(ActionEvent event) {
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Choose a directory to save your file: ");
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xlsx");
		jfc.addChoosableFileFilter(filter);

		int returnValue = jfc.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			String filePath = jfc.getSelectedFile().getPath();
			if (!filePath.endsWith(".xlsx")) {
				filePath += ".xlsx";
			}
			ReadWriteFile.writeToExcell(tbvOutputData, filePath);
			JOptionPane.showMessageDialog(null, "Done", "Export Data To Excel File", 1);
		}
	}

	public void Learn(ActionEvent event) {
		//TODO
	}
	
	private void setupJpColumn(TableColumn<OutputData, String> jp) {
		jp.setCellFactory(TextFieldTableCell.forTableColumn());
		// updates the salary field on the OutputData object to the
		// committed value
		jp.setOnEditCommit(event -> {
			String value = event.getNewValue() != null
					? event.getNewValue() : event.getOldValue();
			((OutputData) event.getTableView().getItems()
					.get(event.getTablePosition().getRow())).setStrWordJP(value);
			if (JapaneseString.containsKanji(value)) {
				((OutputData) event.getTableView().getItems()
				.get(event.getTablePosition().getRow())).setStrWordVN(Util.findWord(dictionaryKanji, value));
			} else if (JapaneseString.containsKatakana(value)) {
				((OutputData) event.getTableView().getItems()
						.get(event.getTablePosition().getRow())).setStrWordVN(Util.findWordKata(dictionaryKata, value));
			} else {
				((OutputData) event.getTableView().getItems()
						.get(event.getTablePosition().getRow())).setKana("");
				((OutputData) event.getTableView().getItems()
						.get(event.getTablePosition().getRow())).setStrWordVN("");
			}
			List<Token> result = tokenizer.tokenize(value);
			if (result.size() > 0) {
				((OutputData) event.getTableView().getItems()
						.get(event.getTablePosition().getRow())).setKana(result.get(0).getReading());
			}
			tbvOutputData.refresh();
		});
	}
	


	private void setupVnColumn(TableColumn<OutputData, String> vn) {
		//vn.setCellFactory(WrappingTextFieldTableCell.forTableColumn());
		vn.setCellFactory(tc -> {
			WrappingTextFieldTableCell<OutputData> cell = new WrappingTextFieldTableCell<OutputData>();
	        return cell;
		});
		// updates the salary field on the OutputData object to the
		// committed value
		vn.setOnEditCommit(event -> {
			String value = event.getNewValue() != null
					? event.getNewValue() : event.getOldValue();
			((OutputData) event.getTableView().getItems()
					.get(event.getTablePosition().getRow())).setStrWordVN(value);			
			tbvOutputData.refresh();
		});
		
	}


	private void setTableEditable() {
		tbvOutputData.setEditable(true);
		// allows the individual cells to be selected
		tbvOutputData.getSelectionModel().cellSelectionEnabledProperty().set(true);
		// when character or numbers pressed it will start edit in editable
		// fields
		tbvOutputData.setOnKeyPressed(event -> {
			if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
				editFocusedCell();
			} else if (event.getCode() == KeyCode.RIGHT
					|| event.getCode() == KeyCode.TAB) {
				tbvOutputData.getSelectionModel().selectNext();
				event.consume();
			} else if (event.getCode() == KeyCode.LEFT) {
				// work around due to
				// TableView.getSelectionModel().selectPrevious() due to a bug
				// stopping it from working on
				// the first column in the last row of the table
				selectPrevious();
				event.consume();
			}
		});
	}
	
	private void selectPrevious() {
		if (tbvOutputData.getSelectionModel().isCellSelectionEnabled()) {
			// in cell selection mode, we have to wrap around, going from
			// right-to-left, and then wrapping to the end of the previous line
			@SuppressWarnings("unchecked")
			TablePosition<OutputData, ?> pos = tbvOutputData.getFocusModel()
					.getFocusedCell();
			if (pos.getColumn() - 1 >= 0) {
				// go to previous row
				tbvOutputData.getSelectionModel().select(pos.getRow(),
						getTableColumn(pos.getTableColumn(), -1));
			} else if (pos.getRow() < tbvOutputData.getItems().size()) {
				// wrap to end of previous row
				tbvOutputData.getSelectionModel().select(pos.getRow() - 1,
						tbvOutputData.getVisibleLeafColumn(
								tbvOutputData.getVisibleLeafColumns().size() - 1));
			}
		} else {
			int focusIndex = tbvOutputData.getFocusModel().getFocusedIndex();
			if (focusIndex == -1) {
				tbvOutputData.getSelectionModel().select(tbvOutputData.getItems().size() - 1);
			} else if (focusIndex > 0) {
				tbvOutputData.getSelectionModel().select(focusIndex - 1);
			}
		}
	}
	
	private TableColumn<OutputData, ?> getTableColumn(
			final TableColumn<OutputData, ?> column, int offset) {
		int columnIndex = tbvOutputData.getVisibleLeafIndex(column);
		int newColumnIndex = columnIndex + offset;
		return tbvOutputData.getVisibleLeafColumn(newColumnIndex);
	}


	@SuppressWarnings("unchecked")
	private void editFocusedCell() {
		final TablePosition<OutputData, ?> focusedCell = tbvOutputData
				.focusModelProperty().get().focusedCellProperty().get();
		tbvOutputData.edit(focusedCell.getRow(), focusedCell.getTableColumn());
	}


}
