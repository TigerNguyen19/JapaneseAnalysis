/*
 * Copyright (C) 2017 Tiger Nguyen (kysubrse.com)
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 */
package org.kysubrse.tigernguyen.japaneseanalysis;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
/**
 * Utility functions for handling Japanese characters and strings.
 *
 * @author TigerNguyen
 */
public class TokenizerForJapanese extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frame;

	private List<String> dictionaryKanji = ReadWriteFile.getDictionaryDataList(Util.FILENAMEKANJI);
	private List<String> dictionaryKata = ReadWriteFile.getDictionaryDataList(Util.FILENAMEKATA);

	public TokenizerForJapanese() {
		initialize();
	}

	private void initialize() {
		// ************Variable - START*************
		String[] columnNames = { "No", "WORD", "KANA", "JP->VN" };

		Object[][] data = null;
		final Tokenizer tokenizer = Tokenizer.builder().build();
		// ************Variable - END*************

		// ************GUI - START*************
		frame = new JFrame();
		frame.setBounds(20, 20, 1040, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setVisible(true);
		JScrollPane scrollPaneInput = new JScrollPane(textArea);
		scrollPaneInput.setSize(1000, 160);
		scrollPaneInput.setLocation(10, 40);

		frame.getContentPane().add(scrollPaneInput);
		final JTable table = new JTable(new DefaultTableModel(data, columnNames));
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(700);
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(new WordWrapCellRenderer());
		table.setFillsViewportHeight(true);
		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setSize(1000, 344);
		scrollPane.setLocation(10, 271);
		// Add the scroll pane to this panel.
		frame.getContentPane().add(scrollPane);
		frame.getContentPane().setLayout(null);

		JButton btnAnalysis = new JButton("ANALYSIS");
		btnAnalysis.setBounds(408, 217, 228, 44);
		frame.getContentPane().add(btnAnalysis);

		JButton btnLoadFile = new JButton("Load File");
		btnLoadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("txt File", "txt"));
				fileChooser.setCurrentDirectory(new java.io.File("."));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int option = fileChooser.showDialog(null, "Select File");
				if (option == JFileChooser.APPROVE_OPTION) {
					File f = fileChooser.getSelectedFile();
					if (!f.isDirectory()) {
						textArea.setText(ReadWriteFile.getDataTextFile(f.getPath()));
					}
				}
			}
		});
		btnLoadFile.setBounds(10, 10, 93, 27);
		frame.getContentPane().add(btnLoadFile);

		JButton btnExport = new JButton("Export Excel");
		btnExport.setBounds(718, 625, 140, 27);
		frame.getContentPane().add(btnExport);

		JButton btnClearData = new JButton("Clear Data");
		btnClearData.setBounds(566, 625, 140, 27);
		frame.getContentPane().add(btnClearData);

		JButton btnLearn = new JButton("Learn");
		btnLearn.setBounds(870, 625, 140, 27);
		frame.getContentPane().add(btnLearn);

		// ************GUI - END*************

		// ************Handle - START*************
		// Edit content in cell of table
		table.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				final JTable jTable = (JTable) e.getSource();
				final int row = jTable.getSelectedRow();
				final int column = jTable.getSelectedColumn();
				if (column == 1) {
					final String valueInCell = (String) jTable.getValueAt(row, column);
					String translateResult = "";
					if (JapaneseString.containsKanji(valueInCell)) {
						translateResult = findWord(dictionaryKanji, valueInCell);
					} else if (JapaneseString.containsKatakana(valueInCell)) {
						translateResult = findWordKata(dictionaryKata, valueInCell);
					}
					jTable.setValueAt(translateResult, row, column + 2);
					List<Token> result = tokenizer.tokenize(valueInCell);
					if (result.size() > 0) {
						jTable.setValueAt(result.get(0).getReading(), row, column + 1);
					}
				}
			}
		});
		
		//Clear data from table
		btnClearData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel dm = (DefaultTableModel) table.getModel();
				while (dm.getRowCount() > 0) {
					dm.removeRow(0);
				}
				JOptionPane.showMessageDialog(null, "Done", "Delete All Item", 1);
			}
		});

		//TODO : 
		btnLearn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "lien he admin kysubrse !!!", "Learn Japanese Word", 1);
			}
		});
		
		//Export to excel
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
					ReadWriteFile.writeToExcell(table, filePath);
					JOptionPane.showMessageDialog(null, "Done", "Export Data To Excel File", 1);
				}

			}
		});

		// Button ANALYSIS Click Event
		btnAnalysis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (textArea.getText().length() > 0) {
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					model.getDataVector().removeAllElements();
					List<OutputData> lstOutput = new ArrayList<OutputData>();
					String line = textArea.getText();
					int i = 1;
					List<Token> result = tokenizer.tokenize(line);
					for (Token token : result) {
						// output = output + (token.getSurfaceForm() + "\n");
						OutputData outputData = new OutputData();
						outputData.No = i;
						outputData.strWordJP = token.getSurfaceForm().toString();
						outputData.kana = token.getReading();
						if (isNotExist(lstOutput, outputData.strWordJP)) {
							if (JapaneseString.containsKanji(outputData.strWordJP)) {
								outputData.strWordVN = findWord(dictionaryKanji, outputData.strWordJP);
								lstOutput.add(outputData);
								model.addRow(new Object[] { i, outputData.strWordJP, outputData.kana,
										outputData.strWordVN });
								i++;
							} else if (JapaneseString.containsKatakana(outputData.strWordJP)) {
								outputData.strWordVN = findWordKata(dictionaryKata, outputData.strWordJP);
								lstOutput.add(outputData);
								model.addRow(new Object[] { i, outputData.strWordJP, outputData.kana,
										outputData.strWordVN });
								i++;
							}
						}
					}
				}

			}
		});
		// ************Handle - END*************
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TokenizerForJapanese window = new TokenizerForJapanese();
				window.frame.setVisible(true);
			}
		});
	}

	//Find word from dictionary with input = kanji
	public String findWord(List<String> dataList, String word) {
		String[] strLineArr;
		for (String string : dataList) {
			strLineArr = string.split("#");
			if (strLineArr.length > 3 && strLineArr[2].contains("Åu " + word + " Åv")) {
				return strLineArr[3];
			}
		}
		return "";
	}

	//Find word from dictionary with input = katanana
	public String findWordKata(List<String> dataList, String word) {
		String[] strLineArr;
		for (String string : dataList) {
			strLineArr = string.split("#");
			if (strLineArr.length > 3 && strLineArr[2].equals(word)) {
				return strLineArr[3];
			}
		}
		return "";
	}

	public boolean isNotExist(List<OutputData> lstOutputData, String strWordJP) {
		if (lstOutputData != null && lstOutputData.size() > 0) {
			for (OutputData item : lstOutputData) {
				if (item.strWordJP.equals(strWordJP)) {
					return false;
				}
			}
		}
		return true;
	}
}
