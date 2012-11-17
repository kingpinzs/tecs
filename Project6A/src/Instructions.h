/*
 * Instructions.h
 *
 *  Created on: Sep 26, 2012
 *      Author: John Ness
 */

#ifndef INSTRUCTIONS_H_
#define INSTRUCTIONS_H_

#include <string>
#include <map>
using namespace std;

//std::map for destinations and their corresponding binary forms
map<string, string> destMap;

//std::map for comps and their corresponding binary forms
map<string, string> compMap;

//std::map for jumps and their corresponding binary forms
map<string, string> jumpMap;

//builds the destination map
void buildDestMap() {
	destMap["NA"] = "000";
	destMap["M"] = "001";
	destMap["D"] = "010";
	destMap["MD"] = "011";
	destMap["A"] = "100";
	destMap["AM"] = "101";
	destMap["AD"] = "110";
	destMap["AMD"] = "111";
}

//builds the comp map
void buildCompMap() {
	compMap["0"] = "0101010";
	compMap["1"] = "0111111";
	compMap["-1"] = "0111010";
	compMap["D"] = "0001100";
	compMap["A"] = "0110000";
	compMap["M"] = "1110000";
	compMap["!D"] = "0001101";
	compMap["!A"] = "0110001";
	compMap["!M"] = "1110001";
	compMap["-D"] = "0001111";
	compMap["-A"] = "0110011";
	compMap["-M"] = "1110011";
	compMap["D+1"] = "0011111";
	compMap["A+1"] = "0110111";
	compMap["M+1"] = "1110111";
	compMap["D-1"] = "0001110";
	compMap["A-1"] = "0110010";
	compMap["M-1"] = "1110010";
	compMap["D+A"] = "0000010";
	compMap["D+M"] = "1000010";
	compMap["D-A"] = "0010011";
	compMap["D-M"] = "1010011";
	compMap["A-D"] = "0000111";
	compMap["M-D"] = "1000111";
	compMap["D&A"] = "0000000";
	compMap["D&M"] = "1000000";
	compMap["D|A"] = "0010101";
	compMap["D|M"] = "1010101";
}

//builds the jump map
void buildJumpMap() {
	jumpMap["NA"] = "000";
	jumpMap["JGT"] = "001";
	jumpMap["JEQ"] = "010";
	jumpMap["JGE"] = "011";
	jumpMap["JLT"] = "100";
	jumpMap["JNE"] = "101";
	jumpMap["JLE"] = "110";
	jumpMap["JMP"] = "111";
}

//convert an integer to a binary in string format
string toBin(int v) {
    if(v == 0)
    	return "0";
    if(v == 1)
    	return "1";

    if(v%2 == 0)
        return toBin(v/2) + "0";
    else
        return toBin(v/2) + "1";
}

//convert an integer into a 16bit binary in string format
string to16BitBin(int v) {
	string ret = toBin(v);

	if(ret.length() < 16) {
		int toFill = 16 - ret.length();
		for(int i = 0; i < toFill; i++) {
			ret.insert(0, "0");
		}
	}

	return ret;
}

#endif /* INSTRUCTIONS_H_ */
