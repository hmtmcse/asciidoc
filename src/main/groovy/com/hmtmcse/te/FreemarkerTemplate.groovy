package com.hmtmcse.te

import com.hmtmcse.common.AsciiDocException
import freemarker.template.Configuration
import freemarker.template.DefaultObjectWrapper
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler

class FreemarkerTemplate {

    private Configuration getConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30)
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(false);
        configuration.setFallbackOnNullLoopVariable(false);

        DefaultObjectWrapper wrapper = new DefaultObjectWrapper();
        wrapper.setExposeFields(true);
        configuration.setObjectWrapper(wrapper);

        return configuration
    }


    public String processText(String template, Map<String, Object> params = [:], String templateName = "templateName") throws AsciiDocException {
        try {
            Template templateProcessor = new Template(templateName, new StringReader(template), getConfiguration());
            Writer output = new StringWriter();
            templateProcessor.process(params, output);
            return output.toString()
        } catch (Exception e) {
            throw new AsciiDocException(e.getMessage())
        }
    }

    public String processTextWithTemplateDir(String templatePath, String templateName, Map<String, Object> params = [:]) throws AsciiDocException {
        Configuration configuration = getConfiguration()
        configuration.setDirectoryForTemplateLoading(new File(templatePath))
        return processTextByScan(configuration, templateName, params)
    }

    public String processTextClassPath(String templatePath, String templateName, Map<String, Object> params = [:]) throws AsciiDocException {
        Configuration configuration = getConfiguration()
        configuration.setClassForTemplateLoading(this.getClass(), templatePath)
        return processTextByScan(configuration, templateName, params)
    }

    private String processTextByScan(Configuration configuration, String templateName, Map<String, Object> params = [:]) throws AsciiDocException {
        try {
            Template templateProcessor = configuration.getTemplate(templateName)
            Writer output = new StringWriter();
            templateProcessor.process(params, output);
            return output.toString()
        } catch (Exception e) {
            throw new AsciiDocException(e.getMessage())
        }
    }


}
