package uk.ac.ebi.fgpt.webpopulous.patterns.oppl;

import org.apache.log4j.Logger;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Simon Jupp<br>
 * Date: Oct 7, 2010<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 *
 * Parses a hidden sheet
 * patterns are save in rows, format is "pattern", id, name, value
 *
 */
public class PatternSheetManager {

    public static final String PATTERN_SHEET_PREFIX = "pwsvpe";

    public static final String PATTERN_ROW_KEY = "pattern";

    private WorkbookManager workbookManager;

    private Sheet sheet;

    private static final Logger logger = Logger.getLogger(PatternSheetManager.class);
    private Set<PopulousPattern> selectedPatterns;

//    private static int counter = 0;


    public PatternSheetManager(WorkbookManager workbookManager) {
        this.workbookManager = workbookManager;
        this.sheet = getPatternSheet();
    }

    private Sheet createPatternSheet() {

        sheet = workbookManager.getWorkbook().addVeryHiddenSheet();
        sheet.setName(PATTERN_SHEET_PREFIX);
        return sheet;
    }

    private Sheet getPatternSheet () {
        for (Sheet sheet : workbookManager.getWorkbook().getSheets()) {
            if (sheet.getName().startsWith(PATTERN_SHEET_PREFIX)) {
                return sheet;
            }
        }
        return createPatternSheet();
    }


    public boolean patternSheetEmpty() {
        if (sheet == null) {
            return false;
        }
        Cell cell = sheet.getCellAt(0, 0);
        return cell != null && cell.getValue().trim().equals(PATTERN_ROW_KEY);
    }

    public Map<String, PopulousPattern> parsePatterns() {
        Map<String, PopulousPattern> result = new LinkedHashMap<String, PopulousPattern>();
        for (int row = 0; ; row++) {
            Cell cell = sheet.getCellAt(0, row);
            if (cell == null) {
                break;
            }
            if (cell.getValue().equals(PATTERN_ROW_KEY)) {
                PopulousPattern pattern = new PopulousPattern();
                pattern.setPatternID(sheet.getCellAt(1, row).getValue());
                pattern.setPatternName(sheet.getCellAt(2, row).getValue());
                pattern.setPatternValue(sheet.getCellAt(3, row).getValue());
                result.put(pattern.getPatternID(), pattern);
            }
        }
        return result;
    }


    public Set<PopulousPattern> getPopulousPatterns() {
        return new HashSet<PopulousPattern>(parsePatterns().values());
    }


    public Set<PopulousPattern> getSelectedPatterns() {
        return selectedPatterns;
    }


    private void setPatterns(Map<String, PopulousPattern> patternMap) {
        removeAllPatterns();
        int row = 0;
        for (String id : patternMap.keySet()) {
            System.err.println("writing pattern to file:" + patternMap.get(id).getPatternName());
            sheet.addCellAt(0, row).setValue(PATTERN_ROW_KEY);
            sheet.addCellAt(1, row).setValue(id);
            sheet.addCellAt(2, row).setValue(patternMap.get(id).getPatternName());
            sheet.addCellAt(3, row).setValue(patternMap.get(id).getPatternValue());
            row++;
        }
    }

    public void addPopulousPattern(PopulousPattern pattern) {
        Map<String, PopulousPattern> result = parsePatterns();
        result.put(pattern.getPatternID(), pattern);
        setPatterns(result);
    }

    public void removeAllPatterns() {
        workbookManager.getWorkbook().deleteSheet(PATTERN_SHEET_PREFIX);
        sheet = createPatternSheet();        
    }

    public void removePopulousPattern(PopulousPattern pattern) {
        Map<String, PopulousPattern> result = parsePatterns();
        result.remove(pattern.getPatternID());
        setPatterns(result);
    }

    public static void main(String[] args) {

        WorkbookManager manager = new WorkbookManager();
        Workbook workbook = manager.createNewWorkbook();

        PopulousPattern pattern1 = new PopulousPattern("pattern1", "?cell:CLASS,\n" +
                "?anatomyPart:CLASS,\n" +
                "?valueRestriction:CLASS = part_of some ?anatomyPart,\n" +
                "?anatomy:CLASS = createIntersection(?valueRestriction.VALUES)\n" +
                "BEGIN\n" +
                "\tADD ?cell equivalentTo ?anatomy\n" +
                "END;");

        PopulousPattern pattern2 = new PopulousPattern("pattern2", "?cell:CLASS,\n" +
                "?anatomyPart:CLASS,\n" +
                "?valueRestriction:CLASS = part_of some ?anatomyPart,\n" +
                "?anatomy:CLASS = createIntersection(?valueRestriction.VALUES)\n" +
                "BEGIN\n" +
                "\tADD ?cell equivalentTo ?anatomy\n" +
                "END;");

        PopulousPattern pattern3 = new PopulousPattern("pattern3", "?cell:CLASS,\n" +
                "?anatomyPart:CLASS,\n" +
                "?valueRestriction:CLASS = part_of some ?anatomyPart,\n" +
                "?anatomy:CLASS = createIntersection(?valueRestriction.VALUES)\n" +
                "BEGIN\n" +
                "\tADD ?cell equivalentTo ?anatomy\n" +
                "END;");


        PatternSheetManager man = new PatternSheetManager(manager);

        man.addPopulousPattern(pattern1);
        man.addPopulousPattern(pattern2);
        man.addPopulousPattern(pattern3);


        try {
            workbook.saveAs(URI.create("file:/Users/simon/dev/java/projects/RightField/myGrid-RightField-05c653d/tmp/pop_pattern1.xls"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        man.removePopulousPattern(pattern2);

        try {
            workbook.saveAs(URI.create("file:/Users/simon/dev/java/projects/RightField/myGrid-RightField-05c653d/tmp/pop_pattern2.xls"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        man.addPopulousPattern(pattern2);

        try {
            workbook.saveAs(URI.create("file:/Users/simon/dev/java/projects/RightField/myGrid-RightField-05c653d/tmp/pop_pattern3.xls"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        man.removeAllPatterns();

        try {
            workbook.saveAs(URI.create("file:/Users/simon/dev/java/projects/RightField/myGrid-RightField-05c653d/tmp/pop_pattern4.xls"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

}
