package com.github.mashlol.Messages;

public class Command {

	private String command;
	private String permissionNode;
	private String commandHelp;
	private String parameters;
	
	public Command (String command, String commandHelp, String parameters, String permissionNode) {
		this.command = command;
		this.permissionNode = permissionNode;
		this.commandHelp = commandHelp;
		this.parameters = parameters;
	}
	
	public String getCommand () {
		return command;
	}
	
	public String getPermissionNode () {
		return permissionNode;
	}
	
	public String getCommandHelp () {
		return commandHelp;
	}
	
	public String getParameters () {
		return parameters;
	}
	
}
