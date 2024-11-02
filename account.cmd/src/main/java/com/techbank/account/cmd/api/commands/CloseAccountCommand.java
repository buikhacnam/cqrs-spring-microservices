package com.techbank.account.cmd.api.commands;

import com.techbank.cqrs.cors.commands.BaseCommand;

public class CloseAccountCommand extends BaseCommand {
    public CloseAccountCommand(String id) {
        super(id);
    }
}
