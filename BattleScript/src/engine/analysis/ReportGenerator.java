package engine.analysis;

import analyzer.Error;
import analyzer.TokenInfo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Generador de reportes HTML a partir de los tokens y errores del análisis.
 * Los reportes se guardan en la carpeta "reports/" y se abren en el navegador.
 */
public class ReportGenerator {

    /**
     * Genera un reporte HTML y lo abre en el navegador predeterminado.
     *
     * @param tokens    Lista de tokens reconocidos (puede ser vacía pero no nula).
     * @param errors    Lista de errores encontrados (puede ser vacía pero no nula).
     * @param fileName  Nombre base del archivo (ej. "mi_programa").
     * @return La ruta absoluta del archivo generado, o null si falló.
     */
    public static Path generateReport(List<TokenInfo> tokens, List<Error> errors, String fileName) {
        try {
            // Crear carpeta "reports" si no existe
            Path reportsDir = Paths.get("reports");
            if (!Files.exists(reportsDir)) {
                Files.createDirectories(reportsDir);
            }

            // Generar nombre único con timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String baseName = (fileName != null && !fileName.isEmpty()) 
                    ? fileName.replaceAll("[^a-zA-Z0-9_\\-]", "_") 
                    : "reporte";
            Path reportFile = reportsDir.resolve(baseName + "_" + timestamp + ".html");

            // Construir contenido HTML
            String html = buildHtml(tokens, errors, baseName);

            // Escribir archivo
            Files.write(reportFile, html.getBytes(StandardCharsets.UTF_8));

            return reportFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Construye el contenido HTML del reporte.
     */
    private static String buildHtml(List<TokenInfo> tokens, List<Error> errors, String fileName) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("    <meta charset='UTF-8'>\n");
        html.append("    <title>Reporte de Análisis - BattleScript</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 20px; background: #f9f9f9; }\n");
        html.append("        h1 { color: #2c3e50; text-align: center; }\n");
        html.append("        h2 { color: #34495e; border-bottom: 2px solid #3498db; }\n");
        html.append("        table { border-collapse: collapse; width: 100%; margin: 20px 0; background: white; }\n");
        html.append("        th { background: #3498db; color: white; padding: 10px; text-align: left; }\n");
        html.append("        td { padding: 8px; border: 1px solid #ddd; }\n");
        html.append("        tr:nth-child(even) { background: #f2f2f2; }\n");
        html.append("        .token { color: #2980b9; }\n");
        html.append("        .error { background: #fdecea; }\n");
        html.append("        .success { color: #27ae60; font-weight: bold; }\n");
        html.append("        .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #888; }\n");
        html.append("    </style>\n");
        html.append("</head>\n<body>\n");

        // Encabezado
        html.append("<h1>📊 Reporte de Análisis - BattleScript</h1>\n");
        html.append("<p><strong>Fecha:</strong> ").append(new Date().toString()).append("</p>\n");
        html.append("<p><strong>Archivo:</strong> ").append(escapeHtml(fileName)).append("</p>\n");

        // Tabla de tokens
        html.append("<h2>📌 Tabla de Tokens</h2>\n");
        if (tokens != null && !tokens.isEmpty()) {
            html.append("<table>\n");
            html.append("<tr><th>#</th><th>Lexema</th><th>Tipo</th><th>Línea</th><th>Columna</th></tr>\n");
            int i = 1;
            for (TokenInfo t : tokens) {
                html.append("<tr class='token'>\n");
                html.append("<td>").append(i++).append("</td>\n");
                html.append("<td>").append(escapeHtml(t.getLexeme())).append("</td>\n");
                html.append("<td>").append(escapeHtml(t.getType())).append("</td>\n");
                html.append("<td>").append(t.getLine()).append("</td>\n");
                html.append("<td>").append(t.getColumn()).append("</td>\n");
                html.append("</tr>\n");
            }
            html.append("</table>\n");
        } else {
            html.append("<p class='success'>No se encontraron tokens.</p>\n");
        }

        // Tabla de errores
        html.append("<h2>⚠️ Tabla de Errores</h2>\n");
        if (errors != null && !errors.isEmpty()) {
            html.append("<table>\n");
            html.append("<tr><th>#</th><th>Descripción</th><th>Tipo</th><th>Línea</th><th>Columna</th></tr>\n");
            int i = 1;
            for (Error e : errors) {
                html.append("<tr class='error'>\n");
                html.append("<td>").append(i++).append("</td>\n");
                html.append("<td>").append(escapeHtml(e.getMessage())).append("</td>\n");
                html.append("<td>").append(escapeHtml(e.getType())).append("</td>\n");
                html.append("<td>").append(e.getLine()).append("</td>\n");
                html.append("<td>").append(e.getColumn()).append("</td>\n");
                html.append("</tr>\n");
            }
            html.append("</table>\n");
        } else {
            html.append("<p class='success'>✅ No se encontraron errores.</p>\n");
        }

        // Pie de página
        html.append("<div class='footer'>");
        html.append("Reporte generado automáticamente por BattleScript - ");
        html.append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        html.append("</div>\n");

        html.append("</body>\n</html>");
        return html.toString();
    }

    /**
     * Escapa caracteres especiales para HTML.
     */
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#039;");
    }
}