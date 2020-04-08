package com.hmtmcse.asciidoc

import com.hmtmcse.te.FreemarkerTemplate
import spock.lang.Specification

class FreemarkerTemplateTest extends Specification {


    def "Template String To parse"(){

        expect: "Template String To parse"
        String template = "<html>\n" +
                "<head>\n" +
                "    <title>Welcome!</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Welcome \${user}!</h1>\n" +
                "<p>Our latest product:\n" +
                "</body>\n" +
                "</html>";
        FreemarkerTemplate freemarkerTemplate = new FreemarkerTemplate()
        println(freemarkerTemplate.processText(template))
    }

}
