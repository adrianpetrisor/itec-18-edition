package com.seventailed.engine.commands;

import com.seventailed.engine.email.EngineEmailService;
import com.seventailed.engine.logger.EngineLogging;
import com.seventailed.engine.utils.EngineUtils;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class EngineCommands {
    @Autowired
    private EngineEmailService emailService;

    @Autowired
    private ConfigurableApplicationContext context;

    private Thread commandsThread;

    private Logger commandsLogger = LoggerFactory.getLogger("commands");
    private HashMap<String, EngineCommand> commands = new HashMap<>();

    private boolean listeningToCommands = false;

    public void registerCommand(String name, EngineCommand command) {
        commands.put(name, command);
    }

    private void registerDefaultCommands() {
        registerCommand("stop", new EngineCommand() {
            @Override
            public void run(String[] arguments) {
                commandsLogger.info("Stopping engine.");

                listeningToCommands = false;
                SpringApplication.exit(context);
                System.exit(0);
            }
        });

        registerCommand("email", new EngineCommand() {
            @Override
            public void run(String[] arguments) {
                if(arguments.length == 1) {
                    commandsLogger.info("Email command syntax: </email> <template> <subject> <to> <title> <message> [optional: <redirectUrl> <redirectMessage>]");
                    return;
                }

                if(arguments.length < 6) {
                    commandsLogger.info("Invalid arguments. Minimum 6 required.");
                    return;
                }

                String template = arguments[1];
                String subject = getMessageFromCompressed(arguments[2].split(";"));
                String to = arguments[3];

                if(!EngineUtils.isValidEmail(to)) {
                    commandsLogger.info("Invalid email address.");
                    return;
                }

                String title = getMessageFromCompressed(arguments[4].split(";"));
                String body = getMessageFromCompressed(arguments[5].split(";"));

                commandsLogger.info("Sending your email to " + to + ".");

                if(template.equalsIgnoreCase("redirect")) {
                    String redirectUrl = arguments[6];
                    String redirectMessage = getMessageFromCompressed(arguments[7].split(";"));

                    emailService.sendEmail(template, to, subject, title, body, redirectUrl, redirectMessage);
                }else {
                    emailService.sendEmail(template, to, subject, title, body);
                }
            }
        });
    }

    public String getMessageFromCompressed(String[] compressed) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String component : compressed) {
            stringBuilder.append(component + " ");
        }

        return stringBuilder.toString();
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        registerDefaultCommands();
        commandsLogger.info("Listening to commands.");

        listeningToCommands = true;

        this.commandsThread = new Thread(() -> {
            try {
                while (listeningToCommands) {
                    String data = EngineLogging.getLineReader().readLine("> ");
                    String[] arguments = data.split(" ");

                    if (commands.containsKey(arguments[0])) {
                        commands.get(arguments[0]).run(arguments);
                    } else {
                        commandsLogger.info("Can't find command " + arguments[0] + ".");
                    }
                }
            }catch (Exception e) {
                if(!(e instanceof UserInterruptException)) {
                    e.printStackTrace();
                }
            }
        });

        commandsThread.start();
    }
}
