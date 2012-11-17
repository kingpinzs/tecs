/*
 * Parser.h
 *
 *  Created on: Sep 24, 2012
 *      Author: John Ness
 */

#ifndef PARSER_H_
#define PARSER_H_

#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <cstdlib>
#include "CommandType.h"
using namespace std;

//The Parser class is responsible reading and parsing an assembly language source file,
//and exporting the binary equivalent.
class Parser {

public:
	Parser(const char *fileName);
	virtual ~Parser();
	bool hasMoreCommands();
	void advance();
	void exportHackFile(const char *fileName);
	CommandType commandType();
	string symbol();
	string dest();
	string comp();
	string jump();
	bool _fileExists;

private:
	ifstream _file;
	string _fileContent;
	string _outputContent;
	char _currentCharacter;
	int _currentCharLocation;
	string _currentCommand;
	CommandType _command;
	void readFile();
	void readNextCharacter();

};

#endif /* PARSER_H_ */
