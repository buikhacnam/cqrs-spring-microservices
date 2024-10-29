package com.techbank.account.cmd.api.commands;

import com.techbank.cqrs.cors.commands.BaseCommand;

public class CloseCommand extends BaseCommand {
    public CloseCommand(String id) {
        super(id);
    }
}
