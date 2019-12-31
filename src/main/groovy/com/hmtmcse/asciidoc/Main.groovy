package com.hmtmcse.asciidoc

import org.asciidoctor.Asciidoctor

class Main {


    public static void main(String[] args) {



        Asciidoctor asciidoctor = Asciidoctor.Factory.create()


        String html = asciidoctor.convert(
                "Writing AsciiDoc is _easy_!",
                new HashMap<String, Object>())
        System.out.println(html)


         html = asciidoctor.convertFile(
                new File("all-plugins/asciidoc/asciidoc/index.adoc"),
                new HashMap<String, Object>());
        System.out.println(html);

    }


}
