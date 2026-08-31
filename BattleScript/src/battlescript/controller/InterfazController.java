    package battlescript.controller;

    import battlescript.view.Interfaz;
    import engine.analysis.AnalysisResult;
    import engine.analysis.SourceAnalyzer;
    import engine.battle.BattleResult;
    import engine.battle.ProgramExecutor;
    import java.io.IOException;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import engine.analysis.ReportGenerator;
    import java.awt.Desktop;
    import java.util.Collections;
    import java.util.List;
    import javax.swing.JFileChooser;
    import javax.swing.JOptionPane;
    import javax.swing.filechooser.FileNameExtensionFilter;

    /**
     * Funciones:
     * - Crear un archivo nuevo.
     * - Abrir archivos
     * - Guardar archivos
     * - Analizar y ejecutar el código escrito por el usuario.
     * - Mostrar tokens, errores y resultados en la interfaz.
     */ 
    public final class InterfazController {

        // Referencia a la ventana principal de la aplicación.
        private final Interfaz view;

        // Analizador encargado de procesar el código BattleScript.
        private final SourceAnalyzer analyzer = new SourceAnalyzer();

        // Ruta del archivo que actualmente está abierto.
        private Path currentFile;

        /**
         * Constructor del controlador.
         *
         * Recibe la interfaz gráfica y registra las acciones que deben
         * ejecutarse cuando el usuario utiliza los botones o menús.
         */
        public InterfazController(Interfaz view) {

            this.view = view;

            // Acciones asociadas a los botones principales de la interfaz.
            view.onNew(event -> createNew());
            view.onOpen(event -> open());
            view.onSave(event -> save());
            view.onRun(event -> run());

            // Acciones asociadas a las opciones del menú.
            view.onMenuNew(event -> createNew());
            view.onMenuOpen(event -> open());
            view.onMenuSave(event -> save());

            // Cierra completamente la aplicación.
            view.onMenuExit(event -> System.exit(0));
        }

        /**
         * Crea un nuevo archivo
         *
         * Se elimina el contenido actual del editor y se limpian
         * los tokens, errores y resultados de ejecuciones anteriores.
         */
        public void createNew() {

            // El nuevo archivo todavía no tiene una ruta asociada.
            currentFile = null;

            // Limpia el área donde se escribe el código.
            view.setSourceText("");

            // Limpia la lista de tokens.
            view.showTokens(java.util.Collections.emptyList());

            // Limpia la lista de errores.
            view.showErrors(java.util.Collections.emptyList());

            // Actualiza el mensaje de estado de la interfaz.
            view.setStatus("Nuevo archivo creado.");

            // Limpia los resultados de ejecuciones anteriores.
            view.showResults(java.util.Collections.emptyList());
        }

        /**
         * Abre un archivo btl existente.
         *
         * Muestra un selector de archivos, obtiene el archivo seleccionado
         * y carga su contenido en el editor.
         */
        public void open() {

            // Crea el selector de archivos.
            JFileChooser chooser = chooser();

            // Si el usuario cancela, no se realiza ninguna acción.
            if (chooser.showOpenDialog(view) != JFileChooser.APPROVE_OPTION)
                return;

            // Guarda la ruta del archivo seleccionado.
            currentFile = chooser.getSelectedFile().toPath();

            // Informa en la interfaz qué archivo se abrió.
            view.setStatus("Archivo abierto: " + currentFile.getFileName());

            try {

                // Lee todo el contenido del archivo utilizando UTF-8
                // y lo coloca en el editor.
                view.setSourceText(
                    new String(
                        Files.readAllBytes(currentFile),
                        StandardCharsets.UTF_8
                    )
                );

            } catch (IOException exception) {
                showError(
                    "No se pudo abrir el archivo: "
                    + exception.getMessage()
                );
            }
        }

        /**
         * Guarda el código actual en un archivo.
         *
         * Si el archivo todavía no tiene una ruta, se abre el selector
         * para que el usuario indique dónde desea guardarlo.
         */
        public void save() {

            // Si no existe un archivo asociado al código actual,
            // se solicita una ubicación para guardarlo.
            if (currentFile == null) {
                JFileChooser chooser = chooser();

                // Si el usuario cancela el guardado, se termina el método.
                if (chooser.showSaveDialog(view) != JFileChooser.APPROVE_OPTION)
                    return;

                // Obtiene la ruta elegida por el usuario.
                currentFile = chooser.getSelectedFile().toPath();

                // Si el usuario no escribió la extensión .btl,
                // se agrega automáticamente.
                if (!currentFile.toString().endsWith(".btl"))
                    currentFile = Path.of(
                        currentFile.toString() + ".btl"
                    );
            }
            try {

                // Obtiene el código escrito actualmente en el editor
                // y lo guarda en el archivo utilizando UTF-8.
                Files.write(
                    currentFile,
                    view.getSourceText().getBytes(StandardCharsets.UTF_8)
                );

                // Informa que el archivo fue guardado correctamente.
                view.setStatus(
                    "Archivo guardado: "
                    + currentFile.getFileName()
                );

            } catch (IOException exception) {
                // Muestra cualquier error producido durante el guardado.
                showError(
                    "No se pudo guardar el archivo: "
                    + exception.getMessage()
                );
            }
        }

        /**
         * Analiza y ejecuta el programa BattleScript escrito en la interfaz.
         *
         * Proceso
         * 1. Obtener el código del editor.
         * 2. Analizarlo mediante SourceAnalyzer.
         * 3. Mostrar tokens y errores.
         * 4. Si existen errores, detener la ejecución.
         * 5. Si el análisis es correcto, ejecutar el programa.
         * 6. Mostrar los resultados de las partidas.
         */
        private void run() {
            view.showResults(Collections.emptyList());
            
            // 1. Validar que el editor no esté vacío
            if (view.getSourceText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "No hay código para ejecutar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                view.setStatus("Error: editor vacío");
                return;
            }

            // 2. Analizar
            AnalysisResult result = analyzer.analyze(view.getSourceText());
            view.showTokens(result.getTokens());
            view.showErrors(result.getErrors());

            // 3. Generar reporte SIEMPRE (con o sin errores)
            String baseName = (currentFile != null) 
                    ? currentFile.getFileName().toString().replace(".btl", "") 
                    : "sin_nombre";
            Path reportPath = ReportGenerator.generateReport(result.getTokens(), result.getErrors(), baseName);
            if (reportPath != null) {
                view.setStatus("Reporte HTML generado: " + reportPath.getFileName());
                // Abrir en navegador en segundo plano (sin bloquear)
                try {
                    Desktop.getDesktop().browse(reportPath.toUri());
                } catch (IOException e) {
                    // Si falla abrir el navegador, solo mostramos la ubicación
                    view.setStatus("Reporte guardado en: " + reportPath.toAbsolutePath());
                }
            } else {
                view.setStatus("Error al generar el reporte");
            }

            // 4. Si hay errores, detener la ejecución
            if (!result.isSuccessful()) {
                JOptionPane.showMessageDialog(
                    view,
                    "Se encontraron errores en el código. "
                    + "Se ejecutarán únicamente las partidas que puedan ser procesadas correctamente.",
                    "Análisis con errores",
                    JOptionPane.WARNING_MESSAGE
                );
            }

            // 5. Ejecutar partidas
            try {
                List<BattleResult> results = new ProgramExecutor().execute(result.getProgram());
                view.showResults(results);
                view.setStatus("Ejecución completada - " + results.size() + " partida(s)");
                JOptionPane.showMessageDialog(view, format(results), "Ejecución completada", JOptionPane.INFORMATION_MESSAGE);
            } catch (RuntimeException e) {
                view.setStatus("Error de ejecución: " + e.getMessage());
                showError("Error de ejecución: " + e.getMessage());
            }
        }


        //Crea y configura el selector de archivos.
        private JFileChooser chooser() {

            // Crea un nuevo selector de archivos.
            JFileChooser chooser = new JFileChooser();

            // Configura el filtro para archivos BattleScript.
            chooser.setFileFilter(
                new FileNameExtensionFilter(
                    "BattleScript (*.btl)",
                    "btl"
                )
            );
            return chooser;
        }

        /**
         * Genera el texto que se muestra en el mensaje
         * después de ejecutar las partidas.
         *
         * Para cada partida se muestra:
         * - Nombre de la partida.
         * - Ganador.
         * - Cantidad de rondas.
         */
        private String format(List<BattleResult> results) {

            // StringBuilder permite construir el texto
            // agregando información progresivamente.
            StringBuilder text = new StringBuilder();

            // Recorre todos los resultados obtenidos.
            for (BattleResult result : results)

                // Agrega una línea con el nombre, ganador y rondas.
                text.append(result.getMatchName())
                    .append(": ")
                    .append(result.getWinner())
                    .append(" (rondas: ")
                    .append(result.getRounds())
                    .append(")\n");

            // Si no hubo partidas, muestra un mensaje apropiado.
            // De lo contrario, devuelve el texto construido.
            return text.length() == 0
                ? "No hay partidas para ejecutar."
                : text.toString();
        }


        //Muestra un mensaje de error mediante una ventana emergente.
        private void showError(String message) {
            JOptionPane.showMessageDialog(
                view,
                message,
                "BattleScript",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }