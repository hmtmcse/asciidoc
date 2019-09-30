package com.hmtmcse.texttoweb.console

import com.hmtmcse.shellutil.common.ShellUtilException
import com.hmtmcse.shellutil.console.menu.CommandAction
import com.hmtmcse.shellutil.console.menu.CommandProcessor
import com.hmtmcse.shellutil.console.menu.OptionDefinition
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.shellutil.print.ConsolePrinter
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.processor.GenerateProcessor

class TextToWebMenu {

    public static commonInput(OptionDefinition optionDefinition){
        optionDefinition.addOption(TextToWebConst.SOURCE, "s")
        optionDefinition.setDescription("Please Specify the .adoc source root directory")
        optionDefinition.addOption(TextToWebConst.OUT, "o")
        optionDefinition.setDescription("Please Specify the html export directory")
        return optionDefinition
    }

    public static OptionDefinition generate() {
        OptionDefinition optionDefinition = new OptionDefinition(new CommandAction() {
            @Override
            public void process(OptionValues optionValues) throws ShellUtilException {
                GenerateProcessor generateProcessor = new GenerateProcessor()
                generateProcessor.process(optionValues)
            }
        })
        optionDefinition.setCommandDescription("Generate Descriptor and others")
        optionDefinition.addOption(TextToWebConst.DESCRIPTOR, "t")
        optionDefinition.required().setDescription("" +
                "Please use below Listed descriptor generator type:\n" +
                "1. ${TextToWebConst.LANDING} \n" +
                "2. ${TextToWebConst.TOPICS} \n" +
                "3. ${TextToWebConst.OUTLINE} \n" +
                "3. ${TextToWebConst.DETAILS} \n"
        )
        return commonInput(optionDefinition)
    }

    public static OptionDefinition export() {
        OptionDefinition optionDefinition = new OptionDefinition(new CommandAction() {
            @Override
            public void process(OptionValues optionValues) throws ShellUtilException {

            }
        });
        optionDefinition.setCommandDescription("Export to html and others")

        optionDefinition.addOption("version", "v");
        optionDefinition.required().setDescription("Binary Version Number.");

        optionDefinition.addOption("instance", "i");
        optionDefinition.setDescription("Name of the Instance.").required();

        return optionDefinition;
    }


    public static void bismillah(String[] args) {
        CommandProcessor commandProcessor = new CommandProcessor()
        commandProcessor.addCommand("generate", generate())
        commandProcessor.addCommand("export", export())

        try {
            commandProcessor.processCommands(args);
        } catch (ShellUtilException e) {
            ConsolePrinter.errorPrint(e.getMessage());
            commandProcessor.printMenu();
        }
    }


}
