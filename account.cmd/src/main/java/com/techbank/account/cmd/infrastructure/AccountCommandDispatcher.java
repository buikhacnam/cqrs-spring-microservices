package com.techbank.account.cmd.infrastructure;

import com.techbank.cqrs.cors.commands.BaseCommand;
import com.techbank.cqrs.cors.commands.CommandHandlerMethod;
import com.techbank.cqrs.cors.infrastructure.CommandDispatcher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AccountCommandDispatcher implements CommandDispatcher {
    private final Map<Class<? extends BaseCommand>, List<CommandHandlerMethod>> routes = new HashMap<>();

//   sampleRoutes = {
//            OpenAccountCommand.class -> [
//            commandHandler.handle(OpenAccountCommand)
//            ],
//
//    DepositFundsCommand.class -> [
//            commandHandler.handle(DepositFundsCommand)
//            ],
//
//    WithdrawFundsCommand.class -> [
//            commandHandler.handle(WithdrawFundsCommand)
//            ],
//
//    CloseAccountCommand.class -> [
//            commandHandler.handle(CloseAccountCommand)
//            ],
//
//    RestoreReadDbCommand.class -> [
//            commandHandler.handle(RestoreReadDbCommand)
//            ]
//}

    @Override
    public <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod<T> handler) {
        //        List<CommandHandlerMethod> handlers;
//        if (routes.containsKey(type)) {
//            handlers = routes.get(type);  // handlers points to the list in routes
//        } else {
//            handlers = new LinkedList<>();  // create new list
//            routes.put(type, handlers);    // put the list reference in routes
//        }
//        handlers.add(handler);  // modifies the list that routes points to

        var handlers = routes.computeIfAbsent(type, c -> new LinkedList<>());
        handlers.add(handler);

    }

    @Override
    public void send(BaseCommand command) {
        var handlers = routes.get(command.getClass());
        if(handlers == null || handlers.isEmpty()) {
            throw new RuntimeException("No handler for the command " + command.getClass().getName());
        }

        if(handlers.size() > 1) {
            throw new RuntimeException("There is more than one handler for the command " + command.getClass().getName());
        }
        handlers.get(0).handle(command);
    }
}
