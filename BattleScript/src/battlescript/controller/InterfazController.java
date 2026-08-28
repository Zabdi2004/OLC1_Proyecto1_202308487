package battlescript.controller;

import battlescript.view.Interfaz;
import engine.analysis.AnalysisResult;
import engine.analysis.SourceAnalyzer;
import engine.battle.BattleResult;
import engine.battle.ProgramExecutor;
import java.awt.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Coordina la vista Swing con el analizador y el motor; la vista no conoce la lógica. */
public final class InterfazController {
    private final Interfaz view;
    private final SourceAnalyzer analyzer = new SourceAnalyzer();
    private Path currentFile;

    public InterfazController(Interfaz view) {
        this.view = view;
        view.onNew(event -> createNew());
        view.onOpen(event -> open());
        view.onSave(event -> save());
        view.onRun(event -> run());
        
        view.onMenuNew(event -> createNew());
        view.onMenuOpen(event -> open());
        view.onMenuSave(event -> save());
        view.onMenuExit(event -> System.exit(0));
    }

    public void createNew() { 
        currentFile = null; view.setSourceText(""); 
        view.showTokens(java.util.Collections.emptyList()); 
        view.showErrors(java.util.Collections.emptyList()); 
        view.setStatus("Nuevo archivo creado.");
    }
    
    public void open() {
        JFileChooser chooser = chooser();
        if (chooser.showOpenDialog(view) != JFileChooser.APPROVE_OPTION) return;
        currentFile = chooser.getSelectedFile().toPath();
        view.setStatus("Archivo abierto: " + currentFile.getFileName());
        try { 
            view.setSourceText(new String(Files.readAllBytes(currentFile), StandardCharsets.UTF_8)); }
        catch (IOException exception) { 
            showError("No se pudo abrir el archivo: " + exception.getMessage()); }
    }
    public void save() {
        if (currentFile == null) { 
            JFileChooser chooser = chooser(); 
            if (chooser.showSaveDialog(view) != JFileChooser.APPROVE_OPTION)
                return; 
            currentFile = chooser.getSelectedFile().toPath(); 
            
            if (!currentFile.toString().endsWith(".btl")) currentFile = Path.of(currentFile.toString() + ".btl"); 
        }
        try { 
            Files.write(currentFile, view.getSourceText().getBytes(StandardCharsets.UTF_8)); 
            view.setStatus("Archivo guardado: " + currentFile.getFileName());
        }
        catch (IOException exception) { 
            showError("No se pudo guardar el archivo: " + exception.getMessage()); }
    }
    
    private void run() {
        AnalysisResult result = analyzer.analyze(view.getSourceText());
        view.showTokens(result.getTokens()); 
        view.showErrors(result.getErrors());
        if (!result.isSuccessful()) { 
            JOptionPane.showMessageDialog(view, "Se encontraron errores. Revisa la pestaña Errores.", "Análisis detenido", JOptionPane.WARNING_MESSAGE); 
            return; 
        }
        
        try { 
            List<BattleResult> results = new ProgramExecutor().execute(result.getProgram());
            JOptionPane.showMessageDialog(view, format(results), "Ejecución completada", JOptionPane.INFORMATION_MESSAGE);
            view.showResults(results);
        } catch (RuntimeException exception) { 
            showError("Error de ejecución: " + exception.getMessage()); 
        }
    }
    
    private JFileChooser chooser() { 
        JFileChooser chooser = new JFileChooser(); 
        chooser.setFileFilter(new FileNameExtensionFilter("BattleScript (*.btl)", "btl")); 
        return chooser; }
    private String format(List<BattleResult> results) { 
        StringBuilder text = new StringBuilder(); 
        for (BattleResult result : results) 
            text.append(result.getMatchName()).append(": ").append(result.getWinner()).append(" (rondas: ").append(result.getRounds()).append(")\n"); 
        return text.length() == 0 ? "No hay partidas para ejecutar." : text.toString(); }
    private void showError(String message) { 
        JOptionPane.showMessageDialog(view, message, "BattleScript", JOptionPane.ERROR_MESSAGE); }
}
