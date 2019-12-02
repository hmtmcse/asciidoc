package com.hmtmcse.asciidoc

import com.hmtmcse.texttoweb.console.TextToWebMenu
import spock.lang.Specification

class TextToWebMenuTest extends Specification{

    def "check main menu"() {
        expect: "Print Menu"
        TextToWebMenu.bismillah()
    }

    def "check generate menu help"() {
        expect: "generate command help"
        String[] command = ["generate", "-help"];
        TextToWebMenu.bismillah(command)
    }

    def "check generate menu with descriptor landing"() {
        expect: "generate command landing"
        String[] command = ["generate", "-t"]
        TextToWebMenu.bismillah(command)
    }

    def "check export menu php"() {
        expect: "export command php"
        String[] command = ["export", "-t", "php"]
        TextToWebMenu.bismillah(command)
    }

    def "check export menu php -t test"() {
        expect: "export command php"
        String[] command = ["export", "-t"]
        TextToWebMenu.bismillah(command)
    }

    def "check export menu help"() {
        expect: "export command php"
        String[] command = ["export", "-help"]
        TextToWebMenu.bismillah(command)
    }

    def "generate json and yml descriptor for landing"() {
        expect: "generate -t landing"
        String[] command = ["generate", "-t", "yml"]
        TextToWebMenu.bismillah(command)
    }


}
