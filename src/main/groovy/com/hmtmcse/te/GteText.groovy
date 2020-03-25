package com.hmtmcse.te

import groovy.text.SimpleTemplateEngine

class GteText {

    public static void main(String[] args) {
        def values = [ "1", "2", "3" ]
        def engine = new SimpleTemplateEngine()
//        def text = '''<% values.each { println it} %>'''
        def text = "<% values.each { out << %>  (it) <%}%> "
        println engine.createTemplate(text).make([values: values])
    }

}
