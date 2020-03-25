package com.hmtmcse.te

import com.hmtmcse.common.AsciiDocException
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler

class FreemarkerTemplate {

    private Configuration getConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30)
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(false);
        configuration.setFallbackOnNullLoopVariable(false);
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


}
