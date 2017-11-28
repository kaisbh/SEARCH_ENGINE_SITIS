package isped.sitis.se.model;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class File {
	private final StringProperty numFile;
    private final StringProperty pathFile;
    private final StringProperty scoreFile;
    

    /**
     * Default constructor.
     */
    public File() {
        this(null, null, null);
    }

    /**
     * Constructor with some initial data.
     * 
     * @param numFile
     * @param pathFile
     * @param scoreFile
     */
    public File(String numFile, String pathFile, String scoreFile) {
        this.numFile = new SimpleStringProperty(numFile);
        this.pathFile = new SimpleStringProperty(pathFile);
        this.scoreFile = new SimpleStringProperty(scoreFile);
        
    }

    public String getnumDoc() {
        return numFile.get();
    }

    public void setnumDocc(String numDoc) {
        this.numFile.set(numDoc);
    }

    public StringProperty numFileProperty() {
        return numFile;
    }

    public String getpathFile() {
        return pathFile.get();
    }

    public void setpathFile(String pathFile) {
        this.pathFile.set(pathFile);
    }

    public StringProperty pathFileProperty() {
        return pathFile;
    }

    public String getscoreFile() {
        return scoreFile.get();
    }

    public void setscoreFile(String scoreFile) {
        this.scoreFile.set(scoreFile);
    }

    public StringProperty scoreFileProperty() {
        return scoreFile;
    }

    

}
