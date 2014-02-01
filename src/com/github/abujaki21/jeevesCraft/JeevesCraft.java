/************************************************************\
 * Author: abujaki21										*
 * Plugin: JeevesCraft										*
 * Notes: This is a collection of small one-offs used to 	*
 * fine tune things I dont agree with. Presented to Josh for*
 * his server.												*
 * 															*
 * website: https://github.com/abujaki21/minestock			*
 * 															*
 * Version: 3.0												*
 * 															*
 * This software is presented AS IS and without warranty	*
 * of any kind. I will not be held responsible for			*
 * losses imagined or otherwise from use of this software.	*
 *  														*
 \***********************************************************/

package com.github.abujaki21.jeevesCraft;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Server;

public final class JeevesCraft extends JavaPlugin implements Listener{
	private Logger logger;
	private Server server;
	private File configFile;
	private YamlConfiguration config;

	@Override
	public void onEnable(){
		logger = getLogger();
		server = getServer();
		configFile = new File(getDataFolder(), "config.yml");

		//---If there is no configuration file, create one from default
		if(!configFile.exists()){
			logger.info("No configuration exists! Setting up for the first time");
			saveDefaultConfig();
		}

		loadConfiguration();

		//Load recipes from recipe book
		logger.info("Loading Recipes...");
		RecipeBook.enableRecipes(server, config);

		//Load listeners
		logger.info("Listening intently...");
		new GiantSpawner(this);
		new SpongeMech(this);
		new PlayerDeath(this);
	}

	@Override
	public void onDisable(){
		saveConfiguration();
		
		logger.info("Goodnight");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if((cmd.getName().equalsIgnoreCase("loadConfig") && sender.isOp())){
			loadConfiguration();
		}
		else if(cmd.getName().equalsIgnoreCase("saveConfig")){
			saveConfiguration();
		}
		return false;
	}

	private void loadConfiguration(){
		//--Load configuration file
		try {
			config = new YamlConfiguration();
			config.load(configFile);
		} catch (InvalidConfigurationException e) { //Bad configuration
			logger.severe("Config file is broken. Restoring defaults...");
			saveDefaultConfig();
		} catch (IOException e){ //Issues with opening the file
			logger.severe("Unable to read the config file, Using defaults");
		}
	}

	private void saveConfiguration(){
		//Try to save the config file
		// Configuration cannot be changed while running.
		try {
			//Copy over any new values (plugin update, omitted, etc)
			config.options().copyDefaults(true);
			//Save values in memory
			config.save(configFile);
		} catch (IOException e) {
			logger.severe("Config file could not be saved");
		}
	}
}
