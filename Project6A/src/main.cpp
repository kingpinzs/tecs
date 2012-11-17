/*
 * main.cpp
 *
 *  Created on: Sep 24, 2012
 *      Author: John Ness
 */

#include "Parser.h"
using namespace std;

string fileName = "PongL.asm"; //File name of the source file to be processed.

//The main function for the assembler.
int main(int argc, char **argv) {

	//If a file name is specified in the first command line argument, set the file name to that.
	if(argc > 1)
		fileName = argv[1];

	cout << "Assembling " << fileName << "..." << endl;

	//Create a Parser to perform the assembling operations on the specified file.
	Parser parser(fileName.c_str());

	//If the file exists, export the output to a .hack file of the same name as the specified .asm file.
	if(parser._fileExists) {
		size_t extension = fileName.find(".asm");
		fileName.erase(extension);
		fileName += ".hack";
		parser.exportHackFile(fileName.c_str());
		cout << "File has been successfully assembled: " << fileName << endl;
	//If the file does not exist, inform the user.
	} else {
		cout << "File does not exist." << endl;
	}

	return 0;
}
