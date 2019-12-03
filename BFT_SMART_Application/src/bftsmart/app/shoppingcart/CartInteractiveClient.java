/**
Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package bftsmart.app.shoppingcart;

import java.io.IOException;
import java.util.Scanner;
import java.util.Map;
import java.util.InputMismatchException;

import java.io.Console;
import java.util.TreeMap;

/**
 *
 * @Editted by Haoru
 */
public class CartInteractiveClient {


	public static void main(String[] args) throws IOException {
		if(args.length < 1) {
			System.out.println("Usage: java CartInteractiveClient <process id>");
			System.exit(-1);
		}

		Cart cart = new Cart(Integer.parseInt(args[0]));
//		System.out.println(cart.test());

		Console console = System.console();
		Scanner sc = new Scanner(System.in);
		//TODO: DATA INITIALIZATION


		while(true) {
			//TODO: implement READ ALL
//			System.out.println();
//			System.out.println("---------------Select one of the commands ----------------");
//			System.out.println("select a command : 0. READ CONTENTS OF ALL SHOPPING CARTS");
//			System.out.println("select a command : 1. CREATE A NEW SHOPPING CART");
//			System.out.println("select a command : 2. REMOVE AN EXISTING SHOPPING CART");
//			System.out.println("select a command : 3. GET THE SIZE OF THE SHOPPING CART");
//			System.out.println("select a command : 4. PUT VALUES INTO A TABLE");
//			System.out.println("select a command : 5. GET VALUES FROM A TABLE");
//			System.out.println("select a command : 6. GET THE SIZE OF A TABLE");
//			System.out.println("select a command : 7. REMOVE AN EXISTING TABLE");
//			System.out.println("select a command : 11. EXIT");
//			System.out.println("---------------------------------------------------------");

			System.out.println();
			System.out.println("---------------Select one of the commands ----------------");
			System.out.println("select a command : 0. GENERATE SHOPPING CART TABLE");
			System.out.println("select a command : 1. CREATE A NEW SHOPPING CART");
			System.out.println("select a command : 2. REMOVE AN EXISTING SHOPPING CART");
			System.out.println("select a command : 3. GET THE COUNT OF ALL SHOPPING CARTS");
			System.out.println("select a command : 4. PUT PRODUCT INTO A SHOPPING CART");
			System.out.println("select a command : 5. GET PRODUCT FROM A SHOPPING CART"); // can be replaced by command 8
			System.out.println("select a command : 6. GET THE SIZE OF A SHOPPING CART");
			System.out.println("select a command : 7. REMOVE PRODUCT FROM A SHOPPING CART");
			System.out.println("select a command : 8. READ CONTENT OF A SHOPPING CART");
			System.out.println("select a command : 9. EXIT");
			System.out.println("----------------------------------------------------------");

			int cmd = sc.nextInt();
			boolean continueInput = true;
			String cartName;
			boolean cartExists;
			switch(cmd) {
			//TODO READALL
			case CartRequestType.READALL:

				break;
			//TODO  like CART_REMOVE
			case CartRequestType.READ:
				cartExists = false;
				do {
					cartName = console.readLine("Enter the Cart name: ");
					cartExists = cart.containsKey(cartName);
					System.out.println("cart name: "+ cartName);
					if (!cartExists) {
						//if the table name does not exist then print the error message
						System.out.println("cart Doest not exists");
					}else{
//						System.out.println("here");
						Map<String,byte[]> res = cart.read(cartName);
						System.out.println("-------------------------------------------");
						System.out.println("cart content of "+ cartName);
						for(String k: res.keySet()){
							System.out.println("key: "+k + ", value: "+new String(res.get(k)));
						}
						System.out.println("-------------------------------------------");
						cartExists = true;
					}
				} while(cartExists);
				break;

				case CartRequestType.CART_REMOVE:
					//Remove the table entry
					cartExists = false;
					cartName = null;
					System.out.println("Removing table");
					cartName = console.readLine("Enter the valid table name you want to remove: ");
					cartExists = cart.containsKey(cartName);
					if(cartExists) {
						Map<String,byte[]> map = cart.remove(cartName);
						System.out.println("Table removed: " + map.keySet());
					} else
						System.out.println("Table not found");
					break;

				//operations on the table
			case CartRequestType.CART_CREATE:
				cartExists = false;
				do {
					cartName = console.readLine("Enter the Cart name: ");
					cartExists = cart.containsKey(cartName);
					if (!cartExists) {
						//if the table name does not exist then create the table
						cart.put(cartName, new TreeMap<String,byte[]>());
					}else{
						System.out.println("Cart already exists");
					}
				} while(cartExists);
				break;

			case CartRequestType.SIZE_CART:
				//obtain the size of the table of tables.
				System.out.println("Computing the size of the table");
				int size = cart.size();
				System.out.println("The size of the table of tables is: "+size);
				break;

				//operations on the hashmap
			case CartRequestType.PUT:
				System.out.println("Execute put function");
				cartExists = false;
				cartName = null;
				size = -1;
				cartName = console.readLine("Enter the valid table name in which you want to insert data: ");
				String key = console.readLine("Enter a numeric key for the new record in the range 0 to 9999: ");
				String value = console.readLine("Enter the value for the new record: ");

				byte[] resultBytes;
				cartExists = cart.containsKey(cartName);
				if(cartExists) {
					while(key.length() < 4)
						key = "0" + key;
					byte[] byteArray = value.getBytes();
					resultBytes = cart.putEntry(cartName, key, byteArray);
				} else
					System.out.println("Table not found");
				break;

			case CartRequestType.GET:
				System.out.println("Execute get function");
				cartExists = false;
				boolean keyExists = false;
				cartName = null;
				key = null;
				cartName = console.readLine("Enter the valid table name from which you want to get the values: ");
				cartExists = cart.containsKey(cartName);
				if (cartExists) {
					key = console.readLine("Enter the key: ");
					while(key.length() < 4)
						key = "0" + key;
					keyExists = cart.containsKey1(cartName, key);
					if(keyExists) {
						resultBytes = cart.getEntry(cartName,key);
						System.out.println("The value received from GET is: " + new String(resultBytes));
					} else
						System.out.println("Key not found");
				} else
					System.out.println("Table not found");
				break;

			case CartRequestType.SIZE:
				System.out.println("Execute get function");
				cartExists = false;
				cartName = null;
				size = -1;
				cartName = console.readLine("Enter the valid table whose size you want to detemine: ");

				cartExists = cart.containsKey(cartName);
				if (cartExists) {
					size = cart.size1(cartName);
					System.out.println("The size is: " + size);
				} else {
					System.out.println("Table not found");
				}
				break;
			case CartRequestType.REMOVE:
				System.out.println("Execute get function");
				cartExists = false;
				keyExists = false;
				cartName = null;
				key = null;
				cartName = console.readLine("Enter the table name from which you want to remove: ");
				cartExists = cart.containsKey(cartName);
				if(cartExists) {
					key = console.readLine("Enter the valid key: ");
					keyExists = cart.containsKey1(cartName, key);
					if(keyExists) {
						byte[] result2 = cart.removeEntry(cartName,key);
						System.out.println("The previous value was : "+new String(result2));
					} else
						System.out.println("Key not found");
				} else
					System.out.println("Table not found");
				break;
			case CartRequestType.EXIT:
				System.exit(-1);
				break;
			default:
				System.out.println("Invalid Input, try again");
			}
		}
	}
}
