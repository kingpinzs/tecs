/*
 * Parser.cpp
 *
 *  Created on: Sep 24, 2012
 *      Author: John Ness
 */

#include "Parser.h"
#include "Instructions.h"

//Parser's constructor is responsible for building each map, initializing variables,
//reading in an assembly language file and parsing the file.
Parser::Parser(const char *fileName) {

	buildDestMap();
	buildCompMap();
	buildJumpMap();
	buildSymbolMap();

	_availableRamAddress = 16;
	_commandCount = 0;
	_currentCharLocation = 0;
	_outputContent = "";
	_fileExists = true;
	_file.open(fileName, ifstream::in);
	if(!_file)
		_fileExists = false;
	else {
		try {
			readFile();
			readNextCharacter();

			//First pass to find all L commands.
			while(hasMoreCommands()) {
				advance(true); //findLcommands = true
			}

			//Reset values.
			_commandCount = 0;
			_currentCharLocation = 0;
			_outputContent = "";
			_currentCharacter = ' ';

			//Second pass to find the rest of the commands.
			while(hasMoreCommands()) {
				advance(false); //findLcommands = false
			}
		} catch(ifstream::failure &e) {
			cout << "Problem reading file." << endl;
		}
		_file.close();
	}
}

//Parser's destructor is not used.
Parser::~Parser() { }

//Read the entire file to a stringstream and store it's contents into a string (_fileContent)
void Parser::readFile() {
	stringstream in;
	in << _file.rdbuf();
	_fileContent = in.str();
	_file.close();
}

//Read the next character of the file and increment the current character location.
void Parser::readNextCharacter() {
	_currentCharacter = _fileContent[_currentCharLocation];
	_currentCharLocation++;
}

//Determine whether there are more commands or we have reached the end of the data.
bool Parser::hasMoreCommands() {
	bool ret = true;

	if(_currentCharacter == '\0')
		ret = false;

	return ret;
}

//Read the next command from the input and make it the current command.
void Parser::advance(bool findLcommands) {
	_currentCommand = "";
	bool endOfCommand = false;

	while(!endOfCommand) {
		//Skip white space
		if(_currentCharacter == ' ' || _currentCharacter == '\t' ||
				_currentCharacter == '\r') {
			; //Do nothing

		//Command ends at the end of the line, which could be at the end of the file
		} else if(_currentCharacter == '\n') {
			endOfCommand = true;

		//Add the current character to the current command
		} else {
			_currentCommand += _currentCharacter;
		}
		readNextCharacter();
	}
	bool isComment = false;

	//Flag a comment
	if(_currentCommand != "") {
		if(_currentCommand[0] == '/' && _currentCommand[1] == '/')
			isComment = true;
	}

	//If command turns out to be a comment or a blank line, skip to the next command
	if(isComment || _currentCommand == "")
		advance(findLcommands);
	//Find any comments after commands and convert the parsed text.
	else {
		size_t foundComment = _currentCommand.find("//");
		int i = (int)foundComment;

		if(i != -1)
			_currentCommand.replace(foundComment, _currentCommand.length(), "");

		//If the command is an A command, output the decimal value as 16bit binary.
		if(commandType() == A_COMMAND && findLcommands == false) {
			_currentCommand.erase(0,1);

			//If the current A command's first char element is a digit, it's a numerical constant.
			if(isdigit(_currentCommand[0]))
				_outputContent += to16BitBin(atoi(_currentCommand.c_str()));
			else {
				//If the symbol map does not contain the symbol, add it and assign it to available ram address.
				map<string,int>::iterator it = symbolMap.find(_currentCommand);
				if(it == symbolMap.end()) {
					symbolMap[_currentCommand] = _availableRamAddress;
					_availableRamAddress++;
					_outputContent += to16BitBin(symbolMap[_currentCommand]);
				}
				else
					_outputContent += to16BitBin(symbolMap[_currentCommand]);
			}

		//If it's not an A command, we can assume it's a C command until L commands are implemented.
		//Place all of the bits depending on what comp, dest, and jump are for the current command.
		} else if(commandType() == L_COMMAND) {
			//If we are just finding L commands, add the L command to the symbol map.
			if(findLcommands == true) {
				//Remove "(" and ")" and store result into symbol map.
				size_t foundParen = _currentCommand.find("(");
				_currentCommand.replace(foundParen, 1, "");

				foundParen = _currentCommand.find(")");
				_currentCommand.replace(foundParen, 1, "");

				//Store label into the symbol map with the appropriate memory address.
				symbolMap[_currentCommand] = _commandCount;
			}

			//Decrement the number of commands since this "command" was only a label.
			_commandCount--;

		} else if(findLcommands == false) {
			_outputContent += "111";
			_outputContent += comp();
			_outputContent += dest();
			_outputContent += jump();
		}

		//Add a new line to the output after a command has been converted.
		if(commandType() != L_COMMAND)
			_outputContent += "\n";

		//Increment the number of commands.
		_commandCount++;
	}
}

//Export the binary format to a file.
void Parser::exportHackFile(const char *fileName) {
	ofstream out(fileName);
	out << _outputContent;
	out.close();
}

//Returns the type of the current command.
CommandType Parser::commandType() {
	if(_currentCommand[0] == '@')
		_command = A_COMMAND;
	else if(_currentCommand[0] == '(')
		_command = L_COMMAND;
	else
		_command = C_COMMAND;

	return _command;
}

//Returns the symbol or decimal of the current A command.
string Parser::symbol() {
	string symbol = _currentCommand;

	if(commandType() == A_COMMAND)
		symbol.erase(0,1);
	else
		symbol = "";


	return symbol;
}

//Returns the corresponding destination binary form for the current command.
string Parser::dest() {
	string destination = "NA";
	string ret = "";

	if(_currentCommand[1] == '=')
		destination = _currentCommand[0];
	else if(_currentCommand[2] == '='){
		destination = _currentCommand[0];
		destination += _currentCommand[1];
	}

	ret = destMap[destination];

	return ret;
}

//Returns the corresponding comp binary form for the current command.
string Parser::comp() {
	string comp = "NA";
	string ret = "";

	if(_currentCommand[1] == '=') {
		comp  = _currentCommand.substr(2);
	}
	else if(_currentCommand[1] == ';') {
		comp = _currentCommand[0];
	}
	else {
		comp  = _currentCommand.substr(3);
	}

	ret = compMap[comp];

	return ret;
}

//Returns the corresponding jump binary form for the current command.
string Parser::jump() {
	string ret = "000";

	size_t foundJump = _currentCommand.find("J");
	int location = (int)foundJump;

	if(location != -1){
		string jumpType = _currentCommand.substr(location, 3);
		ret = jumpMap[jumpType];
	}

	return ret;
}
