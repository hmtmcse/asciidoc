package com.hmtmcse.te;

import freemarker.template.*;
import java.util.*;
import java.io.*;


public class TestFreeMarker {

//    public static void main(String[] args) throws IOException, TemplateException {
//        // Get your template as a String from the DB
//        String template = getTemplateFromDatabase();
//        Map<String, Object> model = getModel();
//
//        Configuration cfg = new Configuration();
//        cfg.setObjectWrapper(new DefaultObjectWrapper());
//
//        Template t = new Template("templateName", new StringReader(template), cfg);
//
//        Writer out = new StringWriter();
//        t.process(model, out);
//
//        String transformedTemplate = out.toString();
//    }

    public static void main(String[] args) throws Exception {

        /* ------------------------------------------------------------------------ */
        /* You should do this ONLY ONCE in the whole application life-cycle:        */

        /* Create and adjust the configuration singleton */
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
//        cfg.setDirectoryForTemplateLoading(new File("H:\\pages"));
        // Recommended settings for new projects:
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);

        /* ------------------------------------------------------------------------ */
        /* You usually do these for MULTIPLE TIMES in the application life-cycle:   */

        /* Create a data-model */
        Map root = new HashMap();
        root.put("user", "Big Joe");


        /* Get the template (uses cache internally) */
        String template = "<html>\n" +
                "<head>\n" +
                "    <title>Welcome!</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Welcome ${user}!</h1>\n" +
                "<p>Our latest product:\n" +
                "</body>\n" +
                "</html>";
        Template temp = new Template("templateName", new StringReader(template), cfg);

        Writer out = new StringWriter();
        temp.process(root, out);
        System.out.println(out.toString());
        // Note: Depending on what `out` is, you may need to call `out.close()`.
        // This is usually the case for file output, but not for servlet output.
    }
}
