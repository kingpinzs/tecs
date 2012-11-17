/*
 * CommandType.h
 *
 *  Created on: Sep 24, 2012
 *      Author: John Ness
 */

#ifndef COMMANDTYPE_H_
#define COMMANDTYPE_H_

//Hack assembly language command types
enum CommandType {

	//@Xxx, where Xxx is a number
	A_COMMAND,

	//dest = comp, jump
	C_COMMAND
};


#endif /* COMMANDTYPE_H_ */
