package com.techbank.account.cmd.api.commands;

import com.techbank.cqrs.cors.commands.BaseCommand;
import lombok.Data;

@Data
public class DepositFundsCommand extends BaseCommand {
    private double amount;
}
