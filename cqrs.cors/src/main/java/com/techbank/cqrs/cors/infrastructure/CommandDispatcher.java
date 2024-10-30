package com.techbank.cqrs.cors.infrastructure;

import com.techbank.cqrs.cors.commands.BaseCommand;
import com.techbank.cqrs.cors.commands.CommandHandlerMethod;

public interface CommandDispatcher {
    <T extends BaseCommand> void registerHandler(Class<T> commandType, CommandHandlerMethod<T> handler);
    void send(BaseCommand command);
}
