package com.techbank.account.cmd;

import com.techbank.account.cmd.api.commands.*;
import com.techbank.cqrs.cors.infrastructure.CommandDispatcher;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	@Autowired
	private CommandDispatcher commandDispatcher;

	@Autowired
	private CommandHandler commandHandler;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct
	public void registerHandlers() {
		commandDispatcher.registerHandler(OpenAccountCommand.class, commandHandler::handle);
		commandDispatcher.registerHandler(DepositFundsCommand.class, commandHandler::handle);
		commandDispatcher.registerHandler(WithdrawFundsCommand.class, commandHandler::handle);
		commandDispatcher.registerHandler(CloseAccountCommand.class, commandHandler::handle);
		commandDispatcher.registerHandler(RestoreReadDbCommand.class, commandHandler::handle);

		// Using lambda expressions
		//		commandDispatcher.registerHandler(OpenAccountCommand.class, command -> commandHandler.handle(command));
		//		commandDispatcher.registerHandler(DepositFundsCommand.class, command -> commandHandler.handle(command));
		//		commandDispatcher.registerHandler(WithdrawFundsCommand.class, command -> commandHandler.handle(command));
		//		commandDispatcher.registerHandler(CloseAccountCommand.class, command -> commandHandler.handle(command));


		// Using anonymous implementations
//		commandDispatcher.registerHandler(OpenAccountCommand.class, new CommandHandlerMethod<OpenAccountCommand>() {
//			@Override
//			public void handle(OpenAccountCommand command) {
//				commandHandler.handle(command);
//			}
//		});
//
//		commandDispatcher.registerHandler(DepositFundsCommand.class, new CommandHandlerMethod<DepositFundsCommand>() {
//			@Override
//			public void handle(DepositFundsCommand command) {
//				commandHandler.handle(command);
//			}
//		});

		// ... and so on for other commands

	}

}

