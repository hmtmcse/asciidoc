package com.hmtmcse.texttoweb.console

import com.hmtmcse.shellutil.common.ShellUtilException
import com.hmtmcse.shellutil.console.menu.CommandAction
import com.hmtmcse.shellutil.console.menu.CommandProcessor
import com.hmtmcse.shellutil.console.menu.OptionDefinition
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.shellutil.print.ConsolePrinter

class TextToWebMenu {

    public static OptionDefinition generate(){
        OptionDefinition optionDefinition = new OptionDefinition(new CommandAction() {
            @Override
            public void process(OptionValues optionValues) throws ShellUtilException {
                ConsolePrinter.printLine("I am in Action Processor");
                ConsolePrinter.successPrint("Branch: " + optionValues.valueAsString("branch"));
                ConsolePrinter.successPrint("Type: " + optionValues.valueAsString("type"));
            }
        });
        optionDefinition.setCommandDescription("Generate Descriptor and others");

        optionDefinition.addOption("descriptor", "d");
        optionDefinition.setDescription("" +
                "Please use below Listed descriptor generator type:\n" +
                "1. landing \n" +
                "2. topics \n" +
                "3. outline \n" +
                "3. chapter \n"
        )

        optionDefinition.addOption("type", "t");
        optionDefinition.setDescription("Build Type: optimize / fresh").setDefaultValue("optimize");

        return optionDefinition;
    }

    public static OptionDefinition export(){
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


    public static void init(String[] args) {
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
