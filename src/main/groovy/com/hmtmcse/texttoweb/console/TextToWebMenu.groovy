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
                "1. ${TextToWebConst.GENERATE_YML} \n"
        )
        return optionDefinition
    }

    public static OptionDefinition export() {
        OptionDefinition optionDefinition = new OptionDefinition(new CommandAction() {
            @Override
            public void process(OptionValues optionValues) throws ShellUtilException {
                GenerateProcessor generateProcessor = new GenerateProcessor()
                generateProcessor.process(optionValues)
            }
        })
        optionDefinition.setCommandDescription("Export to Static")
        optionDefinition.addOption(TextToWebConst.DESCRIPTOR, "t")
        optionDefinition.required().setDescription("" +
                "Please use below Listed descriptor export type:\n" +
                "1. ${TextToWebConst.EXPORT_TO_STATIC_PHP} \n"
        )
        return optionDefinition
    }


    public static void bismillah(String[] args) {
        CommandProcessor commandProcessor = new CommandProcessor()
        commandProcessor.addCommand("generate", generate())
        commandProcessor.addCommand("export", export())

        try {
            commandProcessor.processCommands(args);
        } catch (ShellUtilException e) {
            commandProcessor.printMenu();
            ConsolePrinter.errorPrint(e.getMessage());
        }
    }


}
